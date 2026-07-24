package org.omnione.did.list.v1.admin.dto.oid4vci;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;

import java.time.Instant;

@Getter
@Builder
public class Oid4vciIssuerDto {
    private final Long id;
    private final String credentialIssuer;
    private final String credentialIssuerMetadataUri;
    private final String userInitiationUri;
    private final Oid4vciIssuerStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static Oid4vciIssuerDto from(ListOid4vciIssuer issuer) {
        return Oid4vciIssuerDto.builder()
                .id(issuer.getId())
                .credentialIssuer(issuer.getCredentialIssuer())
                .credentialIssuerMetadataUri(issuer.getCredentialIssuerMetadataUri())
                .userInitiationUri(issuer.getUserInitiationUri())
                .status(issuer.getStatus())
                .createdAt(issuer.getCreatedAt())
                .updatedAt(issuer.getUpdatedAt())
                .build();
    }
}
