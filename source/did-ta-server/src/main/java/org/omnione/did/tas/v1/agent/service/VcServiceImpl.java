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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant.Issuer;
import org.omnione.did.base.datamodel.data.zkp.ZkpIssueProfile;
import org.omnione.did.base.datamodel.enums.EmailTemplateType;
import org.omnione.did.base.datamodel.enums.PayloadType;
import org.omnione.did.base.datamodel.enums.QrType;
import org.omnione.did.base.datamodel.enums.ServerTokenPurpose;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.Role;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.domain.CertificateVc;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListVcSchema;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.exception.TaSdkErrorMapper;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.model.request.protocol.vc.ConfirmIssueVcRequest;
import org.omnione.did.tas.model.request.protocol.vc.ConfirmRevokeVcRequest;
import org.omnione.did.tas.model.request.protocol.vc.ProposeIssueVcRequest;
import org.omnione.did.tas.model.request.protocol.vc.ProposeRevokeVcRequest;
import org.omnione.did.tas.model.request.protocol.vc.RequestIssueProfileRequest;
import org.omnione.did.tas.model.request.protocol.vc.RequestIssueVcRequest;
import org.omnione.did.tas.model.request.protocol.vc.RequestRevokeVcRequest;
import org.omnione.did.tas.model.response.protocol.vc.ProposeIssueVcResult;
import org.omnione.did.tas.model.response.protocol.vc.ProposeRevokeVcResult;
import org.omnione.did.tas.model.response.protocol.vc.RequestIssueProfileResult;
import org.omnione.did.tas.model.response.protocol.vc.RequestIssueVcResult;
import org.omnione.did.tas.protocol.vc.VcIssuanceProtocol;
import org.omnione.did.tas.protocol.vc.VcRevocationProtocol;
import org.omnione.did.tas.provider.VcIssuanceDataProvider;
import org.omnione.did.tas.v1.agent.client.IssuerHttpApiClientImpl;
import org.omnione.did.tas.v1.agent.helper.TaSdkDataConverter;
import org.omnione.did.tas.v1.config.provider.VcRevocationDataProviderImpl;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.list.v1.admin.service.query.ListVcSchemaQueryService;
import org.omnione.did.noti.v1.agent.dto.email.EmailTemplate;
import org.omnione.did.noti.v1.agent.dto.email.RequestSendEmailReqDto;
import org.omnione.did.noti.v1.agent.dto.push.FcmNotificationDto;
import org.omnione.did.noti.v1.agent.dto.push.RequestSendPushReqDto;
import org.omnione.did.noti.v1.agent.service.NotiEmailService;
import org.omnione.did.noti.v1.agent.service.NotiPushService;
import org.omnione.did.tas.v1.agent.api.dto.InspectProposeRevokeApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.OfferIssueVcApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.OfferIssueVcApiResDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmRevokeVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcEmailReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcNotiResDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcPushReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcQrReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeRevokeVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueProfileReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueProfileResDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestRevokeVcResDto;
import org.omnione.did.tas.v1.agent.helper.CertificateVcSchemaProvider;
import org.omnione.did.tas.v1.agent.helper.EmailServiceHelper;
import org.omnione.did.tas.v1.agent.helper.PushServiceHelper;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.CertificateVcQueryService;
import org.omnione.did.tas.v1.common.service.query.EcdhQueryService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.v1.agent.service.validator.DidAuthValidator;
import org.omnione.did.tas.v1.agent.service.validator.TokenValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;


/**
 * Service for handling VC-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!sample")
public class VcServiceImpl implements VcService {
    private final EntityQueryService entityQueryService;
    private final TransactionService transactionService;
    private final EcdhQueryService ecdhQueryService;
    private final DidAuthValidator didAuthValidator;
    private final TokenValidator tokenValidator;
    private final NotiPushService notiPushService;
    private final NotiEmailService notiEmailService;
    private final CertificateVcQueryService certificateVcQueryService;
    private final PushServiceHelper pushServiceHelper;
    private final EmailServiceHelper emailServiceHelper;
    private final StorageService storageService;
    private final ListVcSchemaQueryService listVcSchemaQueryService;
    private final TasQueryService tasQueryService;
    private final VcIssuanceProtocol vcIssuanceProtocol;
    private final VcRevocationProtocol vcRevocationProtocol;
    private final VcIssuanceDataProvider vcIssuanceDataProvider;

    /**
     * Propose to issue a VC.
     *
     * @param proposeIssueVcReqDto ProposeIssueVcReqDto
     * @return ProposeIssueVcResDto
     * @throws OpenDidException if an error occurs while sending the offer request.
     */
    @Override
    public ProposeIssueVcResDto proposeIssueVc(ProposeIssueVcReqDto proposeIssueVcReqDto) {
        try {
            log.debug("=== Starting proposeIssueVc ===");
            // Validate Issuer Information.
            // The 'did' of the Issuer is an optional parameter.
            // However, in the sample code, it is treated as a required parameter
            // to ensure smooth operation.
            log.debug("\t--> Validating issuer information");
            Entity entity = validateIssuer(proposeIssueVcReqDto.getIssuer());

            // Send inspect-propose to Issuer
            log.debug("\t--> Sending inspect-propose to Issuer");
            ProposeIssueVcResult proposeIssueVcResult = vcIssuanceProtocol.proposeIssueVc(
                    ProposeIssueVcRequest.builder()
                            .id(proposeIssueVcReqDto.getId())
                            .vcPlanId(proposeIssueVcReqDto.getVcPlanId())
                            .issuer(proposeIssueVcReqDto.getIssuer())
                            .offerId(proposeIssueVcReqDto.getOfferId())
                            .build(),
                    new IssuerHttpApiClientImpl(entity.getServerUrl()),
                    vcIssuanceDataProvider);

            // Insert transaction information.
            String txId = proposeIssueVcResult.getTxId();
            log.debug("\t--> Inserting transaction information");
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.ISSUE_VC)
                    .status(TransactionStatus.PENDING)
                    .externalTxId(proposeIssueVcResult.getExternalTxId())
                    .externalDid(entity.getDid())
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.PROPOSE_ISSUE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished proposeIssueVc ***");

            return ProposeIssueVcResDto.builder()
                    .txId(txId)
                    .refId(proposeIssueVcResult.getRefId())
                    .build();
        } catch (TaSdkException e) {
            log.error("A TaSdkException occurred while sending proposeIssueVc request", e);
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_PROPOSE_ISSUE_VC);
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending proposeIssueVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending proposeIssueVc request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_PROPOSE_ISSUE_VC);
        }
    }

    /**
     * Validates the issuer information.
     *
     * @param issuerDid issuer DID
     * @return findEntityByDid
     * @throws OpenDidException if Failed to complete issuer registration
     * @throws OpenDidException if Failed to complete entity registration
     */
    private Entity validateIssuer(String issuerDid) {
        if (issuerDid == null) {
            log.error("\t--> Issuer DID is null");
            throw new OpenDidException(ErrorCode.ISSUER_INFO_NOT_FOUND);
        }

        Entity entity = entityQueryService.findEntityByDid(issuerDid);
        if (entity.getRole() != Role.ISSUER) {
            log.error("\t--> Entity role is not ISSUER for DID: {}", issuerDid);
            throw new OpenDidException(ErrorCode.ISSUER_INFO_NOT_FOUND);
        }
        if (entity.getStatus() != EntityStatus.COMPLETED) {
            log.error("\t--> Entity status is not COMPLETED for DID: {}", issuerDid);
            throw new OpenDidException(ErrorCode.ISSUER_REGISTRATION_INCOMPLETE);
        }

        return entity;
    }


    /**
     * Converts an external error response string to an ErrorResponse object.
     * This method attempts to parse the given JSON string into an ErrorResponse instance.
     *
     * @param resBody The JSON string representing the external error response
     * @return An ErrorResponse object parsed from the input string
     * @throws OpenDidException with ErrorCode.ISSUER_UNKNOWN_RESPONSE if parsing fails
     */
    private ErrorResponse convertExternalErrorResponse(String resBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resBody, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse external error response: {}", resBody, e);
            throw new OpenDidException(ErrorCode.ISSUER_UNKNOWN_RESPONSE);
        }
    }


    /**
     * Processes a request to generate an issue profile for a Verifiable Credential (VC).
     * This method performs several steps including transaction validation, server token validation,
     * ECDH information retrieval, and communication with the Issuer to generate the profile.
     *
     * @param requestIssueProfileReqDto The DTO containing the request information
     * @return RequestIssueProfileResDto containing the transaction ID, authentication nonce, and generated profile
     * @throws OpenDidException if any step in the process fails
     */
    @Override
    public RequestIssueProfileResDto requestIssueProfile(RequestIssueProfileReqDto requestIssueProfileReqDto) {
        try {
            log.debug("=== Starting requestIssueProfile ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving Transaction information");
            Transaction transaction = transactionService.findTransactionByTxId(requestIssueProfileReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Retrieve Entity information.
            log.debug("\t-->  Retrieving Issuer information");
            Entity entity = validateIssuer(transaction.getExternalDid());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_requestIssueProfile(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(requestIssueProfileReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.ISSUE_VC);

            // Retrieve Ecdh information.
            log.debug("\t--> Retrieving Ecdh information");
            ecdhQueryService.findEcdhByTransactionId(transaction.getId());

            // Send issuer-propose to Issuer and generate auth nonce.
            log.debug("\t--> Sending issuer-propose to Issuer");
            RequestIssueProfileResult requestIssueProfileResult = vcIssuanceProtocol.requestIssueProfile(
                    RequestIssueProfileRequest.builder()
                            .id(requestIssueProfileReqDto.getId())
                            .txId(requestIssueProfileReqDto.getTxId())
                            .serverToken(requestIssueProfileReqDto.getServerToken())
                            .build(),
                    new IssuerHttpApiClientImpl(entity.getServerUrl()),
                    vcIssuanceDataProvider);
            String authNonce = requestIssueProfileResult.getAuthNonce();

            // Update auth nonce.
            log.debug("\t--> Updating transactioin authNonce");
            transactionService.updateTransactionAuthNonce(transaction.getId(), authNonce);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_ISSUE_PROFILE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished requestIssueProfile ***");

            return RequestIssueProfileResDto.builder()
                    .txId(transaction.getTxId())
                    .authNonce(authNonce)
                    .profile((ZkpIssueProfile) requestIssueProfileResult.getProfile())
                    .build();
        } catch (TaSdkException e) {
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_REQUEST_ISSUE_PROFILE);
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.FAIL_TO_REQUEST_ISSUE_PROFILE);
        }
    }

    /**
     * Validates the transaction information for a request to generate an issue profile.
     * This method checks the transaction status, expiration time, and sub-transaction type.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The last sub-transaction for the transaction
     * @throws OpenDidException if the transaction is invalid
     * @throws OpenDidException if the transaction has expired
     * @throws OpenDidException if the sub-transaction type is not REQUEST_CREATE_TOKEN
     */
    private void validateTransaction_requestIssueProfile(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_CREATE_TOKEN) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Processes a request to issue a Verifiable Credential (VC).
     * This method handles the entire process of VC issuance request, including
     * transaction validation, issuer verification, DID authentication, and communication with the Issuer.
     *
     * @param requestIssueVcReqDto The DTO containing the VC issuance request information
     * @return RequestIssueVcResDto containing the transaction ID and E2E (End-to-End) information
     * @throws OpenDidException if any step in the process fails, with specific error codes
     */
    @Override
    public RequestIssueVcResDto requestIssueVc(RequestIssueVcReqDto requestIssueVcReqDto) {
        try {
            log.debug("=== Starting requestIssueVc ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving Transaction information");
            Transaction transaction = transactionService.findTransactionByTxId(requestIssueVcReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Retrieve Entity information.
            log.debug("\t-->  Retrieving Issuer information");
            Entity entity = validateIssuer(transaction.getExternalDid());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_requestIssueVc(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(requestIssueVcReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.ISSUE_VC);

            // Validate Did Auth.
            log.debug("\t--> Validating DidAuth");
            didAuthValidator.validateDidAuth(requestIssueVcReqDto.getDidAuth(), transaction);

            // Send issuer-propose to Issuer
            log.debug("\t--> Sending issue-vc to Issuer");
            RequestIssueVcResult requestIssueVcResult = vcIssuanceProtocol.requestIssueVc(
                    RequestIssueVcRequest.builder()
                            .id(requestIssueVcReqDto.getId())
                            .txId(requestIssueVcReqDto.getTxId())
                            .serverToken(requestIssueVcReqDto.getServerToken())
                            .didAuth(TaSdkDataConverter.toSdkDidAuth(requestIssueVcReqDto.getDidAuth()))
                            .accE2e(TaSdkDataConverter.toSdkAccE2e(requestIssueVcReqDto.getAccE2e()))
                            .encReqVc(requestIssueVcReqDto.getEncReqVc())
                            .build(),
                    new IssuerHttpApiClientImpl(entity.getServerUrl()),
                    vcIssuanceDataProvider);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_ISSUE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished requestIssueVc ***");

            return RequestIssueVcResDto.builder()
                    .txId(transaction.getTxId())
                    .e2e(TaSdkDataConverter.toServerE2e(requestIssueVcResult.getE2e()))
                    .build();
        } catch (TaSdkException e) {
            log.error("A TaSdkException occurred while sending requestIssueVc request", e);
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_REQUEST_ISSUE_VC);
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending requestIssueVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending requestIssueVc request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_REQUEST_ISSUE_VC);
        }
    }

    /**
     * Validates the transaction information for a request to issue a VC.
     * This method checks the transaction status, expiration time, and sub-transaction type.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The last sub-transaction for the transaction
     * @throws OpenDidException if the transaction is invalid
     * @throws OpenDidException if the transaction has expired
     * @throws OpenDidException if the sub-transaction type is not REQUEST_CREATE_TOKEN
     */
    private void validateTransaction_requestIssueVc(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("\t--> Invalid transaction status: {}", transaction.getStatus());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            log.error("\t--> Transaction is expired at: {}", transaction.getExpiredAt());
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_ISSUE_PROFILE) {
            log.error("\t--> Invalid sub-transaction type: {}", subTransaction.getType());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Processes a request to offer a VC issuance request via QR code.
     * This method handles the entire process of VC issuance request, including
     * transaction validation, issuer verification, DID authentication, and communication with the Issuer.
     *
     * @param confirmIssueVcReqDto The DTO containing the VC issuance request information
     * @return OfferIssueVcResDto containing the transaction ID and E2E (End-to-End) information
     * @throws OpenDidException if Failed to communicate with issuer: unknown error occurred
     */
    @Override
    public ConfirmIssueVcResDto confirmIssueVc(ConfirmIssueVcReqDto confirmIssueVcReqDto) {
        try {
            log.debug("=== Starting confirmIssueVc ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving Transaction information");
            Transaction transaction = transactionService.findTransactionByTxId(confirmIssueVcReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Retrieve Entity information.
            log.debug("\t-->  Retrieving Issuer information");
            Entity entity = validateIssuer(transaction.getExternalDid());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_confirmIssueVc(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(confirmIssueVcReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.ISSUE_VC);

            // Send complete-vc to Issuer
            vcIssuanceProtocol.confirmIssueVc(
                    ConfirmIssueVcRequest.builder()
                            .id(confirmIssueVcReqDto.getId())
                            .txId(confirmIssueVcReqDto.getTxId())
                            .serverToken(confirmIssueVcReqDto.getServerToken())
                            .vcId(confirmIssueVcReqDto.getVcId())
                            .build(),
                    new IssuerHttpApiClientImpl(entity.getServerUrl()),
                    vcIssuanceDataProvider);

            // Update transaction status.
            log.debug("\t--> Updating transaction status for transaction ID: {} to {}", transaction.getId(), TransactionStatus.COMPLETED);
            transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.COMPLETED);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.CONFIRM_ISSUE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished confirmIssueVc ***");

            return ConfirmIssueVcResDto.builder()
                    .txId(confirmIssueVcReqDto.getTxId())
                    .build();

        } catch (TaSdkException e) {
            log.error("A TaSdkException occurred while sending confirmIssueVc request", e);
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_CONFIRM_ISSUE_VC);
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending confirmIssueVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending inspect propose request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_CONFIRM_ISSUE_VC);
        }
    }

    /**
     * Validates the transaction information for a request to issue a VC.
     * This method checks the transaction status, expiration time, and sub-transaction type.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The last sub-transaction for the transaction
     * @throws OpenDidException if the transaction is invalid
     * @throws OpenDidException if the transaction has expired
     * @throws OpenDidException if the sub-transaction type is not REQUEST_ISSUE_VC
     */
    private void validateTransaction_confirmIssueVc(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("\t--> Invalid transaction status: {}", transaction.getStatus());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            log.error("\t--> Transaction is expired at: {}", transaction.getExpiredAt());
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_ISSUE_VC) {
            log.error("\t--> Invalid sub-transaction type: {}", subTransaction.getType());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Processes a request to propose the revocation of a Verifiable Credential (VC).
     * This method handles the entire process of VC revocation proposal, including
     * transaction validation, issuer verification, DID authentication, and communication with the Issuer.
     *
     * @param request The DTO containing the VC revocation proposal information
     * @return ProposeRevokeVcResDto containing the transaction ID and reference ID
     * @throws OpenDidException if any step in the process fails
     */
    @Override
    public OfferIssueVcResDto offerIssueVcQr(OfferIssueVcQrReqDto request) {
        try {
            log.debug("=== Starting offerIssueVcQr ===");
            OfferIssueVcResDto offerIssueVcResDto = this.offerIssueVc(request.getVcPlanId(), request.getIssuer());
            log.debug("*** Finished offerIssueVcQr ***");

            return offerIssueVcResDto;
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending offerIssueVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending offerIssueVc request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_OFFER_ISSUE_VC_QR);
        }
    }

    /**
     * Processes a request to offer a Verifiable Credential (VC) issuance via email.
     * This method handles the process of generating a VC offer and sending it to the specified email address.
     *
     * @param request The DTO containing the request information including issuer, VC plan ID, and recipient email
     * @return OfferIssueVcNotiResDto containing the offer ID and its validity period
     * @throws OpenDidException if any step in the process fails, with specific error codes
     */
    @Override
    public OfferIssueVcNotiResDto offerIssueVcEmail(OfferIssueVcEmailReqDto request) {
        try {
            log.debug("=== Starting offerIssueVcEmail ===");

            // Retrieve Entity information.
            log.debug("\t-->  Retrieving Issuer information");
            Entity entity = validateIssuer(request.getIssuer());

            // Send offer-issue-vc request to Issuer
            OfferIssueVcResDto offerIssueVcResDto = this.offerIssueVc(entity, request.getVcPlanId());

            // Generate email data
            log.debug("\t--> Generating email data");
            Map<String, String> emailData = emailServiceHelper.generateEmailDataForIssuerVc(offerIssueVcResDto.getIssueOfferPayload(), entity);

            // Send email
            log.debug("\t--> Sending email");
            notiEmailService.requestSendEmail(RequestSendEmailReqDto.builder()
                    .email(EmailTemplate.builder()
                            .title(emailServiceHelper.getEmailTitle(QrType.ISSUE_VC))
                            .recipientAddress(request.getEmail())
                            .contentData(emailData)
                            .templateType(EmailTemplateType.ISSUE_VC)
                            .build())
                    .build());

            log.debug("*** Finished offerIssueVcEmail ***");

            return OfferIssueVcNotiResDto.builder()
                    .offerId(offerIssueVcResDto.getOfferId())
                    .validUntil(offerIssueVcResDto.getValidUntil())
                    .build();
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending offerIssueVcEmail request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending offerIssueVcEmail request", e);
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Processes a request to offer a Verifiable Credential (VC) issuance via push notification.
     *
     * @param request The DTO containing the request information including issuer, VC plan ID, and holder
     * @return OfferIssueVcNotiResDto containing the offer ID and its validity period
     * @throws OpenDidException if any step in the process fails, with specific error codes
     */
    @Override
    public OfferIssueVcNotiResDto offerIssueVcPush(OfferIssueVcPushReqDto request) {
        try {
            log.debug("=== Starting offerIssueVcPush ===");

            // Retrieve Entity information.
            log.debug("\t-->  Retrieving Issuer information");
            Entity entity = validateIssuer(request.getIssuer());

            // Send offer-issue-vc request to Issuer
            OfferIssueVcResDto offerIssueVcResDto = this.offerIssueVc(entity, request.getVcPlanId());

            // Generate push data
            log.debug("\t--> Generating push data");
            Map<String, String> pushData = pushServiceHelper.generatePushDataForIssueVc(offerIssueVcResDto.getIssueOfferPayload(), entity);

            // Generate Fcm Notification data (title, body)
            log.debug("\t--> Generating Fcm Notification data");
            FcmNotificationDto fcmNotificationDto = pushServiceHelper.generateVcOrVpNotification(PayloadType.ISSUE_VC, entity);

            // Send push notification
            log.debug("\t--> Sending push notification");
            notiPushService.requestSendPush(RequestSendPushReqDto.builder()
                    .data(pushData)
                    .targetDids(Collections.singletonList(request.getHolder()))
                    .notification(fcmNotificationDto)
                    .build());

            log.debug("*** Finished offerIssueVcPush ***");

            return OfferIssueVcNotiResDto.builder()
                    .offerId(offerIssueVcResDto.getOfferId())
                    .validUntil(offerIssueVcResDto.getValidUntil())
                    .build();
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending offerIssueVcPush request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending offerIssueVcPush request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_OFFER_ISSUE_VC_PUSH);
        }
    }

    /**
     * Offers to issue a Verifiable Credential (VC) based on the given VC plan ID and issuer
     *  @param vcPlanId The ID of the VC plan
     *  @param issuer The issuer of the VC
     *  @return OfferIssueVcResDto containing the offer details
     *  @throws OpenDidException if there's an error in the offer process
     */

    private OfferIssueVcResDto offerIssueVc(String vcPlanId, String issuer) {
        // Retrieve Entity information.
        log.debug("\t-->  Retrieving Issuer information");
        Entity entity = validateIssuer(issuer);

        log.debug("\t--> Request Issue offer");
        OfferIssueVcApiResDto offerIssueVcApiResDto = sendOfferIssueVc(entity, vcPlanId);

        return OfferIssueVcResDto.builder()
                .issueOfferPayload(offerIssueVcApiResDto.getIssueOfferPayload())
                .offerId(offerIssueVcApiResDto.getOfferId())
                .validUntil(offerIssueVcApiResDto.getIssueOfferPayload().getValidUntil())
                .build();
    }

    /**
     *  Offers to issue a Verifiable Credential (VC) based on the given VC plan ID and entity
     *  @param vcPlanId The ID of the VC plan
     *  @param entity entity
     *  @return OfferIssueVcResDto containing the offer details
     *  @throws OpenDidException if there's an error in the offer process
     */
    private OfferIssueVcResDto offerIssueVc(Entity entity, String vcPlanId) {
        log.debug("\t--> Request request-offer to Issuer");
        OfferIssueVcApiResDto offerIssueVcApiResDto = sendOfferIssueVc(entity, vcPlanId);

        return OfferIssueVcResDto.builder()
                .issueOfferPayload(offerIssueVcApiResDto.getIssueOfferPayload())
                .offerId(offerIssueVcApiResDto.getOfferId())
                .validUntil(offerIssueVcApiResDto.getIssueOfferPayload().getValidUntil())
                .build();
    }


    /**
     * Sends an offer to issue a Verifiable Credential (VC) to the issuer's server
     *
     * @param entity The entity representing the issuer
     * @param vcPlanId The ID of the VC plan
     * @return OfferIssueVcApiResDto containing the API response
     * @throws OpenDidException if there's an error in communication with the issuer
     */
    private OfferIssueVcApiResDto sendOfferIssueVc(Entity entity, String vcPlanId) {
        String url = entity.getServerUrl() + Issuer.V1 + Issuer.REQUEST_OFFER;

        OfferIssueVcApiReqDto offerIssueVcApiReqDto = OfferIssueVcApiReqDto.builder()
                .vcPlanId(vcPlanId)
                .build();
        try {
            String request = JsonUtil.serializeToJson(offerIssueVcApiReqDto);
            return HttpClientUtil.postData(url, request, OfferIssueVcApiResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending offer-issue-vc request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        } catch (Exception e) {
            log.error("An unknown error occurred while sending offer-issue-vc request", e);
            throw new OpenDidException(ErrorCode.ISSUER_COMMUNICATION_ERROR);
        }
    }

    /**
     *  Retrieves and parses the Certificate Verifiable Credential (VC)
     *  @return String representation of the VerifiableCredential
     *  @throws OpenDidException if there's an error retrieving or parsing the certificate VC
     */
    @Override
    public String requestCertificateVc() {
        try {
            log.debug("=== Starting offerIssueVcEmail ===");
            // Retrieve Certificate VC information.
            log.debug("\t--> Retrieving Certificate VC information");
            CertificateVc certificateVc = certificateVcQueryService.findCertificateVc();

            // Parse VerifiableCredential from Certificate VC
            log.debug("\t--> Parsing VerifiableCredential from Certificate VC");
            VerifiableCredential verifiableCredential = new VerifiableCredential();
            verifiableCredential.fromJson(certificateVc.getVc());

            log.debug("*** Finished offerIssueVcPush ***");

            return verifiableCredential.toJson();
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending requestCertificateVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending requestCertificateVc request", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_CERTIFICATE_VC);
        }
    }

    /**
     * Retrieves and parses the Certificate Verifiable Credential (VC)
     * @return String representation of the VerifiableCredential
     * @throws OpenDidException if there's an error retrieving or parsing the certificate VC
     */
    @Override
    public Map<String, Object> requestVcSchema(String id, String name) {
        try {
            log.debug("=== Starting requestVcSchema ===");

            if (name != null && name.equals("certificate")) {
                Tas existedTas = tasQueryService.findTas();

                String vcSchemaJson = CertificateVcSchemaProvider.getSchema(existedTas.getServerUrl());

                return parseVcSchemaToMap(vcSchemaJson);
            }

            ListVcSchema existingListVcSchema = listVcSchemaQueryService.findBySchemaId(id);

            VcSchema vcSchema = new VcSchema();
            vcSchema.fromJson(existingListVcSchema.getSchema());

            log.debug("*** Finished requestVcSchema ***");

            return parseVcSchemaToMap(vcSchema.toJson()); // JSON을 Map으로 변환하여 반환
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending requestVcSchema request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending requestCertificateVc request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_GET_VC_SCHEMA);
        }
    }

    private Map<String, Object> parseVcSchemaToMap(String schemaJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(schemaJson, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse VC schema JSON (invalid format): {}", schemaJson, e);
            throw new OpenDidException(ErrorCode.INVALID_VC_SCHEMA);
        } catch (Exception e) {
            log.error("Unexpected error while parsing VC schema JSON", e);
            throw new OpenDidException(ErrorCode.INVALID_VC_SCHEMA);
        }
    }

    /**
     * Initiates the process to revoke a Verifiable Credential (VC)
     *
     * @param proposeRevokeVcReqDto The DTO containing revocation proposal details
     * @return ProposeRevokeVcResDto containing the response to the revocation proposal
     * @throws OpenDidException if there's an error in the revocation proposal process
     */
    @Override
    public ProposeRevokeVcResDto proposeRevokeVc(ProposeRevokeVcReqDto proposeRevokeVcReqDto) {
        try {
            log.debug("=== Starting proposeRevokeVc ===");

            // Retrieve VC Meta information.
            log.debug("\t--> Retrieving VC Meta information");
            VcMeta vcMeta = storageService.findVcMeta(proposeRevokeVcReqDto.getVcId());

            // Validate if VC can be revoked.
            log.debug("\t--> Validating if VC can be revoked");
            validateIfVcCanBeRevoked(vcMeta);

            // Retrieve Issuer information.
            log.debug("\t--> Retrieving Issuer information");
            String issuerDid = vcMeta.getIssuer().getDid();
            Entity entity = entityQueryService.findEntityByDid(issuerDid);

            // Send inspect-propose to Issuer
            log.debug("\t--> Sending inspect-propose to Issuer");
            IssuerHttpApiClientImpl issuerClient = new IssuerHttpApiClientImpl(entity.getServerUrl());
            ProposeRevokeVcResult proposeRevokeVcResult = vcRevocationProtocol.proposeRevokeVc(
                    ProposeRevokeVcRequest.builder()
                            .id(proposeRevokeVcReqDto.getId())
                            .vcId(proposeRevokeVcReqDto.getVcId())
                            .build(),
                    issuerClient,
                    VcRevocationDataProviderImpl.forPropose(vcMeta));
            InspectProposeRevokeApiResDto inspectProposeRevokeApiResDto = issuerClient.getInspectProposeRevokeResponse();

            // Insert transaction information.
            log.debug("\t--> Inserting transaction information");
            String txId = proposeRevokeVcResult.getTxId();
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.REVOKE_VC)
                    .status(TransactionStatus.PENDING)
                    .externalTxId(inspectProposeRevokeApiResDto.getTxId())
                    .externalDid(entity.getDid())
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.PROPOSE_REVOKE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished proposeRevokeVc ***");

            return ProposeRevokeVcResDto.builder()
                    .txId(txId)
                    .issuerNonce(proposeRevokeVcResult.getIssuerNonce())
                    .authType(inspectProposeRevokeApiResDto.getAuthType())
                    .build();

        } catch (TaSdkException e) {
            log.error("A TaSdkException occurred while sending proposeRevokeVc request", e);
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_PROPOSE_REVOKE_VC);
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending proposeRevokeVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending proposeRevokeVc request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_PROPOSE_REVOKE_VC);
        }
    }

    /**
     * Validates if a Verifiable Credential (VC) can be revoked.
     * This method checks if the VC is already revoked.
     *
     * @param vcMeta The VC Meta information
     * @throws OpenDidException if the VC is already revoked
     */
    private void validateIfVcCanBeRevoked(VcMeta vcMeta) {
        if (VcStatus.REVOKED == VcStatus.fromString(vcMeta.getStatus())) {
            log.error("\t--> VC is already revoked");
            throw new OpenDidException(ErrorCode.VC_ALREADY_REVOKED);
        }
    }

    /**
     * Processes a request to revoke a Verifiable Credential (VC).
     * This method handles the entire process of VC revocation, including
     * transaction validation, issuer verification, DID authentication, and communication with the Issuer.
     *
     * @param requestRevokeVcReqDto The DTO containing the VC revocation request information
     * @return RequestRevokeVcResDto containing the transaction ID
     * @throws OpenDidException if any step in the process fails
     */
    @Override
    public RequestRevokeVcResDto requestRevokeVc(RequestRevokeVcReqDto requestRevokeVcReqDto) {
        try {
            log.debug("=== Starting requestRevokeVc ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving Transaction information");
            Transaction transaction = transactionService.findTransactionByTxId(requestRevokeVcReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Retrieve Entity information.
            log.debug("\t-->  Retrieving Issuer information");
            Entity entity = validateIssuer(transaction.getExternalDid());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_requestRevokeVc(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(requestRevokeVcReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.REVOKE_VC);

            // Send revoke-vc to Issuer
            log.debug("\t--> Sending revoke-vc to Issuer");
            vcRevocationProtocol.requestRevokeVc(
                    RequestRevokeVcRequest.builder()
                            .id(requestRevokeVcReqDto.getId())
                            .txId(requestRevokeVcReqDto.getTxId())
                            .serverToken(requestRevokeVcReqDto.getServerToken())
                            .request(TaSdkDataConverter.toSdkReqRevokeVc(requestRevokeVcReqDto.getRequest()))
                            .build(),
                    new IssuerHttpApiClientImpl(entity.getServerUrl(), transaction.getExternalTxId()),
                    VcRevocationDataProviderImpl.forRequest(requestRevokeVcReqDto.getRequest()));

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_REVOKE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished requestRevokeVc ***");

            return RequestRevokeVcResDto.builder()
                    .txId(requestRevokeVcReqDto.getTxId())
                    .build();
        } catch (TaSdkException e) {
            log.error("A TaSdkException occurred while sending requestRevokeVc request", e);
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_REQUEST_REVOKE_VC);
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending requestRevokeVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending requestRevokeVc request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_REQUEST_REVOKE_VC);
        }
    }

    /**
     * Validates the transaction information for a request to revoke a VC.
     * This method checks the transaction status, expiration time, and sub-transaction type.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The last sub-transaction for the transaction
     * @throws OpenDidException if the transaction is invalid
     * @throws OpenDidException if the transaction has expired
     * @throws OpenDidException if the sub-transaction type is not REQUEST_CREATE_TOKEN
     */
    private void validateTransaction_requestRevokeVc(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_CREATE_TOKEN) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Processes a request to confirm the revocation of a Verifiable Credential (VC).
     * This method handles the entire process of VC revocation confirmation, including
     * transaction validation, issuer verification, DID authentication, and communication with the Issuer.
     *
     * @param confirmRevokeVcReqDto The DTO containing the VC revocation confirmation information
     * @return ConfirmRevokeVcResDto containing the transaction ID
     * @throws OpenDidException if any step in the process fails
     */
    @Override
    public ConfirmRevokeVcResDto confirmRevokeVc(ConfirmRevokeVcReqDto confirmRevokeVcReqDto) {
        try {
            log.debug("=== Starting confirmRevokeVc ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving Transaction information");
            Transaction transaction = transactionService.findTransactionByTxId(confirmRevokeVcReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Retrieve Entity information.
            log.debug("\t-->  Retrieving Issuer information");
            Entity entity = validateIssuer(transaction.getExternalDid());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_confirmRevokeVc(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(confirmRevokeVcReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.REVOKE_VC);

            // Send complete-revoke to Issuer
            log.debug("\t--> Sending complete-revoke to Issuer");
            vcRevocationProtocol.confirmRevokeVc(
                    ConfirmRevokeVcRequest.builder()
                            .id(confirmRevokeVcReqDto.getId())
                            .txId(confirmRevokeVcReqDto.getTxId())
                            .serverToken(confirmRevokeVcReqDto.getServerToken())
                            .build(),
                    new IssuerHttpApiClientImpl(entity.getServerUrl(), transaction.getExternalTxId()),
                    VcRevocationDataProviderImpl.forConfirm());

            // Update transaction status.
            log.debug("\t--> Updating transaction status for transaction ID: {} to {}", transaction.getId(), TransactionStatus.COMPLETED);
            transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.COMPLETED);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.CONFIRM_REVOKE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished confirmRevokeVc ***");

            return ConfirmRevokeVcResDto.builder()
                    .txId(confirmRevokeVcReqDto.getTxId())
                    .build();

        } catch (TaSdkException e) {
            log.error("A TaSdkException occurred while sending confirmRevokeVc request", e);
            throw TaSdkErrorMapper.toOpenDidException(e, ErrorCode.FAIL_TO_CONFIRM_REVOKE_VC);
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending confirmRevokeVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending confirmRevokeVc request", e);
            throw new OpenDidException(ErrorCode.FAIL_TO_CONFIRM_REVOKE_VC);
        }
    }

    /**
     * Validates the transaction information for a request to confirm the revocation of a VC.
     * This method checks the transaction status, expiration time, and sub-transaction type.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The last sub-transaction for the transaction
     * @throws OpenDidException if the transaction is invalid
     * @throws OpenDidException if the transaction has expired
     * @throws OpenDidException if the sub-transaction type is not REQUEST_REVOKE_VC
     */
    private void validateTransaction_confirmRevokeVc(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_REVOKE_VC) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }
}
