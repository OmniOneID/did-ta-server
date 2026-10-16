package org.omnione.did.list.v1.admin.dto.mdoc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.omnione.did.base.db.constant.MdocIssuerStatus;

@Getter
@NoArgsConstructor
public class ChangeMdocIssuerStatusReqDto {
    private MdocIssuerStatus status;
    private String reason;
}
