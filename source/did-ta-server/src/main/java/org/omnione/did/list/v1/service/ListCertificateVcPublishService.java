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
package org.omnione.did.list.v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListCertificateVc;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.repository.ListCertificateVcRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * Service for publishing certificate VCs to the list provider (list_certificate_vc table).
 * Called during TAS registration and entity registration/certificate re-issuance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListCertificateVcPublishService {

    private final ListCertificateVcRepository listCertificateVcRepository;
    private final TasQueryService tasQueryService;
    private final EntityQueryService entityQueryService;

    /**
     * Registers a certificate VC to the list provider (list_certificate_vc table).
     *
     * @param verifiableCredential The certificate VC to publish
     * @return The published URL for this certificate VC
     */
    public String registerCertificateVc(VerifiableCredential verifiableCredential) {
        try {
            log.debug("=== Starting registerCertificateVc ===");

            if (verifiableCredential == null) {
                throw new OpenDidException(ErrorCode.INVALID_CERTIFICATE_VC);
            }

            String did = verifiableCredential.getCredentialSubject().getId();
            log.debug("DID: {}", did);

            Instant issuanceDateInstant = parseToInstant(verifiableCredential.getIssuanceDate(), "issuanceDate");
            Instant expiredDateInstant = parseToInstant(verifiableCredential.getValidUntil(), "validUntil");

            String name = resolveEntityName(did);
            String publishedUrl = buildPublishedCertificateVcUrl(did);

            ListCertificateVc listCertificateVc = ListCertificateVc.builder()
                    .did(did)
                    .name(name)
                    .certificateVc(verifiableCredential.toJson())
                    .publishedUrl(publishedUrl)
                    .publishedAt(issuanceDateInstant)
                    .expiredAt(expiredDateInstant)
                    .build();

            listCertificateVcRepository.save(listCertificateVc);
            log.debug("Certificate VC registered for DID: {}, publishedUrl: {}", did, publishedUrl);
            log.debug("*** Finished registerCertificateVc ***");

            return publishedUrl;
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error registering certificate VC: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_CERTIFICATE_VC);
        }
    }

    private String resolveEntityName(String did) {
        Tas tas = tasQueryService.findTas();
        if (tas.getDid().equals(did)) {
            return tas.getName();
        }
        Entity entity = entityQueryService.findEntityByDid(did);
        return entity.getName();
    }

    private Instant parseToInstant(String dateString, String fieldName) {
        try {
            return Instant.parse(dateString);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format for {}: {}", fieldName, dateString, e);
            throw new OpenDidException(ErrorCode.INVALID_CERTIFICATE_VC);
        }
    }

    private String buildPublishedCertificateVcUrl(String did) {
        String tasServerUrl = tasQueryService.findTas().getServerUrl();
        if (tasServerUrl.endsWith("/tas")) {
            tasServerUrl = tasServerUrl.substring(0, tasServerUrl.length() - 4);
        }
        return tasServerUrl + "/list/api/v1/certificate?did=" + did;
    }
}
