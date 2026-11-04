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

package org.omnione.did.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.exception.TaSdkErrorCode;
import org.omnione.did.tas.exception.TaSdkException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Converts TA SDK errors to the error codes of the TA server.
 * <p>
 * The SDK has its own error codes (SSDKTRA*). Clients of the TA server must keep receiving the
 * existing server error codes (SSRVTRA*), so every SDK error code is mapped here.
 */
@Slf4j
public final class TaSdkErrorMapper {

    private static final Map<TaSdkErrorCode, ErrorCode> MAP = new EnumMap<>(TaSdkErrorCode.class);

    static {
        MAP.put(TaSdkErrorCode.PASSWORD_MISMATCH, ErrorCode.PASSWORD_MISMATCH);
        MAP.put(TaSdkErrorCode.VC_META_REGISTRATION_FAILED, ErrorCode.VC_META_REGISTRATION_FAILED);
        MAP.put(TaSdkErrorCode.WALLET_PROVIDER_NOT_REGISTERED, ErrorCode.WALLET_PROVIDER_NOT_REGISTERED);
        MAP.put(TaSdkErrorCode.CERTIFICATE_VC_NOT_FOUND, ErrorCode.CERTIFICATE_VC_NOT_FOUND);
        MAP.put(TaSdkErrorCode.INVALID_CERTIFICATE_VC_ISSUER, ErrorCode.INVALID_CERTIFICATE_VC_ISSUER);
        MAP.put(TaSdkErrorCode.WALLET_ID_ALREADY_EXISTS, ErrorCode.WALLET_ID_ALREADY_EXISTS);
        MAP.put(TaSdkErrorCode.DID_DOCUMENT_REGISTRATION_FAILED, ErrorCode.DID_DOCUMENT_REGISTRATION_FAILED);
        MAP.put(TaSdkErrorCode.SERVER_TOKEN_ENCRYPTION_FAILED, ErrorCode.SERVER_TOKEN_ENCRYPTION_FAILED);
        MAP.put(TaSdkErrorCode.USER_DID_ALREADY_EXISTS, ErrorCode.USER_DID_ALREADY_EXISTS);
        MAP.put(TaSdkErrorCode.USER_NOT_ACTIVATED, ErrorCode.USER_NOT_ACTIVATED);
        MAP.put(TaSdkErrorCode.AUTH_NONCE_MISMATCH, ErrorCode.AUTH_NONCE_MISMATCH);
        MAP.put(TaSdkErrorCode.NONCE_GENERATION_FAILED, ErrorCode.NONCE_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.INVALID_PROOF_PURPOSE, ErrorCode.INVALID_PROOF_PURPOSE);
        MAP.put(TaSdkErrorCode.USER_NOT_DEACTIVATED, ErrorCode.USER_NOT_DEACTIVATED);
        MAP.put(TaSdkErrorCode.EMAIL_SEND_FAILED, ErrorCode.EMAIL_SEND_FAILED);
        MAP.put(TaSdkErrorCode.VC_ENCRYPTION_FAILED, ErrorCode.VC_ENCRYPTION_FAILED);
        MAP.put(TaSdkErrorCode.PUSH_TOKEN_NOT_FOUND, ErrorCode.PUSH_TOKEN_NOT_FOUND);
        MAP.put(TaSdkErrorCode.VC_ALREADY_REVOKED, ErrorCode.VC_ALREADY_REVOKED);
        MAP.put(TaSdkErrorCode.SDK_INVALID_DID, ErrorCode.INVALID_SIGNATURE);
        MAP.put(TaSdkErrorCode.SDK_INVALID_PROOF, ErrorCode.INVALID_SIGNATURE);
        MAP.put(TaSdkErrorCode.SDK_INVALID_SIGNATURE, ErrorCode.INVALID_SIGNATURE);
        MAP.put(TaSdkErrorCode.SDK_INVALID_MULTIBASE, ErrorCode.DECODING_FAILED);
        MAP.put(TaSdkErrorCode.SDK_FIELD_REQUIRED, ErrorCode.REQUEST_BODY_INVALID);
        MAP.put(TaSdkErrorCode.SDK_INVALID_PARAMETER, ErrorCode.REQUEST_BODY_INVALID);
        MAP.put(TaSdkErrorCode.SDK_INVALID_CONFIGURATION, ErrorCode.INVALID_SERVER_CONFIGURATION);
        MAP.put(TaSdkErrorCode.SDK_INVALID_TRANSACTION, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.SDK_INVALID_PROTOCOL_STEP, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.SDK_INVALID_TRANSACTION_STATUS, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.SDK_INVALID_SUB_TRANSACTION_STATUS, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.SDK_TRANSACTION_EXPIRED, ErrorCode.TRANSACTION_EXPIRED);
        MAP.put(TaSdkErrorCode.SDK_TRANSACTION_NOT_FOUND, ErrorCode.TRANSACTION_NOT_FOUND);
        MAP.put(TaSdkErrorCode.VC_ISSUANCE_TRANSACTION_NOT_FOUND, ErrorCode.TRANSACTION_NOT_FOUND);
        MAP.put(TaSdkErrorCode.VC_ISSUANCE_TRANSACTION_INVALID_STATE, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.VC_ISSUANCE_TRANSACTION_EXPIRED, ErrorCode.TRANSACTION_EXPIRED);
        MAP.put(TaSdkErrorCode.VC_ISSUANCE_INVALID_PREVIOUS_STEP, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.TRANSACTION_NOT_FOUND_FOR_RESTORE, ErrorCode.TRANSACTION_NOT_FOUND);
        MAP.put(TaSdkErrorCode.SDK_SIGNATURE_GENERATION_FAILED, ErrorCode.SIGNATURE_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_SIGNATURE_VERIFICATION_FAILED, ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_ECDH_KEY_AGREEMENT_FAILED, ErrorCode.SESSION_KEY_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_ENCRYPTION_FAILED, ErrorCode.ENCRYPTION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_DECRYPTION_FAILED, ErrorCode.DECRYPTION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_VC_ISSUANCE_FAILED, ErrorCode.VC_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.MULTIBASE_DECODE_FAILED, ErrorCode.DECODING_FAILED);
        MAP.put(TaSdkErrorCode.PROOF_MISSING, ErrorCode.INVALID_SIGNATURE);
        MAP.put(TaSdkErrorCode.INVALID_VERIFICATION_METHOD, ErrorCode.INVALID_SIGNATURE);
        MAP.put(TaSdkErrorCode.SIGNATURE_INVALID, ErrorCode.INVALID_SIGNATURE);
        MAP.put(TaSdkErrorCode.ATTESTATION_VERIFICATION_FAILED, ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        MAP.put(TaSdkErrorCode.VP_SIGNATURE_VERIFICATION_FAILED, ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        MAP.put(TaSdkErrorCode.VP_DECRYPTION_FAILED, ErrorCode.DECRYPTION_FAILED);
        MAP.put(TaSdkErrorCode.WALLET_INFO_PROOF_VERIFICATION_FAILED, ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        MAP.put(TaSdkErrorCode.CA_APP_INFO_PROOF_VERIFICATION_FAILED, ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        MAP.put(TaSdkErrorCode.SIGNED_DID_DOC_WALLET_PROOF_FAILED, ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        MAP.put(TaSdkErrorCode.SERVER_TOKEN_DATA_SIGNING_FAILED, ErrorCode.SIGNATURE_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_DID_DOCUMENT_NOT_FOUND, ErrorCode.DID_DOCUMENT_RETRIEVAL_FAILED);
        MAP.put(TaSdkErrorCode.DID_DOCUMENT_NOT_FOUND, ErrorCode.DID_DOCUMENT_RETRIEVAL_FAILED);
        MAP.put(TaSdkErrorCode.TAS_DID_DOCUMENT_NOT_FOUND, ErrorCode.DID_DOCUMENT_RETRIEVAL_FAILED);
        MAP.put(TaSdkErrorCode.SDK_DID_DOCUMENT_REGISTRATION_FAILED, ErrorCode.DID_DOCUMENT_REGISTRATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_DID_DOCUMENT_UPDATE_FAILED, ErrorCode.UPDATE_DID_DOC_FAILED);
        MAP.put(TaSdkErrorCode.SDK_VC_META_NOT_FOUND, ErrorCode.VC_META_RETRIEVAL_FAILED);
        MAP.put(TaSdkErrorCode.VC_META_FETCH_FAILED, ErrorCode.VC_META_RETRIEVAL_FAILED);
        MAP.put(TaSdkErrorCode.SDK_BLOCKCHAIN_CONNECTION_FAILED, ErrorCode.BLOCKCHAIN_INITIALIZATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_VC_REVOKED, ErrorCode.VC_ALREADY_REVOKED);
        MAP.put(TaSdkErrorCode.USER_VC_META_REGISTRATION_FAILED, ErrorCode.VC_META_REGISTRATION_FAILED);
        MAP.put(TaSdkErrorCode.VC_METADATA_REGISTRATION_FAILED, ErrorCode.VC_META_REGISTRATION_FAILED);
        MAP.put(TaSdkErrorCode.INVOKED_DID_DOC_CREATION_FAILED, ErrorCode.INVOKED_DOCUMENT_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_WALLET_CONNECTION_FAILED, ErrorCode.WALLET_CONNECTION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_WALLET_NOT_CONNECTED, ErrorCode.WALLET_CONNECTION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_WALLET_CREATION_FAILED, ErrorCode.WALLET_CREATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_KEY_NOT_FOUND, ErrorCode.FAILED_TO_LOAD_KEY_ELEMENT);
        MAP.put(TaSdkErrorCode.SDK_KEY_GENERATION_FAILED, ErrorCode.KEY_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.SDK_USER_NOT_FOUND, ErrorCode.USER_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.USER_NOT_FOUND, ErrorCode.USER_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.USER_NOT_FOUND_FOR_RESTORE, ErrorCode.USER_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.HOLDER_NOT_FOUND, ErrorCode.USER_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.SDK_ENTITY_NOT_FOUND, ErrorCode.ENTITY_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.SDK_WALLET_INFO_NOT_FOUND, ErrorCode.WALLET_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.SDK_ECDH_SESSION_NOT_FOUND, ErrorCode.ECDH_NOT_FOUND);
        MAP.put(TaSdkErrorCode.SDK_TOKEN_NOT_FOUND, ErrorCode.TOKEN_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.SDK_VC_SCHEMA_NOT_FOUND, ErrorCode.VC_SCHEMA_NOT_FOUND);
        MAP.put(TaSdkErrorCode.ISSUER_NOT_FOUND, ErrorCode.ISSUER_INFO_NOT_FOUND);
        MAP.put(TaSdkErrorCode.VC_PLAN_NOT_FOUND, ErrorCode.VC_PLAN_RETRIEVAL_FAILED);
        MAP.put(TaSdkErrorCode.VC_PLAN_INVALID, ErrorCode.VC_PLAN_RETRIEVAL_FAILED);
        MAP.put(TaSdkErrorCode.ISSUE_PROFILE_RETRIEVAL_FAILED, ErrorCode.FAIL_TO_REQUEST_ISSUE_PROFILE);
        MAP.put(TaSdkErrorCode.VC_SCHEMA_VALIDATION_FAILED, ErrorCode.INVALID_VC_SCHEMA);
        MAP.put(TaSdkErrorCode.OFFER_NOT_FOUND, ErrorCode.DID_OFFER_NOT_FOUND);
        MAP.put(TaSdkErrorCode.CERTIFICATE_VC_GENERATION_FAILED, ErrorCode.VC_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.CERTIFICATE_VC_SIGNING_FAILED, ErrorCode.SIGNATURE_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.CERTIFICATE_VC_PUBLISHING_FAILED, ErrorCode.FAILED_TO_REGISTER_CERTIFICATE_VC);
        MAP.put(TaSdkErrorCode.TAS_ENROLLMENT_FAILED, ErrorCode.FAILED_API_PROPOSE_ENROLL_TAS);
        MAP.put(TaSdkErrorCode.TAS_PASSWORD_NOT_CONFIGURED, ErrorCode.TAS_PASSWORD_NOT_FOUND);
        MAP.put(TaSdkErrorCode.ENTITY_NOT_COMPLETED, ErrorCode.ENTITY_REGISTRATION_INCOMPLETE);
        MAP.put(TaSdkErrorCode.PROVIDER_DID_MISMATCH, ErrorCode.MISMATCH_PROVIDER_DID);
        MAP.put(TaSdkErrorCode.DID_DOCUMENT_KEY_VERIFICATION_FAILED, ErrorCode.VERIFY_DIDDOC_KEY_PROOF_FAILED);
        MAP.put(TaSdkErrorCode.OWNER_DID_DOC_KEY_PROOF_FAILED, ErrorCode.VERIFY_DIDDOC_KEY_PROOF_FAILED);
        MAP.put(TaSdkErrorCode.DID_DOC_KEY_VERIFICATION_FAILED, ErrorCode.VERIFY_DIDDOC_KEY_PROOF_FAILED);
        MAP.put(TaSdkErrorCode.DID_DOCUMENT_PARSING_FAILED, ErrorCode.PARSE_DIDDOC_FAILED);
        MAP.put(TaSdkErrorCode.INVALID_DID_DOC_VERSION, ErrorCode.INVALID_DIDDOC_VERSION);
        MAP.put(TaSdkErrorCode.INVALID_DID_DOC_UPDATED, ErrorCode.INVALID_DIDDOC_UPDATED);
        MAP.put(TaSdkErrorCode.INVALID_DID_DOC_CONTEXT, ErrorCode.INVALID_DIDDOC_CONTEXT);
        MAP.put(TaSdkErrorCode.INVALID_DID_DOC_ID, ErrorCode.INVALID_DIDDOC_ID);
        MAP.put(TaSdkErrorCode.INVALID_DID_DOC_CONTROLLER, ErrorCode.INVALID_DIDDOC_CONTROLLER);
        MAP.put(TaSdkErrorCode.INVALID_DID_DOC_CREATED, ErrorCode.INVALID_DIDDOC_CREATED);
        MAP.put(TaSdkErrorCode.INVALID_DID_DOC_DEACTIVATED, ErrorCode.INVALID_DIDDOC_DEACTIVATED);
        MAP.put(TaSdkErrorCode.DID_MISMATCH, ErrorCode.DID_DOCUMENT_ID_MISMATCH);
        MAP.put(TaSdkErrorCode.PUBLIC_KEY_NOT_FOUND, ErrorCode.GET_DIDDOC_PUBLIC_KEY_FAILED);
        MAP.put(TaSdkErrorCode.SERVER_TOKEN_INVALID, ErrorCode.INVALID_TOKEN);
        MAP.put(TaSdkErrorCode.SERVER_TOKEN_VALIDATION_FAILED, ErrorCode.INVALID_TOKEN);
        MAP.put(TaSdkErrorCode.KYC_DATA_RETRIEVAL_FAILED, ErrorCode.KYC_COMMUNICATION_ERROR);
        MAP.put(TaSdkErrorCode.KYC_TOKEN_AND_TXID_BOTH_MISSING, ErrorCode.KYC_COMMUNICATION_ERROR);
        MAP.put(TaSdkErrorCode.USER_PII_ALREADY_EXISTS, ErrorCode.USER_DID_ALREADY_EXISTS);
        MAP.put(TaSdkErrorCode.ISSUER_COMMUNICATION_FAILED, ErrorCode.ISSUER_COMMUNICATION_ERROR);
        MAP.put(TaSdkErrorCode.VC_ISSUANCE_FAILED, ErrorCode.VC_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.USER_VC_GENERATION_FAILED, ErrorCode.VC_GENERATION_FAILED);
        MAP.put(TaSdkErrorCode.VC_ID_MISMATCH, ErrorCode.VC_ID_NOT_MATCH);
        MAP.put(TaSdkErrorCode.VC_EXPIRED, ErrorCode.VC_VERIFICATION_FAILED);
        MAP.put(TaSdkErrorCode.VC_STATUS_INVALID, ErrorCode.VC_STATUS_UPDATE_FAILED);
        MAP.put(TaSdkErrorCode.ISSUER_NONCE_MISMATCH, ErrorCode.AUTH_NONCE_MISMATCH);
        MAP.put(TaSdkErrorCode.PROPOSE_REVOKE_VC_FAILED, ErrorCode.FAIL_TO_PROPOSE_REVOKE_VC);
        MAP.put(TaSdkErrorCode.REQUEST_REVOKE_VC_FAILED, ErrorCode.FAIL_TO_REQUEST_REVOKE_VC);
        MAP.put(TaSdkErrorCode.CONFIRM_REVOKE_VC_FAILED, ErrorCode.FAIL_TO_CONFIRM_REVOKE_VC);
        MAP.put(TaSdkErrorCode.PROPOSE_UPDATE_DIDDOC_FAILED, ErrorCode.FAILED_API_PROPOSE_UPDATE_DIDDOC);
        MAP.put(TaSdkErrorCode.REQUEST_UPDATE_DIDDOC_FAILED, ErrorCode.FAILED_API_REQUEST_UPDATE_DIDDOC);
        MAP.put(TaSdkErrorCode.CONFIRM_UPDATE_DIDDOC_FAILED, ErrorCode.FAILED_API_CONFIRM_UPDATE_DIDDOC);
        MAP.put(TaSdkErrorCode.PROPOSE_RESTORE_DIDDOC_FAILED, ErrorCode.FAILED_API_PROPOSE_RESTORE_DIDDOC);
        MAP.put(TaSdkErrorCode.REQUEST_ISSUE_OFFER_RESTORE_DIDDOC_FAILED, ErrorCode.FAILED_API_OFFER_RESTORE_DID_PUSH);
        MAP.put(TaSdkErrorCode.VERIFY_OFFER_RESTORE_DIDDOC_FAILED, ErrorCode.FAILED_API_REQUEST_RESTORE_DIDDOC);
        MAP.put(TaSdkErrorCode.REQUEST_RESTORE_DIDDOC_FAILED, ErrorCode.FAILED_API_REQUEST_RESTORE_DIDDOC);
        MAP.put(TaSdkErrorCode.CONFIRM_RESTORE_DIDDOC_FAILED, ErrorCode.FAILED_API_CONFIRM_RESTORE_DIDDOC);
        MAP.put(TaSdkErrorCode.OFFER_CREATION_FAILED, ErrorCode.DID_OFFER_SAVE_FAILED);
        MAP.put(TaSdkErrorCode.PUSH_SEND_FAILED, ErrorCode.FCM_SEND_FAILED);
        MAP.put(TaSdkErrorCode.OFFER_CODE_MISMATCH, ErrorCode.MISMATCH_OFFER_DID);
        MAP.put(TaSdkErrorCode.OFFER_EXPIRED, ErrorCode.TRANSACTION_EXPIRED);
        MAP.put(TaSdkErrorCode.OFFER_ALREADY_USED, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.DEACTIVATED_FLAG_NOT_FALSE, ErrorCode.INVALID_DIDDOC_DEACTIVATED);
        MAP.put(TaSdkErrorCode.OFFER_TRANSACTION_MISMATCH, ErrorCode.TRANSACTION_INVALID);
        MAP.put(TaSdkErrorCode.INVALID_OFFER_TYPE, ErrorCode.REQUEST_BODY_INVALID);
        MAP.put(TaSdkErrorCode.OFFER_SEND_FAILED, ErrorCode.FCM_SEND_FAILED);
        MAP.put(TaSdkErrorCode.SDK_NOT_INITIALIZED, ErrorCode.UNKNOWN_SERVER_ERROR);
        MAP.put(TaSdkErrorCode.SDK_UNKNOWN_ERROR, ErrorCode.UNKNOWN_SERVER_ERROR);
    }

    private TaSdkErrorMapper() {
    }

    public static ErrorCode toErrorCode(TaSdkErrorCode sdkErrorCode) {
        return MAP.getOrDefault(sdkErrorCode, ErrorCode.UNKNOWN_SERVER_ERROR);
    }

    public static OpenDidException toOpenDidException(TaSdkException e) {
        return toOpenDidException(e, ErrorCode.UNKNOWN_SERVER_ERROR);
    }

    /**
     * Converts an SDK error, keeping the original server error when the SDK only passed it through.
     *
     * @param e       SDK exception
     * @param unknown code for an unexpected SDK failure, normally the generic code of the failing API
     * @return server exception to return to the client
     */
    public static OpenDidException toOpenDidException(TaSdkException e, ErrorCode unknown) {
        if (e instanceof ProviderOpenDidException preserved) {
            return preserved.getOriginal();
        }
        log.error("TA SDK error - code: {}, message: {}", e.getErrorCode().getCode(), e.getMessage());
        ErrorCode errorCode = toErrorCode(e.getErrorCode());
        return new OpenDidException(errorCode == ErrorCode.UNKNOWN_SERVER_ERROR ? unknown : errorCode);
    }
}
