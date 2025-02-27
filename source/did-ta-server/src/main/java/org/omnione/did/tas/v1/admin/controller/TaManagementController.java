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
import org.omnione.did.base.constants.UrlConstant.Tas;
import org.omnione.did.tas.v1.admin.dto.tas.RequestTasInfoResDto;
import org.omnione.did.tas.v1.admin.service.TaManagementService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides APIs for managing TA.
 *
 * @author : yklee0911
 * @fileName : TaManagementController
 * @since : 2/18/25
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = Tas.ADMIN_V1)
public class TaManagementController {
    private final TaManagementService taManagementService;

    /**
     * Request TA information.
     *
     * @return TA information
     */
    @RequestMapping(value = "/ta/info", method = RequestMethod.GET)
    public RequestTasInfoResDto requestTaInfo() {
        return taManagementService.requestTaInfo();
    };

    /**
     * Register TA with simple process. (1st development version)
     *
     * @return TA information
     */
    @RequestMapping(value = "/ta/register-simple", method = RequestMethod.POST)
    public RequestTasInfoResDto registerTaSimple() {
        return taManagementService.registerTaSimple();
    }
}
