package org.omnione.did.base.exception;

public enum AdminErrorCode {
    UNKNOWN_SERVER_ERROR("99999", "An unknown server error has occurred.", 500),
    FAILED_TO_REGISTER_TA_CERTIFICATE("1", "Failed to register TA certificate.", 500),
    FAILED_TO_REGISTER_TA_DID_DOCUMENT("2", "Failed to register TA DID Document.", 500),
    TA_ALREADY_REGISTERED("3", "TA is already registered.", 400),
    URL_PING_ERROR("4", "Failed to ping the URL.", 400);

    private final String code;
    private final String message;
    private final int httpStatus;

    AdminErrorCode(String code, String message, int httpStatus) {
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
}
