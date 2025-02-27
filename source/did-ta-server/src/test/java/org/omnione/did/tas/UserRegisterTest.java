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
import org.omnione.did.base.datamodel.data.SignedDidDoc;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RetrieveKycReqDto;
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

@DisplayName("User Register Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = TasApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
class UserRegisterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @Test
    @DisplayName("Propose Register User Test")
    void testProposeRegisterUser() throws Exception {
        // 1. Set request DTO
        ProposeRegisterUserReqDto reqDto = new ProposeRegisterUserReqDto();
        reqDto.setId("20240905165727669000CDD0FA74");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/propose-register-user")
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

    @Order(3)
    @Test
    @DisplayName("Create Token Test")
    void testCreateToken() throws Exception {
        String req = """
                {"id":"202409051657283100001C7EAC45","seed":{"caAppInfo":{"appId":"202409G2u67yk1X8l","nonce":"mcUm9ylVEgYk/NalyZbOjoA","proof":{"created":"2024-09-05T16:57:27.893047Z","proofPurpose":"assertionMethod","proofValue":"mILBzHDclcVNpLm/y7sjd+cT8epOOXnF75kpmbg9cgZruYXQmvz2oC2E1by8+OJPPv3vG9NeB9zlzKEcR9GQEvQU","type":"Secp256r1Signature2018","verificationMethod":"did:omn:cas?versionId=1#assert"},"provider":{"certVcRef":"http://192.168.3.130:8094/cas/api/v1/certificate-vc","did":"did:omn:cas"}},"purpose":5,"walletInfo":{"nonce":"zBkNQ6fiQ6hM772BJRDHGW8","proof":{"created":"2024-09-05T07:57:28Z","proofPurpose":"assertionMethod","proofValue":"z3uLoJjDsTMvK7RFW28Eeq46MKDe8uUcEnbsiRMez98bqfX2B13zEohNoNq6TXagJ2uUt7GJrHBEAbzE5V42FT3hqp","type":"Secp256r1Signature2018","verificationMethod":"did:omn:2kEDLDEjCxNUCPBL4VMJ7hmDAdHc?versionId=1#assert"},"wallet":{"did":"did:omn:2kEDLDEjCxNUCPBL4VMJ7hmDAdHc","id":"WID202409fZzUMQO359p"}}},"txId":"61e4164d-939d-4252-b2f4-5026c8225a3b"}
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
    @DisplayName("Retrieve KYC Test")
    void testRetrieveKyc() throws Exception {
        // 1. Set request DTO
        RetrieveKycReqDto reqDto = new RetrieveKycReqDto();
        reqDto.setId("61e4164d-939d-4252-b2f4-5026c8225a3b");
        reqDto.setTxId("b28a35a0");
        reqDto.setKycTxId("b28a35a0");
        reqDto.setServerToken("mCpmk2VhUL6Q8aBerIxm1CaGv86eWoH7toZQKhz8Te6g");


        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/retrieve-kyc")
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
    @DisplayName("Request Register User Test")
    void testRequestRegisterUser() throws Exception {
        // 1. Set request DTO
        String signedDidDocJson = """
                {"nonce":"zwnCp7CDerpkza7bPDNsx8","ownerDidDoc":"z5HwRWXrRb8sLAdWBxZtQcN9JR588dnpvojhXG2BCRYC9CX9Xpq6RokkvXzGQYYCqAKC2EkFPgaA3TzWCsCvVUZnk6Cz33Xbyie2wsytg3mUQ5hvQnvRpqVucbrSCanHbc9JYVsT9knFqFqdSc9MsUitAHCo5H1uQGzEnSGUuGiatJeMzTEZmJAkfBSMSnAsZ3h5cYkW4S3XfqkpLPdX41r5ZG1UmkXE9TLNsnwCL9XP5D7K1ND6VTHi2Xqg82mFeynC64dje559VqQkUHMqNCviFa9NzkieS5jstDyCkiK5vafB4pgwWp7AJitQF7iHUw2o395UfRSwaqVYnYzis3oUiT79WxYQ1AbaYPS1fQNFjfQWpGgemXRTzjpnPcCaeC1n6aj3hgSoYsLuuuTDuz3knxw6nxW2nUVhnL8fTN68y88V373u1vnZ3edS6L8risucq8fTxPjaZ1zkJjbmNc4Wi99WVG7aYpYCdnpe6P1xaubkH7LNF7iYgTAcmHPLGLZnV8PU76Vzmbz4W9JtC3PBqCJnt8BrwrpW3CKVFBC17KYWA5PHvRKgwFkm4TE82gCsGdbZrN3GK8wS7Ugb3DiLvYydcuzm68JLZhqmkdH27DFCBFqyCHrAwNvM6gLNH9mKC9RnmqVAUYToD7TU3J6BabsD836tEEqQEobYTaTwbGwLCURRxYJELQ3ifcDbTXGTdfV9yBTe1sTrZPiAjbgzAz8vmiFLkUyqDPsDdyXznhBjZqXWK3cyvMCrRXg3JPoxFmHHM9exwz8LKWmVLCx9DYpirtrPPFPFy96qYHDFK3pANwfGLcjhLEXXPrbQrfubGpHQcXk3RNtU9WYErng8qNgnPFjbxNFSMwgwdbZVQKMaZz2aQMyqkZjK9JCcaxEJUBh5kTh12X1BAv9FtddFivF9xDjejRSmkqwSsTUPn2pG74ujmVn2vjLaaDou2ohWnDJKCi6eSZn91Lnvi8rBqEMvfwrKn6fYhCtPqUgArZHxaL9gXZPXKWPAbtjewa5JTbBqZZCW6v5jKeYc4Ans6CoiPRkHdHXKXqxSQ4yZU57zsBC7XyQwKyfkHyN6oW755qwdnwC5bW56H2RheTkgYXppMJQtBMZT95N5rpZRunc4zsJcDaQJ9yjPzThfSfHdZvB6Fq7wE7KM3UpgJiPKx165ipYyuSQq5C1HrZUY58PWFPd67Njp5LKJfg8EzaF4QZ3VGFd69p9nsEPsiggN6u17jZj5SokDYfkREWzQRReWQvYTjycubQwsmHZ1HmN58tAh9b8U6wYHmWEH6MVSVYaEcSQq2Uu3HXBtFXgb8ZtgqGUukByyPf3n3Evztd7i","proof":{"created":"2024-09-05T07:57:36Z","proofPurpose":"assertionMethod","proofValue":"z3tCpV6S5SjZhdzsgfAHmSEq3wRp3xmPcaaq31Hz86Vq8hHpqJ1wEmWtV9cQsiN7aUaWhpbJjkLigPLqPUKUUtbn1s","type":"Secp256r1Signature2018","verificationMethod":"did:omn:2kEDLDEjCxNUCPBL4VMJ7hmDAdHc?versionId=1#assert"},"wallet":{"did":"did:omn:2kEDLDEjCxNUCPBL4VMJ7hmDAdHc","id":"WID202409fZzUMQO359p"}}
                """;
        SignedDidDoc signedDidDoc = objectMapper.readValue(signedDidDocJson, SignedDidDoc.class);
        RequestRegisterUserReqDto reqDto = new RequestRegisterUserReqDto();
        reqDto.setId("20240905165736842000502ABE65");
        reqDto.setTxId("61e4164d-939d-4252-b2f4-5026c8225a3b");
        reqDto.setServerToken("mCpmk2VhUL6Q8aBerIxm1CaGv86eWoH7toZQKhz8Te6g");
        reqDto.setSignedDidDoc(signedDidDoc);

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-register-user")
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
    @DisplayName("Confirm Register User Test")
    void testConfirmRegisterUser() throws Exception {
        // 1. Set request DTO
        ConfirmRegisterUserReqDto reqDto = new ConfirmRegisterUserReqDto();
        reqDto.setId("202409051657391450005463C74A");
        reqDto.setTxId("61e4164d-939d-4252-b2f4-5026c8225a3b");
        reqDto.setServerToken("mCpmk2VhUL6Q8aBerIxm1CaGv86eWoH7toZQKhz8Te6g");

        // 2.  Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/confirm-register-user")
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
