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
package org.omnione.did.list.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.ListVcPlan;
import org.omnione.did.base.db.repository.ListVcPlanRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.vcplan.ListVcPlanDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListVcPlanQueryService {
    private final ListVcPlanRepository listVcPlanRepository;

    public Page<ListVcPlanDto> searchVcPlanList(String searchKey, String searchValue, Pageable pageable) {
        Page<ListVcPlan> listVcPlanPage = listVcPlanRepository.searchVcPlans(searchKey, searchValue, pageable);

        List<ListVcPlanDto> listVcPlanDtos = listVcPlanPage.getContent().stream()
                .map(ListVcPlanDto::fromListVcPlan)
                .collect(Collectors.toList());

        return new PageImpl<>(listVcPlanDtos, pageable, listVcPlanPage.getTotalElements());
    }

    public ListVcPlan findById(Long id) {
        return listVcPlanRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_PLAN_RETRIEVAL_FAILED));
    }

    public ListVcPlan findByVcPlanId(String vcPlanID) {
        return listVcPlanRepository.findByVcPlanId(vcPlanID)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_PLAN_RETRIEVAL_FAILED));
    }
    public ListVcPlan findByVcPlanIdAndIssuerDidOrNull(String vcPlanId, String issuerDid) {
        return listVcPlanRepository.findByVcPlanIdAndIssuerDid(vcPlanId, issuerDid).orElse(null);
    }

    public List<ListVcPlan> findAll() {
        return listVcPlanRepository.findAll();
    }

    public List<ListVcPlan> findAllByInitiate(String initiate) {

        return listVcPlanRepository.findAllByInitiate(initiate);
    }
}
