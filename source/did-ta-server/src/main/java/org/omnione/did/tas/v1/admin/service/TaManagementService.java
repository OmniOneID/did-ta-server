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

package org.omnione.did.tas.v1.admin.service;

import com.google.gson.JsonParseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module.Open;
import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.SetupProperty;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.tas.v1.admin.dto.tas.RequestTasInfoReqDto;
import org.omnione.did.tas.v1.admin.dto.tas.RequestTasInfoResDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto.Request;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.service.TasService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.v1.common.service.query.VcSchemaQueryService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
    private final DidDocService didDocService;
    private final VcSchemaRepository vcSchemaRepository;
    private final VcSchemaQueryService vcSchemaQueryService;

    /**
     * Request TA information.
     *
     * @return TA information
     */
    public RequestTasInfoResDto requestTaInfo() {
        Tas existedTas = tasQueryService.findTasOrNull();
        log.debug("\t--> Found TAS: {}", existedTas);

        if (existedTas == null || existedTas.getStatus() == TasStatus.DID_DOCUMENT_REQUIRED) {
            return RequestTasInfoResDto.fromEntity(existedTas);
        }

        log.debug("\t--> Finding TAS DID Document");
        DidDocument tasDidDocument = findTasDidDocument(existedTas);
        return RequestTasInfoResDto.fromEntity(existedTas, tasDidDocument);
    }

    /**
     * Register TA with simple process. (1st development version)
     *
     * @return TA information
     */
    public RequestTasInfoResDto registerTaSimple(RequestTasInfoReqDto requestTasInfoReqDto) {
        log.debug("=== Starting registerTaSimple ===");

        log.debug("\t--> Finding TAS");
        Tas tas = tasQueryService.findTasOrNull();
        log.debug("\t--> Found TAS: {}", tas);

        if (tas == null) {
            log.debug("\t--> TAS is not registered yet. Proceeding with new registration.");
            registerNewTas(requestTasInfoReqDto);
        } else {
            log.debug("\t--> Tas is already registered.");
            processExistingTasRegistration(tas, requestTasInfoReqDto);
        }

        Tas updatedTas = tasQueryService.findTas();

        log.debug("\t--> Finding TAS DID Document");
        DidDocument tasDidDocument = findTasDidDocument(updatedTas);

        log.debug("*** Finished registerTaSimple ***");
        return RequestTasInfoResDto.fromEntity(updatedTas, tasDidDocument);
    }

    /**
     * Register TA with simple process. (1st development version)
     */
    private void registerNewTas(RequestTasInfoReqDto requestTasInfoReqDto) {
        log.debug("\t--> Registering TA DID Document");
        registerTaDidDocument(requestTasInfoReqDto.getServerUrl());

        log.debug("\t--> Registering Certificate VC Schema");
        registerCertificateVcSchema(requestTasInfoReqDto.getServerUrl());

        log.debug("\t--> Registering TA Certificate");
        registerTaCertificate();
        log.debug("*** Finished registerTaSimple ***");
    }

    /**
     * Process existing TA registration. (1st development version)
     *
     * @param tas TA
     */
    private void processExistingTasRegistration(Tas tas, RequestTasInfoReqDto requestTasInfoReqDto) {
        switch (tas.getStatus()) {
            case DID_DOCUMENT_REQUIRED:
                log.debug("\t--> Registering TA DID Document");
                registerTaDidDocument(requestTasInfoReqDto.getServerUrl());

                log.debug("\t--> Registering Certificate VC Schema");
                registerCertificateVcSchema(requestTasInfoReqDto.getServerUrl());

                log.debug("\t--> Registering TA Certificate");
                registerTaCertificate();
                break;

            case CERTIFICATE_VC_REQUIRED:
                log.debug("\t--> Registering TA Certificate");
                registerTaCertificate();
                break;

            default:
                log.error("TA is already registered");
                throw new OpenDidException(ErrorCode.TA_ALREADY_REGISTERED);
        }
    }

    /**
     * Register TA DID Document.
     */
    private void registerTaDidDocument(String serverUrl) {
        File didDocFile = new File(setupProperty.getPath() + "/TA/tas.did");
        if (!didDocFile.exists() || !didDocFile.isFile()) {
            log.error("DID Document file not found at path: {}", setupProperty.getPath());
            throw new OpenDidException(ErrorCode.FILE_NOT_FOUND);
        }

        try {
            byte[] didDocBytes = Files.readAllBytes(didDocFile.toPath());
            String certificateUrl = serverUrl + "/tas/api/v1/certificate-vc";
            setupService.registerTasDidDocument(didDocBytes, "tas", serverUrl, certificateUrl);
        } catch (IOException e) {
            log.error("I/O error while reading TA DID Document file", e);
            throw new OpenDidException(ErrorCode.FILE_IO_ERROR);
        } catch (OpenDidException e) {
            log.error("OpenDID error while registering TA DID Document", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while registering TA DID Document", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_TA_DID_DOCUMENT);
        }
    }

    private void registerCertificateVcSchema(String serverUrl) {
        try {
            if (vcSchemaQueryService.findByVcTypeOrNull(VcType.CERTIFICATE_VC) != null) {
                return;
            }

            String vcSchemaJson = """
                    {
                        "@id": "%s/tas/api/v1/vc-schema?name=certificate",
                        "@schema": "https://opendid.org/schema/vc.osd",
                        "title": "OpenDID Certificate Verifiable Credential",
                        "description": "VC-formatted OpenDID enrollment certificate.",
                        "metadata": {
                            "language": "ko",
                            "formatVersion": "1.0"
                        },
                        "credentialSubject": {
                            "claims": [{
                                "namespace": {
                                    "id": "org.opendid.v1",
                                    "name": "OpenDID - Certificate Verifiable Credential"
                                },
                                "items": [
                                    {"id": "subject", "caption": "subject", "type": "text", "format": "plain"},
                                    {"id": "role", "caption": "role", "type": "text", "format": "plain"}
                                ]
                            }]
                        }
                    }
                    """.formatted(serverUrl);

            org.omnione.did.data.model.schema.VcSchema vcSchema =
                    BaseCoreVcUtil.parseVcSchema(vcSchemaJson);

            vcSchemaRepository.save(VcSchema.builder()
                    .type(VcType.CERTIFICATE_VC)
                    .schema(vcSchema.getSchema())
                    .schemaId(vcSchema.getId())
                    .version(vcSchema.getMetadata()
                            .getFormatVersion())
                    .schema(vcSchema.toJson())
                    .build());
        } catch (JsonParseException | ClassCastException e) {
            log.error("Failed to parse Certificate VC Schema JSON", e);
            throw new OpenDidException(ErrorCode.PARSE_VC_SCHEMA_FAILED);
        } catch (DataAccessException e) {
            log.error("Database error while saving Certificate VC Schema", e);
            throw new OpenDidException(ErrorCode.DB_ERROR_ON_VC_SCHEMA_SAVE);
        } catch (Exception e) {
            log.error("Unexpected error while registering Certificate VC Schema", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_CERTIFICATE_VC_SCHEMA);
        }
    }

    /**
     * Register TA certificate.
     */
    private void registerTaCertificate() {
        try {
            RequestEnrollTasReqDto requestEnrollTasReqDto = RequestEnrollTasReqDto.builder()
                    .id("12345")
                    .request(Request.builder()
                            .password("VoOyEuOyal")
                            .build())
                    .build();

            tasService.requestEnrollTas(requestEnrollTasReqDto);
        } catch (OpenDidException e) {
            log.error("Failed to enroll TA", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while enrolling TA", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_TA_CERTIFICATE);
        }
    }

    /**
     * Find TAS DID Document.
     *
     * @return TAS DID Document
     */
    private DidDocument findTasDidDocument(Tas tas) {
        return didDocService.getDidDocument(tas.getDid());
    }
}
