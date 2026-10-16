package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;
import org.omnione.did.base.db.constant.MdocIssuerStatus;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@jakarta.persistence.Entity
@Table(name = "\"list_mdoc_issuer_status_history\"")
public class ListMdocIssuerStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mdoc_issuer_id", nullable = false)
    private Long mdocIssuerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private MdocIssuerStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private MdocIssuerStatus newStatus;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "changed_by", length = 200)
    private String changedBy;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;
}
