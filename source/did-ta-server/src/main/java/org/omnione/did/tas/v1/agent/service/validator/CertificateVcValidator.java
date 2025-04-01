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

import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.tas.v1.agent.service.SignatureService;
import org.omnione.did.tas.v1.common.service.DidDocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.DidValidator;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service for validating Certificate Verifiable Credentials (VCs).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateVcValidator {

    private final DidDocService didDocService;
    private final TasQueryService tasQueryService;

    /**
     * Validates a Certificate VC.
     *
     * @param certificateVcUrl The URL of the Certificate VC to validate
     * @param providerDid The DID of the provider
     * @throws OpenDidException if validation fails
     */
    public void validateCertificateVc(String certificateVcUrl, String providerDid) {

        // Retrieve TAS
        log.debug("\t-->Retrieving TAS");
        Tas existedTas = tasQueryService.findTas();

        // Get TAS DID Document
        log.debug("\t-->Getting TAS DID Document");
        DidDocument tasDidDoc = didDocService.getDidDocument(existedTas.getDid());

        // Get Certificate VC using URL
        log.debug("\t-->Getting Certificate VC");
        VerifiableCredential certificateVc = getCertificateVc(certificateVcUrl);

        // Validate Provider DID
        log.debug("\t-->Validating Provider DID");
        validateProviderDid(certificateVc, providerDid);

        // Validate Issuer
        log.debug("\t-->Validating Issuer");
        validateIssuer(tasDidDoc, certificateVc, providerDid);

        // Validate VC
        log.debug("\t-->Validating VC");
        validateVc(certificateVc);
    }

    /**
     * Retrieves a Certificate VC from a given URL.
     *
     * @param certificateVcUrl The URL of the Certificate VC
     * @return VerifiableCredential The retrieved Certificate VC
     * @throws OpenDidException if the Certificate VC cannot be retrieved
     */
    private VerifiableCredential getCertificateVc(String certificateVcUrl) {
        log.info("Getting Certificate VC");

        try {
            String certificateVcJson = HttpClientUtil.getData(certificateVcUrl);
            VerifiableCredential certificateVc = new VerifiableCredential();
            certificateVc.fromJson(certificateVcJson);

            return certificateVc;
        } catch (CommonSdkException | HttpClientException e) {
            log.error("Certificate VC not found: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CERTIFICATE_VC_NOT_FOUND);
        }
    }

    /**
     * Validates the provider DID in the Certificate VC.
     *
     * @param certificateVc The Certificate VC to validate
     * @param providerDid The expected provider DID
     * @throws OpenDidException if the provider DID doesn't match
     */
    private void validateProviderDid(VerifiableCredential certificateVc, String providerDid) {
        String credentialSubjectId = certificateVc.getCredentialSubject().getId();
        if (!providerDid.equals(credentialSubjectId)) {
            log.error("Provider DID mismatch: providerDid={}, credentialSubjectId={}", providerDid, credentialSubjectId);
            throw new OpenDidException(ErrorCode.MISMATCH_PROVIDER_DID);
        }
    }

    /**
     * Validates the issuer of the Certificate VC.
     *
     * @param tasDidDoc The TAS DID Document
     * @param certificateVc The Certificate VC to validate
     * @param providerDid The provider DID
     * @throws OpenDidException if the issuer is invalid
     */
    private void validateIssuer(DidDocument tasDidDoc , VerifiableCredential certificateVc, String providerDid) {
        String issuerDid = certificateVc.getIssuer().getId();
        if (!tasDidDoc.getId().equals(issuerDid)) {
            log.error("Invalid Certificate VC issuer: issuerDid={}, tasDid={}", issuerDid, tasDidDoc.getId());
            throw new OpenDidException(ErrorCode.INVALID_CERTIFICATE_VC_ISSUER);
        }
    }

    /**
     * Validates the Verifiable Credential.
     *
     * @param certificateVc The Certificate VC to validate
     * @throws OpenDidException if the VC is invalid
     */
    private void validateVc(VerifiableCredential certificateVc) {
        String providerDid = certificateVc.getIssuer().getId();
        if (!DidValidator.isValidDid(providerDid)){
            log.error("Invalid DID: {}", providerDid);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        String verificationMethod = certificateVc.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            log.error("Invalid DID key URL: {}", verificationMethod);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the equivalence of did.
        String didOfKeyUrl = DidUtil.extractDid(verificationMethod);
        if (!providerDid.equals(didOfKeyUrl)) {
            log.error("DID mismatch: clientDid={}, didOfKeyUrl={}", providerDid, didOfKeyUrl);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (!certificateVc.getProof().getProofPurpose().equals(ProofPurpose.ASSERTION_METHOD.toString())) {
            log.error("Invalid proof purpose: {}", certificateVc.getProof().getProofPurpose());
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Find Wallet Provider DID Document.
        DidDocument tasDidDocument = didDocService.getDidDocument(verificationMethod);

        // Verify VC
        BaseCoreVcUtil.verifyVc(certificateVc, tasDidDocument, true);
    }
}