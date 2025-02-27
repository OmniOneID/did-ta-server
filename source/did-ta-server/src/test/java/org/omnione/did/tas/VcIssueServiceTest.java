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
import org.omnione.did.base.datamodel.data.EcdhReqData;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcQrReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeIssueVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueProfileReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestIssueVcReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Issue VC Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = TasApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
public class VcIssueServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @Test
    @DisplayName("Offer Issue Vc Request QR Test")
    void testOfferIssueVcQr() throws Exception {
        // 1. Set request DTO
        OfferIssueVcQrReqDto reqDto = new OfferIssueVcQrReqDto();
        reqDto.setId("2024090517094582900020C99687");
        reqDto.setVcPlanId("vcplanid000000000001");
        reqDto.setIssuer("did:omn:issuer");

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/offer-issue-vc/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. Confirm actual response
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }

    @Order(2)
    @Test
    @DisplayName("Propose Issue Vc Request Test")
    void testProposeIssueVc() throws Exception {
        // 1. Set request DTO
        ProposeIssueVcReqDto reqDto = ProposeIssueVcReqDto.builder()
                .id("2024090517094742400079C99687")
                .vcPlanId("vcplanid000000000001")
                .issuer("did:omn:issuer")
                .offerId("91931795-346c-41ad-bf03-5a1ac6420dc8")
                .build();

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/propose-issue-vc")
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
    @DisplayName("Request ECDH Test")
    void testEcdh() throws Exception {
        String reqEcdhJson = """
                {"client":"did:omn:2kEDLDEjCxNUCPBL4VMJ7hmDAdHc","clientNonce":"zBAW92K2iAdSxNDwAw3H3Xx","curve":"Secp256r1","proof":{"created":"2024-09-05T07:57:27Z","proofPurpose":"keyAgreement","proofValue":"z3rUktt9bdVsZ65sbN9oReDNK7YE9jJLHmt5DzCDedYAwrx5Dym47QbpTRnx3UWwnYyQ669W7LqYD6ULq827f2izPa","type":"Secp256r1Signature2018","verificationMethod":"did:omn:2kEDLDEjCxNUCPBL4VMJ7hmDAdHc?versionId=1#keyagree"},"publicKey":"z2BFUEHLriaZHCWowJ2u5zhdX5xfMXQuBbUzCUYo5btwg7"}
                """;
        EcdhReqData ecdhReqData = objectMapper.readValue(reqEcdhJson, EcdhReqData.class);
        RequestECDHReqDto reqDto = RequestECDHReqDto.builder()
                .id("202409051657277950001BB998C7")
                .txId("b86855ad-6793-4e15-bd1c-d44c01a87ee8")
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

    @Order(4)
    @Test
    @DisplayName("Create Token Test")
    void testCreateToken() throws Exception {
        String req = """
                {"id":"20240905170947993000861DAFA8","seed":{"caAppInfo":{"appId":"202409Btz6cMklY2a","nonce":"mo89FVpdpHTebEmxWTowoJg","proof":{"created":"2024-09-05T17:09:47.641218Z","proofPurpose":"assertionMethod","proofValue":"mH3t/sR8N4MSR6X1xbf/M9UsN9vZJJ9e9rkofDASJKJBfF/YiEXi1fXZxm0d8nnnEfZiOHfQapIZXahg6sj79Wbk","type":"Secp256r1Signature2018","verificationMethod":"did:omn:cas?versionId=1#assert"},"provider":{"certVcRef":"http://192.168.3.130:8094/cas/api/v1/certificate-vc","did":"did:omn:cas"}},"purpose":8,"walletInfo":{"nonce":"zH89qcQKR7aWHtBEgBP5yGw","proof":{"created":"2024-09-05T08:09:47Z","proofPurpose":"assertionMethod","proofValue":"z3rVubHq8Qf6xCV91t3YHir4jmiu1q6YqrY5bFZLMdfGD6jBSsRA35s5cv3gYomiPNmkr1S5tq4jLnWTRjHoUbuzY9","type":"Secp256r1Signature2018","verificationMethod":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8?versionId=1#assert"},"wallet":{"did":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8","id":"WID202409HFaOFhPdgvY"}}},"txId":"33d0ac8d-3392-4fcc-88bc-1652fe5fed76"}
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
    @Order(5)
    @Test
    @DisplayName("Generate Issue Profile Test")
    void testGenerateIssueProfile() throws Exception {
        // 1. Set request DTO
        RequestIssueProfileReqDto reqDto = new RequestIssueProfileReqDto();
        reqDto.setId("2024090517094848900077CC2981");
        reqDto.setTxId("33d0ac8d-3392-4fcc-88bc-1652fe5fed76");
        reqDto.setServerToken("mr9B+5H6PbgSCNilyvvdbwJA05P/LyvC7ijTVkid7qGU");

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-issue-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. Confirm actual response
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }

    @Order(6)
    @Test
    @DisplayName("Request Issue Vc Test")
    void testRequestIssueVc() throws Exception {
        // 1. Set request DTO
        String request = """
                {"accE2e":{"iv":"zH75TaC7Gm5iyR16vURZzQV","proof":{"created":"2024-09-05T08:09:51Z","proofPurpose":"keyAgreement","proofValue":"z3rAMDs9cQbnjBoZyuofzRpiBeD4V8kKs8o3Qqa5M43AQvHz1mrRe1PqhMhVq9ZUrnmwr1cErvVcEEhes8BP9RRUYY","type":"Secp256r1Signature2018","verificationMethod":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t?versionId=1#keyagree"},"publicKey":"z23VZ8fxjN2nQLEEdSfG1wy9ZTRhvbSKQXHgntw8K85oKe"},"didAuth":{"authNonce":"mrF7S5K8vnr+TPL0h4dwbxA","did":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t","proof":{"created":"2024-09-05T08:09:51Z","proofPurpose":"authentication","proofValue":"z3jgUargprtpToWL5yGhwU4tNQGET6QUAwMYMDHCM9YGViqExdr7pF3WuHu5CtbuUwsn84uoc3DWHnr86Mjgn679FZ","type":"Secp256r1Signature2018","verificationMethod":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t?versionId=1#pin"}},"encReqVc":"zKrq4BRRZACyGjzbJoHUbbTdfw6mitYQmoiyMiB9V4KTV8YDFQQhLvq5aAFKekBfDceX4YJY9b4ADC3L7kBi1jdiR5A79zrcJtScDAf27xFi7ZTvTeqQ2moS1FbprfaFuUr3zBgVsb8MNEP9inpE6Ko8ix2LfYEuAmkLdBmAeotSc8JA","id":"2024090517095134400058236747","serverToken":"mr9B+5H6PbgSCNilyvvdbwJA05P/LyvC7ijTVkid7qGU","txId":"33d0ac8d-3392-4fcc-88bc-1652fe5fed76"}
                """;
        RequestIssueVcReqDto reqDto = objectMapper.readValue(request, RequestIssueVcReqDto.class);


        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-issue-vc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. Confirm actual response
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }

    @Order(7)
    @Test
    @DisplayName("Confirm Issue Vc Test")
    void testConfirmIssueVc() throws Exception {
        // 1. Set request DTO
        ConfirmIssueVcReqDto reqDto = ConfirmIssueVcReqDto.builder()
                .id("20240905170954522000C2C48F3E")
                .serverToken("mr9B+5H6PbgSCNilyvvdbwJA05P/LyvC7ijTVkid7qGU")
                .vcId("3f0d51d9-57d3-45d0-98a4-a66ae875ced3")
                .txId("33d0ac8d-3392-4fcc-88bc-1652fe5fed76")
                .build();

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/confirm-issue-vc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. Confirm actual response
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }
}
