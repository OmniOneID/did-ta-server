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

package org.omnione.did.tas.v1.agent.service;

import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.TasProperty;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.BaseTasDidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.springframework.stereotype.Service;

/**
 * Service for signing and verifying signatures.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SignatureService {
    private final DidDocService didDocService;
    private final TasProperty tasProperty;
    private final FileWalletService fileWalletService;
    private final StorageService storageService;
    /**
     * Verifies a signature using the provided public key, signature, and message.
     *
     * @param encodedPublicKey The encoded public key used for verification
     * @param signature The signature to verify
     * @param signatureMassage The original message that was signed
     * @param proofType The type of proof used for the signature
     * @throws OpenDidException if signature verification fails
     */
    public void verifySignature(String encodedPublicKey, String signature, byte[] signatureMassage, ProofType proofType) {
        try {
            BaseCryptoUtil.verifySignature(encodedPublicKey, signature, signatureMassage, proofType.toEccCurveType());
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during verifySignature: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Verifies the key proofs in a DID document.
     *
     * @param ownerDidDoc The DID document to verify
     */
    public void verifyDidDocKeyProofs(DidDocument ownerDidDoc) {
        BaseCoreDidUtil.verifyDidDocKeyProofs(ownerDidDoc);
    }

    /**
     * Signs an invoked DID document.
     *
     * @param ownerDidDoc The owner's DID document
     * @return InvokedDidDoc The signed invoked DID document
     */
    public InvokedDidDoc signInvokedDidDoc(DidDocument ownerDidDoc) {
        // Find TAS DID Document.
        DidDocument tasDidDocument = didDocService.getDidDocument(tasProperty.getDid());

        // Generate the signature message.
        DidDocument didDocument = removeProof(ownerDidDoc);
        InvokedDidDoc unsignedInvokedDidDoc = generateInvokedDidDoc(tasDidDocument, didDocument);
        String signatureMessage = generateSignatureMessage(unsignedInvokedDidDoc);

        // Sing data.
        String proofValue = signDidDoc(tasDidDocument, signatureMessage, ProofPurpose.CAPABILITY_INVOCATION);

        // generate signed invoked did document.
        InvokedDidDoc signedInvokedDidDoc = generateSignedInvokedDidDoc(unsignedInvokedDidDoc, proofValue);

        return signedInvokedDidDoc;
    }
    /**
     * Removes the proof from a DID document.
     *
     * @param ownerDidDoc The DID document to remove the proof from
     * @return DidDocument The DID document without proof
     */
    private DidDocument removeProof(DidDocument ownerDidDoc) {
        ownerDidDoc.setProof(null);
        ownerDidDoc.setProofs(null);

        return ownerDidDoc;
    }

    /**
     * Generates an unsigned invoked DID document.
     *
     * @param tasDidDocument The TAS DID document
     * @param didDocument The DID document to invoke
     * @return InvokedDidDoc The unsigned invoked DID document
     */
    private InvokedDidDoc generateInvokedDidDoc(DidDocument tasDidDocument, DidDocument didDocument) {
        return BaseTasDidUtil.generateInvokedDocumentSignatureMessage(tasDidDocument, didDocument, ProofType.SECP_256R1_SIGNATURE_2018, tasProperty.getCertificateVc());
    }

    /**
     * Generates a signature message from an unsigned invoked DID document.
     *
     * @param unsignedInvokedDidDoc The unsigned invoked DID document
     * @return String The generated signature message
     * @throws OpenDidException if message generation fails
     */
    private String generateSignatureMessage(InvokedDidDoc unsignedInvokedDidDoc) {
        try {
            return JsonUtil.serializeAndSort(unsignedInvokedDidDoc);
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during generateSignatureMessage: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.INVOKED_DOCUMENT_GENERATION_FAILED);
        }
    }

    /**
     * Signs a DID document.
     *
     * @param tasDidDocument The TAS DID document
     * @param signatureMessage The message to sign
     * @param proofPurpose The purpose of the proof
     * @return String The generated signature
     */
    private String signDidDoc(DidDocument tasDidDocument, String signatureMessage, ProofPurpose proofPurpose) {
        // Get the key ID
        VerificationMethod verificationMethod = BaseCoreDidUtil.getVerificationMethod(tasDidDocument, proofPurpose.toKeyId());
        String keyId = verificationMethod.getId();

        //  Sign the message.
        byte[] signature = fileWalletService.generateCompactSignature(keyId, signatureMessage);

        return BaseMultibaseUtil.encode(signature);
    }
    /**
     * Generates a signed invoked DID document.
     *
     * @param unsignedInvokedDidDoc The unsigned invoked DID document
     * @param proofValue The proof value to add
     * @return InvokedDidDoc The signed invoked DID document
     */
    private InvokedDidDoc generateSignedInvokedDidDoc(InvokedDidDoc unsignedInvokedDidDoc, String proofValue) {
        return BaseTasDidUtil.generateInvokedDocument(unsignedInvokedDidDoc, proofValue);
    }

    /**
     * Signs a TAS invoked DID document.
     *
     * @param tasOwnerDidDoc The TAS owner's DID document
     * @return InvokedDidDoc The signed TAS invoked DID document
     * @throws OpenDidException if document generation fails
     */
    public InvokedDidDoc signTasInvokedDidDoc(DidDocument tasOwnerDidDoc) {
        try {
            // Generate the signature message.
            DidDocument didDocument = removeProof(tasOwnerDidDoc);
            InvokedDidDoc unsignedInvokedDidDoc = generateInvokedDidDoc(tasOwnerDidDoc, didDocument);
            String signatureMessage = generateSignatureMessage(unsignedInvokedDidDoc);

            // Sing data.
            String proofValue = signDidDoc(tasOwnerDidDoc, signatureMessage, ProofPurpose.CAPABILITY_INVOCATION);

            // generate signed invoked did document.
            InvokedDidDoc signedInvokedDidDoc = generateSignedInvokedDidDoc(unsignedInvokedDidDoc, proofValue);

            // Get the Assertion public key.
            String encodedInvokePublicKey = BaseCoreDidUtil.getPublicKey(tasOwnerDidDoc, "invoke");
            verifySignature(encodedInvokePublicKey, signedInvokedDidDoc.getProof().getProofValue(), BaseDigestUtil.generateHash(signatureMessage), ProofType.fromDisplayName(signedInvokedDidDoc.getProof().getType()));

            return signedInvokedDidDoc;
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.INVOKED_DOCUMENT_GENERATION_FAILED);
        }
    }

    /**
     * Signs an entity invoked DID document.
     *
     * @param entityOwnerDidDoc The entity owner's DID document
     * @return InvokedDidDoc The signed entity invoked DID document
     * @throws OpenDidException if document generation fails
     */
    public InvokedDidDoc signEntityInvokedDidDoc(DidDocument entityOwnerDidDoc) {
        try {
            // Retrievie TAS DID Document.
            DidDocument tasDidDoc = storageService.findDidDoc(tasProperty.getDid());

            // Generate the signature message.
            DidDocument didDocument = removeProof(entityOwnerDidDoc);
            InvokedDidDoc unsignedInvokedDidDoc = generateInvokedDidDoc(tasDidDoc, didDocument);
            String signatureMessage = generateSignatureMessage(unsignedInvokedDidDoc);
            log.debug("\t--> Signature message: {}", signatureMessage);

            // Sing data.
            String proofValue = signDidDoc(tasDidDoc, signatureMessage, ProofPurpose.CAPABILITY_INVOCATION);

            // generate signed invoked did document.
            InvokedDidDoc signedInvokedDidDoc = generateSignedInvokedDidDoc(unsignedInvokedDidDoc, proofValue);

            return signedInvokedDidDoc;
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to Invoked document generation : " + e.getMessage());
            throw new OpenDidException(ErrorCode.INVOKED_DOCUMENT_GENERATION_FAILED);
        }
    }
}
