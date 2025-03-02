package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.domain.NotificationServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationServerRepository extends JpaRepository<NotificationServer, Long> {
    Optional<NotificationServer> findByServerType(NotificationServerType serverType);
}
