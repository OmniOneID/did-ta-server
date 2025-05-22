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

package org.omnione.did.list.v1.agent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.VcPlan;
import org.omnione.did.base.datamodel.enums.InitiateType;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.base.db.repository.ListAllowedCaRepository;
import org.omnione.did.base.db.repository.ListVcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.list.v1.admin.dto.vcschema.ListVcSchemaDto;
import org.omnione.did.list.v1.admin.service.query.ListCredentialDefinitionQueryService;
import org.omnione.did.list.v1.admin.service.query.ListCredentialSchemaQueryService;
import org.omnione.did.list.v1.admin.service.query.ListVcPlanQueryService;
import org.omnione.did.list.v1.agent.dto.ca.AllowedCaResDto;
import org.omnione.did.list.v1.agent.dto.server.RequestTaDidResDto;
import org.omnione.did.list.v1.agent.dto.vcplan.RequestVcplanListResDto;
import org.omnione.did.list.v1.agent.dto.vcplan.VcPlanResDto;
import org.omnione.did.list.v1.agent.dto.vcschema.RequestVcSchemaListResDto;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing lists of allowed CAs, VC plans, and related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListService {
    private final ListAllowedCaRepository listAllowedCaRepository;
    private final ListVcSchemaRepository listVcSchemaRepository;
    private final ListCredentialDefinitionQueryService listCredentialDefinitionQueryService;
    private final ListCredentialSchemaQueryService listCredentialSchemaQueryService;
    private final ListVcPlanQueryService listVcPlanQueryService;
    private final TasQueryService tasQueryService;

    /**
     * Finds the list of allowed CAs for a given wallet service ID.
     *
     * @param walletServiceId The ID of the wallet service
     * @return AllowedCaResDto The response DTO containing the list of allowed CAs
     * @throws OpenDidException if there's an error retrieving the allowed CA list
     */
    public AllowedCaResDto findAllowedAppList(String walletServiceId) {
        try {
            log.debug("=== Starting findAllowedAppList ===");

            Optional<ListAllowedCa> allowedCa = listAllowedCaRepository.findByWalletId(walletServiceId);
            List<String> caList = JsonUtil.deserializeFromJson(allowedCa.get().getCaList(), new TypeReference<>() {
            });

            return AllowedCaResDto.builder()
                    .count(caList.size())
                    .items(caList)
                    .build();
        } catch (CommonSdkException e) {
            log.error("\t--> JSON processing error: ", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_ALLOWED_CA_LIST);
        } catch (Exception e) {
            log.error("\t--> An unknown error occurred retrieving allowed ca list: ", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_ALLOWED_CA_LIST);
        }
    }

    /**
     * Finds a VC plan by its ID.
     *
     * @param vcPlanId The ID of the VC plan to find
     * @return VcPlanResDto The response DTO containing the VC plan
     * @throws OpenDidException if there's an error retrieving the VC plan
     */
    public VcPlanResDto findVcPlan(String vcPlanId) {
        try {
            log.debug("=== Starting findVcPlan ===");

            ListVcPlan exisingListVcPlan = listVcPlanQueryService.findByVcPlanId(vcPlanId);
            VcPlan vcPlan = JsonUtil.deserializeFromJson(exisingListVcPlan.getVcPlan(), VcPlan.class);

            log.debug("*** Finished findVcPlan ***");

            return VcPlanResDto.builder()
                    .vcPlan(vcPlan)
                    .build();
        } catch (OpenDidException e) {
            log.error("\t--> An unknown error occurred retrieving vc plan", e);
            throw e;
        } catch (Exception e) {
            log.error("\t--> An unknown error occurred retrieving vc plan", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_VCPLAN);
        }
    }

    /**
     * Finds a list of VC plans filtered by tags.
     * If the tags parameter is null or empty, the full list of VC plans will be returned.
     *
     * @param tags The list of tags to filter the VC plans (optional).
     * @return The response DTO containing the filtered list of VC plans.
     * @throws OpenDidException if there's an error retrieving the VC plan list.
     */
    public RequestVcplanListResDto findVcPlanListUserInit(List<String> tags) {
        log.debug("=== Starting findAllVcPlanList ===");

        List<ListVcPlan> exsingVcPlanList = listVcPlanQueryService.findAllByInitiate(InitiateType.USER_INIT.getType());


        return getRequestVcplanListResDto(exsingVcPlanList, tags);
    }


    /**
     * Finds a list of VC plans filtered by tags.
     * If the tags parameter is null or empty, the full list of VC plans will be returned.
     *
     * @param tags The list of tags to filter the VC plans (optional).
     * @return The response DTO containing the filtered list of VC plans.
     * @throws OpenDidException if there's an error retrieving the VC plan list.
     */
    public RequestVcplanListResDto findVcPlanListIssuerInit(List<String> tags) {
        log.debug("=== Starting findAllVcPlanList ===");

        List<ListVcPlan> exsingVcPlanList = listVcPlanQueryService.findAllByInitiate(InitiateType.ISSUER_INIT.getType());

        return getRequestVcplanListResDto(exsingVcPlanList, tags);
    }

    public RequestVcplanListResDto getRequestVcplanListResDto(List<ListVcPlan> exsingVcPlanList, List<String> tags) {
        try {
            List<VcPlan> vcPlanList = exsingVcPlanList.stream()
                    .map(vcPlan ->
                            JsonUtil.deserializeFromJson(vcPlan.getVcPlan(), VcPlan.class)
                    )
                    .collect(Collectors.toList());

            List<VcPlan> filteredVcPlanList = filterVcPlanList(vcPlanList, tags);

            log.debug("*** Finished findAllVcPlanList ***");

            return RequestVcplanListResDto.builder()
                    .count(filteredVcPlanList.size())
                    .items(filteredVcPlanList)
                    .build();

        } catch (OpenDidException e) {
            log.error("\t--> An unknown error occurred retrieving all vc plans", e);
            throw e;
        } catch (Exception e) {
            log.error("\t--> An unknown error occurred retrieving all vc plans", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_VCPLAN_LIST);
        }
    }

    /**
     * Filters a list of VC plans based on the provided tags.
     *
     * @param vcPlanList The list of VC plans to filter
     * @param tags       The tags to filter by
     * @return List<VcPlan> The filtered list of VC plans
     */
    private List<VcPlan> filterVcPlanList(List<VcPlan> vcPlanList, List<String> tags) {
        List<VcPlan> filteredVcPlanList = null;
        if (tags != null && !tags.isEmpty() && vcPlanList != null) {
            filteredVcPlanList = vcPlanList.stream().filter(vcPlan -> shouldRemove(vcPlan, tags))
                    .collect(Collectors.toList());
        } else {
            filteredVcPlanList = vcPlanList;
        }

        if (filteredVcPlanList == null) {
            filteredVcPlanList = new ArrayList<>();
        }

        return filteredVcPlanList;
    }

    /**
     * Determines whether a VC plan should be removed based on its tags.
     *
     * @param vcPlan The VC plan to check
     * @param tags   The list of tags to check against
     * @return boolean True if the VC plan should be removed, false otherwise
     */
    private boolean shouldRemove(VcPlan vcPlan, List<String> tags) {
        if (vcPlan == null) return true;

        List<String> vcPlanTags = vcPlan.getTags();
        if (vcPlanTags == null) return true;

        log.debug("result: " + vcPlanTags.stream().noneMatch(tags::contains));

        return vcPlanTags.stream().allMatch(tags::contains);
    }


    public RequestVcSchemaListResDto findVcSchemaList() {
        List<ListVcSchema> vcSchemaList = listVcSchemaRepository.findAll();

        List<ListVcSchemaDto> vcSchemaDtoList = vcSchemaList.stream()
                .map(ListVcSchemaDto::fromListVcSchemaForAgent)
                .collect(Collectors.toList());

        return RequestVcSchemaListResDto.builder()
                .count(vcSchemaList.size())
                .vcSchemaList(vcSchemaDtoList)
                .build();
    }

    // @TODO: Change the logic to actually retrieve the DID
    public RequestTaDidResDto requestTaDid() {
        Tas existedTasInfo = tasQueryService.findTas();
        return RequestTaDidResDto.builder()
                .did(existedTasInfo.getDid())
                .build();
    }

    public CredentialSchema findCredentialSchemaByCredentialDefinitionId(String credentialDefinitionId) {
        String credentialSchemaId = findCredentialDefinitionByCredentialDefinitionId(credentialDefinitionId)
                .getSchemaId();
        String credentialSchemaJson = listCredentialSchemaQueryService.findByCredentialSchemaId(credentialSchemaId).getCredentialSchema();
        return GsonWrapper.getGson().fromJson(credentialSchemaJson, CredentialSchema.class);
    }

    public CredentialSchema findCredentialSchemaByCredentialSchemaId(String credentialSchemaId) {

        String credentialSchemaJson = listCredentialSchemaQueryService.findByCredentialSchemaId(credentialSchemaId)
                .getCredentialSchema();
        return GsonWrapper.getGson().fromJson(credentialSchemaJson, CredentialSchema.class);
    }

    public CredentialDefinition findCredentialDefinitionByCredentialDefinitionId(String credentialDefinitionId) {
        String credentialDefinitionJson = listCredentialDefinitionQueryService.findByCredentialDefinitionId(credentialDefinitionId).getCredentialDefinition();

        return GsonWrapper.getGson().fromJson(credentialDefinitionJson, CredentialDefinition.class);
    }
}
