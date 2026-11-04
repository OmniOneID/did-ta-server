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

import lombok.Getter;
import org.omnione.did.tas.exception.TaSdkErrorCode;
import org.omnione.did.tas.exception.TaSdkException;

/**
 * Carries an {@link OpenDidException} raised by a Provider or the Issuer API client through the TA SDK.
 * <p>
 * The SDK rethrows {@link TaSdkException} as is but replaces any other exception with a generic SDK error.
 * Wrapping the server exception keeps its error code or external error response, so that
 * {@link TaSdkErrorMapper} can return it to the client unchanged.
 */
@Getter
public class ProviderOpenDidException extends TaSdkException {

    private final OpenDidException original;

    public ProviderOpenDidException(OpenDidException original) {
        super(TaSdkErrorCode.SDK_UNKNOWN_ERROR, original.getMessage(), original);
        this.original = original;
    }
}
