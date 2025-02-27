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

package org.omnione.did.tas.v1.agent.service.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.omnione.did.base.datamodel.data.DidAuth;
import org.omnione.did.base.datamodel.data.Proof;
import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.tas.v1.common.service.DidDocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.DidValidator;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Service for validating DID Auth objects.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DidAuthValidator {
    private final DidDocService didDocService;

    /**
     * Validate the given DID Auth object.
     * Throws an OpenDidException if the DID Auth object is invalid.
     *
     * @param didAuth The DID Auth object to validate
     * @param transaction The transaction associated with the DID Auth object
     */
    public void validateDidAuth(DidAuth didAuth, Transaction transaction) {
        // Extract and validate did and didKeyUrl
        String clientDid = didAuth.getDid();
        if (!DidValidator.isValidDid(clientDid)){
            log.error("Invalid DID: {}", clientDid);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        String verificationMethod = didAuth.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            log.error("Invalid DID key URL: {}", verificationMethod);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the equivalence of did.
        String didOfKeyUrl = DidUtil.extractDid(verificationMethod);
        if (!clientDid.equals(didOfKeyUrl)) {
            log.error("DID mismatch: clientDid={}, didOfKeyUrl={}", clientDid, didOfKeyUrl);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (didAuth.getProof().getProofPurpose() != ProofPurpose.AUTHENTICATION) {
            log.error("Invalid proof purpose: {}", didAuth.getProof().getProofPurpose());
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Compare the value of the DID Auth nonce.
        log.debug("\t--> Comparing the value of the DID Auth nonce");
        byte[] reqAuthNonce = BaseMultibaseUtil.decode(didAuth.getAuthNonce());
        byte[] authNonce = BaseMultibaseUtil.decode(transaction.getAuthNonce());

        if (!Arrays.equals(reqAuthNonce, authNonce)) {
            log.error("\t--> DID Auth nonce mismatch");
            throw new OpenDidException(ErrorCode.AUTH_NONCE_MISMATCH);
        }

        // Extract the signature message.
        byte[] signatureMessage = extractSignatureMessage(didAuth);

        // Find Wallet Provider DID Document.
        DidDocument clientDidDocument = didDocService.getDidDocument(verificationMethod);

        // Extract the keyId from the verificationMethod.
        String keyId = DidUtil.extractKeyId(verificationMethod);
        log.debug("\t--> Extracted keyId: {}", keyId);

        // Get the Assertion public key.
        String encodedKeyAgreePublicKey = BaseCoreDidUtil.getPublicKey(clientDidDocument, keyId);

        // Verify the signature.
        verifySignature(encodedKeyAgreePublicKey, didAuth.getProof().getProofValue(), signatureMessage, didAuth.getProof().getType());
    }

    /**
     * Extract the signature message from the given DID Auth object.
     *
     * @param didAuth The DID Auth object to extract the signature message from
     * @return The extracted signature message
     * @throws OpenDidException if the signature message cannot be extracted
     */
    private byte[] extractSignatureMessage(DidAuth didAuth) {
        try {
            // Remove proofValue from Proof fields in the object.
            DidAuth signatureMessageObject = removeProofValue(didAuth);

            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch(JsonProcessingException e) {
            log.error("Failed to extract signature message: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Remove the proofValue from the given DID Auth object.
     *
     * @param data The DID Auth object to remove the proofValue from
     * @return The DID Auth object with the proofValue removed
     */
    private DidAuth removeProofValue(DidAuth data) {
        DidAuth signatureMessageObject = DidAuth.builder()
                .did(data.getDid())
                .authNonce(data.getAuthNonce())
                .proof(new Proof(
                        data.getProof().getType(),
                        data.getProof().getCreated(),
                        data.getProof().getVerificationMethod(),
                        data.getProof().getProofPurpose(),
                        null
                ))
                .build();

        return signatureMessageObject;
    }

    /**
     * Verify the signature of the given signature message.
     *
     * @param encodedPublicKey The encoded public key to verify the signature with
     * @param signature The signature to verify
     * @param signatureMassage The signature message to verify
     * @param proofType The proof type to use for verification
     * @throws OpenDidException if the signature verification fails
     */
    //@TODO: 공통함수로 빼야 함
    private void verifySignature(String encodedPublicKey, String signature, byte[] signatureMassage, ProofType proofType) {
        try {
            BaseCryptoUtil.verifySignature(encodedPublicKey, signature, signatureMassage, proofType.toEccCurveType());
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred during verifySignature: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }
}
