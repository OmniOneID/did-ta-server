package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@jakarta.persistence.Entity
@Table(name = "\"list_oid4vci_issuer\"",
        uniqueConstraints = @UniqueConstraint(name = "uk_list_oid4vci_issuer_credential_issuer",
                columnNames = "credential_issuer"))
public class ListOid4vciIssuer extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credential_issuer", nullable = false, length = 2000)
    private String credentialIssuer;

    @Column(name = "credential_issuer_metadata_uri", nullable = false, length = 2000)
    private String credentialIssuerMetadataUri;

    @Column(name = "user_initiation_uri", nullable = false, length = 2000)
    private String userInitiationUri;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Oid4vciIssuerStatus status;
}
