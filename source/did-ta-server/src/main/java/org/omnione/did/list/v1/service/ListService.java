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

package org.omnione.did.list.v1.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.omnione.did.base.datamodel.data.VcPlan;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.dto.ca.AllowedCaResDto;
import org.omnione.did.list.v1.dto.vcplan.RequestVcplanListResDto;
import org.omnione.did.list.v1.dto.vcplan.VcPlanResDto;
import org.omnione.did.tas.v1.agent.service.FileLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing lists of allowed CAs, VC plans, and related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListService {
    private final FileLoaderService fileLoaderService;

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

            String fullFileName = "allowed-ca-" + walletServiceId + ".json";
            String allowedCaListJson = fileLoaderService.getFileContent(fullFileName);

            List<String> allowedCaList = null;
            if (allowedCaListJson == null) {
                log.debug("\t--> None allowed CA list found for walletIdentifier: {}", walletServiceId);
                allowedCaList = new ArrayList<>();
            } else {
                log.debug("\t--> Converting allowed CA list for walletIdentifier: {}", walletServiceId);
                allowedCaList = convertAllowedCaList(allowedCaListJson, walletServiceId);
                log.debug("*** Finished findAllowedAppList ***");
            }

            return AllowedCaResDto.builder()
                    .count(allowedCaList == null ? 0 : allowedCaList.size())
                    .items(allowedCaList)
                    .build();

        } catch (IOException e) {
            log.error("\t--> An unknown error occurred retrieving allowed ca list: ", e);
            throw new OpenDidException(ErrorCode.ALLOWED_CA_RETRIEVAL_FAILED);
        } catch (Exception e) {
            log.error("\t--> An unknown error occurred retrieving allowed ca list: ", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_ALLOWED_CA_LIST);
        }
    }

    /**
     * Converts the JSON string of allowed CAs to a List of Strings.
     *
     * @param allowedCaList The JSON string of allowed CAs
     * @param walletIdentifier The wallet identifier
     * @return List<String> The list of allowed CAs
     * @throws OpenDidException if there's an error converting the JSON string
     */
    private List<String> convertAllowedCaList(String allowedCaList, String walletIdentifier) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(allowedCaList);

        if (rootNode.has(walletIdentifier)) {
            JsonNode valuesNode = rootNode.get(walletIdentifier);
            if (valuesNode.isArray()) {
                List alloweCaList = mapper.convertValue(valuesNode, List.class);
                return alloweCaList;
            } else {
                log.error("\t--> The value for the key '{}' is not a list.", walletIdentifier);
                throw new IOException("The value for the key '" + walletIdentifier + "' is not a list.");
            }
        }

        log.error("\t--> Unable to find the allowed CA for walletIdentifier: {}", walletIdentifier);
        throw new OpenDidException(ErrorCode.FAILED_TO_FIND_ALLOWED_CA);
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

            String fullFileName = "vc-plan-" + vcPlanId + ".json";
            String vcPlanJson = fileLoaderService.getFileContent(fullFileName);

            if (vcPlanJson == null) {
                log.error("\t--> Failed to retrieve VC plan for vcPlanId: {}", vcPlanId);
                throw new OpenDidException(ErrorCode.VC_PLAN_RETRIEVAL_FAILED);
            }

            log.debug("\t--> Converting vc plan for vcPlanId: {}", vcPlanId);
            VcPlan vcPlan = convertVcPlan(vcPlanJson);
            log.debug("*** Finished findVcPlan ***");

            return VcPlanResDto.builder()
                    .vcPlan(vcPlan)
                    .build();

        } catch (IOException e) {
            log.error("\t--> An unknown error occurred retrieving vc plan", e);
            throw new OpenDidException(ErrorCode.VC_PLAN_RETRIEVAL_FAILED);
        } catch (Exception e) {
            log.error("\t--> An unknown error occurred retrieving vc plan", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_VCPLAN);
        }
    }

    /**
     * Converts a JSON string to a VcPlan object.
     *
     * @param vcPlanJson The JSON string representation of a VC plan.
     * @return The converted VcPlan object.
     * @throws IOException if there's an error in parsing the JSON or the value is not a valid VcPlan object.
     */
    private VcPlan convertVcPlan(String vcPlanJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            VcPlan vcPlan = objectMapper.readValue(vcPlanJson, new TypeReference<VcPlan>() {});
            return vcPlan;
        } catch (IOException e) {
            throw new IOException("The value is not a object.");
        }
    }

    /**
     * Converts a JSON string to a List of VcPlan objects.
     *
     * @param vcListJson The JSON string representation of a list of VC plans.
     * @return The converted list of VcPlan objects.
     * @throws IOException if there's an error in parsing the JSON or the value is not a list.
     */
    private List<VcPlan> convertVcPlanList(String vcListJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<VcPlan> vcPlanList = objectMapper.readValue(vcListJson, new TypeReference<List<VcPlan>>() {});
            return vcPlanList;
        } catch (IOException e) {
            throw new IOException("The value is not a list.");
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
    public RequestVcplanListResDto findVcPlanList(List<String> tags) {
        try {
            log.debug("=== Starting findAllVcPlanList ===");

            String fullFileName = "vc-plan-list.json";
            String vcListJson = fileLoaderService.getFileContent(fullFileName);

            if (vcListJson == null) {
                log.error("\t--> Failed to retrieve VC plan list");
                throw new OpenDidException(ErrorCode.VC_PLAN_RETRIEVAL_FAILED);
            }

            // Convert the vc plan list
            log.debug("\t--> Converting vc plan list");
            List<VcPlan> vcPlanList = convertVcPlanList(vcListJson);

            // Filter the vc plan list based on the tags
            log.debug("\t--> Filtering vc plan list based on the tags");
            List<VcPlan> filteredVcPlanList = filterVcPlanList(vcPlanList, tags);

            log.debug("*** Finished findAllVcPlanList ***");

            return RequestVcplanListResDto.builder()
                    .count(filteredVcPlanList == null ? 0 : filteredVcPlanList.size())
                    .items(filteredVcPlanList)
                    .build();
        } catch (IOException e) {
            log.error("\t--> An unknown error occurred retrieving all vc plans", e);
            throw new OpenDidException(ErrorCode.VC_PLAN_RETRIEVAL_FAILED);
        } catch (Exception e) {
            log.error("\t--> An unknown error occurred retrieving all vc plans", e);
            throw new OpenDidException(ErrorCode.FAILED_API_GET_VCPLAN_LIST);
        }
    }

    /**
     * Filters a list of VC plans based on the provided tags.
     *
     * @param vcPlanList The list of VC plans to filter
     * @param tags The tags to filter by
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
     * @param tags The list of tags to check against
     * @return boolean True if the VC plan should be removed, false otherwise
     */
    private boolean shouldRemove(VcPlan vcPlan, List<String> tags) {
        if (vcPlan == null) return true;

        List<String> vcPlanTags = vcPlan.getTags();
        if (vcPlanTags == null) return true;

        log.debug("result: " + vcPlanTags.stream().noneMatch(tags::contains));

        return vcPlanTags.stream().allMatch(tags::contains);
    }
}
