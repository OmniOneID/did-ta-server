/*
 * Copyright 2024 OmniOne.
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
import org.omnione.did.tas.v1.common.dto.agent.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.common.dto.agent.entity.RequestECDHResDto;
import org.omnione.did.tas.v1.agent.service.EcdhService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * this is a sample implementation of the EcdhService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("sample")
public class EcdhServiceSample implements EcdhService {
    @Override
    public RequestECDHResDto requestECDH(RequestECDHReqDto requestECDHReqDto) {
        ObjectMapper objectMapper = new ObjectMapper();

        String reqEcdhJson = """
                {"accEcdh":{"cipher":"AES-256-CBC","padding":"PKCS5","proof":{"created":"2024-09-05T10:57:21.788620Z","proofPurpose":"keyAgreement","proofValue":"mHwUcQPenuvmgl+4enG0dwBiQ+IZxTIF3X9c0PRCZuXTHBPNL0iC6R7dG5+AUXKd5nbWb6ZsCtPccVL+me7wU+34","type":"Secp256r1Signature2018","verificationMethod":"did:omn:tas#keyagree"},"publicKey":"mAjCb4gPcBIzLlCXCDaAB+MGCxRh6LouwBI4tTqVkQb/b","server":"did:omn:tas","serverNonce":"mG7HZMlbiFRZ8xRimSMKiDg"},"txId":"b86855ad-6793-4e15-bd1c-d44c01a87ee8"}
                """;
        try {
            return objectMapper.readValue(reqEcdhJson, RequestECDHResDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
