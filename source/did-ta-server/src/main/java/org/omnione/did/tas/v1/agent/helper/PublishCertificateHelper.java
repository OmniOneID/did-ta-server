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
package org.omnione.did.tas.v1.agent.helper;

import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.service.ListCertificateVcPublishService;
import org.omnione.did.data.model.vc.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PublishCertificateHelper {

    private final ListCertificateVcPublishService listCertificateVcPublishService;

    public String publishEntityCertificateVc(VerifiableCredential entityCertificateVc) {
        try {
            log.info("publishing Entity certificate VC to list provider");
            String publishedCertificateUrl = listCertificateVcPublishService.registerCertificateVc(entityCertificateVc);
            log.debug("published Entity certificate VC URL: {}", publishedCertificateUrl);
            return publishedCertificateUrl;
        } catch (OpenDidException e) {
            log.warn("Failed to register certificate VC to list provider: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error while registering certificate VC: {}", e.getMessage(), e);
            return null;
        }
    }

    public String getTasCertificateVcURL(Tas tas) {
        return tas.getPublishedCertificateUrl() != null
                ? tas.getPublishedCertificateUrl()
                : tas.getCertificateUrl();
    }
}
