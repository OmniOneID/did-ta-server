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
package org.omnione.did.list.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.ListAllowedCa;
import org.omnione.did.base.db.repository.ListAllowedCaRepository;
import org.omnione.did.list.v1.admin.dto.allowedca.ListAllowedCaDto;
import org.omnione.did.list.v1.admin.dto.allowedca.RegisterAllowedCaReqDto;
import org.omnione.did.list.v1.admin.dto.allowedca.UpdateAllowedCaReqDto;
import org.omnione.did.list.v1.admin.dto.allowedca.VerifyWalletIdUniqueResDto;
import org.omnione.did.list.v1.admin.service.query.ListAllowedCaQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListAllowedCaManagementService {
    private final ListAllowedCaRepository listAllowedCaRepository;
    private final ListAllowedCaQueryService listAllowedCaQueryService;

    public Page<ListAllowedCaDto> searchAllowedCaList(String searchKey, String searchValue, Pageable pageable) {
        return listAllowedCaQueryService.searchAllowedCaList(searchKey, searchValue, pageable);
    }

    public EmptyResDto registerAllowedCa(RegisterAllowedCaReqDto registerAllowedCaReqDto) {
        ListAllowedCa listAllowedCa = ListAllowedCa.builder()
                        .walletId(registerAllowedCaReqDto.getWalletId())
                        .caList(registerAllowedCaReqDto.getCaList())
                        .build();

        listAllowedCaRepository.save(listAllowedCa);

        return new EmptyResDto();
    }

    public EmptyResDto updateAllowedCa(UpdateAllowedCaReqDto updateAllowedCaReqDto) {
        ListAllowedCa listAllowedCa = listAllowedCaQueryService.findById(updateAllowedCaReqDto.getId());

        listAllowedCa.setWalletId(updateAllowedCaReqDto.getWalletId());
        listAllowedCa.setCaList(updateAllowedCaReqDto.getCaList());
        listAllowedCaRepository.save(listAllowedCa);

        return new EmptyResDto();
    }

    public ListAllowedCaDto findById(Long id) {
        return ListAllowedCaDto.fromListAllowedCa(listAllowedCaQueryService.findById(id));
    }

    public VerifyWalletIdUniqueResDto verifyWalletIdUnique(String walletId) {
        long count = listAllowedCaRepository.countByWalletId(walletId);
        return VerifyWalletIdUniqueResDto.builder()
                .isUnique(count == 0)
                .build();
    }

    public EmptyResDto deleteAllowedCa(Long id) {
        listAllowedCaQueryService.findById(id);
        listAllowedCaRepository.deleteById(id);
        return new EmptyResDto();
    }
}
