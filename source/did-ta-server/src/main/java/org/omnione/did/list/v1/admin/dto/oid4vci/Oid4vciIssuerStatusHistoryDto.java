package org.omnione.did.list.v1.admin.dto.oid4vci;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuerStatusHistory;

import java.time.Instant;

@Getter
@Builder
public class Oid4vciIssuerStatusHistoryDto {
    private final Long id;
    private final Oid4vciIssuerStatus previousStatus;
    private final Oid4vciIssuerStatus newStatus;
    private final String reason;
    private final String changedBy;
    private final Instant changedAt;

    public static Oid4vciIssuerStatusHistoryDto from(ListOid4vciIssuerStatusHistory history) {
        return Oid4vciIssuerStatusHistoryDto.builder()
                .id(history.getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .reason(history.getReason())
                .changedBy(history.getChangedBy())
                .changedAt(history.getChangedAt())
                .build();
    }
}
