package org.omnione.did.tas.v1.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.tas.v1.admin.dto.RequestTasInfoResDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto.Request;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.stereotype.Service;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : TaManagementService
 * @since : 2/18/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaManagementService {

    private final TasQueryService tasQueryService;

    public RequestTasInfoResDto requestTaInfo() {
        Tas tas = tasQueryService.findTas();
        return RequestTasInfoResDto.fromEntity(tas);
    }
}
