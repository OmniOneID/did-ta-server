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
import org.omnione.did.base.db.domain.ListCertificateVc;
import org.omnione.did.base.db.repository.ListCertificateVcRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.certificate.ListCertificateVcDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListCertificateVcQueryService {

    private final ListCertificateVcRepository listCertificateVcRepository;

    public Page<ListCertificateVcDto> searchCertificateVcs(String searchKey, String searchValue, Pageable pageable) {
        Page<ListCertificateVc> page = listCertificateVcRepository.searchCertificateVc(searchKey, searchValue, pageable);

        List<ListCertificateVcDto> dtos = page.getContent().stream()
                .map(ListCertificateVcDto::fromListCertificateVc)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public ListCertificateVc findById(Long id) {
        return listCertificateVcRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.LIST_CERTIFICATE_VC_NOT_FOUND));
    }

    public ListCertificateVc findByDid(String did) {
        return listCertificateVcRepository.findTopByDidOrderByPublishedAtDesc(did)
                .orElseThrow(() -> new OpenDidException(ErrorCode.LIST_CERTIFICATE_VC_NOT_FOUND));
    }
}
