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
import org.omnione.did.tas.v1.admin.dto.RequestTasInfoResDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto.Request;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.service.TasService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

/**
 * Please explain the class!!
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

    public RequestTasInfoResDto requestTaInfo() {
        Tas tas = tasQueryService.findTas();
        return RequestTasInfoResDto.fromEntity(tas);
    }

    public void registerTaSimple() {
        log.debug("=== Starting registerTaSimple ===");

        long count = tasQueryService.countByDid(tasProperty.getDid());
        log.debug("\t--> Tas is not registered yet. ");

        if (count == 0) {
            log.debug("\t--> Registering TA DID Document");
            registerTaDidDocument();
            log.debug("\t--> Registering TA Certificate");
            registerTaCertificate();
            log.debug("*** Finished registerTaSimple ***");
            return;
        }

        Tas tas = tasQueryService.findTas();
        log.debug("\t--> Found TAS: {}", tas);

        if (tas.getStatus() == TasStatus.DID_DOCUMENT_REQUIRED) {
            log.debug("\t--> Registering TA DID Document");
            registerTaDidDocument();
            log.debug("\t--> Registering TA Certificate");
            registerTaCertificate();
        } else if (tas.getStatus() == TasStatus.CERTIFICATE_VC_REQUIRED) {
            log.debug("\t--> Registering TA Certificate");
            registerTaCertificate();
        } else {
            log.error("TA is already registered");
            throw new OpenDidAdminException(AdminErrorCode.TA_ALREADY_REGISTERED);
        }

        log.debug("*** Finished registerTaSimple ***");
    }

    private void registerTaDidDocument() {
        File didDocFile = new File(setupProperty.getPath() + "/tas.did");
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
}
