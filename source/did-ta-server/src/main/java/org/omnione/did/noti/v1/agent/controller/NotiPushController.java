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
import org.omnione.did.noti.v1.agent.dto.push.RequestSendPushReqDto;
import org.omnione.did.noti.v1.agent.dto.push.RequestSendPushResDto;
import org.omnione.did.noti.v1.agent.service.NotiPushService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling push notifications.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Noti.AGENT_V1)
public class NotiPushController {
    private final NotiPushService notiPushService;

    /**
     * Sends a push notification based on the provided request data.
     * This method validates the request data and invokes the service layer to process the push notification.
     *
     * @param requestSendPushReqDto The request DTO containing the details of the push notification to be sent.
     * @return A {@link RequestSendPushResDto} containing the result of the push notification request,
     *         including the count of successful and failed notifications.
     */
    @RequestMapping(value = "/send-push", method = RequestMethod.POST)
    @ResponseBody
    public RequestSendPushResDto requestSendPush(@Valid @RequestBody RequestSendPushReqDto requestSendPushReqDto){
        return notiPushService.requestSendPush(requestSendPushReqDto);
    }
}
