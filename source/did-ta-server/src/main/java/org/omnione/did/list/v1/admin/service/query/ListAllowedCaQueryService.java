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
package org.omnione.did.list.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.ListAllowedCa;
import org.omnione.did.base.db.repository.ListAllowedCaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.allowedca.ListAllowedCaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListAllowedCaQueryService {
    private final ListAllowedCaRepository listAllowedCaRepository;

    public Page<ListAllowedCaDto> searchAllowedCaList(String searchKey, String searchValue, Pageable pageable) {
        Page<ListAllowedCa> listAllowedCaPage = listAllowedCaRepository.searchAllowedCa(searchKey, searchValue, pageable);

        List<ListAllowedCaDto> listAllowedCaDtos = listAllowedCaPage.getContent().stream()
                .map(ListAllowedCaDto::fromListAllowedCa)
                .collect(Collectors.toList());

        return new PageImpl<>(listAllowedCaDtos, pageable, listAllowedCaPage.getTotalElements());
    }

    public ListAllowedCa findByIdOrNull(Long id) {
        return listAllowedCaRepository.findById(id).orElse(null);
    }

    public ListAllowedCa findById(Long id) {
        return listAllowedCaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.LIST_ALLOWED_CA_NOT_FOUND));
    }

    public long countByWalletId(String walletId) {
        return listAllowedCaRepository.countByWalletId(walletId);
    }

    public ListAllowedCa findByWalletId(String walletId) {
        return listAllowedCaRepository.findByWalletId(walletId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.LIST_ALLOWED_CA_NOT_FOUND));
    }

}
