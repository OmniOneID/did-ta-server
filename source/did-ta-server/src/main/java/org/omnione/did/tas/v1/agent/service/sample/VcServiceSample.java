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

package org.omnione.did.tas.v1.agent.service.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.omnione.did.base.datamodel.data.E2e;
import org.omnione.did.base.datamodel.enums.VerifyAuthType;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.tas.v1.agent.service.VcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmRevokeVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcEmailReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcNotiResDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcPushReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcQrReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeRevokeVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueProfileReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueProfileResDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueVcResDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestRevokeVcResDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * this is a sample implementation of the VcService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("sample")
public class VcServiceSample implements VcService {
    @Override
    public ProposeIssueVcResDto proposeIssueVc(ProposeIssueVcReqDto proposeIssueVcReqDto) {
        String refId = "1234567890ABCDEFGHIJ";
        String txId = "99999999-9999-9999-9999-999999999999";

        return ProposeIssueVcResDto.builder()
                .refId(refId)
                .txId(txId)
                .build();
    }

    @Override
    public RequestIssueProfileResDto requestIssueProfile(RequestIssueProfileReqDto requestIssueProfileReqDto) {

       ObjectMapper objectMapper = new ObjectMapper();
       String response = """
               {"authNonce":"mrF7S5K8vnr+TPL0h4dwbxA","profile":{"description":"Mobile Driver License","encoding":"UTF-8","id":"8f7fc1ee-7aec-4dd3-94ca-57e7162485ce","language":"ko","profile":{"credentialSchema":{"id":"http://192.168.3.130:8091/issuer/api/v1/vc/vcschema?name=mdl","type":"OsdSchemaCredential"},"issuer":{"certVcRef":"http://192.168.3.130:8091/issuer/api/v1/certificate-vc","did":"did:omn:issuer","name":"issuer"},"process":{"endpoints":["http://192.168.3.130:8091/issuer"],"issuerNonce":"mgM7zQA4IgIYefiLMqkdPcQ","reqE2e":{"cipher":"AES-256-CBC","curve":"Secp256r1","nonce":"mgM7zQA4IgIYefiLMqkdPcQ","padding":"PKCS5","publicKey":"mAnkp5UjBqUnBCkW0pe9jA2jh4bU8v8NhRfN2hhqmPN+w"}}},"proof":{"created":"2024-09-05T17:09:48.542686Z","proofPurpose":"assertionMethod","proofValue":"mH5W1Ix7hcGBneQUFy854p/yBDHAy3+sj1zTu7gqMdLrQcNudaA31U5Pr9YS+o9OA93jlAy7JziutugxwgPqGMZA","type":"Secp256r1Signature2018","verificationMethod":"did:omn:issuer#assert"},"title":"Mobile Driver License","type":"IssueProfile"},"txId":"33d0ac8d-3392-4fcc-88bc-1652fe5fed76"}
               """;
        try {
            return objectMapper.readValue(response, RequestIssueProfileResDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RequestIssueVcResDto requestIssueVc(RequestIssueVcReqDto requestIssueVcReqDto) {
        return RequestIssueVcResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .e2e(E2e.builder()
                        .iv("z75M7MfQsC4p2rTxeKxYh2M")
                        .encVc("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")
                        .build())
                .build();
    }

    @Override
    public ConfirmIssueVcResDto confirmIssueVc(ConfirmIssueVcReqDto confirmIssueVcReqDto) {
        return ConfirmIssueVcResDto.builder().
                txId("99999999-9999-9999-9999-999999999999").
                build();
    }
    @Override
    public OfferIssueVcResDto offerIssueVcQr(OfferIssueVcQrReqDto request) {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = """
                {"issueOfferPayload":{"issuer":"did:omn:issuer","offerId":"91931795-346c-41ad-bf03-5a1ac6420dc8","type":"IssueOffer","validUntil":"2024-09-05T08:12:45.509427Z","vcPlanId":"vcplanid000000000001"},"offerId":"91931795-346c-41ad-bf03-5a1ac6420dc8","validUntil":"2024-09-05T08:12:45.509427Z"}
                """;
        try {
            return objectMapper.readValue(response, OfferIssueVcResDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OfferIssueVcNotiResDto offerIssueVcEmail(OfferIssueVcEmailReqDto request) {
        String qrImg = "data:PNG;base64,iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAABO0lEQVR4Xu2W0a3FIAxDu0H337Ib5JE4CbzoDoAlWwgF59AfQ9vnab1m37PGkpmt5W6dEjZ0Iba6IfejTqCW7ghjw7b5eeiuL51/gDBCDLUz540WxoyhhQPQu+YsjAGDzuVT0WdLGBfWQtbn+ClhQ/dhmS5UX1vc6NMfS2FjeRW2c4/Z+o5//v3tIYwI80bAKLrGltxVXWEUWJsY5/Y+AMK4sITDQTcBKydIYUQYgO1E9GeRTxBGhEGrtn1/fWFV10OE0WDvjhh8Oh19PUcYExa5uwLuYvjCmLBxBir30xdWEAGGbCHYWUfumK1f2mgJuxvbet3EcB/1OAbCKDCEi5TrrymXPQtjw3KuoJ081QBmYXRYvLS9iNxR4DwIo8RiuAk+tmCXMC4s62KGL4wPayH6krd7yylhQ9dhfwRaevkHFSGQAAAAAElFTkSuQmCC";
        Map<String, String> emailData = new HashMap<>();
        emailData.put("name", "Sample Issuer");
        emailData.put("qrImg", qrImg);

        return OfferIssueVcNotiResDto.builder()
                .offerId("99999999-9999-9999-9999-999999999999")
                .validUntil("2030-01-01T09:00:00Z")
                .build();
    }

    @Override
    public OfferIssueVcNotiResDto offerIssueVcPush(OfferIssueVcPushReqDto request) {
        return null;
    }

    @Override
    public String requestCertificateVc() {
        return null;
    }

    @Override
    public Map<String, Object>  requestVcSchema(String id, String name) {
        return null;
    }

    @Override
    public ProposeRevokeVcResDto proposeRevokeVc(ProposeRevokeVcReqDto proposeRevokeVcReqDto) {
        return ProposeRevokeVcResDto.builder()
                .txId("4bc7e7b9-e666-4a90-9eef-783a33326fd9")
                .issuerNonce("mgvugfUjgDu8wrwb3aeqIFw")
                .authType(VerifyAuthType.PIN_OR_BIO)
                .build();
    }

    @Override
    public RequestRevokeVcResDto requestRevokeVc(RequestRevokeVcReqDto requestRevokeVcReqDto) {
        return RequestRevokeVcResDto.builder()
                .txId("4bc7e7b9-e666-4a90-9eef-783a33326fd9")
                .build();
    }

    @Override
    public ConfirmRevokeVcResDto confirmRevokeVc(ConfirmRevokeVcReqDto confirmRevokeVcReqDto) {
        return ConfirmRevokeVcResDto.builder()
                .txId("4bc7e7b9-e666-4a90-9eef-783a33326fd9")
                .build();
    }
}
