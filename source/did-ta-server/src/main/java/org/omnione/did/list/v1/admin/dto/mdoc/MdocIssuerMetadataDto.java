package org.omnione.did.list.v1.admin.dto.mdoc;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.list.v1.common.MdocIssuerMetadataClient.IssuerMetadata;

import java.util.List;

/** mso_mdoc docTypes an issuer declares in its OID4VCI metadata; the admin picks granted docTypes from these. */
@Getter
@Builder
public class MdocIssuerMetadataDto {
    private final String metadataUri;
    private final String credentialIssuer;
    private final List<String> docTypes;

    public static MdocIssuerMetadataDto from(IssuerMetadata metadata) {
        return MdocIssuerMetadataDto.builder()
                .metadataUri(metadata.metadataUri())
                .credentialIssuer(metadata.credentialIssuer())
                .docTypes(metadata.docTypes())
                .build();
    }
}
