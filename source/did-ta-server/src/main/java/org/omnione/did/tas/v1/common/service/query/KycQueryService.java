package org.omnione.did.tas.v1.common.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Kyc;
import org.omnione.did.base.db.repository.KycRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycQueryService {
    private final KycRepository kycRepository;

    public Kyc findKyc(){
        return kycRepository.findTopByOrderByIdAsc().orElseThrow(() -> new OpenDidException(ErrorCode.KYC_INFO_NOT_FOUND));
    }

    public Kyc findKycOrNull(){
        return kycRepository.findTopByOrderByIdAsc().orElse(null);
    }

    public Kyc findKycById(Long id){
        try{
            return kycRepository.findById(id).orElseThrow(() -> new OpenDidException(ErrorCode.KYC_INFO_NOT_FOUND));
        } catch (OpenDidException e){
            log.error("KYC not found for id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e){
            log.error("Unexpected error occurred while finding KYC for id {}: {}", id, e.getMessage());
            throw new OpenDidException(ErrorCode.KYC_INFO_NOT_FOUND);
        }
    }
}
