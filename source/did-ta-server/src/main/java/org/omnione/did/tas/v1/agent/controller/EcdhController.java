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
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestECDHResDto;
import org.omnione.did.tas.v1.agent.service.EcdhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * The EcdhController class is a controller that handles requests related to ecdh.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1)
public class EcdhController {
    private final EcdhService ecdhService;

    /**
     * Handles requests to initiate an ECDH operation.
     *
     * @param requestECDHReqDto The request data for the ECDH operation.
     * @return the response of the ECDH operation.
     */
    @RequestMapping(value = "/request-ecdh", method = RequestMethod.POST)
    @ResponseBody
    public RequestECDHResDto requestECDH(@Valid @RequestBody RequestECDHReqDto requestECDHReqDto) {
        return ecdhService.requestECDH(requestECDHReqDto);
    }
}
