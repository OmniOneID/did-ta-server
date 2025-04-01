package org.omnione.did.tas.v1.common.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.enums.vc.VcType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VcSchemaQueryService {
    private final VcSchemaRepository vcSchemaRepository;

    public VcSchema findByVcType(VcType vcType) {
        return vcSchemaRepository.findByType(vcType)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_RETRIEVAL_FAILED));
    }

    public VcSchema findByVcTypeOrNull(VcType vcType) {
        return vcSchemaRepository.findByType(vcType).orElse(null);
    }

}
