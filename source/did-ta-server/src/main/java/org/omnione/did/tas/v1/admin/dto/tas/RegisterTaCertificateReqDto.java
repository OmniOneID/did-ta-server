package org.omnione.did.tas.v1.admin.dto.tas;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterTaCertificateReqDto {
    @NotNull(message = "certificate cannot be null")
    private String certificate;
}
