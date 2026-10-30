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
package org.omnione.did.tas.v1.admin.controller;

import org.omnione.did.base.annotation.ArticleLog;
import org.omnione.did.base.constants.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant.Tas;
import org.omnione.did.tas.v1.admin.dto.tas.*;
import org.omnione.did.tas.v1.admin.service.TaManagementService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * This controller provides APIs for managing TA.
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

    /**
     * Request TA information.
     *
     * @return TA information
     */
    @RequestMapping(value = "/ta/info", method = RequestMethod.GET)
    public TasInfoResDto requestTaInfo() {
        return taManagementService.requestTaInfo();
    };

    /**
     * Register TA with simple process. (1st development version)
     *
     * @return TA information
     */
    @RequestMapping(value = "/ta/register-simple", method = RequestMethod.POST)
    @ArticleLog(name = "[TA Admin] Register Simple TA", description = "Register Simple TA.",
            targetType = "TA_ADMIN", actionType = ActionType.CREATE)
    public TasInfoResDto registerTaSimple(@RequestBody RequestTasInfoReqDto requestTasInfoReqDto) {
        return taManagementService.registerTaSimple(requestTasInfoReqDto);
    }

    @RequestMapping(value = "/ta/validate-ta-secret", method = RequestMethod.POST)
    @ArticleLog(name = "[TA Admin] Validate TA Secret", description = "Validate TA Secret.",
            targetType = "TA_ADMIN", actionType = ActionType.CREATE)
    public EmptyResDto validateTaSecret(@RequestBody ValidateTaSecretReqDto validateTaSecretReqDto) {
        return taManagementService.validateTaSecret(validateTaSecretReqDto);
    }

    @RequestMapping(value = "/ta/register-ta-info", method = RequestMethod.POST)
    @ArticleLog(name = "[TA Admin] Register TA Info", description = "Register TA Info.",
            targetType = "TA_ADMIN", actionType = ActionType.CREATE)
    public TasInfoResDto registerTaInfo(@RequestBody RegisterTaInfoReqDto registerTaInfoReqDto) {
        return taManagementService.registerTaInfo(registerTaInfoReqDto);
    }

    @RequestMapping(value = "/ta/generate-did-auto", method = RequestMethod.POST)
    @ArticleLog(name = "[TA Admin] Generate DID Auto", description = "Generate DID info.",
            targetType = "TA_ADMIN", actionType = ActionType.CREATE)
    public Map<String, Object> generateTaDidDocumentAuto() {
        return taManagementService.registerTaDidDocumentAuto();
    }

    @RequestMapping(value = "/ta/register-ta-did", method = RequestMethod.POST)
    @ArticleLog(name = "[TA Admin] Register TA DID Document", description = "Register TA DID Document.",
            targetType = "TA_ADMIN", actionType = ActionType.CREATE)
    public EmptyResDto registerTaDidDocument(@RequestBody RegisterTaDidDocumentReqDto registerTaDidDocumentReqDto) {
        return taManagementService.registerTaDidDocument(registerTaDidDocumentReqDto);
    }

    @RequestMapping(value = "/ta/generate-certificate", method = RequestMethod.POST)
    @ArticleLog(name = "[TA Admin] Generate TA Certificate", description = "Generate TA Certificate.",
            targetType = "TA_ADMIN", actionType = ActionType.CREATE)
    public Map<String, Object> generateTaCertificate(@RequestBody GenerateTaCertificateReqDto generateTaCertificateReqDto) {
        return taManagementService.generateTaCertificate(generateTaCertificateReqDto);
    }

    @RequestMapping(value = "/ta/certificate", method = RequestMethod.GET)
    public Map<String, Object> requestTaCertificate() {
        return taManagementService.requestTaCertificateVC();
    }

    @RequestMapping(value = "/ta/certificate", method = RequestMethod.POST)
    @ArticleLog(name = "[TA Admin] Register Certificate for Entity", description = "Register Certificate for Entity.",
            targetType = "TA_ADMIN", actionType = ActionType.CREATE)
    public EmptyResDto registerCertificate(@RequestBody RegisterTaCertificateReqDto registerTaCertificateReqDto) {
        return taManagementService.registerTaCertificate(registerTaCertificateReqDto);
    }
}
