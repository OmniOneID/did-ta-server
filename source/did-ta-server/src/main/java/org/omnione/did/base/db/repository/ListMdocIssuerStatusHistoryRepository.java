package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ListMdocIssuerStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListMdocIssuerStatusHistoryRepository extends JpaRepository<ListMdocIssuerStatusHistory, Long> {
    List<ListMdocIssuerStatusHistory> findAllByMdocIssuerIdOrderByChangedAtDesc(Long mdocIssuerId);
}
