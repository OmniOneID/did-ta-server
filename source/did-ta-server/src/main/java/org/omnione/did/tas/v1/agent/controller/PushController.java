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
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.agent.dto.push.UpdatePushTokenReqDto;
import org.omnione.did.tas.v1.agent.service.PushService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The PushController class is a controller that handles requests related to push.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1)
public class PushController {
    private final PushService pushService;

    /**
     * Updates the push token.
     *
     * @param updatePushTokenReqDto The request DTO for updating the push token.
     * @return EmptyResDto An empty response DTO indicating the result of the update operation.
     */
    @RequestMapping(value = "/update-push-token", method = RequestMethod.POST)
    @ResponseBody
    public EmptyResDto updatePushToken(@Valid @RequestBody UpdatePushTokenReqDto updatePushTokenReqDto) {
        return pushService.updatePushToken(updatePushTokenReqDto);
    }
}
