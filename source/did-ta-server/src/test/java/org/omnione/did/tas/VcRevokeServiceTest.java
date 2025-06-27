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
import org.omnione.did.base.datamodel.data.ReqRevokeVc;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ConfirmRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.ProposeRevokeVcReqDto;
import org.omnione.did.tas.v1.agent.dto.vc.RequestRevokeVcReqDto;
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

@DisplayName("Revoke VC Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = TasApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
public class VcRevokeServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @Test
    @DisplayName("Propose revoke Vc Test")
    void testProposeRevokeVc() throws Exception {
        // 1. Set request DTO
        ProposeRevokeVcReqDto reqDto = new ProposeRevokeVcReqDto();
        reqDto.setId("20240905171312628000DF5BF781");
        reqDto.setVcId("5dbebdf0-6b36-4bdf-9ceb-436262986ac9");

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/propose-revoke-vc")
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
    @DisplayName("Request ECDH Test")
    void testEcdh() throws Exception {
        String reqEcdhJson = """
                {"client":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t","clientNonce":"zSegsxXNQqEAQxkMszDTnGe","curve":"Secp256r1","proof":{"created":"2024-09-05T08:13:12Z","proofPurpose":"keyAgreement","proofValue":"z3m2ApFRTgdrBa22TcSekBSwGgsGrqqQM62tYdbQUwivf9chd5YG3QQzxUBJzJpQQ8fEz2iwZMPP8bq8zhRDumhSNJ","type":"Secp256r1Signature2018","verificationMethod":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t?versionId=1#keyagree"},"publicKey":"z29oq32fXbVvD88aDkhMLGibis7Q4wQVusuuebmvdQ3N8G"}
                """;
        EcdhReqData ecdhReqData = objectMapper.readValue(reqEcdhJson, EcdhReqData.class);
        RequestECDHReqDto reqDto = RequestECDHReqDto.builder()
                .id("202409051713129440007B9BB6B2")
                .txId("4bc7e7b9-e666-4a90-9eef-783a33326fd9")
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
    @DisplayName("Create Token Test")
    void testCreateToken() throws Exception {
        String req = """
                {"id":"202409051713135030009A02C148","seed":{"caAppInfo":{"appId":"202409Btz6cMklY2a","nonce":"mba89KNRDoJKr7eH6kv60mg","proof":{"created":"2024-09-05T17:13:13.067488Z","proofPurpose":"assertionMethod","proofValue":"mIAX852dupvgF3P6JsvDNuWwjM1KrRySGBbnVrOzbXIcBE4T42/thIvHNXRZiocTFhCAt21QgUtJRCVCu1xse+lE","type":"Secp256r1Signature2018","verificationMethod":"did:omn:cas?versionId=1#assert"},"provider":{"certVcRef":"http://192.168.3.130:8094/cas/api/v1/certificate-vc","did":"did:omn:cas"}},"purpose":9,"walletInfo":{"nonce":"z12N48Lbt8cBWtRWSBe41Z4","proof":{"created":"2024-09-05T08:13:13Z","proofPurpose":"assertionMethod","proofValue":"z3oUFPoVwmjZ221gToC6BxFkwYpBQ4qb1AQhJwZBTUvKH4qvim9KfZ9ARGvxRJNGx7UH2j7Vx16uyXg35R4oeBCPT5","type":"Secp256r1Signature2018","verificationMethod":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8?versionId=1#assert"},"wallet":{"did":"did:omn:3yybwkGEF46BXaqhXSJDhWE7ptN8","id":"WID202409HFaOFhPdgvY"}}},"txId":"4bc7e7b9-e666-4a90-9eef-783a33326fd9"}
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
    @DisplayName("Request Revoke Vc Test")
    void testRequestRevokeVc() throws Exception {
        // 1. Set request DTO
        String request = """
                {"issuerNonce":"mgvugfUjgDu8wrwb3aeqIFw","proof":{"created":"2024-09-05T08:13:15Z","proofPurpose":"assertionMethod","proofValue":"z3jziv1Z4zsjYiuS9xp4vrEevtH9uHuENFMthJCmQ1eRiBAEYdDqm7CwAfGcat8bsuiJqAbSC468zh1yvCsGGD9jnL","type":"Secp256r1Signature2018","verificationMethod":"did:omn:gagws6YDE6qAGac2MsjPkAQah3t?versionId=1#pin"},"vcId":"5dbebdf0-6b36-4bdf-9ceb-436262986ac9"}
                """;
        ReqRevokeVc reqRevokeVc = objectMapper.readValue(request, ReqRevokeVc.class);
        RequestRevokeVcReqDto reqDto = new RequestRevokeVcReqDto();
        reqDto.setId("2024090517131595900028442B84");
        reqDto.setTxId("4bc7e7b9-e666-4a90-9eef-783a33326fd9");
        reqDto.setRequest(reqRevokeVc);
        reqDto.setServerToken("mN4iPCsqBBNuMymZDSZoPdqAVbl2sU8bOM8E8rJCMdj0");

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-revoke-vc")
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

    @Order(5)
    @Test
    @DisplayName("Confirm Revoke Vc Test")
    void testConfirmRevokeVc() throws Exception {
        // 1. Set request DTO
        ConfirmRevokeVcReqDto reqDto = new ConfirmRevokeVcReqDto();
        reqDto.setId("20240905171318372000859B2879");
        reqDto.setServerToken("mN4iPCsqBBNuMymZDSZoPdqAVbl2sU8bOM8E8rJCMdj0");
        reqDto.setTxId("4bc7e7b9-e666-4a90-9eef-783a33326fd9");

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/confirm-revoke-vc")
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
