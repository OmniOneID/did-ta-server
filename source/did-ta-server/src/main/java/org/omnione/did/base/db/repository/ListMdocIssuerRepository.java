package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.constant.MdocIssuerStatus;
import org.omnione.did.base.db.domain.ListMdocIssuer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ListMdocIssuerRepository extends JpaRepository<ListMdocIssuer, Long> {
    Optional<ListMdocIssuer> findByEntityId(Long entityId);

    List<ListMdocIssuer> findAllByStatus(MdocIssuerStatus status);

    List<ListMdocIssuer> findAllByStatusOrderByCreatedAtAsc(MdocIssuerStatus status);

    @Query("select max(m.createdAt) from ListMdocIssuer m")
    Instant findLatestCreatedAt();

    @Query("select max(m.updatedAt) from ListMdocIssuer m")
    Instant findLatestUpdatedAt();
}
