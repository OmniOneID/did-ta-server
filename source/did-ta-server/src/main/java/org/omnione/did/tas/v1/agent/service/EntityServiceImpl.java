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

import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.domain.Ecdh;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.exception.TaSdkErrorMapper;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.model.request.protocol.entity.ConfirmEnrollEntityRequest;
import org.omnione.did.tas.model.request.protocol.entity.ProposeEnrollEntityRequest;
import org.omnione.did.tas.model.request.protocol.entity.RequestEnrollEntityRequest;
import org.omnione.did.tas.model.response.protocol.entity.ProposeEnrollEntityResponse;
import org.omnione.did.tas.model.response.protocol.entity.RequestEnrollEntityResponse;
import org.omnione.did.tas.protocol.entity.EntityEnrollmentProtocol;
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.helper.TaSdkDataConverter;
import org.omnione.did.tas.v1.common.service.query.EcdhQueryService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.v1.agent.service.validator.DidAuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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
    private final EntityEnrollmentProtocol entityEnrollmentProtocol;

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
            // Generate transaction code and authNonce. (16-byte)
            ProposeEnrollEntityResponse proposeEnrollEntityResponse = entityEnrollmentProtocol.proposeEnrollEntity(ProposeEnrollEntityRequest.builder()
                    .id(proposeEnrollEntityReqDto.getId())
                    .build());
            String txId = proposeEnrollEntityResponse.getTxId();
            String authNonce = proposeEnrollEntityResponse.getAuthNonce();

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
        } catch (TaSdkException e) {
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_PROPOSE_ENROLL_ENTITY);
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

            // Retrieve TAS information.
            log.debug("\t--> Retrieving TAS information");
            tasQueryService.findTas();

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

            // Generate, sign, publish and encrypt Entity certificate VC, and register its meta.
            log.debug("\t--> Issuing Entity certificate VC");
            RequestEnrollEntityResponse requestEnrollEntityResponse = entityEnrollmentProtocol.requestEnrollEntity(RequestEnrollEntityRequest.builder()
                    .id(requestEnrollEntityReqDto.getId())
                    .txId(requestEnrollEntityReqDto.getTxId())
                    .didAuth(TaSdkDataConverter.toCommonDidAuth(requestEnrollEntityReqDto.getDidAuth()))
                    .dn(blankToNull(requestEnrollEntityReqDto.getDn()))
                    .build());
            String publishedCertificateUrl = requestEnrollEntityResponse.getVcUrl();

            // Update Entity published certificate URL.
            log.debug("\t--> Updating Entity published certificate URL");
            updateEntityPublishedCertificateUrl(entity, publishedCertificateUrl);

            // Update Entity DN if provided in request
            log.debug("\t--> Updating Entity DN if provided in request");
            updateEntityDnIfProvided(entity, requestEnrollEntityReqDto.getDn());

            // Update Entity certificate vc ID.
            log.debug("\t--> Updating Entity certificate VC ID");
            transactionService.updateTransactionCertificateId(transaction.getId(), requestEnrollEntityResponse.getVcId());

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
                    .iv(requestEnrollEntityResponse.getIv())
                    .encVc(requestEnrollEntityResponse.getEncVc())
                    .vcUrl(publishedCertificateUrl)
                    .build();
        } catch (TaSdkException e) {
            throw TaSdkErrorMapper.toOpenDidException(e);
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
     * A blank DN means that no DN is requested. The certificate subject is then derived from the entity name.
     *
     * @param dn The requested DN
     * @return the DN, or null if it is blank
     */
    private String blankToNull(String dn) {
        return dn == null || dn.trim().isEmpty() ? null : dn;
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

        try {
            entityEnrollmentProtocol.confirmEnrollEntity(ConfirmEnrollEntityRequest.builder()
                    .id(confirmEnrollEntityReqDto.getId())
                    .txId(confirmEnrollEntityReqDto.getTxId())
                    .vcId(confirmEnrollEntityReqDto.getVcId())
                    .build());
        } catch (TaSdkException e) {
            throw TaSdkErrorMapper.toOpenDidException(e);
        }

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

    /**
     * Updates the published certificate URL of an entity if the provided URL is not null or empty.
     *
     * @param entity the Entity entity to update
     * @param publishedCertificateUrl the published certificate URL from request
     */
    private void updateEntityPublishedCertificateUrl(Entity entity, String publishedCertificateUrl) {
        if (publishedCertificateUrl != null && !publishedCertificateUrl.trim().isEmpty()) {
            log.debug("\t--> Updating Entity published certificate URL: {}", publishedCertificateUrl);
            entity.setPublishedCertificateUrl(publishedCertificateUrl);
            entityRepository.save(entity);
        }
    }

    /**
     * Update Entity DN if the provided DN value is not null or empty.
     *
     * @param entity the Entity entity to update
     * @param dn the DN value from request
     */
    private void updateEntityDnIfProvided(Entity entity, String dn) {
        if (dn != null && !dn.trim().isEmpty()) {
            log.debug("\t--> Updating Entity DN: {}", dn);
            entity.setDn(dn);
            entityRepository.save(entity);
        }
    }
}
