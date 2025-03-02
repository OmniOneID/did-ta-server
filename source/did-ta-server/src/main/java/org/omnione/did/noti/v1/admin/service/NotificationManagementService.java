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
package org.omnione.did.noti.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.domain.NotificationServer;
import org.omnione.did.base.db.repository.NotificationServerRepository;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.noti.v1.admin.dto.EmailConfigurationDto;
import org.omnione.did.noti.v1.admin.dto.RegisterEmailConfigurationReqDto;
import org.omnione.did.noti.v1.admin.dto.SendTestEmailReqDto;
import org.omnione.did.noti.v1.agent.service.NotiEmailService;
import org.omnione.did.noti.v1.common.service.query.NotificationServerQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationManagementService {
    private final NotificationServerQueryService notificationServerQueryService;
    private final NotificationServerRepository notificationServerRepository;
    private final NotiEmailService notiEmailService;

    public EmailConfigurationDto findEmailConfiguration() {
        return notificationServerQueryService.findEmailConfigurationOrNull();
    }

    public EmailConfigurationDto registerEmailConfiguration(RegisterEmailConfigurationReqDto registerEmailConfigurationReqDto) {
        NotificationServer notificationServer = notificationServerQueryService.findNotificationServerOrNull(NotificationServerType.EMAIL);
        if (notificationServer == null) {
            notificationServer = NotificationServer.builder()
                    .serverType(NotificationServerType.EMAIL)
                    .config(JsonUtil.serializeToJson(registerEmailConfigurationReqDto))
                    .build();
        } else {
            notificationServer.setConfig(JsonUtil.serializeToJson(registerEmailConfigurationReqDto));
        }

        NotificationServer savedNotificationServer = notificationServerRepository.save(notificationServer);
        return  JsonUtil.deserializeFromJson(savedNotificationServer.getConfig(), EmailConfigurationDto.class);
    }

    public EmptyResDto sendTestEmail(SendTestEmailReqDto sendTestEmailReqDto) {
        notiEmailService.sendTestEmail(sendTestEmailReqDto);
        return EmptyResDto.builder().build();
    }
}
