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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.tas.v1.admin.dto.api.ExpirationInfoDto;
import org.omnione.did.tas.v1.admin.dto.api.KeyExchangePolicyInfoDto;
import org.omnione.did.tas.v1.admin.dto.api.RegisterExpirationInfoReqDto;
import org.omnione.did.tas.v1.admin.dto.api.RegisterKeyExchangePolicyInfoReqDto;
import org.omnione.did.tas.v1.admin.service.ApiManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.ADMIN_V1)
public class ApiManagementController {
    private final ApiManagementService apiManagementService;

    @GetMapping(value = "/apis/expiration")
    public ExpirationInfoDto findExpirationInfo() {
       return apiManagementService.findExpirationInfo();
    }

    @PostMapping(value = "/apis/expiration")
    @ResponseBody
    public ExpirationInfoDto registerExpirationInfo(@Valid @RequestBody RegisterExpirationInfoReqDto registerExpirationInfoReqDto) {
        return apiManagementService.registerExpirationInfo(registerExpirationInfoReqDto);
    }

    @GetMapping(value = "/apis/key-exchange-policy")
    public KeyExchangePolicyInfoDto findKeyExchangePolicy() {
        return apiManagementService.findKeyExchangePolicy();
    }

    @PostMapping(value = "/apis/key-exchange-policy")
    @ResponseBody
    public KeyExchangePolicyInfoDto registerKeyExchangePolicy(@Valid @RequestBody RegisterKeyExchangePolicyInfoReqDto registerKeyExchangePolicyInfoReqDto) {
        return apiManagementService.registerKeyExchangePolicy(registerKeyExchangePolicyInfoReqDto);
    }
}
