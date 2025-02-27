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

import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.domain.Ecdh;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.TasProperty;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.BaseTasUtil;
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityResDto;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.EcdhQueryService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.v1.agent.service.validator.DidAuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.IdGenerator;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Implementation of the EntityService interface for managing entity registration and enrollment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!sample")
public class EntityServiceImpl implements EntityService {
    private final TransactionService transactionService;
    private final EntityQueryService entityQueryService;
    private final EcdhQueryService ecdhQueryService;
    private final EntityRepository entityRepository;
    private final DidAuthValidator didAuthValidator;
    private final TasQueryService tasQueryService;
    private final StorageService storageService;
    private final IssueVcService issueVcService;
    private final TasProperty tasProperty;
    private final FileWalletService fileWalletService;

    /**
     * Proposes the enrollment of an entity.
     *
     * @param proposeEnrollEntityReqDto The DTO containing the proposal request details
     * @return ProposeEnrollEntityResDto The response DTO containing transaction ID and auth nonce
     * @throws OpenDidException if there's an error during the proposal process
     */
    @Override
    public ProposeEnrollEntityResDto proposeEnrollEntity(ProposeEnrollEntityReqDto proposeEnrollEntityReqDto) {
        try {
            // Generate transaction code.
            String txId = IdGenerator.generateTxId();

            // Generate authNonce. (16-byte)
            String authNonce = BaseTasUtil.generateNonceWithMultibase();

            // Insert transaction information.
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.ENTITY_REGISTRATION)
                    .status(TransactionStatus.PENDING)
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .authNonce(authNonce)
                    .build()
            );

            // Insert sub-transaction information.
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.PROPOSE_ENROLL_ENTITY)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            return ProposeEnrollEntityResDto.builder()
                    .txId(txId)
                    .authNonce(authNonce)
                    .build();
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.FAIL_TO_PROPOSE_ENROLL_ENTITY);
        }
    }

    /**
     * Handles the request to enroll an entity.
     *
     * @param requestEnrollEntityReqDto The DTO containing the enrollment request details
     * @return RequestEnrollEntityResDto The response DTO containing transaction ID, IV, and encrypted VC
     * @throws OpenDidException if there's an error during the enrollment process
     */
    @Override
    public RequestEnrollEntityResDto requestEnrollEntity(RequestEnrollEntityReqDto requestEnrollEntityReqDto) {
        try {
            log.debug("=== Starting requestEnrollEntity ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving transaction information for txId: {}", requestEnrollEntityReqDto.getTxId());
            Transaction transaction = transactionService.findTransactionByTxId(requestEnrollEntityReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_requestEnrollEntity(transaction, lastSubTransaction);

            // Validate Did Auth.
            log.debug("\t--> Validating Did Auth");
            didAuthValidator.validateDidAuth(requestEnrollEntityReqDto.getDidAuth(), transaction);

            // Verify Entity status.
            log.debug("\t--> Validating Entity status. (status: CERTIFICATE_VC_REQUIRED)");
            verifyEntityInfo(requestEnrollEntityReqDto.getDidAuth().getDid(), EntityStatus.CERTIFICATE_VC_REQUIRED);

            // Retrieve Entity information.
            log.debug("\t--> Retrieving entity information");
            Entity entity = entityQueryService.findEntityByDid(requestEnrollEntityReqDto.getDidAuth().getDid());

            // Generate Entity certificate VC.
            log.debug("\t--> Generating Entity certificate VC");
            VerifiableCredential entityCertificateVc = generateEntityCertificateVc(entity);

            log.debug("\t--> Signing TAS certificate VC.");
            signTasCertificateVc(entityCertificateVc);

            // Register Entity certificate VC meta.
            log.debug("\t--> Registering Entity certificate VC meta");
            registerEntityCertificateVcMeta(entityCertificateVc, entity);

            // Create IV.
            log.debug("\t--> Creating IV");
            byte[] iv = BaseCryptoUtil.generateNonce(16);
            String encodedIv = BaseMultibaseUtil.encode(iv);

            // Encrypt Entity certificate VC
            log.debug("\t--> Encrypting and Encoding Entity certificate VC");
            byte[] encryptedEntityCertificateVc = encryptEntityCertificateVc(transaction.getId(), entityCertificateVc, iv);
            String encodedEncryptedEntityCertificateVc = BaseMultibaseUtil.encode(encryptedEntityCertificateVc);

            // Update Entity certificate vc ID.
            log.debug("\t--> Updating Entity certificate VC ID");
            transactionService.updateTransactionCertificateId(transaction.getId(), entityCertificateVc.getId());

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_ENROLL_ENTITY)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished requestEnrollEntity ***");

            return RequestEnrollEntityResDto.builder()
                    .txId(requestEnrollEntityReqDto.getTxId())
                    .iv(encodedIv)
                    .encVc(encodedEncryptedEntityCertificateVc)
                    .build();
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }
    /**
     * Validates the transaction for the requestEnrollEntity process.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction is invalid or expired
     */
    private void validateTransaction_requestEnrollEntity(Transaction transaction, SubTransaction subTransaction) {
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
     * Verifies the entity information based on DID and status.
     *
     * @param did The DID of the entity
     * @param entityStatus The expected status of the entity
     * @throws OpenDidException if the entity information is not found
     */
    private void verifyEntityInfo(String did, EntityStatus entityStatus) {
        long count = entityQueryService.countByDidAndStatus(did, entityStatus);
        if (count == 0) {
            log.error("\t--> Entity information not found.");
            throw new OpenDidException(ErrorCode.ENROLL_REQUEST_ENTITY_DID_MISMATCH);
        }
    }

    /**
     * Generates an entity certificate Verifiable Credential.
     *
     * @param entity The entity for which to generate the certificate
     * @return VerifiableCredential The generated entity certificate VC
     */
    private VerifiableCredential generateEntityCertificateVc(Entity entity) {
        Tas tas = tasQueryService.findTas();
        IssueVcParam issueVcParam = new IssueVcParam();

        issueVcService.setCertificateVcSchema(issueVcParam);
        issueVcService.setIssuer(issueVcParam, tas, tasProperty.getCertificateVc());
        issueVcService.setEntityClaimInfo(issueVcParam, entity);
        issueVcService.setCertificateVcTypes(issueVcParam);
        issueVcService.setCertificateEvidence(issueVcParam, tas);
        issueVcService.setValidateUntil(issueVcParam,1);

        return issueVcService.generateEntityCertificateVc(issueVcParam, entity);
    }

    /**
     * Signs the TAS certificate VC.
     *
     * @param entityCertificateVc The VC to sign
     */
    private void signTasCertificateVc(VerifiableCredential entityCertificateVc) {
        DidDocument tasDidDoc = storageService.findDidDoc(tasProperty.getDid());
        List<SignatureVcParams> SignatureParamslist = extractVcSignatureMessage(tasDidDoc, entityCertificateVc);

        for(SignatureVcParams signatureParam : SignatureParamslist) {
            String originData = signatureParam.getOriginData();
            log.debug("originData: {}", originData);
            byte[] signatureBytes = fileWalletService.generateCompactSignature(signatureParam.getKeyId(), originData);
            String encodedSignature = BaseMultibaseUtil.encode(signatureBytes);
            signatureParam.setSignatureValue(encodedSignature);
        }

        BaseCoreVcUtil.setVcProof(entityCertificateVc, SignatureParamslist);
    }

    /**
     * Extracts the signature message from the VC.
     *
     * @param tasDidDoc The DID document of the TAS
     * @param verifiableCredential The VC from which to extract the signature message
     * @return List<SignatureVcParams> The extracted signature message
     */
    private List<SignatureVcParams> extractVcSignatureMessage(DidDocument tasDidDoc, VerifiableCredential verifiableCredential) {
        return BaseCoreVcUtil.extractVcSignatureMessage(tasDidDoc, verifiableCredential);
    }

    /**
     * Registers the entity certificate VC metadata.
     *
     * @param verifiableCredential The VC to register
     * @param entity The entity associated with the VC
     */
    private void registerEntityCertificateVcMeta(VerifiableCredential verifiableCredential, Entity entity) {
        VcMeta vcMeta = BaseCoreVcUtil.generateVcMeta(verifiableCredential, entity.getCertificateUrl());
        storageService.registerVcMeta(vcMeta);
    }
    /**
     * Encrypts the entity certificate VC.
     *
     * @param transactionId The ID of the associated transaction
     * @param entityCertificateVc The VC to encrypt
     * @param iv The initialization vector for encryption
     * @return byte[] The encrypted VC
     * @throws OpenDidException if encryption fails
     */
    private byte[] encryptEntityCertificateVc(Long transactionId, VerifiableCredential entityCertificateVc, byte[] iv) {
        // Retrieve Ecdh information.
        Ecdh ecdh = ecdhQueryService.findEcdhByTransactionId(transactionId);

        String entityCertificateVcJson = entityCertificateVc.toJson();

        // Retrieve Ecdh information.
        SymmetricPaddingType symmetricPaddingType = SymmetricPaddingType.fromDisplayName(ecdh.getPadding());
        SymmetricCipherType symmetricCipherType = SymmetricCipherType.fromDisplayName(ecdh.getCipher());
        byte[] sessionKey = BaseMultibaseUtil.decode(ecdh.getSessionKey());

        // Encrypt the ServerTokenData.
        return BaseCryptoUtil.encrypt(entityCertificateVcJson.getBytes(StandardCharsets.UTF_8), sessionKey, iv, symmetricCipherType, symmetricPaddingType);
    }

    /**
     * Confirms the enrollment of an entity.
     *
     * @param confirmEnrollEntityReqDto The DTO containing the confirmation request details
     * @return ConfirmEnrollEntityResDto The response DTO containing the transaction ID
     * @throws OpenDidException if there's an error during the confirmation process
     */
    @Override
    public ConfirmEnrollEntityResDto confirmEnrollEntity(ConfirmEnrollEntityReqDto confirmEnrollEntityReqDto) {
        // Retrieve Transaction information.
        Transaction transaction = transactionService.findTransactionByTxId(confirmEnrollEntityReqDto.getTxId());
        SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

        // Validate transaction's validity.
        validateTransaction_confirmEnrollEntity(transaction, lastSubTransaction);

        // Validate Certificate vc ID.
        validateVcId(confirmEnrollEntityReqDto.getVcId(), transaction.getCertificateId());

        // Retrieve Entity information.
        Ecdh ecdh = ecdhQueryService.findEcdhByTransactionId(transaction.getId());
        Entity entity = entityQueryService.findEntityByDid(ecdh.getClientDid());

        // Update Entity status
        updateEntityStatus(entity.getId(), EntityStatus.COMPLETED);

        // Update transaction status.
        transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.COMPLETED);

        // Insert sub-transaction information.
        transactionService.insertSubTransaction(SubTransaction.builder()
                .transactionId(transaction.getId())
                .step(lastSubTransaction.getStep() + 1)
                .type(SubTransactionType.CONFIRM_ENROLL_ENTITY)
                .status(SubTransactionStatus.COMPLETED)
                .build()
        );

        return ConfirmEnrollEntityResDto.builder()
                .txId(confirmEnrollEntityReqDto.getTxId())
                .build();
    }
    /**
     * Validates the transaction for the confirmEnrollEntity process.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction is invalid or expired
     */
    private void validateTransaction_confirmEnrollEntity(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_ENROLL_ENTITY) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Validates the VC ID.
     *
     * @param reqVcId The requested VC ID
     * @param originVcId The original VC ID
     * @throws OpenDidException if the VC IDs don't match
     */
    private void validateVcId(String reqVcId, String originVcId) {
        if (!reqVcId.equals(originVcId)) {
            throw new OpenDidException(ErrorCode.VC_ID_NOT_MATCH);
        }
    }
    /**
     * Updates the status of an entity.
     *
     * @param id The ID of the entity to update
     * @param entityStatus The new status for the entity
     */
    private void updateEntityStatus(Long id, EntityStatus entityStatus) {
        Entity entity = entityQueryService.findEntityById(id);
        entity.setStatus(entityStatus);

        entityRepository.save(entity);
    }
}
