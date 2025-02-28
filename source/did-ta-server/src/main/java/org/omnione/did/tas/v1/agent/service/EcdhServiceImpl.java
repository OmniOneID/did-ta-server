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
import org.omnione.did.base.datamodel.data.AccEcdh;
import org.omnione.did.base.datamodel.data.Candidate;
import org.omnione.did.base.datamodel.data.EcdhReqData;
import org.omnione.did.base.datamodel.data.Proof;
import org.omnione.did.base.datamodel.enums.EccCurveType;
import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.domain.Ecdh;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.repository.EcdhRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.TasProperty;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.DidValidator;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.crypto.keypair.KeyPairInterface;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.EnumSet;
import java.util.Set;

/**
 * Implementation of the EcdhService interface for handling ECDH (Elliptic Curve Diffie-Hellman) key exchange.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!sample")
public class EcdhServiceImpl implements EcdhService {

    private final TransactionService transactionService;
    private final DidDocService didDocService;
    private final TasProperty tasProperty;
    private final EcdhRepository ecdhRepository;
    private final FileWalletService fileWalletService;

    /**
     * Handles the ECDH request process.
     *
     * @param requestECDHReqDto The DTO containing the ECDH request details
     * @return RequestECDHResDto The response DTO containing the ECDH response details
     * @throws OpenDidException if there's an error during the ECDH process
     */
    @Override
    public RequestECDHResDto requestECDH(RequestECDHReqDto requestECDHReqDto) {
        try {
            log.info("=== Starting requestECDH ===");
            // Retrieve Transaction information.
            log.debug("\t--> Retrieving Transaction information");
            Transaction transaction = transactionService.findTransactionByTxId(requestECDHReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction(transaction, lastSubTransaction);

            // Verify Signature.
            log.debug("\t--> Verifying signature");
            verifyReqEcdh(requestECDHReqDto.getReqEcdh(), transaction);

            // Generate session key, and save information, and generate response message.
            log.debug("\t--> Generating session key and response data");
            RequestECDHResDto requestECDHResDto = generateSessionKeyAndResponseData(requestECDHReqDto, transaction, lastSubTransaction);
            log.debug("*** Finished requestECDH ***");

            return requestECDHResDto;
        } catch (OpenDidException e) {
            log.error("OpenDidException occurred during requestECDH: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during requestECDH: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAIL_TO_REQUEST_ECDH);
        }
    }

    /**
     * Validates the transaction and sub-transaction for the ECDH process.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction is invalid or expired
     */
    private void validateTransaction(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        Set<SubTransactionType> VALID_TYPES = EnumSet.of(
                SubTransactionType.PROPOSE_ENROLL_ENTITY,
                SubTransactionType.PROPOSE_REGISTER_USER,
                SubTransactionType.PROPOSE_ISSUE_VC,
                SubTransactionType.PROPOSE_UPDATE_DIDDOC,
                SubTransactionType.PROPOSE_RESTORE_DIDDOC,
                SubTransactionType.PROPOSE_REVOKE_VC
        );
        if (!VALID_TYPES.contains(subTransaction.getType())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Verifies the ECDH request data.
     *
     * @param ecdhReqData The ECDH request data to verify
     * @param transaction The associated transaction
     * @throws OpenDidException if the verification fails
     */
    private void verifyReqEcdh(EcdhReqData ecdhReqData, Transaction transaction) {

        // Extract and validate did and didKeyUrl
        String clientDid = ecdhReqData.getClient();
        if (!DidValidator.isValidDid(clientDid)){
            log.error("Invalid DID: {}", clientDid);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        String verificationMethod = ecdhReqData.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            log.error("Invalid DID key URL: {}", verificationMethod);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the equivalence of did.
        String didOfKeyUrl = DidUtil.extractDid(verificationMethod);
        if (!clientDid.equals(didOfKeyUrl)) {
            log.error("DID mismatch: clientDid={}, didOfKeyUrl={}", clientDid, didOfKeyUrl);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (ecdhReqData.getProof().getProofPurpose() != ProofPurpose.KEY_AGREEMENT) {
            log.error("Invalid proof purpose: {}", ecdhReqData.getProof().getProofPurpose());
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Extract the signature message.
        byte[] signatureMessage = extractSignatureMessage(ecdhReqData);

        // Find Wallet Provider DID Document.
        DidDocument clientDidDocument = didDocService.getDidDocument(verificationMethod);

        // Get the keyagree public key.
        String encodedKeyAgreePublicKey = BaseCoreDidUtil.getPublicKey(clientDidDocument, "keyagree");

        // Verify the signature.
        verifySignature(encodedKeyAgreePublicKey, ecdhReqData.getProof().getProofValue(), signatureMessage, ecdhReqData.getProof().getType());
    }

    /**
     * Extracts the signature message from the ECDH request data.
     *
     * @param data The ECDH request data
     * @return byte[] The extracted signature message
     * @throws OpenDidException if the extraction fails
     */
    private byte[] extractSignatureMessage(EcdhReqData data) {
        try {
            // Remove proofValue from Proof fields in the object.
            EcdhReqData signatureMessageObject = removeProofValue(data);

            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch(CommonSdkException e) {
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Removes the proof value from the ECDH request data.
     *
     * @param data The original ECDH request data
     * @return EcdhReqData The ECDH request data with proof value removed
     */
    private EcdhReqData removeProofValue(EcdhReqData data) {
        EcdhReqData signatureMessageObject = EcdhReqData.builder()
                .client(data.getClient())
                .clientNonce(data.getClientNonce())
                .curve(data.getCurve())
                .publicKey(data.getPublicKey())
                .candidate(data.getCandidate())
                .candidate(data.getCandidate())
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
     * Verifies the signature of the ECDH request.
     *
     * @param encodedPublicKey The encoded public key
     * @param signature The signature to verify
     * @param signatureMassage The original message that was signed
     * @param proofType The type of proof used for the signature
     * @throws OpenDidException if the signature verification fails
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
     * Determines the cipher type to use for the ECDH process.
     *
     * @param candidate The candidate cipher types
     * @return SymmetricCipherType The determined cipher type
     * @throws OpenDidException if no matching cipher type is found
     */
    private SymmetricCipherType determineCipherType(Candidate candidate) {
        SymmetricCipherType serverCipherType = getServerCipherType();
        if (candidate == null) {
            return serverCipherType;
        }

        return candidate.getCiphers().stream()
                .filter(cipher -> cipher == serverCipherType)
                .findFirst()
                .orElseThrow(() -> new OpenDidException(ErrorCode.NO_MATCHING_CIPHER_TYPE));
    }

    /**
     * Retrieves the server's cipher type from the configuration.
     *
     * @return SymmetricCipherType The server's cipher type
     * @throws OpenDidException if the server configuration is invalid
     */
    private SymmetricCipherType getServerCipherType() {
        try {
            return SymmetricCipherType.fromDisplayName(tasProperty.getCipherType());
        } catch (IllegalArgumentException e) {
            throw new OpenDidException(ErrorCode.INVALID_SERVER_CONFIGURATION);
        }
    }

    /**
     * Determines the padding type to use for the ECDH process.
     *
     * @return SymmetricPaddingType The determined padding type
     * @throws OpenDidException if the server configuration is invalid
     */
    private SymmetricPaddingType determinePaddingType() {
        try {
            return SymmetricPaddingType.fromDisplayName(tasProperty.getPaddingType());
        } catch (IllegalArgumentException e){
            throw new OpenDidException(ErrorCode.INVALID_SERVER_CONFIGURATION);
        }
    }

    private void validateClientNonce(String encodedClientNonce) {
        byte[] clientNonce = BaseMultibaseUtil.decode(encodedClientNonce);
        if (clientNonce.length != 16) {
            throw new OpenDidException(ErrorCode.INVALID_CLIENT_NONCE);
        }
    }

    /**
     * Merges the client and server nonces.
     *
     * @param encodedClientNonce The encoded client nonce
     * @param serverNonce The server nonce
     * @return byte[] The merged nonce
     * @throws OpenDidException if the nonce generation fails
     */
    private byte[] mergeNonces(String encodedClientNonce, byte[] serverNonce) {
        try {
            byte[] clientNonce = BaseMultibaseUtil.decode(encodedClientNonce);
            byte[] combinedNonce = new byte[serverNonce.length + clientNonce.length];
            System.arraycopy(clientNonce, 0, combinedNonce, 0, clientNonce.length);
            System.arraycopy(serverNonce, 0, combinedNonce, clientNonce.length, serverNonce.length);

            return BaseDigestUtil.generateHash(combinedNonce);
        } catch (IllegalArgumentException e) {
            throw new OpenDidException(ErrorCode.NONCE_GENERATION_FAILED);
        }
    }

    /**
     * Generates the session key and response data for the ECDH process.
     *
     * @param requestECDHReqDto The ECDH request DTO
     * @param transaction The associated transaction
     * @param lastSubTransaction The last sub-transaction
     * @return RequestECDHResDto The ECDH response DTO
     */
    private RequestECDHResDto generateSessionKeyAndResponseData(RequestECDHReqDto requestECDHReqDto, Transaction transaction,  SubTransaction lastSubTransaction) {
            // Get client public key.
            byte[] clientPublicKey = BaseMultibaseUtil.decode(requestECDHReqDto.getReqEcdh().getPublicKey());

            // Generate server key pair.
            KeyPairInterface keyPairInterface = BaseCryptoUtil.generateKeyPair(requestECDHReqDto.getReqEcdh().getCurve());
            byte[] serverPublicKey = ((ECPublicKey) keyPairInterface.getPublicKey()).getEncoded();
            byte[] serverPrivateKey = ((ECPrivateKey) keyPairInterface.getPrivateKey()).getEncoded();
            byte[] compressPublicKey = BaseCryptoUtil.compressPublicKey(serverPublicKey, requestECDHReqDto.getReqEcdh().getCurve());

            String encodedServerPublicKey = BaseMultibaseUtil.encode(compressPublicKey);

            // Generate serverNonce.
            byte[] serverNonce = BaseCryptoUtil.generateNonce(16);
            String encodedServerNonce = BaseMultibaseUtil.encode(serverNonce);

            // Merge clientNonce and serverNonce.
            validateClientNonce(requestECDHReqDto.getReqEcdh().getClientNonce());

            byte[] mergedNonce = mergeNonces(requestECDHReqDto.getReqEcdh().getClientNonce(), serverNonce);
            String encodedMergedNonce = BaseMultibaseUtil.encode(mergedNonce);

            // Choose Cipher algorithm and padding type.
            SymmetricCipherType symmetricCipherType = determineCipherType(requestECDHReqDto.getReqEcdh().getCandidate());
            SymmetricPaddingType symmetricPaddingType = determinePaddingType();

            // Generate session key.
            byte[] sessionKey = generateSessionKey(clientPublicKey, serverPrivateKey, mergedNonce, symmetricCipherType, requestECDHReqDto.getReqEcdh().getCurve());
            String encodedSessionKey = BaseMultibaseUtil.encode(sessionKey);

            // Insert ECDH information
            insertEcdh(requestECDHReqDto.getReqEcdh().getClient(), encodedSessionKey, encodedMergedNonce, symmetricCipherType, symmetricPaddingType, transaction.getId());

            // Retrieve TAS did document.
            String tasDid = tasProperty.getDid();
            DidDocument tasDidDocument = didDocService.getDidDocument(tasDid);

            // Generate AccEcdh
            AccEcdh unsignedAccEcdh = AccEcdh.builder()
                    .server(tasDid)
                    .serverNonce(encodedServerNonce)
                    .publicKey(encodedServerPublicKey)
                    .cipher(symmetricCipherType)
                    .padding(symmetricPaddingType)
                    .proof(Proof.builder()
                            .type(ProofType.SECP_256R1_SIGNATURE_2018)
                            .created(DateTimeUtil.getCurrentUTCTimeString())
                            .verificationMethod(didDocService.getVerificationMethod(tasDidDocument, ProofPurpose.KEY_AGREEMENT))
                            .proofPurpose(ProofPurpose.KEY_AGREEMENT)
                            .proofValue(null)
                            .build())
                    .build();

            // Extract the signature message.
            byte[] signatureMessage = extractSignatureMessage(unsignedAccEcdh);

            // Sign AccEcdh.
            String proofValue = sign(signatureMessage, ProofPurpose.KEY_AGREEMENT);

            // Re-gegenrate AccEcdh with proofValue.
            AccEcdh signedAccEcdh = addProofValue(unsignedAccEcdh, proofValue);

            // Insert sub-transaction information.
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_ECDH)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            return RequestECDHResDto.builder()
                    .txId(requestECDHReqDto.getTxId())
                    .accEcdh(signedAccEcdh)
                    .build();
    }

    /**
     * Generates the session key using ECDH.
     *
     * @param compressedClientPublicKey The compressed client public key
     * @param serverPrivateKey The server private key
     * @param mergedNonce The merged nonce
     * @param symmetricCipherType The symmetric cipher type
     * @param eccCurveType The elliptic curve type
     * @return byte[] The generated session key
     */
    private byte[] generateSessionKey(byte[] compressedClientPublicKey, byte[] serverPrivateKey, byte[] mergedNonce, SymmetricCipherType symmetricCipherType, EccCurveType eccCurveType) {
        byte[] sharedSecret = BaseCryptoUtil.generateSharedSecret(compressedClientPublicKey, serverPrivateKey, eccCurveType);
        return BaseCryptoUtil.mergeSharedSecretAndNonce(sharedSecret, mergedNonce, symmetricCipherType);
    }

    /**
     * Inserts the ECDH information into the repository.
     *
     * @param client The client DID
     * @param encodedSessionKey The encoded session key
     * @param encodedMergedNonce The encoded merged nonce
     * @param symmetricCipherType The symmetric cipher type
     * @param symmetricPaddingType The symmetric padding type
     * @param transactionId The transaction ID
     */
    //@TODO: Nonce는 DB에 저장할 필요가 없음. 삭제할 것.
    private void insertEcdh(String client, String encodedSessionKey, String encodedMergedNonce, SymmetricCipherType symmetricCipherType, SymmetricPaddingType symmetricPaddingType, Long transactionId) {
        Ecdh ecdh = Ecdh.builder()
                .clientDid(client)
                .nonce(encodedMergedNonce)
                .sessionKey(encodedSessionKey)
                .cipher(symmetricCipherType.toString())
                .padding(symmetricPaddingType.toString())
                .transactionId(transactionId)
                .build();

        ecdhRepository.save(ecdh);
    }

    /**
     * Adds the proof value to the AccEcdh object.
     *
     * @param accEcdh The original AccEcdh object
     * @param proofValue The proof value to add
     * @return AccEcdh The AccEcdh object with the proof value added
     */
    private AccEcdh addProofValue(AccEcdh accEcdh, String proofValue) {
        return AccEcdh.builder()
                .server(accEcdh.getServer())
                .serverNonce(accEcdh.getServerNonce())
                .publicKey(accEcdh.getPublicKey())
                .cipher(accEcdh.getCipher())
                .padding(accEcdh.getPadding())
                .padding(accEcdh.getPadding())
                .proof(Proof.builder()
                        .type(accEcdh.getProof().getType())
                        .created(accEcdh.getProof().getCreated())
                        .verificationMethod(accEcdh.getProof().getVerificationMethod())
                        .proofPurpose(accEcdh.getProof().getProofPurpose())
                        .proofValue(proofValue)
                        .build())
                .build();
    }

    /**
     * Extracts the signature message from the AccEcdh object.
     *
     * @param accEcdh The AccEcdh object
     * @return byte[] The extracted signature message
     * @throws OpenDidException if the extraction fails
     */
    private byte[] extractSignatureMessage(AccEcdh accEcdh) {
        try {
            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(accEcdh);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch (CommonSdkException e) {
            log.error("Failed to Json Processing: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.JSON_PROCESSING_ERROR);
        } catch (Exception e) {
            log.error("Failed to extract signature message: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.RESPONSE_SIGNATURE_FAILED);
        }
    }

    /**
     * Signs the data using the specified proof purpose.
     *
     * @param data The data to sign
     * @param proofPurpose The proof purpose
     * @return String The generated signature
     */
    private String sign(byte[] data, ProofPurpose proofPurpose) {
        byte[] signatureBytes = fileWalletService.generateCompactSignature(proofPurpose.toKeyId(), data);
        return BaseMultibaseUtil.encode(signatureBytes);
    }
}
