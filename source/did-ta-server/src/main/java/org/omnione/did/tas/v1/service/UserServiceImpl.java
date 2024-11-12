/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.tas.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.omnione.did.base.datamodel.data.DidAuth;
import org.omnione.did.base.datamodel.data.Proof;
import org.omnione.did.base.datamodel.data.RestoreDidOfferPayload;
import org.omnione.did.base.datamodel.data.SignedDidDoc;
import org.omnione.did.base.datamodel.enums.DidDocStatus;
import org.omnione.did.base.datamodel.enums.EmailTemplateType;
import org.omnione.did.base.datamodel.enums.OfferType;
import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.PayloadType;
import org.omnione.did.base.datamodel.enums.QrType;
import org.omnione.did.base.datamodel.enums.ServerTokenPurpose;
import org.omnione.did.base.db.constant.AppStatus;
import org.omnione.did.base.db.constant.DidOfferType;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.constant.UserStatus;
import org.omnione.did.base.db.constant.WalletStatus;
import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.domain.DidOffer;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Token;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.Wallet;
import org.omnione.did.base.db.repository.AppRepository;
import org.omnione.did.base.db.repository.UserRepository;
import org.omnione.did.base.db.repository.WalletRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.EmailProperty;
import org.omnione.did.base.util.BaseBlockChainUtil;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.BaseTasDidUtil;
import org.omnione.did.base.util.BaseTasUtil;
import org.omnione.did.noti.v1.dto.email.EmailTemplate;
import org.omnione.did.noti.v1.dto.email.RequestSendEmailReqDto;
import org.omnione.did.noti.v1.dto.push.FcmNotificationDto;
import org.omnione.did.noti.v1.dto.push.RequestSendPushReqDto;
import org.omnione.did.noti.v1.service.NotiEmailService;
import org.omnione.did.noti.v1.service.NotiPushService;
import org.omnione.did.tas.v1.api.KycFeign;
import org.omnione.did.tas.v1.api.dto.RetrievePiiApiReqDto;
import org.omnione.did.tas.v1.api.dto.RetrievePiiApiResDto;
import org.omnione.did.tas.v1.dto.common.EmptyResDto;
import org.omnione.did.tas.v1.dto.user.ConfirmRegisterUserReqDto;
import org.omnione.did.tas.v1.dto.user.ConfirmRegisterUserResDto;
import org.omnione.did.tas.v1.dto.user.ConfirmRestoreDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ConfirmRestoreDidDocResDto;
import org.omnione.did.tas.v1.dto.user.ConfirmUpdateDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ConfirmUpdateDidDocResDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidEmailReqDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidEmailResDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidPushReqDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidPushResDto;
import org.omnione.did.tas.v1.dto.user.ProposeRegisterUserReqDto;
import org.omnione.did.tas.v1.dto.user.ProposeRegisterUserResDto;
import org.omnione.did.tas.v1.dto.user.ProposeRestoreDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ProposeRestoreDidDocResDto;
import org.omnione.did.tas.v1.dto.user.ProposeUpdateDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ProposeUpdateDidDocResDto;
import org.omnione.did.tas.v1.dto.user.RequestRegisterUserReqDto;
import org.omnione.did.tas.v1.dto.user.RequestRegisterUserResDto;
import org.omnione.did.tas.v1.dto.user.RequestRestoreDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.RequestRestoreDidDocResDto;
import org.omnione.did.tas.v1.dto.user.RequestUpdateDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.RequestUpdateDidDocResDto;
import org.omnione.did.tas.v1.dto.user.RetrieveKycReqDto;
import org.omnione.did.tas.v1.dto.user.RetrieveKycResDto;
import org.omnione.did.tas.v1.dto.user.UpdateDidDocDeactivatedReqDto;
import org.omnione.did.tas.v1.dto.user.UpdateDidDocRevokedReqDto;
import org.omnione.did.tas.v1.dto.user.UpdateUserStatusReqDto;
import org.omnione.did.tas.v1.dto.user.UpdateUserStatusResDto;
import org.omnione.did.tas.v1.helper.EmailServiceHelper;
import org.omnione.did.tas.v1.helper.PushServiceHelper;
import org.omnione.did.tas.v1.service.query.DidOfferQueryService;
import org.omnione.did.tas.v1.service.query.UserQueryService;
import org.omnione.did.tas.v1.service.query.WalletQueryService;
import org.omnione.did.tas.v1.service.validator.DidAuthValidator;
import org.omnione.did.tas.v1.service.validator.TokenValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User service implementation for handling user-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Profile("!sample")
public class UserServiceImpl implements UserService {
    private final TransactionService transactionService;
    private final WalletQueryService walletQueryService;
    private final StorageService storageService;
    private final UserQueryService userQueryService;
    private final TokenValidator tokenValidator;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AppRepository appRepository;
    private final DidDocService didDocService;
    private final KycFeign kycFeign;
    private final SignatureService signatureService;
    private final DidAuthValidator didAuthValidator;
    private final AppQueryService appQueryService;
    private final PushServiceHelper pushServiceHelper;
    private final NotiPushService notiPushService;
    private final EmailServiceHelper emailServiceHelper;
    private final NotiEmailService notiEmailService;
    private final EmailProperty emailProperty;
    private final DidOfferQueryService didOfferQueryService;

    /**
     * Proposes the registration of a new user, generating a transaction ID and initializing sub-transaction.
     *
     * @param proposeRegisterUserReqDto The DTO containing user registration proposal details
     * @return ProposeRegisterUserResDto containing the transaction ID
     * @throws OpenDidException if there's an error during the proposal process
     */
    @Override
    public ProposeRegisterUserResDto proposeRegisterUser(ProposeRegisterUserReqDto proposeRegisterUserReqDto) {
        try {
            log.debug("=== Starting proposeRegisterUser ===");

            // Generate transaction code.
            log.debug("\t--> Generating transaction ID");
            String txId = IdGenerator.generateTxId();

            // Insert transaction information.
            log.debug("\t--> Inserting transaction information for txId: {}", txId);
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.USER_REGISTRATION)
                    .status(TransactionStatus.PENDING)
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information for transaction ID: {}", transaction.getId());
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.PROPOSE_REGISTER_USER)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished proposeRegisterUser ***");

            return ProposeRegisterUserResDto.builder()
                    .txId(txId)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during proposeRegisterUser: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during proposeRegisterUser: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_PROPOSE_REGISTER_USER);
        }
    }

    /**
     * Retrieves KYC information, validates transaction, and updates transaction with PII information.
     *
     * @param retrieveKycReqDto The DTO containing KYC retrieval request details
     * @return RetrieveKycResDto containing the transaction ID
     * @throws OpenDidException if there's an error during KYC retrieval
     */
    @Override
    public RetrieveKycResDto retrieveKyc(RetrieveKycReqDto retrieveKycReqDto) {
        try {
            log.debug("=== Starting retrieveKyc ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving transaction information for txId: {}", retrieveKycReqDto.getTxId());
            Transaction transaction = transactionService.findTransactionByTxId(retrieveKycReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_retrieveKyc(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(retrieveKycReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.CREATE_DID, ServerTokenPurpose.CREATE_DID_AND_ISSUE_VC);

            // Retrieve PII information from KYC server
            log.debug("\t--> Retrieving PII information from KYC server");
            String pii = requestUserPii(retrieveKycReqDto.getKycTxId());

            // Update transaction PII.
            log.debug("\t--> Updating transaction PII for transaction ID: {}", transaction.getId());
            transactionService.updateTransactionPii(transaction.getId(), pii);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.RETRIEVE_KYC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            return RetrieveKycResDto.builder()
                    .txId(retrieveKycReqDto.getTxId())
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during retrieveKyc: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during requestRegisterPii: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_RETRIEVE_KYC);
        }
    }
    /**
     * Validate Transaction and SubTransaction for retrieveKyc
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction or sub-transaction is invalid
     * @throws OpenDidException if the transaction is expired
     * @throws OpenDidException if the sub-transaction type is invalid
     */
    private void validateTransaction_retrieveKyc(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("\t--> Invalid transaction status: {} for transaction ID: {}", transaction.getStatus(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            log.error("\t--> Transaction is expired at: {} for transaction ID: {}", transaction.getExpiredAt(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_CREATE_TOKEN) {
            log.error("\t--> Invalid sub-transaction type: {} for transaction ID: {}", subTransaction.getType(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * requestUserPii retrieves PII information from the KYC server using the transaction ID.
     *
     * @param kycTxId the transaction ID of the KYC request
     * @return the PII information of the user
     */
    private String requestUserPii(String kycTxId) {
        RetrievePiiApiReqDto apiRetrievePiiReqDto = RetrievePiiApiReqDto.builder()
                .userId(kycTxId)
                .build();

        RetrievePiiApiResDto apiRetrievePiiResDto = kycFeign.retrievePii(apiRetrievePiiReqDto);
        return apiRetrievePiiResDto.getPii();
    }

    /**
     * Requests the registration of a user, validating the signed DID document and verifying signatures.
     *
     * @param requestRegisterUserReqDto The DTO containing request registration details
     * @return RequestRegisterUserResDto containing the transaction ID
     * @throws OpenDidException if there's an error during user registration
     */
    @Override
    public RequestRegisterUserResDto requestRegisterUser(RequestRegisterUserReqDto requestRegisterUserReqDto) {
        try {
            log.debug("=== Starting requestRegisterUser ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving transaction information for txId: {}", requestRegisterUserReqDto.getTxId());
            Transaction transaction = transactionService.findTransactionByTxId(requestRegisterUserReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_requestRegisterUser(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            Token token = tokenValidator.validateServerToken(requestRegisterUserReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.CREATE_DID, ServerTokenPurpose.CREATE_DID_AND_ISSUE_VC);

            // Retrieve Wallet information.
            log.debug("\t--> Retrieving wallet information");
            Wallet wallet = walletQueryService.findByWalletIdAndDidAndStatus(
                    requestRegisterUserReqDto.getSignedDidDoc().getWallet().getId(), requestRegisterUserReqDto.getSignedDidDoc().getWallet().getDid(), WalletStatus.CREATED);

            // Validate Signed did document.
            log.debug("\t--> Validating signed DID document");
            validateSignedDidDoc(requestRegisterUserReqDto.getSignedDidDoc());

            // Parse User did document.
            log.debug("\t--> Parsing user DID document");
            DidDocument ownerDidDoc = parseOwnerDidDoc(requestRegisterUserReqDto.getSignedDidDoc().getOwnerDidDoc());
            String userDid = ownerDidDoc.getId();

            // Check if the user has registered.
            log.debug("\t--> Checking if the user has registered");
            validateUserNotRegistered(userDid);

            // Verify DID document key signatures.
            log.debug("\t--> Verifying DID document key signatures");
            verifyDidDocKeyProofs(ownerDidDoc);

            // Sign DID document.
            log.debug("\t--> Signing DID document");
            InvokedDidDoc invokedDidDoc = signatureService.signInvokedDidDoc(ownerDidDoc);

            // Upload User DID document.
            log.debug("\t--> Uploading wallet DID document");
            storageService.registerDidDoc(invokedDidDoc, RoleType.ETC);

            // Insert User information.
            log.debug("\t--> Inserting user information");
            User user = userRepository.save(User.builder()
                    .did(userDid)
                    .pii(transaction.getPii())
                    .status(UserStatus.ACTIVATED)
                    .build());

            // Update wallet status
            log.debug("\t--> Updating wallet status");
            updateWalletStatus(wallet.getId(), user.getId(), WalletStatus.ASSIGNED);

            // Insert App information.
            log.debug("\t--> Inserting app information");
            appRepository.save(App.builder()
                    .appId(token.getAppId())
                    .status(AppStatus.ASSIGNED)
                    .userId(user.getId())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_REGISTER_USER)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished requestRegisterUser ***");

            return RequestRegisterUserResDto.builder()
                    .txId(requestRegisterUserReqDto.getTxId())
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during requestRegisterUser: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during requestRegisterUser: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_REQUEST_REGISTER_USER);
        }
    }

    /**
     * Validate Transaction and SubTransaction for requestRegisterUser
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction or sub-transaction is invalid
     * @throws OpenDidException if the transaction is expired
     * @throws OpenDidException if the sub-transaction type is invalid
     */
    private void validateTransaction_requestRegisterUser(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("\t--> Invalid transaction status: {} for transaction ID: {}", transaction.getStatus(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            log.error("\t--> Transaction is expired at: {} for transaction ID: {}", transaction.getExpiredAt(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.RETRIEVE_KYC) {
            log.error("\t--> Invalid sub-transaction type: {} for transaction ID: {}", subTransaction.getType(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * validates the signed DID document and verifies signatures for the restoration of a DID document.
     *
     * @param signedDidDoc The signed DID document to restore
     * @throws OpenDidException if Failed to verify signature
     */
    private void validateSignedDidDoc(SignedDidDoc signedDidDoc) {
        // Extract and validate did and didKeyUrl
        String clientDid = signedDidDoc.getWallet().getDid();
        if (!DidValidator.isValidDid(clientDid)){
            log.error("\t--> Invalid DID: {}", clientDid);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        String verificationMethod = signedDidDoc.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            log.error("\t--> Invalid DID Key URL: {}", verificationMethod);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the equivalence of did.
        String didOfKeyUrl = DidUtil.extractDid(verificationMethod);
        if (!clientDid.equals(didOfKeyUrl)) {
            log.error("\t--> DID mismatch: clientDid={}, didOfKeyUrl={}", clientDid, didOfKeyUrl);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (signedDidDoc.getProof().getProofPurpose() != ProofPurpose.ASSERTION_METHOD) {
            log.error("\t--> Invalid proof purpose: {}", signedDidDoc.getProof().getProofPurpose());
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Extract the signature message.
        byte[] signatureMessage = generateSignatureMessage(signedDidDoc);

        // Find Wallet  DID Document.
        DidDocument walletDidDocument = didDocService.getDidDocument(verificationMethod);

        // Get the Assertion public key.
        String encodedAssertPublicKey = BaseCoreDidUtil.getPublicKey(walletDidDocument, "assert");

        // Verify the signature.
        signatureService.verifySignature(encodedAssertPublicKey, signedDidDoc.getProof().getProofValue(), signatureMessage, signedDidDoc.getProof().getType());
    }

    /**
     * Parses the owner DID document from the encoded DID document.
     *
     * @param encodedDidDoc The encoded DID document to parse
     * @return the parsed owner DID document
     * @throws OpenDidException if there's an error during parsing
     */
    private DidDocument parseOwnerDidDoc(String encodedDidDoc) {
        try {
            byte[] decodedDidDoc = BaseMultibaseUtil.decode(encodedDidDoc);
            String OwnerDidDoc = new String(decodedDidDoc, StandardCharsets.UTF_8);

            DidManager didManager = BaseCoreDidUtil.parseDidDoc(OwnerDidDoc);
            DidDocument didDocument = didManager.getDocument();

            return didDocument;
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during parseOwnerDidDoc: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.PARSE_DIDDOC_FAILED);
        }
    }

    /**
     * Validates the user status for updating the DID document.
     *
     * @param did The DID of the user to validate
     * @throws OpenDidException if the user status is invalid
     */
    private void validateUserNotRegistered(String did) {
        // Check if the user has registered.
        if (userQueryService.countByDid(did) > 0) {
            throw new OpenDidException(ErrorCode.USER_DID_ALREADY_EXISTS);
        }
    }

    /**
     * verifies the DID document key proofs.
     *
     * @param ownerDidDoc Owner DID document
     */
    private void verifyDidDocKeyProofs(DidDocument ownerDidDoc) {
        signatureService.verifyDidDocKeyProofs(ownerDidDoc);
    }

    /**
     * Generates the signature message for the signed DID document.
     *
     * @param signedDidDoc The signed DID document to generate the signature message
     * @return the generated signature message
     */
    private byte[] generateSignatureMessage(SignedDidDoc signedDidDoc) {
        try {
            // Remove proofValue from Proof fields in the object.
            SignedDidDoc signatureMessageObject = removeProofValue(signedDidDoc);

            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch(JsonProcessingException e) {
            log.error("\t--> Exception occurred in extractSignatureMessage: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Removes the proof value from the signed DID document.
     *
     * @param data The signed DID document to remove the proof value
     * @return the signed DID document with the proof value removed
     */
    private SignedDidDoc removeProofValue(SignedDidDoc data) {
        SignedDidDoc signatureMessageObject = SignedDidDoc.builder()
                .ownerDidDoc(data.getOwnerDidDoc())
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
     * Updates Wallet status
     *
     * @param id The Wallet ID to update
     * @param userID The ID of the user to update
     * @param walletStatus The status to update the wallet to
     */
    private void updateWalletStatus(Long id, Long userID, WalletStatus walletStatus) {
        Wallet wallet = walletQueryService.findById(id);
        wallet.setUserId(userID);
        wallet.setStatus(walletStatus);

        walletRepository.save(wallet);
    }

    /**
     * Confirms user registration by validating the transaction and server token, then updating transaction status.
     *
     * @param confirmRegisterUserReqDto The DTO containing user registration confirmation details
     * @return ConfirmRegisterUserResDto containing the transaction ID
     * @throws OpenDidException if there's an error during confirmation
     */
    @Override
    public ConfirmRegisterUserResDto confirmRegisterUser(ConfirmRegisterUserReqDto confirmRegisterUserReqDto) {
        try {
            log.debug("=== Starting confirmRegisterUser ===");

            // Retrieve Transaction information.
            log.debug("\t--> Validating transaction's validity");
            Transaction transaction = transactionService.findTransactionByTxId(confirmRegisterUserReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_confirmRegisterUser(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(confirmRegisterUserReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.CREATE_DID, ServerTokenPurpose.CREATE_DID_AND_ISSUE_VC);

            // Update transaction status.
            log.debug("\t--> Updating transaction status to COMPLETED for transaction ID: {}", transaction.getId());
            transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.COMPLETED);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.CONFIRM_ENROLL_ENTITY)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished confirmRegisterUser ***");

            return ConfirmRegisterUserResDto.builder()
                    .txId(confirmRegisterUserReqDto.getTxId())
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during confirmRegisterUser: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during confirmRegisterUser: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_REQUEST_CONFIRM_USER);
        }
    }

    /**
     * Validate Transaction and SubTransaction for confirmRegisterUser
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction or sub-transaction is invalid
     * @throws OpenDidException if the transaction is expired
     * @throws OpenDidException if the sub-transaction type is invalid
     */
    private void validateTransaction_confirmRegisterUser(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("\t--> Invalid transaction status: {} for transaction ID: {}", transaction.getStatus(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            log.error("\t--> Transaction is expired at: {} for transaction ID: {}", transaction.getExpiredAt(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_REGISTER_USER) {
            log.error("\t--> Invalid sub-transaction type: {} for transaction ID: {}", subTransaction.getType(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Proposes Update of a DID document, generating a transaction ID and initializing sub-transaction.
     *
     * @param proposeUpdateDidDocReqDto The DTO containing DID document restoration proposal details
     * @return ProposeUpdateDidDocResDto containing the transaction ID
     * @throws OpenDidException if there's an error during the proposal process
     */
    @Override
    public ProposeUpdateDidDocResDto proposeUpdateDidDoc(ProposeUpdateDidDocReqDto proposeUpdateDidDocReqDto) {
        try {
            log.debug("=== Starting proposeUpdateUser ===");

            // Validate user status for update.
            log.debug("\t--> Validating user status for update");
            validateUserStatusForUpdate(proposeUpdateDidDocReqDto.getDid());

            // Generate transaction code.
            log.debug("\t--> Generating transaction ID");
            String txId = IdGenerator.generateTxId();

            // Generate authNonce. (16-byte)
            log.debug("\t--> Generating auth nonce");
            String authNonce = BaseTasUtil.generateNonceWithMultibase();

            // Insert transaction information.
            log.debug("\t--> Inserting transaction information for txId: {}", txId);
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.USER_UPDATE)
                    .status(TransactionStatus.PENDING)
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .authNonce(authNonce)
                    .did(proposeUpdateDidDocReqDto.getDid())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information for transaction ID: {}", transaction.getId());
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.PROPOSE_UPDATE_DIDDOC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished proposeUpdateUser ***");

            return ProposeUpdateDidDocResDto.builder()
                    .txId(txId)
                    .authNonce(authNonce)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during proposeUpdateUser: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during proposeUpdateUser: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Validates the user status for updating the DID document.
     *
     * @param did The DID of the user to restore
     * @return RequestRestoreDidDocResDto containing the transaction ID
     * @throws OpenDidException if there's an error during the restoration process
     */
    private void validateUserStatusForUpdate(String did) {
        User user = userQueryService.findByDid(did);
        if (user.getStatus() != UserStatus.ACTIVATED) {
            throw new OpenDidException(ErrorCode.USER_NOT_ACTIVATED);
        }
    }

    /**
     * This method performs a series of validations and operations to securely update a DID document.
     * It includes transaction validation, token verification, DID document parsing and validation,
     * signature verification, and finally, the storage of the updated DID document.
     *
     * @param requestUpdateDidDocReqDto The DTO containing the request details for updating the DID document
     * @return RequestUpdateDidDocResDto The response DTO containing the transaction ID
     * @throws OpenDidException for various validation failures or processing errors
     */
    @Override
    public RequestUpdateDidDocResDto requestUpdateDidDoc(RequestUpdateDidDocReqDto requestUpdateDidDocReqDto) {
        try {
            log.debug("=== Starting requestUpdateUser ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving transaction information for txId: {}", requestUpdateDidDocReqDto.getTxId());
            Transaction transaction = transactionService.findTransactionByTxId(requestUpdateDidDocReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_requestUpdateUser(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            Token token = tokenValidator.validateServerToken(requestUpdateDidDocReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.UPDATE_DID);

            // Validate Signed did document.
            log.debug("\t--> Validating signed DID document");
            validateSignedDidDoc(requestUpdateDidDocReqDto.getSignedDidDoc());

            // Parse User did document.
            log.debug("\t--> Parsing user DID document");
            DidDocument updatedUserOwnerDidDoc = parseOwnerDidDoc(requestUpdateDidDocReqDto.getSignedDidDoc().getOwnerDidDoc());
            String userDid = updatedUserOwnerDidDoc.getId();

            // Retrieve current DID document.
            log.debug("\t--> Retrieving current DID document");
            DidDocument currentUserDidDoc = storageService.findDidDoc(userDid);

            // Validate DID document's contents.
            log.debug("\t--> Validating DID document's contents");
            validateDidDocContents(currentUserDidDoc, updatedUserOwnerDidDoc);

            // Check if the user has registered.
            log.debug("\t--> Checking if the user has registered");
            User user = validateUserRegistered(userDid);

            // Validate Did Auth.
            log.debug("\t--> Validating Did Auth");
            didAuthValidator.validateDidAuth(requestUpdateDidDocReqDto.getDidAuth(), transaction);

            // Verify DID document key signatures.
            log.debug("\t--> Verifying DID document key signatures");
            verifyDidDocKeyProofs(updatedUserOwnerDidDoc);

            // Validate DID document id
            log.debug("\t--> Validating DID document ID");
            validateUserDidDocId(updatedUserOwnerDidDoc, transaction, requestUpdateDidDocReqDto.getDidAuth());

            // Validate User Mapping Info.(userDid, walletId, appID)
            log.debug("\t--> Validating User Mapping Info(userDid, walletId, appID)");
            validateUserMappingInfo(user, token);

            // Sign DID document.
            log.debug("\t--> Signing DID document");
            InvokedDidDoc invokedDidDoc = signatureService.signInvokedDidDoc(updatedUserOwnerDidDoc);

            // Upload User DID document.
            log.debug("\t--> Uploading wallet DID document");
            storageService.registerDidDoc(invokedDidDoc, RoleType.ETC);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_UPDATE_DIDDOC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished requestUpdateUser ***");

            return RequestUpdateDidDocResDto.builder()
                    .txId(requestUpdateDidDocReqDto.getTxId())
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during requestUpdateUser: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during requestUpdateUser: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_REQUEST_UPDATE_DIDDOC);
        }
    }

    /**
     * Validate Transaction and SubTransaction for requestUpdateUser
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction or sub-transaction is invalid
     * @throws OpenDidException if the transaction is expired
     * @throws OpenDidException if the sub-transaction type is invalid
     */
    private void validateTransaction_requestUpdateUser(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("\t--> Invalid transaction status: {} for transaction ID: {}", transaction.getStatus(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            log.error("\t--> Transaction is expired at: {} for transaction ID: {}", transaction.getExpiredAt(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_CREATE_TOKEN) {
            log.error("\t--> Invalid sub-transaction type: {} for transaction ID: {}", subTransaction.getType(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * did document contents update and validate
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if there's an error during the restoration process
     */
    private void validateDidDocContents(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        // Validate DID document update.
        log.debug("\t--> Validating DID document update");
        validateDidDocVersion(currentDidDoc, updatedDidDoc);

        // Validate DID document updated time.
        log.debug("\t--> Validating DID document updated time");
        validateDidDocUpdatedTime(currentDidDoc, updatedDidDoc);

        // Validate DID document items. (context, id, controller, created, deactivated)
        log.debug("\t--> Validating DID document items( context, id, controller, created, deactivated)");
        validateDidDocItems(currentDidDoc, updatedDidDoc);
    }

    /**
     * Validate DID document version
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if the DID document version is invalid
     */
    private void validateDidDocVersion(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        int currentVersion = Integer.parseInt(currentDidDoc.getVersionId());
        int updatedVersion = Integer.parseInt(updatedDidDoc.getVersionId());
        if (currentVersion + 1 != updatedVersion) {
            log.error("\t--> Invalid DID document version: currentVersion={}, updatedVersion={}", currentVersion, updatedVersion);
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_VERSION);
        }
    }

    private void validateDidDocUpdatedTime(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        String currentDidDocUpdated = currentDidDoc.getUpdated();
        String updatedDidDocUpdated = updatedDidDoc.getUpdated();

        boolean secondDateTimeLater = DateTimeUtil.isSecondDateTimeLater(currentDidDocUpdated, updatedDidDocUpdated);
        if (!secondDateTimeLater) {
            log.error("\t--> Invalid DID document update: currentDidDocUpdated={}, updatedDidDocUpdated={}", currentDidDocUpdated, updatedDidDocUpdated);
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_UPDATED);
        }
    }

    /**
     * Validate DID document items
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if the DID document items are invalid
     */
    private void validateDidDocItems(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        // Validate DID document context
        log.debug("\t--> Validating DID document context");
        isDidDocContextValid(currentDidDoc, updatedDidDoc);

        // Validate DID document ID
        log.debug("\t--> Validating DID document ID");
        isDidDocIdValid(currentDidDoc, updatedDidDoc);

        // Validate DID document controller
        log.debug("\t--> Validating DID document controller");
        isDidDocControllerValid(currentDidDoc, updatedDidDoc);

        // Validate DID document created
        log.debug("\t--> Validating DID document created");
        isDidDocCreatedValid(currentDidDoc, updatedDidDoc);

        // Validate DID document deactivated
        log.debug("\t--> Validating DID document deactivated");
        isDidDocDeactivatedValid(currentDidDoc, updatedDidDoc);
    }

    /**
     * Validate DID document context
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if the DID document context is invalid
     */
    private void isDidDocContextValid(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        List<String> currentDidDocContext = currentDidDoc.getContext();
        List<String> updatedDidDocContext = updatedDidDoc.getContext();

        Set<String> currentSet = new HashSet<>(currentDidDocContext);
        Set<String> updatedSet = new HashSet<>(updatedDidDocContext);

        if (!currentSet.equals(updatedSet)) {
            log.error("\t--> Invalid DID document context: currentDidDocContext={}, updatedDidDocContext={}", currentDidDocContext, updatedDidDocContext);
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_CONTEXT);
        }
    }

    /**
     * Validate DID document ID
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if the DID document ID is invalid
     */
    private void isDidDocIdValid(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        String currentDidDocId = currentDidDoc.getId();
        String updatedDidDocId = updatedDidDoc.getId();

        if (!currentDidDocId.equals(updatedDidDocId)) {
            log.error("\t--> Invalid DID document ID: currentDidDocId={}, updatedDidDocId={}", currentDidDocId, updatedDidDocId);
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_ID);
        }
    }

    /**
     * Validate DID document controller
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if the DID document controller is invalid
     */
    private void isDidDocControllerValid(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        String currentDidDocController = currentDidDoc.getController();
        String updatedDidDocController = updatedDidDoc.getController();

        if (!currentDidDocController.equals(updatedDidDocController)) {
            log.error("\t--> Invalid DID document controller: currentDidDocController={}, updatedDidDocController={}", currentDidDocController, updatedDidDocController);
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_CONTROLLER);
        }
    }

    /**
     * Validate DID document created
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if the DID document created is invalid
     */
    private void isDidDocCreatedValid(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        String currentDidDocCreated = currentDidDoc.getCreated();
        String updatedDidDocCreated = updatedDidDoc.getCreated();

        if (!currentDidDocCreated.equals(updatedDidDocCreated)) {
            log.error("\t--> Invalid DID document created: currentDidDocCreated={}, updatedDidDocCreated={}", currentDidDocCreated, updatedDidDocCreated);
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_CREATED);
        }
    }

    /**
     * Validate DID document deactivated
     *
     * @param currentDidDoc The current DID document to validate
     * @param updatedDidDoc The updated DID document to validate
     * @throws OpenDidException if the DID document deactivated is invalid
     */
    private void isDidDocDeactivatedValid(DidDocument currentDidDoc, DidDocument updatedDidDoc) {
        boolean currentDidDocDeactivated = currentDidDoc.getDeactivated();
        boolean updatedDidDocDeactivated = updatedDidDoc.getDeactivated();

        if (currentDidDocDeactivated != updatedDidDocDeactivated) {
            log.error("\t--> Invalid DID document deactivated: currentDidDocDeactivated={}, updatedDidDocDeactivated={}", currentDidDocDeactivated, updatedDidDocDeactivated);
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_DEACTIVATED);
        }

    }

    /**
     * Validates the user status for updating the DID document.
     *
     * @param did The DID of the user to validate
     * @throws OpenDidException if the user status is invalid
     */
    private User validateUserRegistered(String did) {
        return userQueryService.findByDid(did);
    }

    /**
     * Validates the DID document ID of the user.
     *
     * @param updatedDidDoc The updated DID document to validate
     * @param transaction The transaction to validate
     * @param didAuth The DID authentication information to validate
     * @throws OpenDidException if the DID document ID is invalid
     */
    private void validateUserDidDocId(DidDocument updatedDidDoc, Transaction transaction, DidAuth didAuth) {
        if (!updatedDidDoc.getId().equals(transaction.getDid())) {
            log.error("\t--> DID mismatch: updatedUserOwnerDidDoc.getId={}, transaction.getDid()={}", updatedDidDoc.getId(), transaction.getDid());
            throw new OpenDidException(ErrorCode.DID_DOCUMENT_ID_MISMATCH);
        }
        if (!updatedDidDoc.getId().equals(didAuth.getDid())) {
            log.error("\t--> DID mismatch: updatedUserOwnerDidDoc.getId={}, requestUpdateDidDocReqDto.getDidAuth().getDid()={}", updatedDidDoc.getId(), didAuth.getDid());
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_ID);
        }
    }

    /**
     * Validates the user mapping information.
     *
     * @param user The user to validate
     * @param token The token to validate
     * @throws OpenDidException if the user mapping information is invalid
     */
    private void validateUserMappingInfo(User user, Token token) {
        App app = appQueryService.findByUserId(user.getId());

        if (!token.getAppId().equals(app.getAppId())) {
            log.error("\t--> Invalid app ID: tokenAppId={}, appAppId={}", token.getAppId(), app.getAppId());
            throw new OpenDidException(ErrorCode.APP_ID_MISMATCH);
        }

        Wallet wallet = walletQueryService.findByWalletIdAndUserIdAndStatus(token.getWalletId(), user.getId(), WalletStatus.ASSIGNED);
        if (!wallet.getWalletId().equals(token.getWalletId())) {
            log.error("\t--> Invalid wallet ID: tokenWalletId={}, walletId={}", token.getWalletId(), wallet.getWalletId());
            throw new OpenDidException(ErrorCode.WALLET_ID_MISMATCH);
        }
    }

    /**
     * Confirms the update of a DID document by validating the transaction and server token, then updating the transaction status.
     *
     * @param confirmUpdateDidDocReqDto The DTO containing the DID document update confirmation details
     * @return ConfirmUpdateDidDocResDto containing the transaction ID
     * @throws OpenDidException if there's an error during the confirmation process
     */
    @Override
    public ConfirmUpdateDidDocResDto confirmUpdateDidDoc(ConfirmUpdateDidDocReqDto confirmUpdateDidDocReqDto) {
        try {
            log.debug("=== Starting confirmUpdateUser ===");

            // Retrieve Transaction information.
            log.debug("\t--> Validating transaction's validity");
            Transaction transaction = transactionService.findTransactionByTxId(confirmUpdateDidDocReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_confirmUpdateUser(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(confirmUpdateDidDocReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.UPDATE_DID);

            // Update transaction status.
            log.debug("\t--> Updating transaction status to COMPLETED for transaction ID: {}", transaction.getId());
            transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.COMPLETED);

            // Insert sub-transaction information.
            log.debug("*** Finished confirmUpdateUser ***");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.CONFIRM_UPDATE_DIDDOC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished confirmRegisterUser ***");

            return ConfirmUpdateDidDocResDto.builder()
                    .txId(confirmUpdateDidDocReqDto.getTxId())
                    .build();
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Validate Transaction and SubTransaction for confirmUpdateUser
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction or sub-transaction is invalid
     * @throws OpenDidException if the transaction is expired
     * @throws OpenDidException if the sub-transaction type is invalid
     */
    private void validateTransaction_confirmUpdateUser(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("\t--> Invalid transaction status: {} for transaction ID: {}", transaction.getStatus(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            log.error("\t--> Transaction is expired at: {} for transaction ID: {}", transaction.getExpiredAt(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_UPDATE_DIDDOC) {
            log.error("\t--> Invalid sub-transaction type: {} for transaction ID: {}", subTransaction.getType(), transaction.getId());
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    /**
     * Updates the status of a user, generating a transaction ID and initializing sub-transaction.
     *
     * @param updateUserStatusReqDto The DTO containing the user status update details
     * @return UpdateUserStatusResDto containing the transaction ID
     * @throws OpenDidException if there's an error during the status update process
     */
    @Override
    public UpdateUserStatusResDto updateUserStatus(UpdateUserStatusReqDto updateUserStatusReqDto) {
        try {
            log.debug("=== Starting updateUserStatus ===");

            // Generate transaction code.
            log.debug("\t--> Generating transaction ID");
            String txId = IdGenerator.generateTxId();

            // Retrieve User information.
            log.debug("\t--> Retrieving user information for DID: {}", updateUserStatusReqDto.getDid());
            User user = userQueryService.findByDid(updateUserStatusReqDto.getDid());

            // Retrieve did document.
            // @TODO: B/C SDK 연동 필요
            log.debug("\t--> Retrieving DID document for DID: {}", updateUserStatusReqDto.getDid());
            Object didDocument = didDocService.getDidDocument(updateUserStatusReqDto.getDid());

            // Validate request did document status.
            //@TODO: DataModel 사용 필요
            log.debug("\t--> Validating request DID document status");
            validateRequestDidDocStatus(updateUserStatusReqDto.getStatus(), didDocument);

            // @TODO: B/C SDK 연동 필요
            // Update did document status in blockchain.
            updateDidDocStatus(updateUserStatusReqDto.getDid(), updateUserStatusReqDto.getStatus());

            // Update user status.
            updateUserStatus(user.getId(), updateUserStatusReqDto.getStatus());

            // Insert transaction information.
            log.debug("\t--> Inserting transaction information for txId: {}", txId);
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.UPDATE_USER_STATUS)
                    .status(TransactionStatus.COMPLETED)
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information for transaction ID: {}", transaction.getId());
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.UPDATE_USER_STATUS)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished updateUserStatus ***");

            return UpdateUserStatusResDto.builder()
                    .txId(txId)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during updateUserStatus: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during updateUserStatus: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Updates the status of a DID document in the blockchain.
     * @param did The DID of the user to update
     * @param status The status to update the DID document to
     */
    private void updateDidDocStatus(String did, DidDocStatus status) {
        storageService.updateDidDocStatus(did, status);
    }

    /**
     * Validates the request DID document status.
     *
     * @param status The status to validate
     * @param didDocument The DID document to validate
     */
    //@TODO: DataModel 사용 필요
    private void validateRequestDidDocStatus(DidDocStatus status, Object didDocument) {

    }

    /**
     * Updates the status of a user in the database.
     *
     * @param userId The ID of the user to update
     * @param didDocStatus The status to update the user to
     */
    private void updateUserStatus(Long userId, DidDocStatus didDocStatus) {
        User user = userQueryService.findById(userId);

        if (DidDocStatus.ACTIVATED == didDocStatus) {
            user.setStatus(UserStatus.ACTIVATED);
        } else if (DidDocStatus.DEACTIVATED == didDocStatus) {
            user.setStatus(UserStatus.DEACTIVATED);
        } else if (DidDocStatus.REVOKED == didDocStatus) {
            user.setStatus(UserStatus.REVOKED);
        }

        userRepository.save(user);
    }

    /**
     * Updates the status of a user, generating a transaction ID and initializing sub-transaction.
     *
     * @param updateDidDocDeactivatedReqDto The DTO containing the user status update details
     * @return EmptyResDto
     * @throws OpenDidException if there's an error during the status update process
     */
    @Override
    public EmptyResDto updateDidDocDeactivated(UpdateDidDocDeactivatedReqDto updateDidDocDeactivatedReqDto) {
        try {
            log.debug("=== Starting updateUserStatus ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving PII information from KYC server");
            String pii = requestUserPii(updateDidDocDeactivatedReqDto.getKycTxId());

            // Retrieve User information using PII
            log.debug("\t--> Retrieving user information for PII");
            User user = userQueryService.findByPiiAndStatus(pii, UserStatus.ACTIVATED);

            // Retrieve DID document.
            log.debug("\t--> Retrieving DID document for DID: {}", user.getDid());
            DidDocument userDidDoc = storageService.findDidDoc(user.getDid());

            // Update DID document status.
            log.debug("\t--> Updating DID document status to DEACTIVATED (Blockchain)");
            updateDidDocDeactivated(userDidDoc);

            // Update User status.
            log.debug("\t--> Updating user status to DEACTIVATED (DB)");
            updateUserStatus(user, UserStatus.DEACTIVATED);

            log.debug("*** Finished updateDidDocDeactivated ***");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during updateDidDocDeactivated: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during updateUserStatus: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_UPDATE_DIDDOC_DEACTIVATED);
        }
    }

    /**
     * Updates the status of a DID document to DEACTIVATED in the blockchain.
     * @param userDidDoc The DID document to update
     */
    private void updateDidDocDeactivated(DidDocument userDidDoc) {
        String didWithVersion = BaseTasDidUtil.getDidWithVersion(userDidDoc);
        BaseBlockChainUtil.updateDidDocStatus(didWithVersion, org.omnione.did.data.model.enums.did.DidDocStatus.DEACTIVATED);
    }

    /**
     * Updates the status of a user and saves the changes to the database.
     *
     * @param user The DTO containing the user status update details
     * @param userStatus The status to update the user to
     */
    private void updateUserStatus(User user, UserStatus userStatus) {
        user.setStatus(userStatus);
        userRepository.save(user);
    }

    /**
     * Updates the status of a user, generating a transaction ID and initializing sub-transaction.
     *
     * @param updateDidDocRevokedReqDto The DTO containing the user status update details
     * @return EmptyResDto
     * @throws OpenDidException if there's an error during the status update process
     */
    @Override
    public EmptyResDto updateDidDocRevoked(UpdateDidDocRevokedReqDto updateDidDocRevokedReqDto) {
        try {
            log.debug("=== Starting updateDidDocRevoked ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving PII information from KYC server");
            String pii = requestUserPii(updateDidDocRevokedReqDto.getKycTxId());

            // Retrieve User information using PII
            log.debug("\t--> Retrieving user information for PII");
            User user = userQueryService.findByPiiAndStatus(pii, UserStatus.ACTIVATED);

            // Retrieve DID document.
            log.debug("\t--> Retrieving DID document for DID: {}", user.getDid());
            DidDocument userDidDoc = storageService.findDidDoc(user.getDid());

            // Update DID document status.
            log.debug("\t--> Updating DID document status to REVOKED (Blockchain)");
            updateDidDocRevoked(userDidDoc);

            // Update User status.
            log.debug("\t--> Updating user status to REVOKED (DB)");
            updateUserStatus(user, UserStatus.REVOKED);

            log.debug("*** Finished updateDidDocRevoked ***");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during updateDidDocDeactivated: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during updateUserStatus: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_UPDATE_DIDDOC_REVOKED);
        }
    }

    /**
     * Updates the status of a DID document to REVOKED in the blockchain.
     * @param userDidDoc The DID document to update
     */
    private void updateDidDocRevoked(DidDocument userDidDoc) {
        String didWithVersion = BaseTasDidUtil.getDidWithVersion(userDidDoc);
        BaseBlockChainUtil.updateDidDocStatus(didWithVersion, org.omnione.did.data.model.enums.did.DidDocStatus.REVOKED);
    }

    /**
     * Handles the process of offering DID restoration via push notification.
     *
     * @param offerRestoreDidPushReqDto The DTO containing the request details for offering DID restoration
     * @return OfferRestoreDidPushResDto The response DTO containing the offer ID
     * @throws OpenDidException if there's an error during the process or user validation fails
     */
    @Override
    public OfferRestoreDidPushResDto offerRestoreDidPush(OfferRestoreDidPushReqDto offerRestoreDidPushReqDto) {
        try {
            log.debug("=== Starting offerRestoreDidPush ===");

            // Retrieve DID document.
            log.debug("\t--> Retrieving DID document for DID: {}", offerRestoreDidPushReqDto.getDid());
            User user = userQueryService.findByDid(offerRestoreDidPushReqDto.getDid());

            // Validate user status for restore.
            log.debug("\t--> Validating user status for restore");
            validateUserStatusForRestore(user);

            // Generate push data
            log.debug("\t--> Generating push data");
            String offerId = IdGenerator.generateOfferId();
            RestoreDidOfferPayload restoreDidOfferPayload = RestoreDidOfferPayload.builder()
                    .offerId(offerId)
                    .type(OfferType.RESTORE_DID_OFFER)
                    .did(user.getDid())
                    .build();
            Map<String, String> pushData = pushServiceHelper.generatePushDataForRestoreDid(restoreDidOfferPayload, user.getDid());

            // Generate Fcm Notification data (title, body)
            log.debug("\t--> Generating Fcm Notification data");
            FcmNotificationDto fcmNotificationDto = pushServiceHelper.generateDidNotification(PayloadType.RESTORE_DID, user);

            // Send push notification
            log.debug("\t--> Sending push notification");
            notiPushService.requestSendPush(RequestSendPushReqDto.builder()
                    .data(pushData)
                    .targetDids(Collections.singletonList(user.getDid()))
                    .notification(fcmNotificationDto)
                    .build());

            // Insert DID offer information.
            log.debug("\t--> Insert DID offer information");
            didOfferQueryService.save(DidOffer.builder()
                    .offerId(offerId)
                    .type(DidOfferType.RESTORE_OFFER)
                    .did(user.getDid())
                    .build());

            log.debug("*** Finished offerRestoreDidPush ***");

            return OfferRestoreDidPushResDto.builder()
                    .offerId(offerId)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during offerRestoreDidPush: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during offerRestoreDidPush: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_OFFER_RESTORE_DID_PUSH);
        }
    }

    /**
     * Validates the user status for restoration.
     *
     * @param user The user to validate
     * @throws OpenDidException if the user status is invalid
     */
    private void validateUserStatusForRestore(User user) {
        if (user.getStatus() != UserStatus.DEACTIVATED) {
            log.error("\t--> Invalid user status: {}, expect: {}", user.getStatus(), UserStatus.DEACTIVATED);
            throw new OpenDidException(ErrorCode.USER_NOT_DEACTIVATED);
        }
    }

    /**
     * Handles the process of offering DID restoration via email.
     *
     * @param offerRestoreDidEmailReqDto The DTO containing the request details for offering DID restoration
     * @return OfferRestoreDidEmailResDto The response DTO containing the offer ID
     * @throws OpenDidException if there's an error during the process or user validation fails
     */
    @Override
    public OfferRestoreDidEmailResDto offerRestoreDidEmail(OfferRestoreDidEmailReqDto offerRestoreDidEmailReqDto) {
        try {
            log.debug("=== Starting offerRestoreDidEmail ===");

            // Retrieve DID document.
            log.debug("\t--> Retrieving DID document for DID: {}", offerRestoreDidEmailReqDto.getDid());
            User user = userQueryService.findByDid(offerRestoreDidEmailReqDto.getDid());

            // Validate user status for restore.
            log.debug("\t--> Validating user status for restore");
            validateUserStatusForRestore(user);

            // Generate email data
            log.debug("\t--> Generating email data");
            String offerId = IdGenerator.generateOfferId();
            RestoreDidOfferPayload restoreDidOfferPayload = RestoreDidOfferPayload.builder()
                    .offerId(offerId)
                    .type(OfferType.RESTORE_DID_OFFER)
                    .did(user.getDid())
                    .build();
            Map<String, String> emailData = emailServiceHelper.generateEmailDataForRestoreDid(restoreDidOfferPayload, user);

            // Send email
            log.debug("\t--> Sending email");
            notiEmailService.requestSendEmail(RequestSendEmailReqDto.builder()
                    .email(EmailTemplate.builder()
                            .title(emailServiceHelper.getEmailTitle(QrType.RESTORE_DID))
                            .recipientAddress(offerRestoreDidEmailReqDto.getEmail())
                            .contentData(emailData)
                            .templateType(EmailTemplateType.RESTORE_DID)
                            .build())
                    .senderAddress(emailProperty.getSender())
                    .build());

            // Insert DID offer information.
            log.debug("\t--> Insert DID offer information");
            didOfferQueryService.save(DidOffer.builder()
                    .offerId(offerId)
                    .type(DidOfferType.RESTORE_OFFER)
                    .did(user.getDid())
                    .build());

            log.debug("*** Finished offerRestoreDidEmail ***");

            return OfferRestoreDidEmailResDto.builder()
                    .offerId(offerId)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during offerRestoreDidEmail: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during offerRestoreDidEmail: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Handles the process of proposing DID restoration.
     *
     * @param proposeRestoreDidDocReqDto The DTO containing the request details for proposing DID restoration
     * @return ProposeRestoreDidDocResDto The response DTO containing the transaction ID
     * @throws OpenDidException if there's an error during the process or user validation fails
     */
    @Override
    public ProposeRestoreDidDocResDto proposeRestoreDidDoc(ProposeRestoreDidDocReqDto proposeRestoreDidDocReqDto) {
        try {
            log.debug("=== Starting proposeRestoreDidDoc ===");

            // Generate transaction code.
            log.debug("\t--> Generating transaction code.");
            String txId = IdGenerator.generateTxId();

            // Retrieve DID offer information.
            log.debug("\t--> Retrieving DID offer information for offer ID: {}", proposeRestoreDidDocReqDto.getOfferId());
            DidOffer didOffer = didOfferQueryService.findByOfferId(proposeRestoreDidDocReqDto.getOfferId());

            // Validate DID offer information.
            log.debug("\t--> Validating DID offer information");
            validateDidOffer(proposeRestoreDidDocReqDto, didOffer);

            // Retrieve DID document.
            log.debug("\t--> Retrieving DID document for DID: {}", proposeRestoreDidDocReqDto.getDid());
            User user = userQueryService.findByDid(proposeRestoreDidDocReqDto.getDid());

            // Validate user status for restore.
            log.debug("\t--> Validating user status for restore");
            validateUserStatusForRestore(user);

            // Generate authNonce. (16-byte)
            String authNonce = BaseTasUtil.generateNonceWithMultibase();

            // Insert transaction information.
            log.debug("\t--> Inserting transaction information for txId: {}", txId);
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.DIDDOC_RESTORE)
                    .status(TransactionStatus.PENDING)
                    .authNonce(authNonce)
                    .did(didOffer.getDid())
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information for transaction ID: {}", transaction.getId());
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.PROPOSE_RESTORE_DIDDOC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            // Update DID offer (transaction_id)
            didOfferQueryService.updateTransactionId(didOffer.getId(), transaction.getId());

            log.debug("*** Finished proposeRestoreDidDoc ***");

            return ProposeRestoreDidDocResDto.builder()
                    .txId(txId)
                    .authNonce(authNonce)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during proposeRestoreDidDoc: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during proposeRestoreDidDoc: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_PROPOSE_RESTORE_DIDDOC);
        }
    }

    /**
     * Validates the DID offer information.
     *
     * @param proposeRestoreDidDocReqDto The DTO containing the request details for proposing DID restoration
     * @param didOffer The DID offer information to validate
     * @throws OpenDidException if the DID offer information is invalid
     */
    private void validateDidOffer(ProposeRestoreDidDocReqDto proposeRestoreDidDocReqDto, DidOffer didOffer) {
        if (!proposeRestoreDidDocReqDto.getDid().equals(didOffer.getDid())) {
            log.error("\t--> DID mismatch: proposeRestoreDidDocReqDto.getDid={}, didOffer.getDid()={}", proposeRestoreDidDocReqDto.getDid(), didOffer.getDid());
            throw new OpenDidException(ErrorCode.MISMATCH_OFFER_DID);
        }
    }

    /**
     * Handles the process of requesting DID restoration.
     *
     * @param requestRestoreDidDocReqDto The DTO containing the request details for requesting DID restoration
     * @return RequestRestoreDidDocResDto The response DTO containing the transaction ID
     * @throws OpenDidException if there's an error during the process or user validation fails
     */
    @Override
    public RequestRestoreDidDocResDto requestRestoreDidDoc(RequestRestoreDidDocReqDto requestRestoreDidDocReqDto) {
        try {
            log.debug("=== Starting requestRestoreDidDoc ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving transaction information for txId: {}", requestRestoreDidDocReqDto.getTxId());
            Transaction transaction = transactionService.findTransactionByTxId(requestRestoreDidDocReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Retrieve DID offer information.
            log.debug("\t--> Retrieving DID offer information.");
            DidOffer didOffer = didOfferQueryService.findByTransactionId(transaction.getId());

            // Retrieve User information.
            log.debug("\t--> Retrieving user information for DID: {}", didOffer.getDid());
            User user = userQueryService.findByDid(didOffer.getDid());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_requestRestoreDidDoc(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            Token token = tokenValidator.validateServerToken(requestRestoreDidDocReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.RESTORE_DID);

            // Validate Did Auth.
            log.debug("\t--> Validating DidAuth");
            didAuthValidator.validateDidAuth(requestRestoreDidDocReqDto.getDidAuth(), transaction);

            // Validate DID offer information.
            log.debug("\t--> Validating DID offer information");
            validateDidOffer(requestRestoreDidDocReqDto, didOffer);

            // Validate User Mapping Info.(userDid, walletId, appID)
            log.debug("\t--> Validating User Mapping Info(userDid, walletId, appID)");
            validateUserMappingInfo(user, token);

            // Retrieve current DID document.
            log.debug("\t--> Retrieving current DID document");
            DidDocument userDidDoc = storageService.findDidDoc(transaction.getDid());

            // Update DID document status.
            log.debug("\t--> Updating DID document status to ACTIVATED (Blockchain)");
            updateDidDocActivated(userDidDoc);

            // Update User status.
            log.debug("\t--> Updating user status to ACTIVATED (DB)");
            updateUserStatus(user, UserStatus.ACTIVATED);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information for transaction ID: {}", transaction.getId());
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.REQUEST_RESTORE_DIDDOC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished requestRestoreDidDoc ***");

            return RequestRestoreDidDocResDto.builder()
                    .txId(requestRestoreDidDocReqDto.getTxId())
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during proposeRestoreDidDoc: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during proposeRestoreDidDoc: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_REQUEST_RESTORE_DIDDOC);
        }
    }

    /**
     * Validates the transaction for requesting DID restoration.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction or sub-transaction is invalid
     * @throws OpenDidException if the transaction is expired
     * @throws OpenDidException if the sub-transaction type is invalid
     */
    private void validateTransaction_requestRestoreDidDoc(Transaction transaction, SubTransaction subTransaction) {
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
     * validates the DID offer information.
     *
     * @param requestRestoreDidDocReqDto The DTO containing the request details for requesting DID restoration*
     * @throws OpenDidException if Offer DID mismatch
     */
    private void validateDidOffer(RequestRestoreDidDocReqDto requestRestoreDidDocReqDto, DidOffer didOffer) {
        if (!requestRestoreDidDocReqDto.getDidAuth().getDid().equals(didOffer.getDid())) {
            log.error("\t--> DID mismatch: requestRestoreDidDocReqDto.getDidAuth().getDid={}, didOffer.getDid()={}", requestRestoreDidDocReqDto.getDidAuth().getDid(), didOffer.getDid());
            throw new OpenDidException(ErrorCode.MISMATCH_OFFER_DID);
        }
    }

    /**
     * Updates the status of a DID document to ACTIVATED in the blockchain.
     * @param userDidDoc The DID document to update
     */
    private void updateDidDocActivated(DidDocument userDidDoc) {
        String didWithVersion = BaseTasDidUtil.getDidWithVersion(userDidDoc);
        BaseBlockChainUtil.updateDidDocStatus(didWithVersion, org.omnione.did.data.model.enums.did.DidDocStatus.ACTIVATED);
    }

    /**
     * Handles the process of confirming DID restoration.
     *
     * @param confirmRestoreDidDocReqDto The DTO containing the request details for confirming DID restoration
     * @return ConfirmRestoreDidDocResDto The response DTO containing the transaction ID
     * @throws OpenDidException if there's an error during the process or user validation fails
     */
    @Override
    public ConfirmRestoreDidDocResDto confirmRestoreDidDoc(ConfirmRestoreDidDocReqDto confirmRestoreDidDocReqDto) {
        try {
            log.debug("=== Starting confirmRestoreDidDoc ===");

            // Retrieve Transaction information.
            log.debug("\t--> Retrieving transaction information for txId: {}", confirmRestoreDidDocReqDto.getTxId());
            Transaction transaction = transactionService.findTransactionByTxId(confirmRestoreDidDocReqDto.getTxId());
            SubTransaction lastSubTransaction = transactionService.findLastSubTransaction(transaction.getId());

            // Validate transaction's validity.
            log.debug("\t--> Validating transaction's validity");
            validateTransaction_confirmRestoreDidDoc(transaction, lastSubTransaction);

            // Validate server token.
            log.debug("\t--> Validating server token");
            tokenValidator.validateServerToken(confirmRestoreDidDocReqDto.getServerToken(), transaction.getId(), ServerTokenPurpose.RESTORE_DID);

            // Update transaction status.
            log.debug("\t--> Updating transaction status to COMPLETED for transaction ID: {}", transaction.getId());
            transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.COMPLETED);

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(lastSubTransaction.getStep() + 1)
                    .type(SubTransactionType.CONFIRM_RESTORE_DIDDOC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            log.debug("*** Finished confirmRestoreDidDoc ***");

            return ConfirmRestoreDidDocResDto.builder()
                    .txId(confirmRestoreDidDocReqDto.getTxId())
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> OpenDidException occurred during confirmRestoreDidDoc: {}", e.getErrorCode() != null ? e.getErrorCode() : e.getErrorResponse());
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during confirmRestoreDidDoc: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Validates the transaction for confirming DID restoration.
     *
     * @param transaction The transaction to validate
     * @param subTransaction The sub-transaction to validate
     * @throws OpenDidException if the transaction or sub-transaction is invalid
     * @throws OpenDidException if the transaction is expired
     * @throws OpenDidException if the sub-transaction type is invalid
     */
    private void validateTransaction_confirmRestoreDidDoc(Transaction transaction, SubTransaction subTransaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
        if (DateTimeUtil.isExpired(transaction.getExpiredAt())) {
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (subTransaction.getType() != SubTransactionType.REQUEST_RESTORE_DIDDOC) {
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

}
