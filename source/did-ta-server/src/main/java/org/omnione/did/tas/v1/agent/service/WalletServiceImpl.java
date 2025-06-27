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
import org.omnione.did.base.datamodel.data.AttestedDidDoc;
import org.omnione.did.base.datamodel.data.Proof;
import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.Role;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.constant.WalletStatus;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.domain.Wallet;
import org.omnione.did.base.db.repository.WalletRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.tas.v1.agent.dto.wallet.RegisterWalletReqDto;
import org.omnione.did.tas.v1.agent.dto.wallet.RegisterWalletResDto;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.WalletQueryService;
import org.omnione.did.tas.v1.agent.service.validator.CertificateVcValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.DidValidator;
import org.omnione.did.common.util.IdGenerator;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Service for handling wallet registration requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!sample")
public class WalletServiceImpl implements WalletService {

    private final EntityQueryService entityQueryService;
    private final WalletQueryService walletQueryService;
    private final TransactionService transactionService;
    private final WalletRepository walletRepository;
    private final DidDocService didDocService;
    private final StorageService storageService;
    private final SignatureService signatureService;
    private final CertificateVcValidator certificateVcValidator;

    /**
     * Handles a request to register a wallet.
     *
     * @param registerWalletReqDto Request DTO containing the wallet registration information.
     * @return Response DTO containing the transaction ID.
     * @throws OpenDidException if an error occurs during the registration process.
     */
    @Override
    public RegisterWalletResDto RequestRegisterWallet(RegisterWalletReqDto registerWalletReqDto) {
        try {
            log.debug("=== Starting RequestRegisterWallet ===");

            // Parse Wallet did document.
            log.debug("\t--> Parsing Owner DID Document");
            DidDocument ownerDidDoc = parseOwnerDidDoc(registerWalletReqDto.getAttestedDidDoc().getOwnerDidDoc());

            // Check if the Entity is registered.
            log.debug("\t--> Validating Signer");
            Entity entity = validateSigner(registerWalletReqDto.getAttestedDidDoc());

            // Validate Attested did document.
            log.debug("\t--> Validating Attested DID Document");
            validateAttestedDidDoc(registerWalletReqDto.getAttestedDidDoc());

            // Check for duplicate Wallet ID.
            log.debug("\t--> Checking Wallet ID Duplicate");
            checkWalletIdDuplicate(registerWalletReqDto.getAttestedDidDoc().getWalletId());

            // Verify DID document key signatures.
            log.debug("\t--> Verifying DID Document Key Proofs");
            signatureService.verifyDidDocKeyProofs(ownerDidDoc);

            // Sign DID document.
            log.debug("\t--> Signing Invoked DID Document");
            InvokedDidDoc invokedDidDoc = signatureService.signInvokedDidDoc(ownerDidDoc);

            // Upload Wallet DID document.
            log.debug("\t--> Uploading Wallet DID Document");
            storageService.registerDidDoc(invokedDidDoc, RoleType.WALLET);

            // Insert wallet information
            log.debug("\t--> Inserting Wallet Information");
            insertWallet(Wallet.builder()
                    .walletId(registerWalletReqDto.getAttestedDidDoc().getWalletId())
                    .did(ownerDidDoc.getId())
                    .status(WalletStatus.CREATED)
                    .registeredAt(Instant.now())
                    .entityId(entity.getId())
                    .build());

            // Generate transaction code.
            String txId = IdGenerator.generateTxId();

            // Insert transaction information.
            log.debug("\t--> Inserting Transaction Information");
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.WALLET_REGISTRATION)
                    .status(TransactionStatus.COMPLETED)
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting Sub-Transaction Information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.REQUEST_REGISTER_WALLET)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished RequestRegisterWallet ***");

            return RegisterWalletResDto.builder()
                    .txId(txId)
                    .build();
        } catch (OpenDidException e) {
            log.error("Error occurred while registering wallet: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while registering wallet: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Parses the owner DID document from the encoded DID document.
     *
     * @param encodedDidDoc Encoded DID document.
     * @return Parsed DID document.
     */
    private DidDocument parseOwnerDidDoc(String encodedDidDoc) {
        byte[] decodedDidDoc = BaseMultibaseUtil.decode(encodedDidDoc);
        DidManager didManager = BaseCoreDidUtil.parseDidDoc(new String(decodedDidDoc, StandardCharsets.UTF_8));

        return didManager.getDocument();
    }

    /**
     * Validates the signer of the attested DID document.
     *
     * @param attestedDidDoc Attested DID document.
     * @return Entity object of the signer.
     */
    private Entity validateSigner(AttestedDidDoc attestedDidDoc) {
        String providerDid = attestedDidDoc.getProvider().getDid();
        Entity entity = entityQueryService.findEntityByDid(providerDid);

        if (entity.getRole() != Role.WALLET_PROVIDER) {
            throw new OpenDidException(ErrorCode.WALLET_PROVIDER_NOT_REGISTERED);
        }
        if (entity.getStatus() != EntityStatus.COMPLETED) {
            throw new OpenDidException(ErrorCode.ENTITY_REGISTRATION_INCOMPLETE);
        }

        // Check and validate the entity certificate VC if the URL is provided.
        validateEntityCertificateVc(attestedDidDoc.getProvider().getCertVcRef(), providerDid);

        return entity;
    }

    /**
     * Validates the entity certificate VC.
     *
     * @param certVcRef Certificate VC reference.
     * @param providerDid DID of the entity.
     */
    private void validateEntityCertificateVc(String certVcRef, String providerDid) {
        if (certVcRef != null) {
            certificateVcValidator.validateCertificateVc(certVcRef, providerDid);
        }
    }

    /**
     * Validates the attested DID document.
     *
     * @param attestedDidDoc Attested DID document.
     * @throws OpenDidException if the attested DID document is invalid.
     */
    private void validateAttestedDidDoc(AttestedDidDoc attestedDidDoc) {
        // Extract and validate did and didKeyUrl
        String clientDid = attestedDidDoc.getProvider().getDid();
        if (!DidValidator.isValidDid(clientDid)){
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        String verificationMethod = attestedDidDoc.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the equivalence of did.
        String didOfKeyUrl = DidUtil.extractDid(verificationMethod);
        if (!clientDid.equals(didOfKeyUrl)) {
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (attestedDidDoc.getProof().getProofPurpose() != ProofPurpose.ASSERTION_METHOD) {
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Extract the signature message.
        byte[] signatureMessage = extractSignatureMessage(attestedDidDoc);

        // Find Wallet Provider DID Document.
        DidDocument walletProviderDidDocument = didDocService.getDidDocument(verificationMethod);

        // Get the Assertion public key.
        String encodedAssertPublicKey = BaseCoreDidUtil.getPublicKey(walletProviderDidDocument, "assert");

        // Verify the signature.
        signatureService.verifySignature(encodedAssertPublicKey, attestedDidDoc.getProof().getProofValue(), signatureMessage, attestedDidDoc.getProof().getType());
    }

    /**
     * Extracts the signature message from the attested DID document.
     *
     * @param attestedDidDoc Attested DID document.
     * @return Extracted signature message.
     */
    private byte[] extractSignatureMessage(AttestedDidDoc attestedDidDoc) {
        try {
            // Remove proofValue from Proof fields in the object.
            AttestedDidDoc signatureMessageObject = removeProofValue(attestedDidDoc);

            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch(CommonSdkException e) {
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Removes the proofValue from the Proof fields in the attested DID document.
     *
     * @param data Attested DID document.
     * @return Attested DID document with the proofValue removed.
     */
    private AttestedDidDoc removeProofValue(AttestedDidDoc data) {
        AttestedDidDoc signatureMessageObject = AttestedDidDoc.builder()
                .walletId(data.getWalletId())
                .ownerDidDoc(data.getOwnerDidDoc())
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
     * Checks if the wallet ID already exists.
     *
     * @param walletId Wallet ID.
     * @throws OpenDidException if the wallet ID already exists.
     */
    private void checkWalletIdDuplicate(String walletId) {
        if (walletQueryService.countByWalletId(walletId) > 0) {
            throw new OpenDidException(ErrorCode.WALLET_ID_ALREADY_EXISTS);
        }
    }

    /**
     * Inserts the wallet information into the database.
     *
     * @param wallet Wallet object to insert.
     */
    private void insertWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }
}
