/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.tas.v1.agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.omnione.did.base.datamodel.data.AttestedAppInfo;
import org.omnione.did.base.datamodel.data.Proof;
import org.omnione.did.base.datamodel.data.Provider;
import org.omnione.did.base.datamodel.data.ServerTokenData;
import org.omnione.did.base.datamodel.data.ServerTokenSeed;
import org.omnione.did.base.datamodel.data.SignedWalletInfo;
import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import org.omnione.did.base.datamodel.enums.ServerTokenPurpose;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.constant.WalletStatus;
import org.omnione.did.base.db.domain.Ecdh;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.Token;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.repository.TokenRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenResDto;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.query.ApiQueryService;
import org.omnione.did.tas.v1.common.service.query.EcdhQueryService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.v1.common.service.query.WalletQueryService;
import org.omnione.did.tas.v1.agent.service.validator.CertificateVcValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.DidValidator;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.common.util.NonceGenerator;
import org.omnione.did.data.model.did.DidDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Token service implementation for managing tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!sample")
public class TokenServiceImpl implements TokenService {
    private final TransactionService transactionService;
    private final WalletQueryService walletQueryService;
    private final EcdhQueryService ecdhQueryService;
    private final TasQueryService tasQueryService;
    private final TokenRepository tokenRepository;
    private final FileWalletService fileWalletService;
    private final DidDocService didDocService;
    private final CertificateVcValidator certificateVcValidator;
    private final ApiQueryService apiQueryService;

    /**
     * Handles the request to create a token.
     *
     * @param requestCreateTokenReqDto The request DTO containing the transaction ID and seed.
     * @return The response DTO containing the transaction ID, initialization vector, and encrypted server token data.
     */
    @Override
    public RequestCreateTokenResDto requestCreateToken(RequestCreateTokenReqDto requestCreateTokenReqDto) {
        try {
            log.info("=== Starting requestCreateToken ===");
            // Retrieve Transaction information.
            log.debug("\t--> Retrieving Transaction information");
            Transaction transaction = transactionService.findTransactionByTxId(requestCreateTokenReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction(transaction, lastSubTransaction);

            // Validate Server token data
            log.debug("\t--> Validating Server token seed");
            validateServerTokenSeed(transaction.getType(), requestCreateTokenReqDto.getSeed());

            // Retrieve token expiration date and time.
            log.debug("\t--> Retrieving token expiration date and time");
            String tokenValidUntil = DateTimeUtil.addSecondsToCurrentTimeString(apiQueryService.findTokenExpirationTime());

            // Generate Server token data.
            log.debug("\t--> Generating Server token data");
            ServerTokenData serverTokenData = generateServerTokenData(requestCreateTokenReqDto.getSeed(), tokenValidUntil);

            // Generate Server token.
            log.debug("\t--> Generating Server token");
            byte[] serverTokenBytes = generateServerToken(serverTokenData);
            String encodedServerToken = BaseMultibaseUtil.encode(serverTokenBytes);

            // Retrieve Ecdh information.
            log.debug("\t--> Retrieving Ecdh information");
            Ecdh ecdh = ecdhQueryService.findEcdhByTransactionId(transaction.getId());

            // Generate Initialization Vector.
            log.debug("\t--> Generating Initialization Vector");
            byte[] ivBytes = BaseCryptoUtil.generateInitialVector();
            String encodedIv = BaseMultibaseUtil.encode(ivBytes);

            // Encrypt server token data.
            log.debug("\t--> Encrypting server token data");
            byte[] encryptedServerTokenDataBytes = encryptServerTokenData(serverTokenData, ecdh, ivBytes);
            String encodedEncryptedStd = BaseMultibaseUtil.encode(encryptedServerTokenDataBytes);

            // Insert Server token data.
            log.debug("\t--> Inserting Server token data");
            tokenRepository.save(Token.builder()
                    .purpose(requestCreateTokenReqDto.getSeed().getPurpose().toString())
                    .token(encodedServerToken)
                    .appId(requestCreateTokenReqDto.getSeed().getCaAppInfo().getAppId())
                    .walletId(requestCreateTokenReqDto.getSeed().getWalletInfo().getWallet().getId())
                    .expiredAt(DateTimeUtil.parseUtcTimeStringToInstant(tokenValidUntil))
                    .transactionId(transaction.getId())
                    .build());

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_CREATE_TOKEN)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );
            log.debug("*** Finished requestCreateToken ***");

            return RequestCreateTokenResDto.builder()
                    .txId(requestCreateTokenReqDto.getTxId())
                    .iv(encodedIv)
                    .encStd(encodedEncryptedStd)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during requestCreateToken: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during requestCreateToken: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAIL_TO_REQUEST_CREATE_TOKEN);
        }
    }

    /**
     * Validates the transaction and sub-transaction.
     *
     * @param transaction The transaction to validate.
     * @param subTransaction The sub-transaction to validate.
     */
    private void validateTransaction(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_ECDH) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Validates the Server token seed.
     *
     * @param transactionType The transaction type.
     * @param serverTokenSeed The Server token seed to validate.
     */
    private void validateServerTokenSeed(TransactionType transactionType, ServerTokenSeed serverTokenSeed) {
        // Validate token purpose.
        log.debug("\t--> Validating token purpose");
        validateTokenPurpose(transactionType, serverTokenSeed.getPurpose());

        // Validate Wallet Provider information.
        log.debug("\t--> Validating wallet information");
        validateWalletInfo(serverTokenSeed.getWalletInfo(), transactionType);

        // Validate CAS information.
        log.debug("\t--> Validating cas information");
        validateCasInfo(serverTokenSeed.getCaAppInfo());
    }

    /**
     * Validates the token purpose.
     *
     * @param transactionType The transaction type.
     * @param serverTokenPurpose The token purpose to validate.
     * @throws OpenDidException if the token purpose is invalid.
     */
    private void validateTokenPurpose(TransactionType transactionType, ServerTokenPurpose serverTokenPurpose) {
        if (TransactionType.USER_REGISTRATION == transactionType) {
            if (serverTokenPurpose != ServerTokenPurpose.CREATE_DID
                    && serverTokenPurpose != ServerTokenPurpose.CREATE_DID_AND_ISSUE_VC) {
                log.error("\t--> Unsupported token purpose: {} for transaction type: {}", serverTokenPurpose, transactionType);
                throw new OpenDidException(ErrorCode.UNSUPPORTED_PURPOSE);
            }
        } else if (TransactionType.ISSUE_VC == transactionType) {
            if (serverTokenPurpose != ServerTokenPurpose.ISSUE_VC) {
                log.error("\t--> Unsupported token purpose: {} for transaction type: {}", serverTokenPurpose, transactionType);
                throw new OpenDidException(ErrorCode.UNSUPPORTED_PURPOSE);
            }
        } else if (TransactionType.USER_UPDATE == transactionType) {
            if (serverTokenPurpose != ServerTokenPurpose.UPDATE_DID) {
                log.error("\t--> Unsupported token purpose: {} for transaction type: {}", serverTokenPurpose, transactionType);
                throw new OpenDidException(ErrorCode.UNSUPPORTED_PURPOSE);
            }
        } else if (TransactionType.DIDDOC_RESTORE == transactionType) {
            if (serverTokenPurpose != ServerTokenPurpose.RESTORE_DID) {
                log.error("\t--> Unsupported token purpose: {} for transaction type: {}", serverTokenPurpose, transactionType);
                throw new OpenDidException(ErrorCode.UNSUPPORTED_PURPOSE);
            }
        } else if (TransactionType.REVOKE_VC == transactionType) {
            if (serverTokenPurpose != ServerTokenPurpose.REVOKE_VC) {
                log.error("\t--> Unsupported token purpose: {} for transaction type: {}", serverTokenPurpose, transactionType);
                throw new OpenDidException(ErrorCode.UNSUPPORTED_PURPOSE);
            }
        }
    }

    /**
     * Validates the Wallet information.
     *
     * @param signedWalletInfo The signed wallet information to validate.
     * @param transactionType The transaction type.
     * @throws OpenDidException if the Wallet information is invalid.
     */
    private void validateWalletInfo(SignedWalletInfo signedWalletInfo, TransactionType transactionType) {
        // Determine wallet status based on transaction type.
        WalletStatus walletStatus = (transactionType == TransactionType.USER_REGISTRATION) ? WalletStatus.CREATED : WalletStatus.ASSIGNED;

        // Checks if the Wallet is created but not assigned.
        long walletCount = walletQueryService.countByWalletIdAndDidAndStatus(
                signedWalletInfo.getWallet().getId(), signedWalletInfo.getWallet().getDid(), walletStatus);

        if (walletCount == 0) {
            log.error("\t--> Wallet info not found for wallet ID: {} and DID: {}", signedWalletInfo.getWallet().getId(), signedWalletInfo.getWallet().getDid());
            throw new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND);
        }

        // Validate Wallet Provider's signature.
        validateWalletProof(signedWalletInfo);
    }

    /**
     * Validates the Wallet Provider's signature.
     *
     * @param signedWalletInfo The signed wallet information to validate.
     * @throws OpenDidException if the Wallet Provider's signature is invalid.
     */
    private void validateWalletProof(SignedWalletInfo signedWalletInfo) {
        // Extract and validate didKeyUrl
        String verificationMethod = signedWalletInfo.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            log.error("\t--> Invalid DID key URL: {}", verificationMethod);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (signedWalletInfo.getProof().getProofPurpose() != ProofPurpose.ASSERTION_METHOD) {
            log.error("\t--> Invalid proof purpose: {}", signedWalletInfo.getProof().getProofPurpose());
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Extract the signature message.
        byte[] signatureMessage = extractSignatureMessage(signedWalletInfo);

        // Find Wallet DID Document.
        DidDocument walletProviderDidDocument = didDocService.getDidDocument(verificationMethod);

        // Get the Assertion public key.
        String encodedAssertPublicKey = BaseCoreDidUtil.getPublicKey(walletProviderDidDocument, "assert");

        // Verify the signature.
        verifySignature(encodedAssertPublicKey, signedWalletInfo.getProof().getProofValue(), signatureMessage, signedWalletInfo.getProof().getType());
    }

    /**
     * extracts the signature message.
     *
     * @param signedWalletInfo The signed wallet information to extract the signature message.
     * @return The extracted signature message.
     * @throws OpenDidException if Failed to verify signature.
     */
    private byte[] extractSignatureMessage(SignedWalletInfo signedWalletInfo) {
        try {
            // Remove proofValue from Proof fields in the object.
            SignedWalletInfo signatureMessageObject = removeProofValue(signedWalletInfo);

            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (OpenDidException e) {
          throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during extractSignatureMessage: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Removes the proof value from the signed wallet information.
     *
     * @param data The signed wallet information to remove the proof value.
     * @return The signed wallet information without the proof value.
     */
    private SignedWalletInfo removeProofValue(SignedWalletInfo data) {
        SignedWalletInfo signatureMessageObject = SignedWalletInfo.builder()
                .wallet(data.getWallet())
                .nonce(data.getNonce())
                .proof(new Proof(
                        data.getProof().getType(),
                        data.getProof().getCreated(),
                        data.getProof().getVerificationMethod(),
                        data.getProof().getProofPurpose(),
                        null
                ))
                .build();

        return signatureMessageObject;
    }

    /**
     * verify the signature.
     *
     * @param encodedPublicKey The encoded public key to verify the signature.
     * @param signature The signature to verify.
     * @param signatureMassage The signature message to verify.
     * @param proofType The proof type to verify.
     * @throws OpenDidException if Failed to verify signature.
     */
    //@TODO: 공통함수로 빼야 함
    private void verifySignature(String encodedPublicKey, String signature, byte[] signatureMassage, ProofType proofType) {
        try {
            BaseCryptoUtil.verifySignature(encodedPublicKey, signature, signatureMassage, proofType.toEccCurveType());
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during verifySignature: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * validate the CAS information.
     * @param attestedAppInfo The attested application information to validate.
     */
    private void validateCasInfo(AttestedAppInfo attestedAppInfo) {
        // Validate CAS's signature.
        validateCasProof(attestedAppInfo);

        // Validate Wallet Provider's certificate.
        validateProvider(attestedAppInfo.getProvider().getCertVcRef(), attestedAppInfo.getProvider().getDid());
    }

    /**
     * validate the CAS's signature.
     * @param attestedAppInfo The attested application information to validate.
     * @throws OpenDidException if the CAS's signature is invalid.
     */
    private void validateCasProof(AttestedAppInfo attestedAppInfo) {
        // Extract and validate did and didKeyUrl
        String clientDid = attestedAppInfo.getProvider().getDid();
        if (!DidValidator.isValidDid(clientDid)){
            log.error("\t--> Invalid DID: {}", clientDid);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        String verificationMethod = attestedAppInfo.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            log.error("\t--> Invalid DID key URL: {}", verificationMethod);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the equivalence of did.
        String didOfKeyUrl = DidUtil.extractDid(verificationMethod);
        if (!clientDid.equals(didOfKeyUrl)) {
            log.error("\t--> DID mismatch: clientDid={}, didOfKeyUrl={}", clientDid, didOfKeyUrl);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (attestedAppInfo.getProof().getProofPurpose() != ProofPurpose.ASSERTION_METHOD) {
            log.error("\t--> Invalid proof purpose: {}", attestedAppInfo.getProof().getProofPurpose());
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Extract the signature message.
        byte[] signatureMessage = extractSignatureMessage(attestedAppInfo);

        // Find Wallet Provider DID Document.
        DidDocument didDocument = didDocService.getDidDocument(verificationMethod);

        // Get the Assertion public key.
        String encodedAssertPublicKey = BaseCoreDidUtil.getPublicKey(didDocument, "assert");

        // Verify the signature.
        verifySignature(encodedAssertPublicKey, attestedAppInfo.getProof().getProofValue(), signatureMessage, attestedAppInfo.getProof().getType());
    }

    /**
     * extract the signature message.
     * @param attestedAppInfo The attested application information to extract the signature message.
     * @return The extracted signature message.
     * @throws OpenDidException if Failed to extract signature message.
     */
    private byte[] extractSignatureMessage(AttestedAppInfo attestedAppInfo) {
        try {
            // Remove proofValue from Proof fields in the object.
            AttestedAppInfo signatureMessageObject = removeProofValue(attestedAppInfo);

            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch(CommonSdkException e) {
            log.error("\t--> Exception occurred during extractSignatureMessage: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * remove the proof value from the attested application information.
     * @param data The attested application information to remove the proof value.
     * @return The attested application information without the proof value.
     */
    private AttestedAppInfo removeProofValue(AttestedAppInfo data) {
        AttestedAppInfo signatureMessageObject = AttestedAppInfo.builder()
                .appId(data.getAppId())
                .provider(data.getProvider())
                .nonce(data.getNonce())
                .proof(new Proof(
                        data.getProof().getType(),
                        data.getProof().getCreated(),
                        data.getProof().getVerificationMethod(),
                        data.getProof().getProofPurpose(),
                        null
                ))
                .build();

        return signatureMessageObject;
    }

    /**
     * validate the Wallet Provider's certificate.
     * @param certVcRef The certificate VC reference to validate.
     * @param did The Wallet Provider's DID to validate.
     * @throws OpenDidException if the Wallet Provider's certificate is invalid.
     */
    private void validateProvider(String certVcRef, String did) {
        certificateVcValidator.validateCertificateVc(certVcRef, did);
    }

    /**
     * Generates the Server token data.
     *
     * @param serverTokenSeed The Server token seed to generate the Server token data.
     * @param tokenValidUntil The token expiration date and time.
     * @return The generated Server token data.
     */
    private ServerTokenData generateServerTokenData(ServerTokenSeed serverTokenSeed, String tokenValidUntil) {
        // Generate nonce.
        String encodedNonce = BaseMultibaseUtil.encode(NonceGenerator.generate16ByteNonce());

        // Retrieve TAS information.
        Tas tas = tasQueryService.findTas();

        // Extract the signature message.
        ServerTokenData unsignedServerTokenData = ServerTokenData.builder()
                .purpose(serverTokenSeed.getPurpose())
                .walletId(serverTokenSeed.getWalletInfo()
                        .getWallet()
                        .getId())
                .appId(serverTokenSeed.getCaAppInfo()
                        .getAppId())
                .validUntil(tokenValidUntil)
                .provider(Provider.builder()
                        .did(tas.getDid())
                        .certVcRef(tas.getCertificateUrl())
                        .build())
                .nonce(encodedNonce)
                .proof(Proof.builder()
                        .type(ProofType.SECP_256R1_SIGNATURE_2018)
                        .created(DateTimeUtil.getCurrentUTCTimeString())
                        .verificationMethod(tas.getDid() + "#assert")
                        .proofPurpose(ProofPurpose.ASSERTION_METHOD)
                        .proofValue(null)
                        .build())
                .build();

        // Extract the signature message.
        byte[] signatureMessage = extractSignatureMessage(unsignedServerTokenData);

        // Sign ServerTokenData.
        String proofValue = sign(signatureMessage, ProofPurpose.ASSERTION_METHOD);

        // Re-gegenrate ServerTokenData with proofValue.
        ServerTokenData signedServerTokenData = addProofValue(unsignedServerTokenData, proofValue);

        return signedServerTokenData;
    }

    /**
     * Extracts the signature message.
     *
     * @param signatureMessageObject The Server token data to extract the signature message.
     * @return The extracted signature message.
     * @throws OpenDidException if Failed to extract signature message.
     */
    private byte[] extractSignatureMessage(ServerTokenData signatureMessageObject) {
        try {
            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch(CommonSdkException e) {
            throw new OpenDidException(ErrorCode.EXTRACT_SIGNATURE_MESSAGE_FAILED);
        }
    }

    /**
     * Signs the data.
     *
     * @param data The data to sign.
     * @param proofPurpose The proof purpose for which the data is signed.
     * @return The signature value.
     */
    private String sign(byte[] data, ProofPurpose proofPurpose) {
        byte[] signatureBytes = fileWalletService.generateCompactSignature(proofPurpose.toKeyId(), data);
        return BaseMultibaseUtil.encode(signatureBytes);
    }

    /**
     * Adds the proof value to the Server token data.
     *
     * @param serverTokenData The Server token data to add the proof value.
     * @param proofValue The proof value to add.
     * @return The Server token data with the proof value.
     */
    private ServerTokenData addProofValue(ServerTokenData serverTokenData, String proofValue) {
        return ServerTokenData.builder()
                .purpose(serverTokenData.getPurpose())
                .walletId(serverTokenData.getWalletId())
                .appId(serverTokenData.getAppId())
                .validUntil(serverTokenData.getValidUntil())
                .provider(serverTokenData.getProvider())
                .nonce(serverTokenData.getNonce())
                .proof(Proof.builder()
                        .type(serverTokenData.getProof().getType())
                        .created(serverTokenData.getProof().getCreated())
                        .verificationMethod(serverTokenData.getProof().getVerificationMethod())
                        .proofPurpose(serverTokenData.getProof().getProofPurpose())
                        .proofValue(proofValue)
                        .build())
                .build();
    }

    /**
     * Generates the Server token.
     *
     * @param serverTokenData The Server token data to generate the Server token.
     * @return The generated Server token.
     * @throws OpenDidException if the Server token generation fails.
     */
    private byte[] generateServerToken(ServerTokenData serverTokenData) {
        try {
            String jsonString = JsonUtil.serializeAndSort(serverTokenData);
            return BaseDigestUtil.generateHash(jsonString);
        }  catch (CommonSdkException e) {
            log.error("\t--> Json Processing Error: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.JSON_PROCESSING_ERROR);
        } catch (Exception e) {
            log.error("\t--> Exception occurred during generateServerToken: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SERVER_TOKEN_GENERATION_FAILED);
        } 
    }
    /**
     * Encrypts the Server token data.
     *
     * @param serverTokenData The Server token data to encrypt.
     * @param ecdh The Ecdh information.
     * @param iv The Initialization Vector.
     * @return The encrypted Server token data.
     * @throws OpenDidException if the Server token data encryption fails.
     */
    private byte[] encryptServerTokenData(ServerTokenData serverTokenData, Ecdh ecdh, byte[] iv) {
        try {
            String stdJson = JsonUtil.serializeAndSort(serverTokenData);

            // Retrieve Ecdh information.
            SymmetricPaddingType symmetricPaddingType = SymmetricPaddingType.fromDisplayName(ecdh.getPadding());
            SymmetricCipherType symmetricCipherType = SymmetricCipherType.fromDisplayName(ecdh.getCipher());
            byte[] sessionKey = BaseMultibaseUtil.decode(ecdh.getSessionKey());

            // Encrypt the ServerTokenData.
            return BaseCryptoUtil.encrypt(stdJson.getBytes(StandardCharsets.UTF_8), sessionKey, iv, symmetricCipherType, symmetricPaddingType);
        } catch (CommonSdkException e) {
            log.error("\t--> Json Processing Error: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.JSON_PROCESSING_ERROR);
        } catch (Exception e) {
            log.error("\t--> Exception occurred during encryptServerTokenData: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SERVER_TOKEN_ENCRYPTION_FAILED);
        }
    }
}
