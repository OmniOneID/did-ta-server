package org.omnione.did.list.v1.agent.dto.mdoc;

import lombok.*;

import java.util.List;

/**
 * Public mDoc trusted issuer list consumed by mDoc readers (iOS/AOS).
 * {@code updatedAt} is the last time the list changed (ISO 8601, UTC, seconds precision).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MdocIssuerListResDto {
    private String updatedAt;
    private Integer count;
    private List<MdocIssuerPublicDto> items;
}
