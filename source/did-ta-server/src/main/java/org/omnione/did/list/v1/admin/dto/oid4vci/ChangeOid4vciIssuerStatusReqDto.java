package org.omnione.did.list.v1.admin.dto.oid4vci;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;

@Getter
@NoArgsConstructor
public class ChangeOid4vciIssuerStatusReqDto {
    private Oid4vciIssuerStatus status;
    private String reason;
}
