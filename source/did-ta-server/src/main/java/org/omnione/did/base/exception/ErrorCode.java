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

/**
 * Enumeration of error codes used in the TAS server.
 * Each error code contains a unique identifier, a descriptive message, and an associated HTTP status code.
 */
public enum ErrorCode {

    // 1. General errors (10000 ~ 10999)
    ENCODING_FAILED("SSRVTRA10000", "Failed to encode data.", 500),
    DECODING_FAILED("SSRVTRA10001", "Failed to decode data: incorrect encoding.", 400),
    HASH_GENERATION_FAILED("SSRVTRA10002", "Failed to generate hash value.", 500),
    NONCE_MERGE_FAILED("SSRVTRA10003", "Failed to merge nonce.", 500),
    ENCRYPTION_FAILED("SSRVTRA10004", "Failed to encrypt data.", 500),
    DECRYPTION_FAILED("SSRVTRA10005", "Failed to decrypt data.", 500),
    JSON_PROCESSING_ERROR("SSRVTRA10006", "Error occurred while processing JSON data.", 400),


    // 2. DB-related errors (11000 ~ 11499)
    DID_OFFER_SAVE_FAILED("SSRVTRA11000", "Failed to save DID offer.", 500),
    DID_OFFER_NOT_FOUND("SSRVTRA11001", "Failed to find DID offer.", 400),
    PUSH_TOKEN_UPDATE_FAILED("SSRVTRA11002", "Failed to update push token.", 500),
    DB_ERROR_ON_VC_SCHEMA_SAVE("SSRVTRA11003", "Failed to save VC schema.", 500),

    // 3. Error during API processing (12000 ~ 12999)
    PARSE_VC_SCHEMA_FAILED("SSRVTRA12000", "Failed to parse VC Schema.", 500),
    INVALID_ROLE_TYPE("SSRVTRA12001", "Invalid role type provided.", 500),
    GET_VERIFICATION_METHOD_FAILED("SSRVTRA12002", "Failed to retrieve verification method.", 500),
    PUSH_DATA_GENERATION_FAILED("SSRVTRA12003", "Failed to generate push data.", 500),
    REQUEST_BODY_INVALID("SSRVTRA12004", "Failed to process the request: invalid request body.", 400),
    UNSUPPORTED_PURPOSE("SSRVTRA12005", "Unsupported token purpose provided.", 400),
    PARSE_DIDDOC_FAILED("SSRVTRA12006", "Failed to parse DID Document.", 400),
    MISMATCH_PROVIDER_DID("SSRVTRA12007", "Provider DID mismatch.", 400),
    NO_MATCHING_CIPHER_TYPE("SSRVTRA12008", "Unsupported Cipher Type.", 400),
    INVALID_DIDDOC_VERSION("SSRVTRA12009", "Invalid DID Document version.", 400),
    PASSWORD_MISMATCH("SSRVTRA12010", "Failed to authenticate: password is incorrect.", 400),
    MISMATCH_OFFER_DID("SSRVTRA12011", "The requested DID does not match the DID in the Offer.", 400),
    FAILED_TO_FIND_ALLOWED_CA("SSRVTRA12012", "Failed to find allowed CA list", 400),


    // 03. TAS-related errors (13000 ~ 13499)
    TAS_INFO_NOT_FOUND("SSRVTRA13001", "TAS is not registered.", 500),
    TAS_CERTIFICATE_DATA_NOT_FOUND("SSRVTRA13002", "Tas Certificate VC data not found.", 500),
    TAS_ALREADY_REGISTERED("SSRVTRA13003", "Failed to register TAS: TAS is already registered.", 400),
    TAS_NOT_REGISTERED("SSRVTRA13004", "Failed to find TAS: TAS is not registered.", 500),
    INVALID_TAS_DIDDOC("SSRVTRA13005", "Failed to process TAS DID Document: invalid document.", 400),
    FAILED_API_PROPOSE_ENROLL_TAS("SSRVTRA13006", "Failed to process the 'propose-enroll-tas' API request.", 500),
    FAILED_API_GET_CERTIFICATE_VC("SSRVTRA13007", "Failed to process the 'get-certificate-vc' API request.", 500),
    TAS_PASSWORD_NOT_FOUND("SSRVTRA13008", "Failed to find TAS password.", 500),
    TA_SECRET_NOT_MATCHED("SSRVTRA13009", "Failed to process request: TA secret does not match.", 400),
    TAS_DID_DOCUMENT_ALREADY_REGISTERED("SSRVTRA13010", "Failed to register TAS DID Document: document is already registered.", 400),


    // 04. TAS configuration-related errors (13500 ~ 13999)
    EMAIL_TEMPLATE_READ_FAILED("SSRVTRA13500", "Failed to read email template.", 400),
    FCM_SEND_FAILED("SSRVTRA13501", "Failed to send FCM message.", 500),
    QR_IMAGE_CONVERT_FAILED("SSRVTRA13502", "Failed to convert QR image.", 500),
    MAIL_CONFIGURATION_FAILED("SSRVTRA13503", "Failed to configure mail settings.", 500),
    INVALID_SERVER_CONFIGURATION("SSRVTRA13504", "Failed to initialize server: invalid configuration.", 500),
    ALLOWED_CA_RETRIEVAL_FAILED("SSRVTRA13505", "Failed to retrieve allowed CAs.", 400),
    INVALID_PUSH_TYPE("SSRVTRA13506", "Failed to process push notification: invalid push type.", 400),
    INVALID_QR_TYPE("SSRVTRA13507", "Failed to process QR code: invalid QR type.", 400),
    INVALID_ECC_CURVE_TYPE("SSRVTRA13508", "Failed to process ECC curve: invalid curve type.", 500),
    INVALID_SYMMETRIC_CIPHER_TYPE("SSRVTRA13509", "Failed to process encryption: invalid symmetric cipher type.", 500),
    INVALID_SYMMETRIC_PADDING_TYPE("SSRVTRA13510", "Failed to process encryption: invalid symmetric padding type.", 500),
    FAILED_TO_REGISTER_TA_CERTIFICATE("SSRVTRA13511", "Failed to register TA certificate.", 500),
    FAILED_TO_REGISTER_TA_DID_DOCUMENT("SSRVTRA13512", "Failed to register TA DID Document.", 500),
    TA_ALREADY_REGISTERED("SSRVTRA13513", "TA is already registered.", 400),


    // 05. Entity-related errors (14000 ~ 14499)
    ENTITY_INFO_NOT_FOUND("SSRVTRA14000", "Failed to find entity: entity is not registered.", 500),
    ISSUER_INFO_NOT_FOUND("SSRVTRA14001", "Failed to find issuer: issuer is not registered.", 400),
    ISSUER_REGISTRATION_INCOMPLETE("SSRVTRA14002", "The issuer has not completed registration.", 400),
    ENTITY_REGISTRATION_INCOMPLETE("SSRVTRA14003", "The entity has not completed registration.", 500),
    ENTITY_ALREADY_REGISTERED("SSRVTRA14004", "Failed to register entity: entity is already registered.", 400),
    FAIL_TO_PROPOSE_ENROLL_ENTITY("SSRVTRA14005", "Failed to process the 'propose-enroll-entity' API request.", 500),
    ENROLL_REQUEST_ENTITY_DID_MISMATCH("SSRVTRA14006", "The provided DID does not match the entity that requested registration.", 400),
    FAIL_TO_REQUEST_ENROLL_ENTITY("SSRVTRA14007", "Failed to process the 'request-enroll-entity' API request.", 500),
    FAILED_TO_REGISTER_QUICK_ENTITY("SSRVTRA14008", "Failed to register quick entity.", 500),
    ENTITY_NAME_ALREADY_REGISTERED("SSRVTRA14009", "Failed to register entity: entity name is already registered.", 400),
    ENTITY_DID_DOCUMENT_NOT_FOUND("SSRVTRA14010", "Failed to find entity DID Document: o registration request has been made.", 400),
    FAILED_TO_REGISTER_DID_FROM_ENTITY("SSRVTRA14011", "Failed to register DID from entity.", 500),
    FAILED_TO_APPROVE_ENTITY_DID_DOCUMENT("SSRVTRA14012", "Failed to approve entity DID Document.", 500),
    ENTITY_NOT_DELETABLE("SSRVTRA14013", "Failed to delete entity: entity is not deletable.", 400),


    // 06. Blockchain-related errors (15000 ~ 15499)
    BLOCKCHAIN_INITIALIZATION_FAILED("SSRVTRA15000", "Failed to initialize blockchain.", 500),
    BLOCKCHAIN_DIDDOC_REGISTRATION_FAILED("SSRVTRA15001", "Failed to register DID Document on the blockchain.", 500),
    BLOCKCHAIN_GET_DID_DOC_FAILED("SSRVTRA15002", "Failed to retrieve DID document on the blockchain.", 500),
    BLOCKCHAIN_UPDATE_DID_DOC_FAILED("SSRVTRA15003", "Failed to update DID Document on the blockchain.", 500),
    BLOCKCHAIN_VC_META_REGISTRATION_FAILED("SSRVTRA15004", "Failed to register VC meta on the blockchain.", 500),
    BLOCKCHAIN_VC_META_RETRIEVAL_FAILED("SSRVTRA15005", "Failed to retrieve VC meta on the blockchain.", 500),
    BLOCKCHAIN_VC_STATUS_UPDATE_FAILED("SSRVTRA18507", "Failed to update VC status on the blockchain.", 500),
    BLOCKCHAIN_REMOVE_INDEX_FAILED("SSRVTRA18508", "Failed to remove index on the blockchain", 500),
    BLOCKCHAIN_VC_SCHEMA_REGISTRATION_FAILED("SSRVTRA18509", "Failed to register VC schema on the blockchain.", 500),
    BLOCKCHAIN_VC_SCHEMA_RETRIEVAL_FAILED("SSRVTRA18510", "Failed to retrieve VC schema on the blockchain.", 500),


    // 07. External server integration errors (15500 ~ 15999)
    DID_DOC_REGISTRATION_FAILED("SSRVTRA15500", "Failed to register DID document.", 500),
    ISSUER_UNKNOWN_RESPONSE("SSRVTRA15501", "Failed to process response: received unknown data from the issuer.", 500),
    ISSUER_COMMUNICATION_ERROR("SSRVTRA15502", "Failed to communicate with issuer: unknown error occurred.", 500),
    ISSUER_INVALID_MESSAGE("SSRVTRA15503", "Failed to process message: received an invalid message from the issuer.", 500),
    EMAIL_SEND_FAILED("SSRVTRA15504", "Failed to send email.", 500),
    KYC_COMMUNICATION_ERROR("SSRVTRA15505", "Failed to communicate with KYC server: unknown error occurred.", 500),
    URL_PING_ERROR("SSRVTRA15506", "Failed to ping the URL.", 400),


    // 08. Transaction-related errors (16000 ~ 16499)
    TRANSACTION_NOT_FOUND("SSRVTRA16000", "Failed to find transaction: the transaction does not exist.", 400),
    TRANSACTION_INVALID("SSRVTRA16001", "Failed to process transaction: the transaction is not valid.", 400),
    TRANSACTION_EXPIRED("SSRVTRA16002", "Failed to process transaction: the transaction has expired.", 400),


    // 09. ECDH or Proof-related errors (16500 ~ 16999)
    KEY_GENERATION_FAILED("SSRVTRA16500", "Failed to generate key.", 500),
    ECDH_NOT_FOUND("SSRVTRA16501", "Failed to find ECDH information.", 500),
    PUBLIC_KEY_UNCOMPRESS_FAILED("SSRVTRA16502", "Failed to uncompress public key.", 500),
    PUBLIC_KEY_COMPRESS_FAILED("SSRVTRA16503", "Failed to compress public key.", 500),
    NONCE_GENERATION_FAILED("SSRVTRA16504", "Failed to generate nonce.", 500),
    KEY_PAIR_GENERATION_FAILED("SSRVTRA16505", "Failed to generate key pair.", 500),
    SESSION_KEY_GENERATION_FAILED("SSRVTRA16506", "Failed to generate session key.", 500),
    NONCE_AND_SHARED_SECRET_MERGE_FAILED("SSRVTRA16507", "Failed to merge nonce and shared secret.", 500),
    INITIAL_VECTOR_GENERATION_FAILED("SSRVTRA16508", "Failed to generate initial vector.", 500),
    DID_AUTH_VERIFICATION_FAILED("SSRVTRA16509", "Failed to verify DID Auth.", 400), // 메시지 수정
    INVALID_SIGNATURE("SSRVTRA16510", "Failed to verify signature: the signature is invalid.", 400),
    SIGNATURE_VERIFICATION_FAILED("SSRVTRA16511", "Failed to verify signature.", 400),
    SIGNATURE_GENERATION_FAILED("SSRVTRA16512", "Failed to generate signature.", 500),
    INVALID_PROOF_PURPOSE("SSRVTRA16513", "Failed to process proof: invalid purpose.", 400),
    SIGNATURE_RESPONSE_FAILED("SSRVTRA16514", "Failed to sign response data.", 500),
    RESPONSE_SIGNATURE_FAILED("SSRVTRA16515", "Failed to sign response data.", 500),
    VERIFY_DIDDOC_KEY_PROOF_FAILED("SSRVTRA16516", "Failed to verify DID document key proof.", 400),
    ADD_DIDDOC_KEY_PROOF_FAILED("SSRVTRA16517", "Failed to add key proof to DID document.", 500),
    EXTRACT_SIGNATURE_MESSAGE_FAILED("SSRVTRA16518", "Failed to extract signature message.", 500),
    INVALID_CLIENT_NONCE("SSRVTRA16519", "Failed to process client nonce: invalid nonce.", 400),
    AUTH_NONCE_MISMATCH("SSRVTRA16520", "'authNonce' does not match.", 400),
    FAIL_TO_REQUEST_ECDH("SSRVTRA16521", "Failed to process the 'request-ecdh' API request.", 500),


    // 10. User-related errors (17000 ~ 17499)
    USER_DID_ALREADY_EXISTS("SSRVTRA17000", "Failed to register user DID: user DID already exists.", 400),
    USER_DID_NOT_FOUND("SSRVTRA17001", "Failed to find user DID: user DID not found.", 400),
    USER_INFO_NOT_FOUND("SSRVTRA17002", "Failed to find user: user is not registered.", 400),
    USER_NOT_ACTIVATED("SSRVTRA17003", "Failed to process request: user status is not 'Activated'.", 400),
    APP_INFO_NOT_FOUND("SSRVTRA17004", "Failed to find app: app is not registered.", 400),
    APP_ID_MISMATCH("SSRVTRA17005", "Failed to authenticate app: app ID does not match.", 400),
    INVALID_APP_ID("SSRVTRA17006", "Failed to authenticate app: invalid app ID.", 400),
    PUSH_TOKEN_NOT_FOUND("SSRVTRA17007", "Failed to find push token.", 400),
    FAILED_API_PROPOSE_REGISTER_USER("SSRVTRA17008", "Failed to process the 'propose-register-user' API request.", 500),
    FAILED_API_REQUEST_REGISTER_USER("SSRVTRA17009", "Failed to process the 'request-register-user' API request.", 500),
    FAILED_API_REQUEST_CONFIRM_USER("SSRVTRA17010", "Failed to process the 'request-confirm-user' API request.", 500),
    FAILED_API_PROPOSE_UPDATE_DIDDOC("SSRVTRA17011", "Failed to process the 'propose-update-diddoc' API request.", 500),
    FAILED_API_REQUEST_UPDATE_DIDDOC("SSRVTRA17012", "Failed to process the 'request-update-diddoc' API request.", 500),
    FAILED_API_CONFIRM_UPDATE_DIDDOC("SSRVTRA17013", "Failed to process the 'confirm-update-diddoc' API request.", 500),
    USER_NOT_DEACTIVATED("SSRVTRA17014", "Failed to process request: user status is not 'Deactivated'.", 400),
    FAILED_API_PROPOSE_RESTORE_DIDDOC("SSRVTRA17015", "Failed to process the 'propose-restore-diddoc' API request.", 500),
    FAILED_API_REQUEST_RESTORE_DIDDOC("SSRVTRA17016", "Failed to process the 'request-restore-diddoc' API request.", 500),
    FAILED_API_CONFIRM_RESTORE_DIDDOC("SSRVTRA17017", "Failed to process the 'confirm-restore-diddoc' API request.", 500),
    FAILED_API_RETRIEVE_KYC("SSRVTRA17018", "Failed to process the 'retrieve-kyc' API request.", 500),
    FAILED_API_OFFER_RESTORE_DID_PUSH("SSRVTRA17019", "Failed to process the 'offer-restore-did-push' API request.", 500),
    FAILED_API_OFFER_RESTORE_DID_EMAIL("SSRVTRA20020", "Failed to process the 'offer-restore-did-email' API request.", 500),
    FAILED_API_UPDATE_PUSH_TOKEN("SSRVTRA20021", "Failed to process the 'update-push-token' API request.", 500),
    FAILED_API_UPDATE_DIDDOC_DEACTIVATED("SSRVTRA20022", "Failed to process the 'update-diddoc-deactivated' API request.", 500),
    FAILED_API_UPDATE_DIDDOC_REVOKED("SSRVTRA20023", "Failed to process the 'update-diddoc-revoked' API request.", 500),


    // 11. Wallet-related errors (17500 ~ 17999)
    WALLET_PROVIDER_NOT_REGISTERED("SSRVTRA17500", "Wallet Provider has not registered.", 500),
    WALLET_ID_ALREADY_EXISTS("SSRVTRA17501", "Wallet ID already exists.", 400),
    WALLET_INFO_NOT_FOUND("SSRVTRA17502", "Failed to find wallet: wallet is not registered.", 400),
    WALLET_CREATION_FAILED("SSRVTRA17503", "Failed to create wallet.", 500),
    WALLET_CONNECT_FAILED("SSRVTRA17504", "Failed to connect to wallet.", 500),
    WALLET_PASSWORD_CHANGE_FAILED("SSRVTRA17505", "Failed to change wallet password.", 500),
    WALLET_SIGNATURE_GENERATION_FAILED("SSRVTRA17506", "Failed to generate wallet signature.", 500),
    WALLET_CONNECTION_FAILED("SSRVTRA17507", "Failed to establish wallet connection.", 500),
    WALLET_ID_MISMATCH("SSRVTRA17508", "Failed to authenticate wallet: wallet ID does not match.", 400),
    FAILED_TO_GET_FILE_WALLET_MANAGER("SSRVTRA17509", "Failed to get File wallet manager", 500),
    FAILED_API_REQUEST_REGISTER_WALLET("SSRVTRA17510", "Failed to process the 'request-register-wallet' API request.", 500),
    WALLET_ALREADY_EXISTS("SSRVTRA17511", "Failed to create wallet: wallet already exists.", 500),
    INVALID_WALLET_FILE_PATH("SSRVTRA17512", "Failed to create wallet: invalid wallet file path.", 500),
    KEY_ALREADY_EXISTS("SSRVTRA17513", "Failed to generate keys: key already exists.", 500),
    FAILED_TO_LOAD_KEY_ELEMENT("SSRVTRA17514", "Failed to load key element.", 500),

    // 12. DID Document-related errors (18000 ~ 18499)
    DID_DOCUMENT_RETRIEVAL_FAILED("SSRVTRA18000", "Failed to retrieve DID Document.", 500),
    ENTITY_DIDDOC_REGISTRATION_REQUIRED("SSRVTRA18001", "Entity DID Document registration is required.", 400),
    DIDDOC_GENERATION_FAILED("SSRVTRA18003", "Failed to generate DID document.", 500),
    DIDDOC_SAVE_FAILED("SSRVTRA18004", "Failed to save DID document.", 500),
    GET_DIDDOC_PUBLIC_KEY_FAILED("SSRVTRA18005", "Failed to retrieve DID document public key.", 500),
    INVALID_DIDDOC_UPDATED("SSRVTRA18006", "Failed to process DID document: invalid updated time.", 400),
    INVALID_DIDDOC_CONTEXT("SSRVTRA18007", "Failed to process DID document: invalid context.", 400),
    INVALID_DIDDOC_ID("SSRVTRA18008", "Failed to process DID document: invalid document ID.", 400),
    INVALID_DIDDOC_CONTROLLER("SSRVTRA18009", "Failed to process DID document: invalid controller.", 400),
    FIND_DID_DOC_FAILED("SSRVTRA18010", "Failed to find DID Document.", 500),
    UPDATE_DID_DOC_FAILED("SSRVTRA18012", "Failed to update DID Document.", 500),
    DELETE_DID_DOC_FAILED("SSRVTRA18013", "Failed to delete DID Document.", 500),
    INVALID_DIDDOC_CREATED("SSRVTRA18014", "Failed to process DID document: invalid creation time.", 400),
    INVALID_DIDDOC_DEACTIVATED("SSRVTRA18015", "Failed to process DID document: invalid deactivated.", 400),
    DID_DOCUMENT_REGISTRATION_FAILED("SSRVTRA18016", "Failed to register DID Document.", 500),
    INVOKED_DOCUMENT_GENERATION_FAILED("SSRVTRA18017", "Failed to generate Invoked Document.", 500),
    DID_DOCUMENT_ID_MISMATCH("SSRVTRA18018", "Failed to process request: ID of DID Document does not match the previously requested DID.", 400),
    INVALID_DID_DOCUMENT("SSRVTRA18019", "Failed to process DID Document: invalid document.", 400),
    DID_ALREADY_REGISTERED("SSRVTRA18020", "Failed to register DID: DID is already registered.", 400),

    // 13. VC-related errors (18500 ~ 18999)
    VC_ID_NOT_MATCH("SSRVTRA18500", "VC ID does not match.", 400),
    VC_PLAN_RETRIEVAL_FAILED("SSRVTRA18501", "Failed to retrieve VC plan.", 400),
    VC_CATEGORY_RETRIEVAL_FAILED("SSRVTRA18502", "Failed to retrieve VC categories.", 500),
    VC_SCHEMA_RETRIEVAL_FAILED("SSRVTRA18503", "Failed to retrieve VC schema.", 500),
    VC_META_GENERATION_FAILED("SSRVTRA18504", "Failed to generate VC meta.", 500),
    VC_META_REGISTRATION_FAILED("SSRVTRA18505", "Failed to register VC meta.", 500),
    VC_META_RETRIEVAL_FAILED("SSRVTRA18506", "Failed to retrieve VC meta.", 500),
    VC_STATUS_UPDATE_FAILED("SSRVTRA18507", "Failed to update VC status.", 500),
    VC_ORIGIN_DATA_EXTRACTION_FAILED("SSRVTRA18508", "Failed to extract VC origin data.", 500),
    VC_PROOF_SETTING_FAILED("SSRVTRA18509", "Failed to set VC proof.", 500),
    CERTIFICATE_VC_NOT_FOUND("SSRVTRA18510", "Failed to find certificate VC.", 400),
    INVALID_CERTIFICATE_VC_ISSUER("SSRVTRA18511", "Invalid certificate VC issuer.", 400),
    CLAIM_INFO_SETTING_FAILED("SSRVTRA18512", "Failed to set claim info.", 500),
    VC_TYPE_SETTING_FAILED("SSRVTRA18513", "Failed to set VC type.", 500),
    VC_ENCRYPTION_FAILED("SSRVTRA18514", "Failed to encrypt VC data.", 500),
    VC_GENERATION_FAILED("SSRVTRA18515", "Failed to generate VC.", 500),
    FIND_VC_META_FAILED("SSRVTRA18516", "Failed to find VC meta.", 500),
    PARSE_VC_META_FAILED("SSRVTRA18517", "Failed to parse VC meta.", 500),
    VC_ALREADY_REVOKED("SSRVTRA18518", "Failed to revoke VC: VC is already revoked.", 400),
    VC_VERIFICATION_FAILED("SSRVTRA18519", "Failed to verify VC.", 400),
    FAIL_TO_PROPOSE_ISSUE_VC("SSRVTRA18520", "Failed to process the 'propose-issue-vc' API request.", 500),
    FAIL_TO_REQUEST_ISSUE_PROFILE("SSRVTRA18521", "Failed to process the 'request-issue-profile' API request.", 500),
    FAIL_TO_REQUEST_ISSUE_VC("SSRVTRA18522", "Failed to process the 'request-issue-vc' API request.", 500),
    FAIL_TO_CONFIRM_ISSUE_VC("SSRVTRA18523", "Failed to process the 'confirm-issue-vc' API request.", 500),
    FAIL_TO_PROPOSE_REVOKE_VC("SSRVTRA18524", "Failed to process the 'propose-revoke-vc' API request.", 500),
    FAIL_TO_REQUEST_REVOKE_VC("SSRVTRA18525", "Failed to process the 'request-revoke-vc' API request.", 500),
    FAIL_TO_CONFIRM_REVOKE_VC("SSRVTRA18526", "Failed to process the 'confirm-revoke-vc' API request.", 500),
    FAIL_TO_OFFER_ISSUE_VC_QR("SSRVTRA18527", "Failed to process the 'offer-issue-vc-qr' API request.", 500),
    FAIL_TO_OFFER_ISSUE_VC_PUSH("SSRVTRA18528", "Failed to process the 'offer-issue-vc-push' API request.", 500),
    FAIL_TO_OFFER_ISSUE_VC_EMAIL("SSRVTRA18529", "Failed to process the 'offer-issue-vc-email' API request.", 500),
    FAIL_TO_GET_VC_SCHEMA("SSRVTRA18530", "Failed to process the 'get-vc-schema' API request.", 500),
    FAILED_TO_REGISTER_VC_SCHEMA_FROM_ISSUER("SSRVTRA18531", "Failed to register VC schema from issuer.", 500),
    FAILED_TO_REGISTER_CERTIFICATE_VC_SCHEMA("SSRVTRA18532", "Failed to register certificate VC schema.", 500),
    FAILED_TO_REGISTER_VC_PLAN_FROM_ISSUER("SSRVTRA18533", "Failed to register VC plan from issuer.", 500),
    INVALID_VC_SCHEMA("SSRVTRA18534", "Failed to process VC schema: invalid schema.", 400),
    INVALID_CERTIFICATE_VC_JSON_FORMAT("SSRVTRA18535", "Failed to process certificate VC: invalid JSON format.", 500),
    CREDENTIAL_SCHEMA_RETRIEVAL_FAILED("SSRVTRA18536", "Failed to retrieve Credential Schema.", 400),
    CREDENTIAL_DEFINITION_RETRIEVAL_FAILED("SSRVTRA18536", "Failed to retrieve Credential Definition.", 400),
    VC_SCHEMA_REGISTRATION_FAILED("SSRVTRA18537", "Failed to register VC schema.", 500),
    VC_SCHEMA_NOT_FOUND("SSRVTRA18538", "Failed to find VC schema", 400),


    // 14. Token-related errors (19000 ~ 19499)
    SERVER_TOKEN_GENERATION_FAILED("SSRVTRA19000", "Failed to generate server token.", 500),
    SERVER_TOKEN_ENCRYPTION_FAILED("SSRVTRA19001", "Failed to encrypt server token data.", 500),
    TOKEN_EXPIRED("SSRVTRA19002", "Failed to process token: the token has expired.", 400),
    INVALID_TOKEN("SSRVTRA19003", "Failed to authenticate: the token provided is invalid.", 400),
    TOKEN_INFO_NOT_FOUND("SSRVTRA19004", "Failed to find token: token is not registered.", 400),
    FAIL_TO_REQUEST_CREATE_TOKEN("SSRVTRA19005", "Failed to process the 'request-create-token' API request.", 500),


    // 15. List-related errors (19500 ~ 19999)
    FAILED_API_GET_VCPLAN_LIST("SSRVTRA19500", "Failed to process the 'get-vcplan-list' API request.", 500),
    FAILED_API_GET_VCPLAN("SSRVTRA19501", "Failed to process the 'get-vcplan' API request.", 500),
    FAILED_API_GET_ALLOWED_CA_LIST("SSRVTRA19502", "Failed to process the 'get-allowed-ca-list' API request.", 500),
    LIST_ALLOWED_CA_NOT_FOUND("SSRVTRA19503", "Failed to find allowed CA: allowed CA list not found.", 500),

    // 15. Notification-related errors (20000 ~ 20499)
    FAILED_API_SEND_EMAIL("SSRVTRA20000", "Failed to process the 'send-email' API request.", 500),
    FAILED_API_SEND_PUSH("SSRVTRA20001", "Failed to process the 'send-push' API request.", 500),
    NOTIFICATION_TEMPLATE_NOT_FOUND("SSRVTRA20002", "Failed to find notification template: requested template not found.", 500),
    FAILED_TO_REGISTER_PUSH_CONFIGURATION("SSRVTRA20003", "Failed to register push configuration.", 500),
    FAILED_TO_REGISTER_EMAIL_CONFIGURATION("SSRVTRA20004", "Failed to register email configuration.", 500),
    NOTIFICATION_EMAIL_CONFIGURATION_NOT_FOUND("SSRVTRA20005", "Failed to find email configuration", 500),

    // 16. KYC-related errors (20500 ~ 20999)
    KYC_INFO_NOT_FOUND("SSRVTRA20500", "Failed to find KYC: KYC is not registered.", 500),


    // 17. Admin-related errors (21000 ~ 21499)
    ADMIN_INFO_NOT_FOUND("SSRVTRA21000", "Failed to find admin: admin is not registered.", 400),
    ADMIN_ALREADY_EXISTS("SSRVTRA21001", "Failed to register admin: admin is already registered.", 400),

    // 99. Miscellaneous errors (90000 ~ 99999)
    FILE_NOT_FOUND("SSRVTRA0000", "Failed to find file: requested file not found.", 400),
    TODO_CODE("SSRVTRA90001", "Temporary error code: to be replaced.", 400),
    FILE_IO_ERROR("SSRVTRA90002", "Failed to process file: I/O error occurred.", 500),
    UNKNOWN_SERVER_ERROR("SSRVTRA90003", "An unknown server error has occurred.", 500),

    ;

    private final String code;
    private final String message;
    private final int httpStatus;

    /**
     * Constructs an ErrorCode with the specified code, message, and HTTP status code.
     *
     * @param code the error code
     * @param message the error message
     * @param httpStatus the HTTP status code
     */
    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * Returns the error message associated with the specified error code.
     *
     * @param code the error code
     * @return the error message
     */
    public static String getMessageByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getMessage();
            }
        }
        return "Unknown error code: " + code;
    }

    @Override
    public String toString() {
        return String.format("ErrorCode{code='%s', message='%s', httpStatus=%d}", code, message, httpStatus);
    }
}
