package org.omnione.did.tas.v1.admin.dto.admin;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class AdminPasswordPolicyDto {
    private final Long id;
    private final Short minLength;
    private final Boolean requireUppercase;
    private final Boolean requireNumber;
    private final Boolean requireSpecial;
    private final Short passwordExpiryDays;
    private final String createdAt;
    private final String updatedAt;

    public static AdminPasswordPolicyDto fromAdminPasswordPolicy(AdminPasswordPolicy policy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return AdminPasswordPolicyDto.builder()
                .id(policy.getId())
                .minLength(policy.getMinLength())
                .requireUppercase(policy.getRequireUppercase())
                .requireNumber(policy.getRequireNumber())
                .requireSpecial(policy.getRequireSpecial())
                .passwordExpiryDays(policy.getPasswordExpiryDays())
                .createdAt(formatInstant(policy.getCreatedAt(), formatter))
                .updatedAt(formatInstant(policy.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
