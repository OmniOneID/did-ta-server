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
package org.omnione.did.list.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.list.v1.admin.dto.vcplan.ListVcPlanDto;
import org.omnione.did.list.v1.admin.dto.vcplan.RegisterVcPlanFromIssuerReqDto;
import org.omnione.did.list.v1.admin.service.ListVcPlanManagementService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.List.ADMIN_V1)
public class ListVcPlanManagementController {
    private final ListVcPlanManagementService listVcPlanManagementService;

    @GetMapping(value = "/vc-plans/list")
    public Page<ListVcPlanDto> searchVcPlanList(String searchKey, String searchValue, Pageable pageable) {
        return listVcPlanManagementService.searchVcPlanList(searchKey, searchValue, pageable);
    }

    @GetMapping(value = "/vc-plans")
    public ListVcPlanDto getVcPlan(@RequestParam Long id) {
        return listVcPlanManagementService.findById(id);
    }

    @PostMapping(value = "/vc-plans/public")
    public EmptyResDto registerVcPlanFromIssuer(@RequestBody RegisterVcPlanFromIssuerReqDto registerVcPlanFromIssuerReqDto) {
        return listVcPlanManagementService.registerVcPlanFromIssuer(registerVcPlanFromIssuerReqDto);
    }
}
