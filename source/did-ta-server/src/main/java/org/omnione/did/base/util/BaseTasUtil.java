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

import org.omnione.did.common.util.NonceGenerator;

/**
 * Utility class for TAS (Trusted Agent) operations.
 * This class provides helper methods related to TAS, such as nonce generation with multibase encoding.
 */
public class BaseTasUtil {

    /**
     * Generates a 16-byte nonce and encodes it using a multibase encoding scheme.
     *
     * @return The nonce encoded with a multibase scheme.
     */
    public static String generateNonceWithMultibase() {
        byte[] nonce = NonceGenerator.generate16ByteNonce();
        return BaseMultibaseUtil.encode(nonce);
    }
}
