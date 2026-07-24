package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@jakarta.persistence.Entity
@Table(name = "\"list_oid4vci_issuer_status_history\"")
public class ListOid4vciIssuerStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issuer_id", nullable = false)
    private Long issuerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private Oid4vciIssuerStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private Oid4vciIssuerStatus newStatus;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "changed_by", length = 200)
    private String changedBy;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;
}
