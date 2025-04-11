package org.omnione.did.tas.v1.admin.dto.tas;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterTaDidDocumentReqDto {
    @NotNull(message = "didDocument cannot be null")
    private String didDocument;
}
