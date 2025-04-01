package org.omnione.did.base.db.repository;

import org.omnione.did.base.datamodel.enums.EmailTemplateType;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.constant.NotificationTemplateType;
import org.omnione.did.base.db.domain.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByServerTypeAndTemplateType(NotificationServerType serverType, NotificationTemplateType notificationTemplateType);
}
