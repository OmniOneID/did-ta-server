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

package org.omnione.did.base.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.omnione.did.base.exception.ErrorCode;

/**
 * Represents an error response in the DID system.
 * This class encapsulates error information including an error code and description.
 *
 */
public class ErrorResponse {
    private final String code;
    private final String description;

    /**
     * Constructs an ErrorResponse with a given code and description.
     *
     * @param code The error code.
     * @param description The error description.
     */
    @JsonCreator
    public ErrorResponse(@JsonProperty("code") String code, @JsonProperty("description") String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Constructs an ErrorResponse based on the provided ErrorCode.
     *
     * @param errorCode The ErrorCode object that contains both the code and the description.
     */
    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
    }

    /**
     * Returns the error code associated with this response.
     *
     * @return The error code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the description of the error.
     *
     * @return The error description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a string representation of the ErrorResponse.
     *
     * @return A string representation of the ErrorResponse.
     */
    @Override
    public String toString() {
        return String.format("ErrorResponse{code='%s', description='%s'}", code, description);
    }
}
