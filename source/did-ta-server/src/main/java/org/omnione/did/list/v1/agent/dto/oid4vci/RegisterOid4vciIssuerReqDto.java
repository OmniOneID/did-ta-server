package org.omnione.did.list.v1.agent.dto.oid4vci;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterOid4vciIssuerReqDto {
    private String credentialIssuer;
    private String credentialIssuerMetadataUri;
    private String userInitiationUri;
}
