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

package org.omnione.did.list.v1.agent.controller;

import org.omnione.did.base.constants.UrlConstant.List;
import org.omnione.did.list.v1.agent.dto.ca.AllowedCaResDto;
import org.omnione.did.list.v1.agent.dto.vcplan.RequestVcplanListResDto;
import org.omnione.did.list.v1.agent.dto.vcplan.VcPlanResDto;
import org.omnione.did.list.v1.agent.dto.vcschema.RequestVcSchemaListResDto;
import org.omnione.did.list.v1.agent.service.ListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling list-related requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = List.AGENT_V1)
public class ListVcController {
    private final ListService listService;

    /**
     * Retrieves the list of allowed Certificate Applications (CAs) package names for a given wallet.
     *
     * @param wallet The wallet identifier for which the allowed CAs are requested.
     * @return An {@link AllowedCaResDto} containing the list of allowed Certificate Applications.
     */
    @RequestMapping(value = "/allowed-ca/list", method = RequestMethod.GET)
    public AllowedCaResDto requestAllowedCa(@RequestParam String wallet) {
        return listService.findAllowedAppList(wallet);
    }

    /**
     * Retrieves the VC plan associated with a specific ID.
     *
     * @param id The identifier of the VC plan to retrieve.
     * @return A {@link VcPlanResDto} containing the details of the requested VC plan.
     */
    @RequestMapping(value = "/vcplan", method = RequestMethod.GET)
    public VcPlanResDto requestVcPlan(@RequestParam String id) {
        return listService.findVcPlan(id);
    }

    /**
     * Retrieves a list of VC plans filtered by the provided tags.
     * If the tags parameter is null or empty, the full list of VC plans will be retrieved.
     *
     * @param tags A list of tags used to filter the VC plans (optional). If null or empty, all VC plans will be retrieved.
     * @return A {@link RequestVcplanListResDto} containing the list of VC plans that match the provided tags, or the full list if no tags are provided.
     */
    @RequestMapping(value = "/vcplan/list", method = RequestMethod.GET)
    public RequestVcplanListResDto requestVcPlanListUserInit(@RequestParam(value = "tags[]", required = false) java.util.List<String> tags) {
        return listService.findVcPlanListUserInit(tags);
    }

    @RequestMapping(value = "/vcplan/list/issuer", method = RequestMethod.GET)
    public RequestVcplanListResDto requestVcPlanListIssuerInit(@RequestParam(value = "tags[]", required = false) java.util.List<String> tags) {
        return listService.findVcPlanListIssuerInit(tags);
    }



    /**
     * Retrieves a list of VC schemas.
     *
     * @return A {@link RequestVcSchemaListResDto} containing the list of VC schemas.
     */
    @RequestMapping(value = "/vcschema/list", method = RequestMethod.GET)
    public RequestVcSchemaListResDto requestVcSchemaList() {
        return listService.findVcSchemaList();
    }

    @GetMapping("/credential-schema")
    public ResponseEntity<CredentialSchema> getCredentialSchemaById(@RequestParam String credentialSchemaId) {
        return new ResponseEntity<>(listService.findCredentialSchemaByCredentialSchemaId(credentialSchemaId), HttpStatus.OK);
    }
}
