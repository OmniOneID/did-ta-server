package org.omnione.did.list.v1.admin.dto.mdoc;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.MdocIssuerStatus;
import org.omnione.did.base.db.domain.ListMdocIssuerStatusHistory;

import java.time.Instant;

@Getter
@Builder
public class MdocIssuerStatusHistoryDto {
    private final Long id;
    private final MdocIssuerStatus previousStatus;
    private final MdocIssuerStatus newStatus;
    private final String reason;
    private final String changedBy;
    private final Instant changedAt;

    public static MdocIssuerStatusHistoryDto from(ListMdocIssuerStatusHistory history) {
        return MdocIssuerStatusHistoryDto.builder()
                .id(history.getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .reason(history.getReason())
                .changedBy(history.getChangedBy())
                .changedAt(history.getChangedAt())
                .build();
    }
}
