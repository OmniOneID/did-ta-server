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
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;

/**
 * Utility class for core DID operations.
 * This class provides methods for parsing DID documents, getting verification methods, and verifying DID document key proofs.
 */
@Slf4j
public class BaseCoreDidUtil {

    /**
     * Parses a DID document from its JSON representation.
     *
     * @param didDocJson The JSON string representing the DID document.
     * @return The parsed DidManager object.
     * @throws OpenDidException if the DID document parsing fails.
     */
    public static DidManager parseDidDoc(String didDocJson) {
        DidManager didManager = new DidManager();
        didManager.parse(didDocJson);

        return didManager;
    }

    /**
     * Parses a DID document.
     *
     * @param didDocument The DID document object.
     * @return The parsed DidManager object.
     * @throws OpenDidException if the DID document parsing fails.
     */
    public static DidManager parseDidDoc(DidDocument didDocument) {
        DidManager didManager = new DidManager();
        didManager.parse(didDocument.toJson());

        return didManager;
    }

    /**
     * Retrieves a verification method from a DID document using a DidManager.
     *
     * @param didManager The DidManager object managing the DID document.
     * @param keyId The key ID of the verification method.
     * @return The verification method object.
     */
    public static VerificationMethod getVerificationMethod(DidManager didManager, String keyId) {
        return didManager.getVerificationMethodByKeyId(keyId);
    }

    /**
     * Retrieves a verification method from a DID document.
     *
     * @param didDocument The DID document object.
     * @param keyId The key ID of the verification method.
     * @return The verification method object.
     */
    public static VerificationMethod getVerificationMethod(DidDocument didDocument, String keyId) {
        DidManager didManager = parseDidDoc(didDocument);
        return didManager.getVerificationMethodByKeyId(keyId);
    }

    /**
     * Retrieves a public key from a DID document using a DidManager.
     *
     * @param didManager The DidManager object managing the DID document.
     * @param keyId The key ID of the public key.
     * @return The public key as a string.
     */
    public static String getPublicKey(DidManager didManager, String keyId) {
        VerificationMethod verificationMethod = didManager.getVerificationMethodByKeyId(keyId);
        return verificationMethod.getPublicKeyMultibase();
    }

    /**
     * Retrieves a public key from a DID document.
     *
     * @param didDocument The DID document object.
     * @param keyId The key ID of the public key.
     * @return The public key as a string.
     * @throws OpenDidException if the public key retrieval fails.
     */
    public static String getPublicKey(DidDocument didDocument, String keyId) {
        DidManager didManager = parseDidDoc(didDocument);

        VerificationMethod verificationMethod = didManager.getVerificationMethodByKeyId(keyId);
        return verificationMethod.getPublicKeyMultibase();
    }

    /**
     * Verifies the key proofs within a DID document.
     *
     * @param didDocument The DID document object.
     * @throws OpenDidException if the DID document key proof verification fails.
     */
    public static void verifyDidDocKeyProofs(DidDocument didDocument) {
        try {
            DidManager didManager = parseDidDoc(didDocument);
            didManager.verifyDocumentSignature();
        } catch (CoreException e) {
            log.error("Failed to verify DID document key proofs: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VERIFY_DIDDOC_KEY_PROOF_FAILED);
        }
    }
}
