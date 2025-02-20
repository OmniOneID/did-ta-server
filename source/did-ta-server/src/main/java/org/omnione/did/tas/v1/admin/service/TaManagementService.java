/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.exception.AdminErrorCode;
import org.omnione.did.base.exception.OpenDidAdminException;
import org.omnione.did.base.property.SetupProperty;
import org.omnione.did.base.property.TasProperty;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.tas.v1.common.dto.admin.tas.RequestTasInfoResDto;
import org.omnione.did.tas.v1.common.dto.agent.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.common.dto.agent.tas.RequestEnrollTasReqDto.Request;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.service.TasService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

/**
 * This service provides methods for managing TA.
 *
 * @author : yklee0911
 * @fileName : TaManagementService
 * @since : 2/18/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaManagementService {

    private final TasQueryService tasQueryService;
    private final SetupProperty setupProperty;
    private final SetupService setupService;
    private final TasService tasService;
    private final TasProperty tasProperty;
    private final DidDocService didDocService;

    /**
     * Request TA information.
     *
     * @return TA information
     */
    public RequestTasInfoResDto requestTaInfo() {
        Tas tas = tasQueryService.findTasOrNull();
        log.debug("\t--> Found TAS: {}", tas);

        if (tas == null || tas.getStatus() == TasStatus.DID_DOCUMENT_REQUIRED) {
            return RequestTasInfoResDto.fromEntity(tas);
        }

        log.debug("\t--> Finding TAS DID Document");
        DidDocument tasDidDocument = findTasDidDocument();
        return RequestTasInfoResDto.fromEntity(tas, tasDidDocument);
    }

    /**
     * Register TA with simple process. (1st development version)
     *
     * @return TA information
     */
    public RequestTasInfoResDto registerTaSimple() {
        log.debug("=== Starting registerTaSimple ===");

        Tas tas = tasQueryService.findTasOrNull();
        log.debug("\t--> Found TAS: {}", tas);

        if (tas == null) {
            log.debug("\t--> TAS is not registered yet. Proceeding with new registration.");
            registerNewTas();
        } else {
            log.debug("\t--> Tas is already registered.");
            processExistingTasRegistration(tas);
        }

        Tas updatedTas = tasQueryService.findTas();

        log.debug("\t--> Finding TAS DID Document");
        DidDocument tasDidDocument = findTasDidDocument();

        log.debug("*** Finished registerTaSimple ***");
        return RequestTasInfoResDto.fromEntity(updatedTas, tasDidDocument);
    }

    /**
     * Register TA with simple process. (1st development version)
     */
    private void registerNewTas() {
        log.debug("\t--> Registering TA DID Document");
        registerTaDidDocument();
        log.debug("\t--> Registering TA Certificate");
        registerTaCertificate();
        log.debug("*** Finished registerTaSimple ***");
    }

    /**
     * Process existing TA registration. (1st development version)
     *
     * @param tas TA
     */
    private void processExistingTasRegistration(Tas tas) {
        switch (tas.getStatus()) {
            case DID_DOCUMENT_REQUIRED:
                log.debug("\t--> Registering TA DID Document");
                registerTaDidDocument();
                log.debug("\t--> Registering TA Certificate");
                registerTaCertificate();
                break;

            case CERTIFICATE_VC_REQUIRED:
                log.debug("\t--> Registering TA Certificate");
                registerTaCertificate();
                break;

            default:
                log.error("TA is already registered");
                throw new OpenDidAdminException(AdminErrorCode.TA_ALREADY_REGISTERED);
        }
    }

    /**
     * Register TA DID Document.
     */
    private void registerTaDidDocument() {
        File didDocFile = new File(setupProperty.getPath() + "tas.did");
        if (!didDocFile.exists() || !didDocFile.isFile()) {
            log.error("DID Document file not found at path: {}", setupProperty.getPath());
            throw new OpenDidAdminException(AdminErrorCode.UNKNOWN_SERVER_ERROR);
        }

        try {
            byte[] didDocBytes = Files.readAllBytes(didDocFile.toPath());
            setupService.registerTasDidDocument(didDocBytes);
        } catch (Exception e) {
            log.error("Failed to read DID Document file", e);
            throw new OpenDidAdminException(AdminErrorCode.FAILED_TO_REGISTER_TA_DID_DOCUMENT);
        }
    }

    /**
     * Register TA certificate.
     */
    private void registerTaCertificate() {
        try {
            RequestEnrollTasReqDto requestEnrollTasReqDto = RequestEnrollTasReqDto.builder()
                    .id("12345")
                    .request(Request.builder().password("VoOyEuOyal").build())
                    .build();

            tasService.requestEnrollTas(requestEnrollTasReqDto);
        } catch (Exception e) {
            log.error("Failed to register TA certificate", e);
            throw new OpenDidAdminException(AdminErrorCode.FAILED_TO_REGISTER_TA_CERTIFICATE);
        }
    }

    /**
     * Find TAS DID Document.
     *
     * @return TAS DID Document
     */
    private DidDocument findTasDidDocument() {
        return didDocService.getDidDocument(tasProperty.getDid());
    }
}
