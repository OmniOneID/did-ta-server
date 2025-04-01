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
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.service.EntityService;
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * The EntityController class is a controller that handles requests related to entity.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1)
public class EntityController {
    private final EntityService entityService;

    /**
     * Proposes the enrollment of an entity.
     *
     * @param proposeEnrollEntityReqDto The entity enrollment proposal data.
     * @return The response of the enrollment proposal.
     */
    @RequestMapping(value = "/propose-enroll-entity", method = RequestMethod.POST)
    @ResponseBody
    public ProposeEnrollEntityResDto proposeEnrollEntity(@Valid @RequestBody ProposeEnrollEntityReqDto proposeEnrollEntityReqDto) {
        return entityService.proposeEnrollEntity(proposeEnrollEntityReqDto);
    }

    /**
     * Requests the enrollment of an entity.
     *
     * @param requestEnrollEntityReqDto The entity enrollment request data.
     * @return The response of the enrollment request.
     */
    @RequestMapping(value = "/request-enroll-entity", method = RequestMethod.POST)
    @ResponseBody
    public RequestEnrollEntityResDto requestEnrollEntity(@RequestBody RequestEnrollEntityReqDto requestEnrollEntityReqDto) {
        return entityService.requestEnrollEntity(requestEnrollEntityReqDto);
    }

    /**
     * Confirms the enrollment of an entity.
     *
     * @param confirmEnrollEntityReqDto The entity enrollment confirmation data.
     * @return The response of the enrollment confirmation.
     */
    @RequestMapping(value = "/confirm-enroll-entity", method = RequestMethod.POST)
    @ResponseBody
    public ConfirmEnrollEntityResDto confirmEnrollEntity(@RequestBody ConfirmEnrollEntityReqDto confirmEnrollEntityReqDto) { return entityService.confirmEnrollEntity(confirmEnrollEntityReqDto); }
}
