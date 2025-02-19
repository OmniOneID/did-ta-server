package org.omnione.did.base.exception;

import lombok.Getter;
import org.omnione.did.base.response.ErrorResponse;

/**
 * Custom exception class for OpenDID-Admin-related errors.
 * This exception encapsulates an ErrorCode to provide more detailed error information.
 *
 */
@Getter
public class OpenDidAdminException extends RuntimeException {
    private AdminErrorCode errorCode;
    private ErrorResponse errorResponse;

    /**
     * Constructs a new OpenDidException with the specified error code.
     *
     * @param errorCode The ErrorCode enum value representing the specific error.
     */
    public OpenDidAdminException(AdminErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new OpenDidException with the specified error response.
     *
     * @param errorResponse The ErrorResponse object representing the specific error.
     */
    public OpenDidAdminException(ErrorResponse errorResponse) {
        super(errorResponse.getDescription());
        this.errorResponse = errorResponse;
    }
}
