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

package org.omnione.did.tas.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.tas.v1.admin.dto.admin.RegisterDidFromEntityReqDto;
import org.omnione.did.tas.v1.admin.dto.entity.ApproveDidReqDto;
import org.omnione.did.tas.v1.admin.dto.entity.RequestEntityStatusResDto;
import org.omnione.did.tas.v1.admin.service.EntityManagementService;
import org.omnione.did.tas.v1.admin.dto.entity.EntityInfoDto;
import org.omnione.did.tas.v1.admin.dto.entity.VerifyEntityNameUniqueResDto;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.ADMIN_V1)
public class EntityManagementController {
    private final EntityManagementService entityManagementService;
    private final SetupService setupService;

    @GetMapping(value = "/entities/list")
    public Page<EntityInfoDto> searchEntities(String searchKey, String searchValue, Pageable pageable) {
        return entityManagementService.searchEntities(searchKey, searchValue, pageable);
    }

    @GetMapping(value = "/entities")
    public EntityInfoDto findEntity(@RequestParam Long id) {
        return entityManagementService.findEntity(id);
    }

    @RequestMapping(value = "/entities", method = RequestMethod.POST)
    public EmptyResDto registerEntity(@RequestParam("didDoc") MultipartFile didDoc,
                                      @RequestParam("role") String role,
                                      @RequestParam("serverUrl") String serverUrl,
                                      @RequestParam("name") String name,
                                      @RequestParam("certificateUrl") String certificateUrl) {

        return setupService.registerEntityDidDocument(didDoc, role, serverUrl, certificateUrl, name);
    }

    @RequestMapping(value = "/entities/check-name", method = RequestMethod.GET)
    public VerifyEntityNameUniqueResDto verifyEntityNameUnique(@RequestParam String name) {
        return entityManagementService.verifyNameIsUnique(name);
    }

    @RequestMapping(value = "/entities/register-simple", method = RequestMethod.POST)
    public EmptyResDto registerEntitiesSimple() {
        return entityManagementService.registerEntitiesSimple();
    }

    @PostMapping(value = "/entities/register-did/public")
    public EmptyResDto registerDidFromEntity(@RequestBody RegisterDidFromEntityReqDto registerDidFromEntityReqDto) {
        return entityManagementService.registerDidFromEntity(registerDidFromEntityReqDto);
    }

    @PostMapping(value = "/entities/approve-did")
    public EmptyResDto approveEntityDidDocument(@RequestBody ApproveDidReqDto approveDidReqDto) {
        return entityManagementService.approveEntityDidDocument(approveDidReqDto);
    }

    @GetMapping(value = "/entities/request-status")
    public RequestEntityStatusResDto requestEntityStatus(@RequestParam("did") String did) {
        return entityManagementService.requestEntityStatus(did);
    }

    @DeleteMapping(value = "/entities")
    public EmptyResDto deleteEntity(@RequestParam("id") Long id) {
        return entityManagementService.deleteEntity(id);
    }

}
