package org.omnione.did.list.v1.agent.dto.oid4vci;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;

@Getter
@Builder
public class Oid4vciIssuerRegistrationResDto {
    private final Long id;
    private final Oid4vciIssuerStatus status;

    public static Oid4vciIssuerRegistrationResDto from(ListOid4vciIssuer issuer) {
        return Oid4vciIssuerRegistrationResDto.builder().id(issuer.getId()).status(issuer.getStatus()).build();
    }
}
