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

package org.omnione.did.tas.v1.common.service.sample;

import org.omnione.did.base.db.repository.TasRepository;
import org.omnione.did.tas.v1.common.service.TasService;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * this is a sample implementation of the TasService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("sample")
public class TasServiceSample implements TasService {
    private final TasRepository tasRepository;
    @Override
    public RequestEnrollTasResDto requestEnrollTas(RequestEnrollTasReqDto requestEnrollTasReqDto) {
        return RequestEnrollTasResDto.builder()
                .certVcRef("http://192.168.3.130:8090/tas/api/v1/certificate-vc")
                .txId("ee559cb8-17e6-44b5-9ed7-aa7088c9bac9")
                .build();
    }

    @Override
    public Map<String, Object> generateCertificate(String dn) {
        return null;
    }

    @Override
    public RequestEnrollTasResDto requestEnrollTas(String certificate, RequestEnrollTasReqDto requestEnrollTasReqDto) {
        return null;
    }
}
