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

package org.omnione.did.tas.v1.agent.api;

import org.omnione.did.tas.v1.agent.api.dto.RetrievePiiApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.RetrievePiiApiResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for the KYC server.
 * In the demo, the CAS server is used as the KYC server.
 */
@FeignClient(value = "Kyc", url = "${kyc.url}", path = "/api/v1")
public interface KycFeign {

    /**
     * Sends a request to retrieve PII data.
     *
     * @param reqDto The request DTO for retrieving PII data.
     * @return The response DTO containing the retrieved PII data.
     */
    @PostMapping("/retrieve-pii")
    RetrievePiiApiResDto retrievePii(@RequestBody RetrievePiiApiReqDto reqDto);
}
