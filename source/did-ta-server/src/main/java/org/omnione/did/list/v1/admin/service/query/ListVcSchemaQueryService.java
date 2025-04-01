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
import org.omnione.did.base.db.domain.ListVcSchema;
import org.omnione.did.base.db.repository.ListVcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.vcschema.ListVcSchemaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListVcSchemaQueryService {
    public Page<ListVcSchemaDto> searchVcSchemaList(String searchKey, String searchValue, Pageable pageable) {
        Page<ListVcSchema> listVcSchemaPage = listVcSchemaRepository.searchListVcSchemas(searchKey, searchValue, pageable);

        List<ListVcSchemaDto> listVcSchemaDtos = listVcSchemaPage.getContent().stream()
                .map(ListVcSchemaDto::fromListVcSchema)
                .collect(Collectors.toList());

        return new PageImpl<>(listVcSchemaDtos, pageable, listVcSchemaPage.getTotalElements());
    }

    private final ListVcSchemaRepository listVcSchemaRepository;

    public ListVcSchema findById(Long id) {
        return listVcSchemaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_RETRIEVAL_FAILED));
    }

    public ListVcSchema findBySchemaId(String schemaId) {
        return listVcSchemaRepository.findBySchemaId(schemaId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_RETRIEVAL_FAILED));
    }

    public ListVcSchema findBySchemaIdAndIssuerDidOrNull(String schemaId, String issuerDid) {
        return listVcSchemaRepository.findBySchemaIdAndIssuerDid(schemaId, issuerDid).orElse(null);
    }
}
