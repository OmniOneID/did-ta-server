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

package org.omnione.did.tas.v1.config.provider;

import org.omnione.did.base.db.domain.CertificateVc;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.repository.CertificateVcRepository;
import org.omnione.did.tas.v1.agent.helper.PublishCertificateHelper;
import org.omnione.did.tas.v1.agent.service.validator.CertificateVcValidator;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.exception.TaSdkErrorCode;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.provider.CertificateProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK CertificateProvider interface, delegating to the server's repository and helper.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class CertificateProviderImpl implements CertificateProvider {

    private final CertificateVcRepository certificateVcRepository;
    private final PublishCertificateHelper publishCertificateHelper;
    private final TasQueryService tasQueryService;
    private final CertificateVcValidator certificateVcValidator;

    /**
     * Publishes a Certificate VC to the List provider. A failure to publish does not fail the enrollment.
     * <p>
     * The certificate_vc table holds only the TAS Certificate VC, which is served by the certificate-vc API.
     * An Entity Certificate VC is published to the List provider only.
     *
     * @return the URL to use as the VC meta reference for the TAS, or the published URL for an Entity
     */
    @Override
    public String publishCertificateVc(VerifiableCredential certificateVc) {
        Tas tas = tasQueryService.findTas();
        if (tas.getDid().equals(certificateVc.getCredentialSubject().getId())) {
            certificateVcRepository.save(CertificateVc.builder()
                    .vc(certificateVc.toJson())
                    .build());
            publishCertificateHelper.publishEntityCertificateVc(certificateVc);
            return tas.getCertificateUrl();
        }
        return publishCertificateHelper.publishEntityCertificateVc(certificateVc);
    }

    /**
     * Returns the TAS certificate reference to embed for the given subject.
     * <p>
     * DID Documents and the TAS's own Certificate VC refer to the configured certificate URL. An Entity Certificate
     * VC and its VC meta refer to the published TAS certificate when there is one.
     */
    @Override
    public String generateCertificateVcUrl(String did) {
        Tas tas = tasQueryService.findTas();
        if (tas.getDid().equals(did)) {
            return tas.getCertificateUrl();
        }
        return publishCertificateHelper.getTasCertificateVcURL(tas);
    }

    @Override
    public VerifiableCredential getCertificateVc(String vcId) {
        CertificateVc entity = certificateVcRepository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new TaSdkException(TaSdkErrorCode.CERTIFICATE_VC_NOT_FOUND,
                        "No certificate VC found"));
        VerifiableCredential vc = new VerifiableCredential();
        vc.fromJson(entity.getVc());
        return vc;
    }

    @Override
    public void validateCertificateVc(String certVcRef, String providerDid) {
        certificateVcValidator.validateCertificateVc(certVcRef, providerDid);
    }
}
