package org.omnione.did.tas.v1.admin.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChangeAdminIdAndPasswordReqDto {
    @NotNull
    private String oldLoginId;
    @NotNull
    private String newLoginId;
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}
