package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@jakarta.persistence.Entity
@Table(name = "\"list_mdoc_issuer_doc_type\"",
        uniqueConstraints = @UniqueConstraint(name = "uk_list_mdoc_issuer_doc_type",
                columnNames = {"mdoc_issuer_id", "doc_type"}))
public class ListMdocIssuerDocType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mdoc_issuer_id", nullable = false)
    private Long mdocIssuerId;

    @Column(name = "doc_type", nullable = false, length = 200)
    private String docType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
