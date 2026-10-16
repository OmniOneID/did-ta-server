package org.omnione.did.list.v1.admin.dto.mdoc;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RegisterMdocIssuerReqDto {
    private Long entityId;
    private String metadataUri;
    private List<String> docTypes;
}
