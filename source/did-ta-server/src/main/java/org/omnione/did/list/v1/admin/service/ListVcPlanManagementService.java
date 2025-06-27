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
package org.omnione.did.list.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.VcPlan;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListVcPlan;
import org.omnione.did.base.db.repository.ListVcPlanRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.list.v1.admin.dto.vcplan.ListVcPlanDto;
import org.omnione.did.list.v1.admin.dto.vcplan.RegisterVcPlanFromIssuerReqDto;
import org.omnione.did.list.v1.admin.service.query.ListVcPlanQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListVcPlanManagementService {
    private final ListVcPlanQueryService listVcPlanQueryService;
    private final EntityQueryService entityQueryService;
    private final ListVcPlanRepository listVcPlanRepository;

    public Page<ListVcPlanDto> searchVcPlanList(String searchKey, String searchValue, Pageable pageable) {
        return listVcPlanQueryService.searchVcPlanList(searchKey, searchValue, pageable);
    }

    public ListVcPlanDto findById(Long id) {
        ListVcPlan listVcPlan = listVcPlanQueryService.findById(id);
        return ListVcPlanDto.fromListVcPlan(listVcPlan);
    }

    public EmptyResDto registerVcPlanFromIssuer(RegisterVcPlanFromIssuerReqDto request) {
        try {
            log.debug("=== Starting registerVcPlanFromIssuer ===");

            VcPlan vcPlan = decodeVcPlan(request.getVcPlan());
            Entity entity = findIssuerEntity(request.getIssuerDid());

            ListVcPlan existingPlan = listVcPlanQueryService.findByVcPlanIdAndIssuerDidOrNull(vcPlan.getVcPlanId(), entity.getDid());

            if (existingPlan != null) {
                updateExistingVcPlan(existingPlan, vcPlan);
            } else {
                insertNewVcPlan(request.getIssuerDid(), request.getInitiate(), entity, vcPlan);
            }

            log.debug("*** Finished registerVcPlanFromIssuer ***");
            return EmptyResDto.builder().build();
        } catch (OpenDidException e) {
            log.error("OpenDidException occurred while registering vc plan from issuer", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to register vc plan from issuer", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_VC_PLAN_FROM_ISSUER);
        }
    }

    private VcPlan decodeVcPlan(String encodedVcPlan) {
        log.debug("\t--> Decoding VC Plan");
        byte[] decodedData = BaseMultibaseUtil.decode(encodedVcPlan);
        VcPlan vcPlan = JsonUtil.deserializeFromJson(new String(decodedData), VcPlan.class);
        log.debug("\t--> Decoded VC Plan: {}", vcPlan);
        return vcPlan;
    }

    private Entity findIssuerEntity(String issuerDid) {
        log.debug("\t--> Finding Entity by DID: {}", issuerDid);
        return entityQueryService.findEntityByDid(issuerDid);
    }

    private void updateExistingVcPlan(ListVcPlan existingPlan, VcPlan vcPlan) {
        log.debug("\t--> VC Plan already exists. VC Plan ID: {}", vcPlan.getVcPlanId());
        log.debug("\t--> Updating vc-plan");

        existingPlan.setVcPlanId(vcPlan.getVcPlanId());
        existingPlan.setName(vcPlan.getName());
        existingPlan.setDescription(vcPlan.getDescription());
        existingPlan.setVcPlan(JsonUtil.serializeToJson(vcPlan));

        listVcPlanRepository.save(existingPlan);
    }

    private void insertNewVcPlan(String issuerDid, String initiate, Entity entity, VcPlan vcPlan) {
        log.debug("\t--> Inserting vc-plan");

        ListVcPlan newVcPlan = ListVcPlan.builder()
                .issuerDid(issuerDid)
                .issuerName(entity.getName())
                .vcPlanId(vcPlan.getVcPlanId())
                .name(vcPlan.getName())
                .description(vcPlan.getDescription())
                .vcPlan(JsonUtil.serializeToJson(vcPlan))
                .initiate(initiate)
                .build();

        listVcPlanRepository.save(newVcPlan);
    }

}
