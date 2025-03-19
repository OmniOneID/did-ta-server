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
package org.omnione.did.noti.v1.common.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.domain.NotificationServer;
import org.omnione.did.base.db.repository.NotificationServerRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.noti.v1.admin.dto.EmailConfigurationDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServerQueryService {
    private final NotificationServerRepository notificationServerRepository;

    public NotificationServer findNotificationServerOrNull(NotificationServerType type) {
        return notificationServerRepository.findByServerType(type).orElse(null);
    }

    public EmailConfigurationDto findEmailConfigurationOrNull() {
        NotificationServer notificationServer = findNotificationServerOrNull(NotificationServerType.EMAIL);

        if (notificationServer != null) {
           EmailConfigurationDto emailConfigurationDto = JsonUtil.deserializeFromJson(notificationServer.getConfig(), EmailConfigurationDto.class);
           return emailConfigurationDto;
        }

        return null;
    }

    public EmailConfigurationDto findEmailConfiguration() {
        NotificationServer notificationServer = notificationServerRepository.findByServerType(NotificationServerType.EMAIL)
                .orElseThrow(() -> new OpenDidException(ErrorCode.NOTIFICATION_EMAIL_CONFIGURATION_NOT_FOUND));

        return JsonUtil.deserializeFromJson(notificationServer.getConfig(), EmailConfigurationDto.class);
    }
}
