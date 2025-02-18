package org.omnione.did.tas.v1.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.domain.Tas;

import java.util.Optional;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : TasDto
 * @since : 2/18/25
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestTasInfoResDto {
    private Long id;
    private String did;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TasStatus status;
    private String serverUrl;
    private String certificateUrl;

    public static RequestTasInfoResDto fromEntity(Tas tas) {
        return Optional.ofNullable(tas)
                .map(t -> RequestTasInfoResDto.builder()
                        .id(t.getId())
                        .did(t.getDid())
                        .name(t.getName())
                        .status(t.getStatus())
                        .serverUrl(t.getServerUrl())
                        .certificateUrl(t.getCertificateUrl())
                        .build())
                .orElse(null);
    }
}