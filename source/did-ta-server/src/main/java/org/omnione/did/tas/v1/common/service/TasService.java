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

package org.omnione.did.tas.v1.common.service;

import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasResDto;

import java.util.Map;

/**
 * Tas service interface for managing TAS enrollments.
 */
public interface TasService {
    RequestEnrollTasResDto requestEnrollTas(RequestEnrollTasReqDto requestEnrollTasReqDto);

    Map<String, Object> generateCertificate(String dn);
    RequestEnrollTasResDto requestEnrollTas(String certificate, RequestEnrollTasReqDto requestEnrollTasReqDto);
}
