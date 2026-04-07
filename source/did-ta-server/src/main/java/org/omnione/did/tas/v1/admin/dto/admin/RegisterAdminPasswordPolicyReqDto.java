package org.omnione.did.tas.v1.admin.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterAdminPasswordPolicyReqDto {
    @NotNull
    private Short minLength;
    @NotNull
    private Boolean requireUppercase;
    @NotNull
    private Boolean requireNumber;
    @NotNull
    private Boolean requireSpecial;
    @NotNull
    private Short passwordExpiryDays;
}
