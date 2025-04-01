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

import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.CryptoUtils;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.provider.Provider;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for TAS (Trusted Attestation Service) DID operations.
 * This class provides methods for generating invoked DID documents, creating signatures,
 * and retrieving verification methods for the DID.
 */
@Slf4j
public class BaseTasDidUtil {

    /**
     * Generates a signature message for an invoked DID document.
     *
     * @param tasDidDoc The DID document of the TAS.
     * @param didDoc The target DID document to be invoked.
     * @param proofType The type of proof.
     * @param certVcRef The URL of the certificate VC.
     * @return The generated InvokedDocument signature message.
     * @throws OpenDidException if the generation of the InvokedDocument signature message fails.
     */
    public static InvokedDidDoc generateInvokedDocumentSignatureMessage(DidDocument tasDidDoc, DidDocument didDoc, ProofType proofType, String certVcRef) {
        try {
            Provider provider = new Provider();
            provider.setDid(tasDidDoc.getId());
            provider.setCertVcRef(certVcRef);

            String nonce = BaseMultibaseUtil.encode(CryptoUtils.generateNonce(16));

            String verificationMethod = getVerificationMethod(tasDidDoc, ProofPurpose.CAPABILITY_INVOCATION);

            Proof proof = new Proof();
            proof.setProofPurpose(ProofPurpose.CAPABILITY_INVOCATION.toString());
            proof.setCreated(DateTimeUtil.getCurrentUTCTimeString());
            proof.setType(proofType.toString());
            proof.setVerificationMethod(verificationMethod);

            String encodedDidDocJson = BaseMultibaseUtil.encode(didDoc.toJson().getBytes(StandardCharsets.UTF_8));

            return new InvokedDidDoc(encodedDidDocJson, proof, provider, nonce);
        } catch (CryptoException e) {
            log.error("Failed to generate InvokedDocument signature message: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.INVOKED_DOCUMENT_GENERATION_FAILED);
        }
    }

    /**
     * Retrieves the verification method for a given proof purpose within the TAS DID document.
     *
     * @param tasDidDoc The DID document of the TAS.
     * @param proofPurpose The purpose of the proof.
     * @return The verification method identifier string.
     */
    public static String getVerificationMethod(DidDocument tasDidDoc, ProofPurpose proofPurpose) {
        String version = tasDidDoc.getVersionId();
        VerificationMethod verificationMethod = BaseCoreDidUtil.getVerificationMethod(tasDidDoc, proofPurpose.toKeyId());

        return tasDidDoc.getId() + "?versionId=" + version + "#" + verificationMethod.getId();
    }


    /**
     * Generates an invoked DID document with the provided proof value.
     *
     * @param unsignedInvokedDidDoc The unsigned InvokedDocument.
     * @param proofValue The value of the proof.
     * @return The signed InvokedDocument.
     * @throws OpenDidException if the generation of the InvokedDocument fails.
     */
    public static InvokedDidDoc generateInvokedDocument(InvokedDidDoc unsignedInvokedDidDoc, String proofValue) {
        Proof proof = new Proof();
        proof.setProofPurpose(unsignedInvokedDidDoc.getProof().getProofPurpose());
        proof.setCreated(unsignedInvokedDidDoc.getProof().getCreated());
        proof.setType(unsignedInvokedDidDoc.getProof().getType());
        proof.setVerificationMethod(unsignedInvokedDidDoc.getProof().getVerificationMethod());
        proof.setProofValue(proofValue);

        Provider provider = new Provider();
        provider.setDid(unsignedInvokedDidDoc.getController().getDid());
        provider.setCertVcRef(unsignedInvokedDidDoc.getController().getCertVcRef());

        return new InvokedDidDoc(unsignedInvokedDidDoc.getDidDoc(), proof, provider, unsignedInvokedDidDoc.getNonce());
    }

    /**
     * Constructs the DID with its version identifier.
     *
     * @param didDoc The DID document.
     * @return The DID string including the version identifier.
     */
    public static String getDidWithVersion(DidDocument didDoc) {
        return didDoc.getId() + "?versionId=" + didDoc.getVersionId();
    }
}
