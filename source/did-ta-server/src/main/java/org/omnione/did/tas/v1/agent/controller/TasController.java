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

package org.omnione.did.tas.v1.agent.controller;

import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.tas.v1.common.service.TasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasResDto;
import org.springframework.web.bind.annotation.*;

/**
 * The TasController class is a controller that handles requests related to tas.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1)
public class TasController {
    private final TasService tasService;

    /**
     * Handles requests to enroll a TAS.
     *
     * @param requestEnrollTas The TAS enrollment request data.
     * @return RequestEnrollTasResDto The response data for the TAS enrollment request.
     */
    @RequestMapping(value = "/request-enroll-tas", method = RequestMethod.POST)
    @ResponseBody
    public RequestEnrollTasResDto requestEnrollTas(@Valid @RequestBody RequestEnrollTasReqDto requestEnrollTas) {
        return tasService.requestEnrollTas(requestEnrollTas);
    }

}
