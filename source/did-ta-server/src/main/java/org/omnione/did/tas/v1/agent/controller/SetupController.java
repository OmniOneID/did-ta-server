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
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.agent.dto.setup.RemoveBlockChainIndexReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * The SetupController class is a controller that handles requests related to setup.
 * It provides endpoints for registering and updating did documents and removing blockchain index.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1 + "/setup")
public class SetupController {
    private final SetupService setupService;

    /**
     * Removes a blockchain index.
     * This method is intended for testing purposes and should be used with caution.
     *
     * @param removeBlockChainIndexReqDto The request DTO for removing a blockchain index.
     * @return EmptyResDto A response DTO representing an empty object.
     */
    @RequestMapping(value = "/blockchain/index", method = RequestMethod.DELETE)
    public EmptyResDto removeBlockchainIndex(@RequestBody RemoveBlockChainIndexReqDto removeBlockChainIndexReqDto) {
        return setupService.removeBlockchainIndex(removeBlockChainIndexReqDto);
    }


    @RequestMapping(value = "/blockchain/all", method = RequestMethod.DELETE)
    public EmptyResDto removeBlockchainAll() {
        return setupService.removeBlockchainAll();
    }
}
