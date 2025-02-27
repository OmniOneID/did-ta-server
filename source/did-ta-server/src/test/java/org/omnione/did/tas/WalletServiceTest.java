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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.omnione.did.TasApplication;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.data.AttestedDidDoc;
import org.omnione.did.tas.v1.agent.dto.wallet.RegisterWalletReqDto;
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

@DisplayName("Register Wallet")
@SpringBootTest(classes = TasApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
public class WalletServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Request Register Wallet Test")
    void testRequestRegisterWallet() throws Exception {
        // 1. Set request DTO
        String response = """
                {"nonce":"462cf788a0de320fbc289fa2e6034605","ownerDidDoc":"ueyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvbnMvZGlkL3YxIl0sImFzc2VydGlvbk1ldGhvZCI6WyJhc3NlcnQiXSwiYXV0aGVudGljYXRpb24iOlsiYXV0aCJdLCJjb250cm9sbGVyIjoiZGlkOm9tbjp0YXMiLCJjcmVhdGVkIjoiMjAyNC0wOS0wNVQwODoxMToxMloiLCJkZWFjdGl2YXRlZCI6ZmFsc2UsImlkIjoiZGlkOm9tbjo0R052ZUxrdmJ1R29naVdKbXRNa3ZocWV0QUQzIiwia2V5QWdyZWVtZW50IjpbImtleWFncmVlIl0sInByb29mcyI6W3siY3JlYXRlZCI6IjIwMjQtMDktMDVUMDg6MTE6MTJaIiwicHJvb2ZQdXJwb3NlIjoiYXNzZXJ0aW9uTWV0aG9kIiwicHJvb2ZWYWx1ZSI6Im1IOTA0MFdUL1Z3SHZFL1dIdXp3U1VsazJwOEwrRDlEeHJrcDEyMEllWEkrK1dpTHV1TCtvVklFeEJvMXdDaU8rZEdjZUY3T01nT1AyU0tFODVIRmpPZjQ9IiwidHlwZSI6IlNlY3AyNTZyMVNpZ25hdHVyZTIwMTgiLCJ2ZXJpZmljYXRpb25NZXRob2QiOiJkaWQ6b21uOjRHTnZlTGt2YnVHb2dpV0ptdE1rdmhxZXRBRDM_dmVyc2lvbklkPTEjYXNzZXJ0In0seyJjcmVhdGVkIjoiMjAyNC0wOS0wNVQwODoxMToxMloiLCJwcm9vZlB1cnBvc2UiOiJhdXRoZW50aWNhdGlvbiIsInByb29mVmFsdWUiOiJtSDBpWnJXaWYrdzBmVlo1eWxHcHFTMGVMZGdKRGtKNVFjRFMycFJUc0g2eGVMa3pWd1JNRlB2cktZZGsvNng4Nm5XNjFER2JValdQV0NDQ2F3b3BTcldZPSIsInR5cGUiOiJTZWNwMjU2cjFTaWduYXR1cmUyMDE4IiwidmVyaWZpY2F0aW9uTWV0aG9kIjoiZGlkOm9tbjo0R052ZUxrdmJ1R29naVdKbXRNa3ZocWV0QUQzP3ZlcnNpb25JZD0xI2F1dGgifV0sInVwZGF0ZWQiOiIyMDI0LTA5LTA1VDA4OjExOjEyWiIsInZlcmlmaWNhdGlvbk1ldGhvZCI6W3siYXV0aFR5cGUiOjEsImNvbnRyb2xsZXIiOiJkaWQ6b21uOnRhcyIsImlkIjoia2V5YWdyZWUiLCJwdWJsaWNLZXlNdWx0aWJhc2UiOiJtQTNlTnViQ2hXTnRGTkhCVDdCREVTRm15Ulc1V0hRK1gyOFZmUGdnTWVDYkQiLCJ0eXBlIjoiU2VjcDI1NnIxVmVyaWZpY2F0aW9uS2V5MjAxOCJ9LHsiYXV0aFR5cGUiOjEsImNvbnRyb2xsZXIiOiJkaWQ6b21uOnRhcyIsImlkIjoiYXV0aCIsInB1YmxpY0tleU11bHRpYmFzZSI6Im1BaTdNQWE3MEwxSGdnd0t4MER1V0NoQWVEOGpodlZkVklKSDBjemJSc0tnbSIsInR5cGUiOiJTZWNwMjU2cjFWZXJpZmljYXRpb25LZXkyMDE4In0seyJhdXRoVHlwZSI6MSwiY29udHJvbGxlciI6ImRpZDpvbW46dGFzIiwiaWQiOiJhc3NlcnQiLCJwdWJsaWNLZXlNdWx0aWJhc2UiOiJtQXFMWitISDl5S0FOSENyYU1sSGU0eFAySCtkTHpnYUpFc1pIWG54dVh4d3AiLCJ0eXBlIjoiU2VjcDI1NnIxVmVyaWZpY2F0aW9uS2V5MjAxOCJ9XSwidmVyc2lvbklkIjoiMSJ9","proof":{"created":"2024-09-05T17:11:11.612228Z","proofPurpose":"assertionMethod","proofValue":"mH21MpWJ4O2CrFa9zQKMnATapF/S2ySIBiqdK9SAKYogqCKlug0XcYPdh0rT/mTOm6u8pteftNzwE7RQmHkt5S8A","type":"Secp256r1Signature2018","verificationMethod":"did:omn:wallet?versionId=1#assert"},"provider":{"certVcRef":"http://192.168.3.130:8095/wallet/api/v1/certificate-vc","did":"did:omn:wallet"}}
                """;
        AttestedDidDoc attestedDidDoc = objectMapper.readValue(response, AttestedDidDoc.class);

        RegisterWalletReqDto reqDto = new RegisterWalletReqDto();
        reqDto.setId("6dcdde42-c0d9-4f79-82fb-128a94ce709b");
        reqDto.setAttestedDidDoc(attestedDidDoc);

        // 2. Call controller and verify response
        MvcResult result = mockMvc.perform(post(UrlConstant.Tas.AGENT_V1 + "/request-register-wallet")
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
