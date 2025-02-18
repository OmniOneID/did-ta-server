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

package org.omnione.did.tas.v1.agent.api;

import org.omnione.did.tas.v1.agent.api.dto.RegisterDidApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.DidDocApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.VcMetaApiResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for the Storage server.
 * This class was temporarily used instead of the BlockChain service and is no longer in use.
 */
@FeignClient(value = "Storage", url = "http://127.0.0.1:8097/repository", path = "/api/v1")
public interface RepositoryFeign {

    /**
     * Gets a DID document by its DID.
     *
     * @param did DID to get the document for.
     * @return Found DID document.
     */
    @GetMapping("/did-doc")
    DidDocApiResDto getDid(@RequestParam(name = "did") String did);

    /**
     * Gets metadata for a Verifiable Credential (VC) by its identifier.
     *
     * @param vcId Identifier of the Verifiable Credential.
     * @return Found VC metadata.
     */
    @GetMapping("/vc-meta")
    VcMetaApiResDto getVcMetaData(@RequestParam(name = "vcId") String vcId);

    /**
     * Registers a DID document.
     *
     * @param apiRegisterDidReqDto DID document to register.
     */
    @PostMapping("/did-doc")
    void registerDid(@RequestBody RegisterDidApiReqDto apiRegisterDidReqDto);
}
