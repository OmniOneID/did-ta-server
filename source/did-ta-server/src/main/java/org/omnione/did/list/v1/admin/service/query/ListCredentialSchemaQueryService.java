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
import org.omnione.did.base.db.domain.ListCredentialSchema;
import org.omnione.did.base.db.repository.ListCredentialSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.credential.ListCredentialSchemaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListCredentialSchemaQueryService {
    private final ListCredentialSchemaRepository listCredentialSchemaRepository;

    public Page<ListCredentialSchemaDto> searchCredentialSchemaList(String searchKey, String searchValue, Pageable pageable) {
        Page<ListCredentialSchema> listCredentialSchemaPage = listCredentialSchemaRepository.searchListCredentialSchemas(searchKey, searchValue, pageable);

        List<ListCredentialSchemaDto> listCredentialSchemaDtos = listCredentialSchemaPage.getContent().stream()
                .map(ListCredentialSchemaDto::fromListCredentialSchema)
                .collect(Collectors.toList());

        return new PageImpl<>(listCredentialSchemaDtos, pageable, listCredentialSchemaPage.getTotalElements());
    }

    public ListCredentialSchema findById(Long id) {
        return listCredentialSchemaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.CREDENTIAL_SCHEMA_RETRIEVAL_FAILED));
    }

    public ListCredentialSchema findByCredentialSchemaId(String schemaId) {
        return listCredentialSchemaRepository.findByCredentialSchemaId(schemaId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.CREDENTIAL_SCHEMA_RETRIEVAL_FAILED));
    }

    public ListCredentialSchema findByCredentialSchemaIdAndIssuerDid(String schemaId, String issuerDid) {
        return listCredentialSchemaRepository.findByCredentialSchemaIdAndIssuerDid(schemaId, issuerDid).orElse(null);
    }

    public List<ListCredentialSchema> getAllCredentialSchemas() {
       return listCredentialSchemaRepository.findAll();
    }
}
