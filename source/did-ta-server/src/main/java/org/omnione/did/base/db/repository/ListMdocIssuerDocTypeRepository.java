package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ListMdocIssuerDocType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface ListMdocIssuerDocTypeRepository extends JpaRepository<ListMdocIssuerDocType, Long> {
    List<ListMdocIssuerDocType> findAllByMdocIssuerIdOrderByIdAsc(Long mdocIssuerId);

    List<ListMdocIssuerDocType> findAllByMdocIssuerIdInOrderByIdAsc(Collection<Long> mdocIssuerIds);

    @Query("select max(d.createdAt) from ListMdocIssuerDocType d")
    Instant findLatestCreatedAt();

    /** Bulk delete runs immediately, so re-inserting the same docType in one transaction is safe. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ListMdocIssuerDocType d where d.mdocIssuerId = :mdocIssuerId")
    void deleteAllByMdocIssuerId(@Param("mdocIssuerId") Long mdocIssuerId);
}
