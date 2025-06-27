package org.omnione.did.noti.v1.admin.dto;

import lombok.*;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.constant.NotificationTemplateType;
import org.omnione.did.base.db.domain.NotificationTemplate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationTemplateInfoDto {
    private NotificationServerType serverType;
    private NotificationTemplateType templateType;
    private String template;

    public static NotificationTemplateInfoDto fromNotificationTemplate(NotificationTemplate notificationTemplate) {
        return NotificationTemplateInfoDto.builder()
                .serverType(notificationTemplate.getServerType())
                .templateType(notificationTemplate.getTemplateType())
                .template(notificationTemplate.getTemplate())
                .build();
    }
}
