package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ListOid4vciIssuerRepository extends JpaRepository<ListOid4vciIssuer, Long> {
    boolean existsByCredentialIssuer(String credentialIssuer);
    Optional<ListOid4vciIssuer> findByCredentialIssuer(String credentialIssuer);
    Optional<ListOid4vciIssuer> findByCredentialIssuerAndStatus(String credentialIssuer, Oid4vciIssuerStatus status);
    Page<ListOid4vciIssuer> findAllByStatus(Oid4vciIssuerStatus status, Pageable pageable);
    List<ListOid4vciIssuer> findAllByStatusOrderByCreatedAtAsc(Oid4vciIssuerStatus status);
    Page<ListOid4vciIssuer> findByCredentialIssuerContainingIgnoreCase(String credentialIssuer, Pageable pageable);
    Page<ListOid4vciIssuer> findByCredentialIssuerContainingIgnoreCaseAndStatus(
            String credentialIssuer, Oid4vciIssuerStatus status, Pageable pageable);
}
