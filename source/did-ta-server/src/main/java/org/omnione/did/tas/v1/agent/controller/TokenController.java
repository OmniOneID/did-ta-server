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
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenResDto;
import org.omnione.did.tas.v1.agent.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * The TokenController class is a controller that handles requests related to token.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1)
public class TokenController {
    private final TokenService tokenService;

    /**
     * Handles requests to create a token.
     *
     * @param requestCreateTokenReqDto the token to create
     * @return the response of create token
     */
    @RequestMapping(value = "/request-create-token", method = RequestMethod.POST)
    @ResponseBody
    public RequestCreateTokenResDto requestCreateToken(@Valid @RequestBody RequestCreateTokenReqDto requestCreateTokenReqDto) {
        return tokenService.requestCreateToken(requestCreateTokenReqDto);
    }
}
