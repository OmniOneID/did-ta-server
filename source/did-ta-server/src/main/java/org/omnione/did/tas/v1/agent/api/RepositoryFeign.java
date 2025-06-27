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

import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.tas.v1.agent.api.dto.InputVcSchemaReqDto;
import org.omnione.did.tas.v1.agent.api.dto.RegisterDidApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.UpdateDidDocStatusReqDto;
import org.omnione.did.tas.v1.agent.api.dto.UpdateVcMetaStatusReqDto;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for the Storage server.
 * This class was temporarily used instead of the BlockChain service and is no longer in use.
 */
@FeignClient(value = "Storage", url = "${lss.url:http://127.0.0.1:8098}" + UrlConstant.LSS.V1)
public interface RepositoryFeign {

    /**
     * Gets a DID document by its DID.
     *
     * @param did DID to get the document for.
     * @return Found DID document.
     */
    @GetMapping(UrlConstant.LSS.DID)
    String getDid(@RequestParam(name = "did") String did);

    /**
     * Registers a DID document.
     *
     * @param apiRegisterDidReqDto DID document to register.
     */
    @PostMapping(value = UrlConstant.LSS.DID, consumes = MediaType.APPLICATION_JSON_VALUE)
    void registerDid(@RequestBody String apiRegisterDidReqDto);

    @PatchMapping(UrlConstant.LSS.DID)
    ResponseEntity<EmptyResDto> updateDid(UpdateDidDocStatusReqDto updateDidDocStatusReqDto);

    /**
     * Gets metadata for a Verifiable Credential (VC) by its identifier.
     *
     * @param vcId Identifier of the Verifiable Credential.
     * @return Found VC metadata.
     */
    @GetMapping(UrlConstant.LSS.VC_META)
    String getVcMetaData(@RequestParam(name = "vcId") String vcId);

    @PostMapping(UrlConstant.LSS.VC_META)
    void registerVcMeta(@RequestBody VcMeta vcMeta);

    @PatchMapping(UrlConstant.LSS.VC_META)
    void updateVcMetaStatus(@RequestBody UpdateVcMetaStatusReqDto updateVcMetaStatus);

    /**
     * Register a VC Schema.
     * @param vcSchema the VC Schema to register
     */
    @PostMapping("/vc-schema")
    void registerVcSchema(InputVcSchemaReqDto vcSchema);

    /**
     * Get a VC Schema by schema-id
     * @param schemaId the credential schema id
     * @return the encoded Credential Schema
     */
    @GetMapping("/vc-schema")
    String getVcSchema(@RequestParam(name = "schemaId") String schemaId);
}
