package org.omnione.did.list.v1.agent.dto.oid4vci;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;

@Getter
@Builder
public class Oid4vciIssuerPublicDto {
    private final String credentialIssuer;
    private final String credentialIssuerMetadataUri;
    private final String userInitiationUri;

    public static Oid4vciIssuerPublicDto from(ListOid4vciIssuer issuer) {
        return Oid4vciIssuerPublicDto.builder()
                .credentialIssuer(issuer.getCredentialIssuer())
                .credentialIssuerMetadataUri(issuer.getCredentialIssuerMetadataUri())
                .userInitiationUri(issuer.getUserInitiationUri())
                .build();
    }
}
