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
package org.omnione.did.noti.v1.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.constant.NotificationTemplateType;
import org.omnione.did.noti.v1.admin.dto.*;
import org.omnione.did.noti.v1.admin.service.NotificationManagementService;
import org.omnione.did.noti.v1.agent.dto.email.CheckNotificationServerConfigurationResDto;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Noti.ADMIN_V1)
public class NotificationManagementController {
    private final NotificationManagementService notificationManagementService;

    @GetMapping(value = "/servers/email")
    public EmailConfigurationDto findEmailConfiguration() {
        return notificationManagementService.findEmailConfiguration();
    }

    @PostMapping(value = "/servers/email")
    @ResponseBody
    public EmailConfigurationDto registerEmailConfiguration(@Valid @RequestBody RegisterEmailConfigurationReqDto registerEmailConfigurationReqDto) {
        return notificationManagementService.registerEmailConfiguration(registerEmailConfigurationReqDto);
    }

    @PostMapping(value = "/servers/email/test")
    @ResponseBody
    public EmptyResDto sendTestEmail(@Valid @RequestBody SendTestEmailReqDto sendTestEmailReqDto) {
        return notificationManagementService.sendTestEmail(sendTestEmailReqDto);
    }

    @GetMapping(value = "/templates")
    public NotificationTemplateInfoDto findNotificationTemplate(@RequestParam NotificationServerType serverType, @RequestParam NotificationTemplateType templateType) {
        return notificationManagementService.findNotificationTemplate(serverType, templateType);
    }

    @PostMapping(value = "/templates")
    @ResponseBody
    public NotificationTemplateInfoDto registerNotificationTemplate(@Valid @RequestBody RegisterNotificationTemplateReqDto registerNotificationTemplateReqDto) {
        return notificationManagementService.registerNotificationTemplate(registerNotificationTemplateReqDto);
    }

    @GetMapping(value = "/servers/status")
    public CheckNotificationServerConfigurationResDto checkNotificationServerConfiguration() {
        return notificationManagementService.checkNotificationServerConfiguration();
    }

    @PostMapping(value = "/servers/push")
    @ResponseBody
    public void registerPushConfiguration(@RequestParam("push") MultipartFile push) {
        notificationManagementService.registerPushConfiguration(push);
    }
}
