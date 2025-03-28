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
import org.omnione.did.base.db.constant.NotificationTemplateType;
import org.omnione.did.base.db.domain.NotificationServer;
import org.omnione.did.base.db.domain.NotificationTemplate;
import org.omnione.did.base.db.repository.NotificationServerRepository;
import org.omnione.did.base.db.repository.NotificationTemplateRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.noti.v1.admin.dto.*;
import org.omnione.did.noti.v1.agent.dto.email.CheckNotificationServerConfigurationResDto;
import org.omnione.did.noti.v1.agent.service.NotiEmailService;
import org.omnione.did.noti.v1.agent.service.NotiPushService;
import org.omnione.did.noti.v1.common.service.query.NotificationServerQueryService;
import org.omnione.did.noti.v1.common.service.query.NotificationTemplateQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationManagementService {
    private final NotificationServerQueryService notificationServerQueryService;
    private final NotificationServerRepository notificationServerRepository;
    private final NotiEmailService notiEmailService;
    private final NotificationTemplateQueryService notificationTemplateQueryService;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotiPushService notiPushService;

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

    public NotificationTemplateInfoDto findNotificationTemplate(NotificationServerType serverType, NotificationTemplateType templateType) {
        NotificationTemplate notificationTemplate = notificationTemplateQueryService.findNotificationTemplate(serverType, templateType);
        return NotificationTemplateInfoDto.fromNotificationTemplate(notificationTemplate);
    }

    public NotificationTemplateInfoDto registerNotificationTemplate(RegisterNotificationTemplateReqDto registerNotificationTemplateReqDto) {
        NotificationTemplate notificationTemplate = notificationTemplateQueryService.findNotificationTemplate(registerNotificationTemplateReqDto.getServerType(), registerNotificationTemplateReqDto.getTemplateType());

        if (notificationTemplate == null) {
            notificationTemplate = NotificationTemplate.builder()
                    .serverType(registerNotificationTemplateReqDto.getServerType())
                    .templateType(registerNotificationTemplateReqDto.getTemplateType())
                    .template(registerNotificationTemplateReqDto.getTemplate())
                    .build();
        } else {
            notificationTemplate.setTemplate(registerNotificationTemplateReqDto.getTemplate());
        }

        NotificationTemplate savedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        return NotificationTemplateInfoDto.fromNotificationTemplate(savedNotificationTemplate);
    }

    public CheckNotificationServerConfigurationResDto checkNotificationServerConfiguration() {
        NotificationServer emailNotificationServer = notificationServerQueryService.findNotificationServerOrNull(NotificationServerType.EMAIL);
        NotificationServer pushNotificationServer =notificationServerQueryService.findNotificationServerOrNull(NotificationServerType.PUSH);

        return CheckNotificationServerConfigurationResDto.builder()
                .emailConfigured(emailNotificationServer != null)
                .pushConfigured(pushNotificationServer != null)
                .build();
    }

    public void registerPushConfiguration(MultipartFile push) {
        try {
            String pushSettingJson = new String(push.getBytes());
            NotificationServer notificationServer = notificationServerQueryService.findNotificationServerOrNull(NotificationServerType.PUSH);

            if (notificationServer == null) {
                notificationServer = NotificationServer.builder()
                        .serverType(NotificationServerType.PUSH)
                        .config(pushSettingJson)
                        .build();
            } else {
                notificationServer.setConfig(pushSettingJson);
            }

            notificationServerRepository.save(notificationServer);
            notiPushService.initializeFirebase();
        } catch (IOException e) {
            throw new OpenDidException(ErrorCode.FILE_NOT_FOUND);
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_PUSH_CONFIGURATION);
        }
    }
}
