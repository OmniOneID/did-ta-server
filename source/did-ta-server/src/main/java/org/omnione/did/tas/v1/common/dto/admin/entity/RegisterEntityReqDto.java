package org.omnione.did.tas.v1.common.dto.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : RegisterEntityReqDto
 * @since : 2/24/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterEntityReqDto {
    private String didDoc;
    private String role;
    private String serverUrl;
    private String name;
    private String certificateUrl;
}
