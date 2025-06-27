package org.omnione.did.tas.v1.agent.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.omnione.did.data.model.enums.vc.VcStatus;

/**
 * Description...
 */
@Builder
@Getter
@Setter
public class UpdateVcMetaStatusReqDto {
    private String vcId;
    private VcStatus vcStatus;
}
