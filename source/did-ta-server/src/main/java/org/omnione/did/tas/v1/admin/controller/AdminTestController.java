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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.db.domain.Kyc;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.tas.v1.agent.api.dto.RetrievePiiApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.RetrievePiiApiResDto;
import org.omnione.did.tas.v1.admin.dto.entity.SendCertificateVcReqDto;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.query.ApiQueryService;
import org.omnione.did.tas.v1.common.service.query.KycQueryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.ADMIN_V1)
public class AdminTestController {
    private final KycQueryService kycQueryService;
    private final ApiQueryService apiQueryService;

    @RequestMapping(value = "/certificate-vc", method = RequestMethod.POST)
    public EmptyResDto getCertificateVc(@RequestBody SendCertificateVcReqDto sendCertificateVcReqDto) {
        byte[] decodedVc = BaseMultibaseUtil.decode(sendCertificateVcReqDto.getCertificateVc());
        log.debug("Decoded VC: {}", new String(decodedVc));

        return new EmptyResDto();
    }

    @RequestMapping(value = "/pii", method = RequestMethod.GET)
    public String getUserPid(@RequestParam String userId) {
        log.debug("pii: {}", userId);

        Kyc kyc = kycQueryService.findKyc();

        RetrievePiiApiReqDto apiRetrievePiiReqDto = RetrievePiiApiReqDto.builder()
                .userId(userId)
                .build();

        try {
            String request = JsonUtil.serializeToJson(apiRetrievePiiReqDto);
            RetrievePiiApiResDto retrievePiiApiResDto = HttpClientUtil.postData(kyc.getServerUrl() + "/api/v1/retrieve-pii", request, RetrievePiiApiResDto.class);

            return retrievePiiApiResDto.getPii();
        }  catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending retrieve-pii request:: {}", e.getMessage(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }  catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to retrieve PII information: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    private ErrorResponse convertExternalErrorResponse(String resBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resBody, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse external error response: {}", resBody, e);
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    @RequestMapping(value = "/token-expiration", method = RequestMethod.GET)
    public String getTokenExpirationTime() {
        String tokenValidUntil = DateTimeUtil.addSecondsToCurrentTimeString(apiQueryService.findTokenExpirationTime());
        return tokenValidUntil;
    }

    @RequestMapping(value = "/transaction-expiration", method = RequestMethod.GET)
    public Instant getTransactionExpirationTime() {
        return Instant.now().plus(apiQueryService.findTransactionExpirationTime(), ChronoUnit.SECONDS);
    }

    @RequestMapping(value = "/cipher-type", method = RequestMethod.GET)
    public SymmetricCipherType getCipherType() {
        SymmetricCipherType cipherType = apiQueryService.findCipherType();
        return cipherType;
    }

    @RequestMapping(value = "/padding-type", method = RequestMethod.GET)
    public SymmetricPaddingType getPaddingType() {
        SymmetricPaddingType paddingType = apiQueryService.findPaddingType();
        return paddingType;
    }
}
