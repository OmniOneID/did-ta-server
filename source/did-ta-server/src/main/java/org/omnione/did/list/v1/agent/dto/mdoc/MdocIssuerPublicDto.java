package org.omnione.did.list.v1.agent.dto.mdoc;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MdocIssuerPublicDto {
    private final String did;
    private final String name;
    private final List<String> docTypes;
}
