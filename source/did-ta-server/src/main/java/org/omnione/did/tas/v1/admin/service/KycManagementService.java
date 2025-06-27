/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Kyc;
import org.omnione.did.base.db.repository.KycRepository;
import org.omnione.did.tas.v1.admin.dto.kyc.KycInfoDto;
import org.omnione.did.tas.v1.admin.dto.kyc.RegisterKycReqDto;
import org.omnione.did.tas.v1.common.service.query.KycQueryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KycManagementService {
    private final KycQueryService kycQueryService;
    private final KycRepository kycRepository;

    /**
     * Finds the KYC server information.
     *
     * @param id the KYC server ID.
     * @return the KYC server information.
     */
    public KycInfoDto findKyc(Long id) {
        Kyc kyc = (id != null) ? kycQueryService.findKycById(id) : kycQueryService.findKycOrNull();
        return (kyc != null) ? KycInfoDto.fromKyc(kyc) : KycInfoDto.builder().build();
    }

    /**
     * Registers a new KYC server.
     *
     * * <p>Currently, only one KYC server can be configured at a time.</p>
     *
     * @param registerKycReqDto the KYC server information to register.
     * @return the registered KYC server information.
     */
    public KycInfoDto registerKyc(RegisterKycReqDto registerKycReqDto) {
        Kyc kyc = kycQueryService.findKycOrNull();

        if (kyc == null) {
            kyc = Kyc.builder()
                    .name(registerKycReqDto.getName())
                    .serverUrl(registerKycReqDto.getServerUrl())
                    .enabled(true)
                    .build();
        } else {
            kyc.setName(registerKycReqDto.getName());
            kyc.setServerUrl(registerKycReqDto.getServerUrl());
            kyc.setEnabled(true);
        }

        return KycInfoDto.fromKyc(kycRepository.save(kyc));
    }

}
