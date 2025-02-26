package org.omnione.did.tas.v1.admin.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Kyc;
import org.omnione.did.tas.v1.common.dto.admin.kyc.KycInfoDto;
import org.omnione.did.tas.v1.common.service.query.KycQueryService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KycManagementService {
    private final KycQueryService kycQueryService;

    public KycInfoDto findKyc(Long id) {
        Kyc kyc = (id != null) ? kycQueryService.findKycById(id) : kycQueryService.findKycOrNull();
        return (kyc != null) ? KycInfoDto.fromKyc(kyc) : KycInfoDto.builder().build();
    }

}
