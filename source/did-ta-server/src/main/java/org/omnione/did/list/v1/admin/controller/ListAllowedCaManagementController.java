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
import org.omnione.did.list.v1.admin.dto.allowedca.ListAllowedCaDto;
import org.omnione.did.list.v1.admin.dto.allowedca.RegisterAllowedCaReqDto;
import org.omnione.did.list.v1.admin.dto.allowedca.UpdateAllowedCaReqDto;
import org.omnione.did.list.v1.admin.dto.allowedca.VerifyWalletIdUniqueResDto;
import org.omnione.did.list.v1.admin.service.ListAllowedCaManagementService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.List.ADMIN_V1)
public class ListAllowedCaManagementController {
    private final ListAllowedCaManagementService listAllowedCaManagementService;

    @GetMapping(value = "/allowed-cas/list")
    public Page<ListAllowedCaDto> searchAllowedCas(String searchKey, String searchValue, Pageable pageable) {
        return listAllowedCaManagementService.searchAllowedCaList(searchKey, searchValue, pageable);
    }

    @GetMapping(value = "/allowed-cas")
    public ListAllowedCaDto getAllowedCa(Long id){
        return listAllowedCaManagementService.findById(id);
    }

    @PostMapping(value = "/allowed-cas")
    public EmptyResDto registerAllowedCa(@RequestBody RegisterAllowedCaReqDto registerAllowedCaReqDto){
        return listAllowedCaManagementService.registerAllowedCa(registerAllowedCaReqDto);
    }

    @PutMapping(value = "/allowed-cas")
    public EmptyResDto updateAllowedCa(@RequestBody UpdateAllowedCaReqDto updateAllowedCaReqDto){
        return listAllowedCaManagementService.updateAllowedCa(updateAllowedCaReqDto);
    }

    @RequestMapping(value = "/allowed-cas/check-wallet-id", method = RequestMethod.GET)
    public VerifyWalletIdUniqueResDto verifyWalletIdUnique(@RequestParam String walletId) {
        return listAllowedCaManagementService.verifyWalletIdUnique(walletId);
    }

    @RequestMapping(value = "/allowed-cas", method = RequestMethod.DELETE)
    public EmptyResDto deleteAllowedCa(@RequestParam Long id){
        return listAllowedCaManagementService.deleteAllowedCa(id);
    }



}
