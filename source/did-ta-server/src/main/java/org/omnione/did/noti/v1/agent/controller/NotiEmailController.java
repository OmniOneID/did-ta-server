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

package org.omnione.did.noti.v1.agent.controller;

import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.noti.v1.agent.service.NotiEmailService;
import org.omnione.did.noti.v1.agent.dto.email.RequestSendEmailReqDto;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling email notifications.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Noti.AGENT_V1)
public class NotiEmailController {
    private final NotiEmailService notiEmailService;

    /**
     * Sends an email using a predefined HTML template.
     *
     * The specific email template is determined by the `templateType` provided in the request.
     * Each template includes placeholders that can be dynamically replaced with actual content.
     * The dynamic content to be inserted into the template is provided in the `contentData` map,
     * where each key corresponds to a placeholder in the template, and each value is the content to replace it.
     *
     * @param requestSendEmailReqDto The request DTO containing the email details, including the template type
     *                               and content data for dynamic insertion.
     * @return An {@link EmptyResDto} indicating that the email sending operation was completed.
     */
    @RequestMapping(value = "/send-email", method = RequestMethod.POST)
    @ResponseBody
    public EmptyResDto requestSendEmail(@Valid @RequestBody RequestSendEmailReqDto requestSendEmailReqDto){
        return notiEmailService.requestSendEmail(requestSendEmailReqDto);
    }
}
