package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;
import org.omnione.did.base.db.constant.MdocIssuerStatus;

import java.io.Serializable;

/**
 * mDoc trusted issuer grant. References {@code entity.id} logically (no DB foreign key),
 * so existing entity management is never constrained by this table.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@jakarta.persistence.Entity
@Table(name = "\"list_mdoc_issuer\"",
        uniqueConstraints = @UniqueConstraint(name = "uk_list_mdoc_issuer_entity_id",
                columnNames = "entity_id"))
public class ListMdocIssuer extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /** Issuer OID4VCI metadata URL; granted docTypes must be declared there as mso_mdoc. */
    @Column(name = "metadata_uri", length = 2000)
    private String metadataUri;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MdocIssuerStatus status;
}
