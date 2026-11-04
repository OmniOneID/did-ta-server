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

package org.omnione.did.base.util;

import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.common.util.multibase.BaseMultibaseUtil;

/**
 * Decodes multibase values and reports a failure as {@link ErrorCode#DECODING_FAILED}.
 * <p>
 * The Common SDK reports decoding failures with its generic ENCODING_FAILED code. Clients of the TA server
 * expect DECODING_FAILED for a badly encoded value.
 */
public final class MultibaseDecoder {

    private MultibaseDecoder() {
    }

    public static byte[] decode(String encoded) {
        try {
            return BaseMultibaseUtil.decode(encoded);
        } catch (CommonSdkException e) {
            throw new OpenDidException(ErrorCode.DECODING_FAILED);
        }
    }
}
