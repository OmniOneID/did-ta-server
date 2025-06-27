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
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityReqDto;
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

@DisplayName("Enroll Entity Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = TasApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
public class EnrollEntityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @Test
    @DisplayName("Propose Enroll Entity Test")
    void testProposeEnrollEntity() throws Exception {
        // 1. Set request DTO
        ProposeEnrollEntityReqDto reqDto = new ProposeEnrollEntityReqDto();
        reqDto.setId("20240905105721157000631bff19");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/propose-enroll-entity")
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
    @DisplayName("Request ECDH Test")
    void testEcdh() throws Exception {
        String reqEcdhJson = """
                {"candidate":{"ciphers":["AES-128-CBC","AES-128-ECB","AES-256-CBC","AES-256-ECB"]},"client":"did:omn:issuer","clientNonce":"mazS27bP/XeZl1EDoF4E6sw","curve":"Secp256r1","proof":{"created":"2024-09-05T10:57:21.392505Z","proofPurpose":"keyAgreement","proofValue":"mICxEbCOvZ3rVWtb33O4MREY+I53TZh1LV4mEZKjxYLTYUIzbzzB+zA6DD47saZFWTYuces4ZlEMNp/WyEUz6Kps","type":"Secp256r1Signature2018","verificationMethod":"did:omn:issuer?versionId=1#keyagree"},"publicKey":"zp7E4rrt57ELyyDfNWhMpLeeCK9i6T4bq26PmKihkxK66"}
                """;
        EcdhReqData ecdhReqData = objectMapper.readValue(reqEcdhJson, EcdhReqData.class);
        RequestECDHReqDto reqDto = RequestECDHReqDto.builder()
                .id("202409051057215930001a8e8722")
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

    @Order(3)
    @Test
    @DisplayName("Request Enroll Entity Test")
    void testRequestEnrollEntity() throws Exception {
        // 1. Set request DTO
        String didAuthJson = """
                {"didAuth":{"authNonce":"mxbrrv9EupAFUumfyXD9Vag","did":"did:omn:issuer","proof":{"created":"2024-09-05T10:57:21.813619Z","proofPurpose":"authentication","proofValue":"mH9aszl/8gg+HLq0KLp3nHPOwmWNjL+KWOTXdtfAFvCfmPC40cten2OuclmkYKu9+ucRdljU4CVvF+hLMt0CEyYM","type":"Secp256r1Signature2018","verificationMethod":"did:omn:issuer?versionId=1#auth"}}}
                """;
        DidAuth didAuth = objectMapper.readValue(didAuthJson, DidAuth.class);

        RequestEnrollEntityReqDto reqDto = new RequestEnrollEntityReqDto();
        reqDto.setDidAuth(didAuth);
        reqDto.setId("202409051057218230006d1055cf");
        reqDto.setTxId("b86855ad-6793-4e15-bd1c-d44c01a87ee8");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-enroll-entity")
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
    @DisplayName("Confirm Enroll Entity Test")
    void testConfirmEnrollEntity() throws Exception {
        // 1. Set request DTO
        ConfirmEnrollEntityReqDto reqDto = new ConfirmEnrollEntityReqDto();
        reqDto.setId("20240905105724406000f16623c3");
        reqDto.setTxId("b86855ad-6793-4e15-bd1c-d44c01a87ee8");
        reqDto.setVcId("d0a11e31-5068-491e-8de3-24bad1463f08");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/confirm-enroll-entity")
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
