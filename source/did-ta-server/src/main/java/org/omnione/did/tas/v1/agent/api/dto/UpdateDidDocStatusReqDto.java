package org.omnione.did.tas.v1.agent.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.omnione.did.data.model.enums.did.DidDocStatus;

/**
 * Description...
 */

@Builder
@Getter
@Setter
public class UpdateDidDocStatusReqDto {
    private String did;
    @JsonProperty("status")
    private DidDocStatus didDocStatus;
}
