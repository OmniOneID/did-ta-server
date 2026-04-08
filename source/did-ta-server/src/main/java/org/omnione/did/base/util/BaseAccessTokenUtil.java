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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.crypto.enums.DigestType;
import org.omnione.did.crypto.enums.EccCurveType;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.DigestUtils;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.crypto.util.SignatureUtils;
import org.omnione.did.data.model.did.DidDocument;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

/**
 * Utility class for JWT/Access Token validation and PII extraction.
 *
 * <p>Supports the Token-Based KYC flow where the OP server issues a JWT access token
 * containing the user's PII. This utility handles the full validation process:
 * signature verification, time claim validation, and PII extraction.</p>
 */
public class BaseAccessTokenUtil {

    private static final Gson gson = new Gson();

    /**
     * JWT token structure containing header, payload, and signature components.
     */
    public static class JwtComponents {
        private final String header;
        private final String payload;
        private final String signature;
        private final String signatureOrigin;

        public JwtComponents(String header, String payload, String signature, String signatureOrigin) {
            this.header = header;
            this.payload = payload;
            this.signature = signature;
            this.signatureOrigin = signatureOrigin;
        }

        public String getHeader() { return header; }
        public String getPayload() { return payload; }
        public String getSignature() { return signature; }
        public String getSignatureOrigin() { return signatureOrigin; }
    }

    /**
     * Splits a JWT token into its three components.
     *
     * @param jwtToken the JWT token string
     * @return JwtComponents containing header, payload, signature, and signatureOrigin
     * @throws OpenDidException if the token format is invalid
     */
    public static JwtComponents splitJwtToken(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length != 3) {
                throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
            }
            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];
            String signatureOrigin = header + "." + payload;
            return new JwtComponents(header, payload, signature, signatureOrigin);
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    /**
     * Extracts the kid (key identifier) from the JWT header.
     *
     * @param header the Base64 URL-encoded JWT header
     * @return the kid value
     * @throws OpenDidException if the header cannot be parsed or kid is missing
     */
    public static String extractKidFromHeader(String header) {
        try {
            byte[] headerBytes = Base64.getUrlDecoder().decode(header);
            String headerJson = new String(headerBytes, StandardCharsets.UTF_8);
            Map<String, Object> headerMap = gson.fromJson(headerJson, new TypeToken<Map<String, Object>>() {}.getType());
            String kid = (String) headerMap.get("kid");
            if (kid == null) {
                throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
            }
            return kid;
        } catch (JsonSyntaxException e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    /**
     * Verifies the JWT signature using the signer's public key from the DID document.
     *
     * @param jwtComponents the JWT components
     * @param publicKey     the multibase-encoded public key
     * @throws OpenDidException if signature verification fails
     */
    public static void verifyJwtSignature(JwtComponents jwtComponents, String publicKey) {
        try {
            byte[] hashedSignOriginData = DigestUtils.getDigest(
                    jwtComponents.getSignatureOrigin().getBytes(StandardCharsets.UTF_8),
                    DigestType.SHA256);

            byte[] signatureBytes = Base64.getUrlDecoder().decode(jwtComponents.getSignature());
            byte[] compressedPublicKeyBytes = MultiBaseUtils.decode(publicKey);

            SignatureUtils.verifyCompactSignWithCompressedKey(
                    compressedPublicKeyBytes,
                    hashedSignOriginData,
                    signatureBytes,
                    EccCurveType.Secp256r1);

        } catch (CryptoException e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    /**
     * Validates the time claims (iat, exp) in the JWT payload.
     *
     * @param payload the Base64 URL-encoded JWT payload
     * @throws OpenDidException if the token is expired or time claims are invalid
     */
    public static void validateTokenTimeClaims(String payload) {
        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(payload);
            String payloadJson = new String(payloadBytes, StandardCharsets.UTF_8);
            Map<String, Object> payloadMap = gson.fromJson(payloadJson, new TypeToken<Map<String, Object>>() {}.getType());

            Object iatObj = payloadMap.get("iat");
            Object expObj = payloadMap.get("exp");
            if (iatObj == null || expObj == null) {
                throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
            }

            long iat = ((Number) iatObj).longValue();
            long exp = ((Number) expObj).longValue();
            long now = Instant.now().getEpochSecond();

            if (now >= exp) {
                throw new OpenDidException(ErrorCode.TOKEN_EXPIRED);
            }
            if (now + 300 < iat) {
                throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
            }
            if (exp <= iat) {
                throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
            }
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    /**
     * Extracts PII from the JWT payload.
     *
     * @param payload the Base64 URL-encoded JWT payload
     * @return the PII string
     * @throws OpenDidException if PII cannot be found in the payload
     */
    public static String extractPiiFromPayload(String payload) {
        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(payload);
            String payloadJson = new String(payloadBytes, StandardCharsets.UTF_8);
            Map<String, Object> payloadMap = gson.fromJson(payloadJson, new TypeToken<Map<String, Object>>() {}.getType());

            String pii = (String) payloadMap.get("pii");
            if (pii == null) {
                pii = (String) payloadMap.get("sub");
            }
            if (pii == null) {
                throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
            }
            return pii;
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    /**
     * Validates the JWT access token and extracts PII.
     *
     * <p>Performs the following steps:
     * <ol>
     *   <li>Splits JWT into components</li>
     *   <li>Extracts kid and validates signer DID against DID document</li>
     *   <li>Retrieves the public key (assert key) from the DID document</li>
     *   <li>Verifies the JWT signature</li>
     *   <li>Validates time claims (iat, exp)</li>
     *   <li>Extracts PII from payload</li>
     * </ol>
     * </p>
     *
     * @param jwtToken          the JWT access token
     * @param signerDidDocument the DID document of the expected token signer
     * @return the PII extracted from the token
     * @throws OpenDidException if any validation step fails
     */
    public static String validateTokenAndExtractPii(String jwtToken, DidDocument signerDidDocument) {
        try {
            // 1. Split JWT token
            JwtComponents jwtComponents = splitJwtToken(jwtToken);

            // 2. Extract kid and validate signer DID
            String kid = extractKidFromHeader(jwtComponents.getHeader());
            String signerDidFromToken = org.omnione.did.common.util.DidUtil.extractDid(kid);
            if (!signerDidDocument.getId().equals(signerDidFromToken)) {
                throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
            }

            // 3. Get public key (assert) from DID document
            String publicKey = BaseCoreDidUtil.getPublicKey(signerDidDocument, "assert");

            // 4. Verify JWT signature
            verifyJwtSignature(jwtComponents, publicKey);

            // 5. Validate time claims
            validateTokenTimeClaims(jwtComponents.getPayload());

            // 6. Extract PII
            return extractPiiFromPayload(jwtComponents.getPayload());

        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }
}
