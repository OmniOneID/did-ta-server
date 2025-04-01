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

package org.omnione.did.tas;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.omnione.did.TasApplication;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.data.DidAuth;
import org.omnione.did.base.datamodel.data.EcdhReqData;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidEmailReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRestoreDidDocReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DID Restore Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = TasApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
class DidRestoreTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @Test
    @DisplayName("Offer Restore Did Email")
    void testOfferRestoreDidEmail() throws Exception {
        // 1. Set request DTO
        OfferRestoreDidEmailReqDto reqDto = new OfferRestoreDidEmailReqDto();
        reqDto.setDid("did:omn:gagws6YDE6qAGac2MsjPkAQah3t");
        reqDto.setId("202409061024378200004A6EE7C2");
        reqDto.setEmail("test@example.com");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/offer-restore-did/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        // 3. Actual response check
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }


    @Order(2)
    @Test
    @DisplayName("Request ECDH")
    void testEcdh() throws Exception {
        String reqEcdhJson = """
                {"client":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8","clientNonce":"zW9wYU7dzYfwzYq2HsKTXrH","curve":"Secp256r1","proof":{"created":"2024-09-06T01:24:37Z","proofPurpose":"keyAgreement","proofValue":"z3m3xwu53VLbznUa8CyAS4MvC54ueCw8zJq4UgWpeqYjfqzKa9evCbQEKqkXH5MBv6uaWWY1Ah8Ftbo6tRGwRoVvnA","type":"Secp256r1Signature2018","verificationMethod":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8?versionId=1#keyagree"},"publicKey":"zh7epe6WHeSYjQybGL3ctmaYRDtXR8uMyBoHWaMSEjqZT"}
                """;
        EcdhReqData ecdhReqData = objectMapper.readValue(reqEcdhJson, EcdhReqData.class);
        RequestECDHReqDto reqDto = RequestECDHReqDto.builder()
                .id("20240906102437450000382635A9")
                .txId("cad7a1e8-0e27-47f3-b9dd-b6590a349852")
                .reqEcdh(ecdhReqData)
                .build();

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-ecdh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. Actual response check
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }


    @Order(3)
    @Test
    @DisplayName("Create Token")
    void testCreateToken() throws Exception {
        String req = """
                {"id":"202409061024378200004A6EE7C2","seed":{"caAppInfo":{"appId":"202409Btz6cMklY2a","nonce":"mr8H6sYyMnNHT3Urz6HQgaw","proof":{"created":"2024-09-06T10:24:37.604775Z","proofPurpose":"assertionMethod","proofValue":"mIMrVCiyWS8Tp2rHUaaw0JfQfgfaIKB7n7mUTzFPRbqgsRQ/VtcfXq672G7pBLqctJEyaaZarvbOAiibJqdUFXLM","type":"Secp256r1Signature2018","verificationMethod":"did:omn:cas?versionId=1#assert"},"provider":{"certVcRef":"http://192.168.3.130:8094/cas/api/v1/certificate-vc","did":"did:omn:cas"}},"purpose":7,"walletInfo":{"nonce":"zLJoyk4d9cKHK5gcqrYxsvm","proof":{"created":"2024-09-06T01:24:37Z","proofPurpose":"assertionMethod","proofValue":"z3phZ31Z8jSYeRGZrwdggBz8L6sr69mQgu4k89HegX36AxqJLxqK6dYizFwCu5apUy3e958Rxha3MKwdhK1CtMihRR","type":"Secp256r1Signature2018","verificationMethod":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8?versionId=1#assert"},"wallet":{"did":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8","id":"WID202409HFaOFhPdgvY"}}},"txId":"cad7a1e8-0e27-47f3-b9dd-b6590a349852"}
                """;
        RequestCreateTokenReqDto reqDto = objectMapper.readValue(req, RequestCreateTokenReqDto.class);

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-create-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. Actual response check
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }

    @Order(4)
    @Test
    @DisplayName("Propose Restore DID doc")
    void testProposeRestoreDidDoc() throws Exception {
        // 1. Set request DTO
        ProposeRestoreDidDocReqDto reqDto = new ProposeRestoreDidDocReqDto();
        reqDto.setDid("did:omn:gagws6YDE6qAGac2MsjPkAQah3t");
        reqDto.setId("202409061024373320008E32CA36");
        reqDto.setOfferId("aae54cdf-0412-4878-bd32-b9745dd60482");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/propose-restore-diddoc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        // 3. Actual response check
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }



    @Order(5)
    @Test
    @DisplayName("Request Restore DID doc")
    void testRequestRestoreDidDoc() throws Exception {
        // 1. Set request DTO
        String didAuthJson = """
                {"authNonce":"mXx4ZICWZNczC1jpFHXyxDA","did":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t","proof":{"created":"2024-09-06T01:24:40Z","proofPurpose":"authentication","proofValue":"z3ndb7oW3ZpCLKgewocEPFxx6iWgdqAwBMFT6p4Wf4a89HS9jDdAa67Cyo1SYyNYPQpgUk3E3DfKMVU61TzdftAn1Q","type":"Secp256r1Signature2018","verificationMethod":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t?versionId=1#pin"}}
                """;
        DidAuth didAuth = objectMapper.readValue(didAuthJson, DidAuth.class);

        RequestRestoreDidDocReqDto reqDto = new RequestRestoreDidDocReqDto();
        reqDto.setId("2024090610244042600051401B88");
        reqDto.setTxId("cad7a1e8-0e27-47f3-b9dd-b6590a349852");
        reqDto.setServerToken("muIA3jnftOaSIZt499pH0Zr3CWNDhZ6bXMOCB6i74HgY");
        reqDto.setDidAuth(didAuth);

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-restore-diddoc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        // 3. Actual response check
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }

    @Order(6)
    @Test
    @DisplayName("Confirm Restore DID doc")
    void testConfirmRestoreDidDoc() throws Exception {
        // 1. Set request DTO
        ConfirmRestoreDidDocReqDto reqDto = new ConfirmRestoreDidDocReqDto();
        reqDto.setId("20240906102442670000A4A355AF");
        reqDto.setTxId("cad7a1e8-0e27-47f3-b9dd-b6590a349852");
        reqDto.setServerToken("muIA3jnftOaSIZt499pH0Zr3CWNDhZ6bXMOCB6i74HgY");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/confirm-restore-diddoc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        // 3. Actual response check
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }

}
