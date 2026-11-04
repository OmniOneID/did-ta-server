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
import org.omnione.did.common.exception.CommonSdkException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Converts Common SDK errors to the error codes of the TA server.
 * <p>
 * Only codes with a single meaning are listed. The Common SDK uses ENCODING_FAILED for encoding, decoding and
 * encryption failures, so decoding and encryption are translated at the call site instead.
 */
@Slf4j
public final class CommonSdkErrorMapper {

    private static final Map<org.omnione.did.common.exception.ErrorCode, ErrorCode> MAP =
            new EnumMap<>(org.omnione.did.common.exception.ErrorCode.class);

    static {
        MAP.put(org.omnione.did.common.exception.ErrorCode.ENCODING_FAILED, ErrorCode.ENCODING_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.SIGNATURE_VERIFICATION_FAILED, ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.INVALID_SIGNATURE, ErrorCode.INVALID_SIGNATURE);
        MAP.put(org.omnione.did.common.exception.ErrorCode.SIGNATURE_GENERATION_FAILED, ErrorCode.SIGNATURE_GENERATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.ECDH_KEY_GENERATION_FAILED, ErrorCode.KEY_PAIR_GENERATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.SESSION_KEY_GENERATION_FAILED, ErrorCode.SESSION_KEY_GENERATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.NONCE_MERGE_FAILED, ErrorCode.NONCE_MERGE_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.VC_GENERATION_FAILED, ErrorCode.VC_GENERATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.VC_SIGNATURE_EXTRACTION_FAILED, ErrorCode.VC_ORIGIN_DATA_EXTRACTION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.VC_PROOF_SETTING_FAILED, ErrorCode.VC_PROOF_SETTING_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.VC_VERIFICATION_FAILED, ErrorCode.VC_VERIFICATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.VC_META_PARSE_FAILED, ErrorCode.PARSE_VC_META_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.VC_SCHEMA_PARSE_FAILED, ErrorCode.PARSE_VC_SCHEMA_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.WALLET_CREATION_FAILED, ErrorCode.WALLET_CREATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.KEY_GENERATION_FAILED, ErrorCode.KEY_GENERATION_FAILED);
        MAP.put(org.omnione.did.common.exception.ErrorCode.FAILED_TO_LOAD_KEY_ELEMENT, ErrorCode.FAILED_TO_LOAD_KEY_ELEMENT);
    }

    private CommonSdkErrorMapper() {
    }

    /**
     * @param e        Common SDK exception
     * @param fallback code for a Common SDK code without a dedicated server code, normally the generic code of
     *                 the failing API
     * @return server exception with the mapped code
     */
    public static OpenDidException toOpenDidException(CommonSdkException e, ErrorCode fallback) {
        log.error("Common SDK error - code: {}, message: {}", e.getErrorCode(), e.getMessage());
        return new OpenDidException(e.getErrorCode() == null ? fallback : MAP.getOrDefault(e.getErrorCode(), fallback));
    }

    public static OpenDidException toOpenDidException(CommonSdkException e) {
        return toOpenDidException(e, ErrorCode.UNKNOWN_SERVER_ERROR);
    }
}
