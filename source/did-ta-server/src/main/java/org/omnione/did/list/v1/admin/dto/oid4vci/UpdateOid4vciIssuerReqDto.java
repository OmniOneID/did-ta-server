package org.omnione.did.list.v1.admin.dto.oid4vci;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateOid4vciIssuerReqDto {
    private Long id;
    private String credentialIssuer;
    private String credentialIssuerMetadataUri;
    private String userInitiationUri;
}
