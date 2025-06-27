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
import org.omnione.did.base.db.domain.ListCredentialDefinition;
import org.omnione.did.base.db.domain.ListCredentialSchema;
import org.omnione.did.base.db.repository.ListCredentialDefinitionRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.credential.ListCredentialDefinitionDto;
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
public class ListCredentialDefinitionQueryService {
    private final ListCredentialDefinitionRepository listCredentialDefinitionRepository;

    public Page<ListCredentialDefinitionDto> searchCredentialSchemaList(String searchKey, String searchValue, Pageable pageable) {
        Page<ListCredentialDefinition> listCredentialDefinitions = listCredentialDefinitionRepository.searchListCredentialDefinition(searchKey, searchValue, pageable);

        List<ListCredentialDefinitionDto> listCredentialSchemaDtos = listCredentialDefinitions.getContent().stream()
                .map(ListCredentialDefinitionDto::fromListCredentialDefinition)
                .collect(Collectors.toList());

        return new PageImpl<>(listCredentialSchemaDtos, pageable, listCredentialDefinitions.getTotalElements());
    }

    public ListCredentialDefinition findById(Long id) {
        return listCredentialDefinitionRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_RETRIEVAL_FAILED));
    }

    public List<ListCredentialDefinition> findByCredentialSchemaId(String schemaId) {
        return listCredentialDefinitionRepository.findByCredentialSchemaId(schemaId);
    }

    public ListCredentialDefinition findByCredentialSchemaIdAndIssuerDid(String schemaId, String issuerDid) {
        return listCredentialDefinitionRepository.findByCredentialDefinitionIdAndIssuerDid(schemaId, issuerDid).orElse(null);
    }

    public ListCredentialDefinition findByCredentialDefinitionId(String credentialDefinitionId) {
        return listCredentialDefinitionRepository.findByCredentialDefinitionId(credentialDefinitionId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_RETRIEVAL_FAILED));

    }
}
