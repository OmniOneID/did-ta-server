package org.omnione.did.list.v1.agent.dto.oid4vci;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Oid4vciIssuerListResDto {
    private Integer count;
    private List<Oid4vciIssuerPublicDto> items;
}
