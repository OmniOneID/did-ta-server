package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ListOid4vciIssuerStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListOid4vciIssuerStatusHistoryRepository extends JpaRepository<ListOid4vciIssuerStatusHistory, Long> {
    List<ListOid4vciIssuerStatusHistory> findAllByIssuerIdOrderByChangedAtDesc(Long issuerId);
}
