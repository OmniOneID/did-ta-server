package org.omnione.did.tas.v1.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.constants.UrlConstant.Tas;
import org.omnione.did.base.property.SetupProperty;
import org.omnione.did.tas.v1.admin.dto.RequestTasInfoResDto;
import org.omnione.did.tas.v1.admin.service.TaManagementService;
import org.omnione.did.tas.v1.agent.dto.common.EmptyResDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto.Request;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasResDto;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.service.TasService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : TaManagementController
 * @since : 2/18/25
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = Tas.ADMIN_V1)
public class TaManagementController {
    private final TaManagementService taManagementService;
    private final TasService tasService;
    private final SetupService setupService;
    private final SetupProperty setupProperty;


    @RequestMapping(value = "/ta/info", method = RequestMethod.GET)
    public RequestTasInfoResDto requestTaInfo() {
        return taManagementService.requestTaInfo();
    };

    @RequestMapping(value = "/ta/diddoc", method = RequestMethod.POST)
    public EmptyResDto registerTasDidDocument() {
        File didDocFile = new File(setupProperty.getPath() + "/tas.did");

        if (!didDocFile.exists() || !didDocFile.isFile()) {
            log.error("DID Document file not found at path: {}", setupProperty.getPath());
            throw new RuntimeException("DID Document file not found.");
        }

        try {
            byte[] didDocBytes = Files.readAllBytes(didDocFile.toPath());
            return setupService.registerTasDidDocument(didDocBytes);
        } catch (IOException e) {
            log.error("Failed to read DID Document file", e);
            throw new RuntimeException("Failed to read DID Document file", e);
        }
    }

    @RequestMapping(value = "/ta/certificate", method = RequestMethod.POST)
    @ResponseBody
    public RequestEnrollTasResDto issueTasCertificate() {

        RequestEnrollTasReqDto requestEnrollTasReqDto = RequestEnrollTasReqDto.builder()
                .id("12345")
                .request(Request.builder().password("VoOyEuOyal").build())
                .build();

        return tasService.requestEnrollTas(requestEnrollTasReqDto);
    }
}
