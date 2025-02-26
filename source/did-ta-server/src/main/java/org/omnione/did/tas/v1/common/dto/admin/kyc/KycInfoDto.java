package org.omnione.did.tas.v1.common.dto.admin.kyc;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Kyc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class KycInfoDto {
    private final Long id;
    private final String name;
    private final String serverUrl;
    private final Boolean enabled;
    private final String createdAt;
    private final String updatedAt;

    public static KycInfoDto fromKyc(Kyc kyc) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return KycInfoDto.builder()
                .id(kyc.getId())
                .name(kyc.getName())
                .serverUrl(kyc.getServerUrl())
                .enabled(kyc.getEnabled())
                .createdAt(formatInstant(kyc.getCreatedAt(), formatter))
                .updatedAt(formatInstant(kyc.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
