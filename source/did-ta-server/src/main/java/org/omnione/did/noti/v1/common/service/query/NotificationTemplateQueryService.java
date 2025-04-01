package org.omnione.did.noti.v1.common.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.constant.NotificationTemplateType;
import org.omnione.did.base.db.domain.NotificationTemplate;
import org.omnione.did.base.db.repository.NotificationTemplateRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateQueryService {
    private final NotificationTemplateRepository notificationTemplateRepository;

    public NotificationTemplate findNotificationTemplate(NotificationServerType serverType, NotificationTemplateType templateType) {
        try {
            return notificationTemplateRepository.findByServerTypeAndTemplateType(serverType, templateType)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.NOTIFICATION_TEMPLATE_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Error occurred while finding notification template for serverType {} and templateType {}: {}", serverType, templateType, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding notification template for serverType {} and templateType {}: {}", serverType, templateType, e.getMessage());
            throw new OpenDidException(ErrorCode.NOTIFICATION_TEMPLATE_NOT_FOUND);
        }
    }
}
