package org.omnione.did.list.v1.admin.dto.mdoc;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.MdocIssuerStatus;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListMdocIssuer;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class MdocIssuerDto {
    private final Long id;
    private final Long entityId;
    private final String did;
    private final String name;
    private final String metadataUri;
    private final List<String> docTypes;
    private final MdocIssuerStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    /** {@code entity} may be null if the referenced entity no longer exists. */
    public static MdocIssuerDto from(ListMdocIssuer issuer, Entity entity, List<String> docTypes) {
        return MdocIssuerDto.builder()
                .id(issuer.getId())
                .entityId(issuer.getEntityId())
                .did(entity == null ? null : entity.getDid())
                .name(entity == null ? null : entity.getName())
                .metadataUri(issuer.getMetadataUri())
                .docTypes(docTypes)
                .status(issuer.getStatus())
                .createdAt(issuer.getCreatedAt())
                .updatedAt(issuer.getUpdatedAt())
                .build();
    }
}
