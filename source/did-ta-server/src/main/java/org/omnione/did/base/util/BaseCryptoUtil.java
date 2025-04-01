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

import org.omnione.did.base.datamodel.enums.EccCurveType;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.crypto.engines.CipherInfo;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.keypair.KeyPairInterface;
import org.omnione.did.crypto.util.CryptoUtils;
import org.omnione.did.crypto.util.DigestUtils;
import org.omnione.did.crypto.util.SignatureUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * Utility class for cryptographic operations.
 * This class provides methods for generating key pairs, nonces, shared secrets, and signatures,
 * as well as encrypting and decrypting data using symmetric and asymmetric encryption algorithms.
 */
@Slf4j
public class BaseCryptoUtil {

    /**
     * Generate a key pair.
     * The key pair is generated using the specified ECC curve type.
     *
     * @param eccCurveType The ECC curve type to use for key pair generation
     * @return The generated key pair
     * @throws OpenDidException if key pair generation fails
     */
    public static KeyPairInterface generateKeyPair(EccCurveType eccCurveType) {
        try {
            return CryptoUtils.generateKeyPair(eccCurveType.toOmnioneDidKeyType());
        } catch (CryptoException e) {
            log.error("Failed to generate key pair: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.KEY_PAIR_GENERATION_FAILED);
        }
    }

    /**
     * Generate a nonce.
     * The nonce is generated with the specified length.
     *
     * @param length The length of the nonce to generate
     * @return The generated nonce
     * @throws OpenDidException if nonce generation fails
     */
    public static byte[] generateNonce(int length) {
        try {
            return CryptoUtils.generateNonce(length);
        } catch (CryptoException e) {
            log.error("Failed to generate nonce: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.NONCE_GENERATION_FAILED);
        }
    }

    /**
     * Merge two nonces.
     * The nonces are merged by concatenating them together.
     *
     * @param clientNonce The client nonce
     * @param serverNonce The server nonce
     * @return The merged nonce
     * @throws OpenDidException if nonce merge fails
     */
    public static byte[] mergeNonce(byte[] clientNonce, byte[] serverNonce) {
        try {
            return DigestUtils.mergeNonce(clientNonce, serverNonce);
        } catch (CryptoException e) {
            log.error("Failed to merge nonce: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.NONCE_MERGE_FAILED);
        }
    }

    /**
     * Generate an initial vector.
     * The initial vector is generated with a length of 16 bytes.
     *
     * @return The generated initial vector
     * @throws OpenDidException if initial vector generation fails
     */
    public static byte[] generateInitialVector()  {
        try {
            return CryptoUtils.generateNonce(16);
        } catch (CryptoException e) {
            log.error("Failed to generate initial vector: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.INITIAL_VECTOR_GENERATION_FAILED);
        }
    }

    /**
     * Generate a shared secret.
     * The shared secret is generated using the public key and private key.
     *
     * @param publicKey The public key
     * @param privateKey The private key
     * @param curveType The ECC curve type
     * @return The generated shared secret
     * @throws OpenDidException if shared secret generation fails
     */
    public static byte[] generateSharedSecret(byte[] publicKey, byte[] privateKey, EccCurveType curveType) {
        try {
            return CryptoUtils.generateSharedSecret(publicKey, privateKey, curveType.toOmnioneEccCurveType());
        } catch (CryptoException e) {
            log.error("Failed to generate shared secret: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.SESSION_KEY_GENERATION_FAILED);
        }
    }

    /**
     * Merge a shared secret and nonce.
     * The shared secret and nonce are merged by hashing them together.
     * The length of the result is determined by the symmetric cipher type.
     *
     * @param sharedSecret The shared secret
     * @param nonce The nonce
     * @param symmetricCipherType The symmetric cipher type
     * @return The merged shared secret and nonce
     * @throws OpenDidException if shared secret and nonce merge fails
     * @throws OpenDidException if invalid symmetric cipher type
     */
    public static byte[] mergeSharedSecretAndNonce(byte[] sharedSecret, byte[] nonce, SymmetricCipherType symmetricCipherType) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(sharedSecret, 0, sharedSecret.length);
            digest.update(nonce, 0, nonce.length);

            byte[] combinedResult = digest.digest();

            return switch (symmetricCipherType) {
                case AES_128_CBC, AES_128_ECB -> Arrays.copyOfRange(combinedResult, 0, 16);
                case AES_256_CBC, AES_256_ECB -> Arrays.copyOfRange(combinedResult, 0, 32);
                default -> throw new OpenDidException(ErrorCode.INVALID_SYMMETRIC_CIPHER_TYPE);
            };
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to merge shared secret and nonce: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.NONCE_AND_SHARED_SECRET_MERGE_FAILED);
        }
    }

    /**
     * Encrypt data.
     *
     * @param plainText The plain text to encrypt
     * @param key The key to use for encryption
     * @param iv The initial vector to use for encryption
     * @param symmetricCipherType The symmetric cipher type
     * @param symmetricPaddingType The symmetric padding type
     * @return The encrypted data
     */
    public static byte[] encrypt(String plainText, byte[] key, byte[] iv, SymmetricCipherType symmetricCipherType, SymmetricPaddingType symmetricPaddingType) {
        return encrypt(plainText.getBytes(StandardCharsets.UTF_8), key, iv, symmetricCipherType, symmetricPaddingType);
    }

    /**
     * Encrypt data.
     *
     * @param plainText The plain text to encrypt
     * @param key The key to use for encryption
     * @param iv The initial vector to use for encryption
     * @param symmetricCipherType The symmetric cipher type
     * @param symmetricPaddingType The symmetric padding type
     * @return The encrypted data
     * @throws OpenDidException if encryption fails
     */
    public static byte[] encrypt(byte[] plainText, byte[] key, byte[] iv, SymmetricCipherType symmetricCipherType, SymmetricPaddingType symmetricPaddingType) {
        try {
            CipherInfo cipherInfo = new CipherInfo(symmetricCipherType.toOmnioneSymmetricCipherType(), symmetricPaddingType.toOmnioneSymmetricPaddingType());
            return CryptoUtils.encrypt(plainText, cipherInfo, key, iv);
        } catch (CryptoException e) {
            log.error("Failed to encrypt data: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.ENCRYPTION_FAILED);
        }
    }

    /**
     * Decrypt data.
     *
     * @param encrypteData The encrypted data to decrypt
     * @param key The key to use for decryption
     * @param iv The initial vector to use for decryption
     * @param symmetricCipherType The symmetric cipher type
     * @param symmetricPaddingType The symmetric padding type
     * @return The decrypted data
     */
    public static byte[] decrypt(String encrypteData, byte[] key, byte[] iv, SymmetricCipherType symmetricCipherType, SymmetricPaddingType symmetricPaddingType) {
        return decrypt(BaseMultibaseUtil.decode(encrypteData), key, iv, symmetricCipherType, symmetricPaddingType);
    }

    /**
     * Decrypt data.
     *
     * @param encryptData The encrypted data to decrypt
     * @param key The key to use for decryption
     * @param iv The initial vector to use for decryption
     * @param symmetricCipherType The symmetric cipher type
     * @param symmetricPaddingType The symmetric padding type
     * @return The decrypted data
     * @throws OpenDidException if decryption fails
     */
    public static byte[] decrypt(byte[] encryptData, byte[] key, byte[] iv, SymmetricCipherType symmetricCipherType, SymmetricPaddingType symmetricPaddingType) {
        try {
            CipherInfo cipherInfo = new CipherInfo(symmetricCipherType.toOmnioneSymmetricCipherType(), symmetricPaddingType.toOmnioneSymmetricPaddingType());
            return CryptoUtils.decrypt(encryptData, cipherInfo, key, iv);
        } catch (CryptoException e) {
            log.error("Failed to decrypt data: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.DECRYPTION_FAILED);
        }
    }

    /**
     * Generates a digital signature using the provided private key and data.
     *
     * @param privateKey The private key to use for signature generation
     * @param signData The data to sign
     * @param eccCurveType The ECC curve type
     * @return The generated signature
     * @throws OpenDidException if signature generation fails
     */
    public static byte[] signature(PrivateKey privateKey, byte[] signData, EccCurveType eccCurveType) {
        try {
            return SignatureUtils.generateEccSignatureFromHashedData(privateKey, signData);
        } catch (CryptoException e) {
            log.error("Failed to generate signature: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.SIGNATURE_GENERATION_FAILED);
        }
    }

    /**
     * Verifies a digital signature using the provided public key and ECC curve type.
     *
     * @param encodedPublicKey The encoded public key.
     * @param encodedSignature The encoded signature.
     * @param signData The data to verify.
     * @param eccCurveType The ECC curve type.
     * @throws OpenDidException if signature verification fails.
     */
    public static void verifySignature(String encodedPublicKey, String encodedSignature, byte[] signData, EccCurveType eccCurveType) {
        try {
            // Decode the public key
            byte[] publicKeyBytes = BaseMultibaseUtil.decode(encodedPublicKey);
            byte[] signatureBytes = BaseMultibaseUtil.decode(encodedSignature);

            // Verify the signature
            SignatureUtils.verifyCompactSignWithCompressedKey(publicKeyBytes, signData, signatureBytes, eccCurveType.toOmnioneEccCurveType());
        }  catch (CryptoException e) {
            log.error("Failed to verify signature: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Verifies a digital signature using the provided public key and ECC curve type.
     *
     * @param publicKey The public key.
     * @param signData The data to verify.
     * @param signature The signature.
     * @param eccCurveType The ECC curve type.
     * @throws OpenDidException if signature verification fails.
     */
    public static void verifySignature(PublicKey publicKey, byte[] signData, byte[] signature, EccCurveType eccCurveType) {
        try {
            // Convert the public key to compressed form
            byte[] compressedPublicKeyBytes = CryptoUtils.compressPublicKey(publicKey.getEncoded(), eccCurveType.toOmnioneEccCurveType());

            // Verify the signature
            SignatureUtils.verifyCompactSignWithCompressedKey(compressedPublicKeyBytes, signData, signature, eccCurveType.toOmnioneEccCurveType());
        }  catch (CryptoException e) {
            log.error("Failed to verify signature: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Compresses a public key using the specified ECC curve type.
     *
     * @param uncompressedPublicKey The uncompressed public key.
     * @param eccCurveType The ECC curve type.
     * @return The compressed public key.
     * @throws OpenDidException if public key compression fails.
     */
    public static byte[] compressPublicKey(byte[] uncompressedPublicKey, EccCurveType eccCurveType) {
        try {
            return CryptoUtils.compressPublicKey(uncompressedPublicKey, eccCurveType.toOmnioneEccCurveType());
        } catch (CryptoException e) {
            log.error("Failed to compress public key: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.PUBLIC_KEY_COMPRESS_FAILED);
        }
    }
}
