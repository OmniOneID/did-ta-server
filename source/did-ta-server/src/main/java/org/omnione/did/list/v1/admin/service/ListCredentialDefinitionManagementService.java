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
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListCredentialDefinition;
import org.omnione.did.base.db.repository.ListCredentialDefinitionRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.list.v1.admin.dto.credential.ListCredentialDefinitionDto;
import org.omnione.did.list.v1.admin.dto.credential.RegisterCredentialDefinitionFromIssuerReqDto;
import org.omnione.did.list.v1.admin.dto.credential.RegisterCredentialSchemaFromIssuerReqDto;
import org.omnione.did.list.v1.admin.service.query.ListCredentialDefinitionQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListCredentialDefinitionManagementService {
    private final ListCredentialDefinitionQueryService listCredentialDefinitionQueryService;
    private final ListCredentialDefinitionRepository listCredentialDefinitionRepository;
    private final EntityQueryService entityQueryService;

    public Page<ListCredentialDefinitionDto> searchCredentialSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return listCredentialDefinitionQueryService.searchCredentialSchemaList(searchKey, searchValue, pageable);
    }

    public ListCredentialDefinitionDto findById(Long id) {
        ListCredentialDefinition listCredentialDefinition = listCredentialDefinitionQueryService.findById(id);
        return ListCredentialDefinitionDto.fromListCredentialDefinition(listCredentialDefinition);
    }

    public List<ListCredentialDefinition> findByCredentialSchemaId(String schemaId) {
        return listCredentialDefinitionQueryService.findByCredentialSchemaId(schemaId);
    }

    public EmptyResDto registerCredentialDefinitionFromIssuer(RegisterCredentialDefinitionFromIssuerReqDto request) {
        try {
            log.debug("=== Starting registerCredentialSchemaFromIssuer ===");

            CredentialDefinition credentialDefinition = decodeCredentialDefinition(request.getCredentialDefinition());
            Entity entity = findIssuerEntity(request.getIssuerDid());

            ListCredentialDefinition existingDefinition = listCredentialDefinitionQueryService.findByCredentialSchemaIdAndIssuerDid(credentialDefinition.getId(), request.getIssuerDid());

            if (existingDefinition != null) {
                updateExistingCredentialDefinition(existingDefinition, credentialDefinition);
            } else {
                insertNewCredentialDefinition(request.getIssuerDid(), entity, credentialDefinition);
            }

            log.debug("*** Finished registerCredentialDefinitionFromIssuer ***");
            return EmptyResDto.builder().build();
        } catch (OpenDidException e) {
            log.error("OpenDidException occurred during registerCredentialDefinitionFromIssuer: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register Credential Definition from issuer", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_VC_SCHEMA_FROM_ISSUER);
        }
    }

    private CredentialDefinition decodeCredentialDefinition(String encodedCredentialDefinition) {
        log.debug("\t--> Decoding Credential Definition");
        byte[] decodedData = BaseMultibaseUtil.decode(encodedCredentialDefinition);
        CredentialDefinition credentialDefinition = GsonWrapper.getGson().fromJson(new String(decodedData), CredentialDefinition.class);
        log.debug("\t--> Decoded Credential Definition: {}", credentialDefinition);
        return credentialDefinition;
    }

    private Entity findIssuerEntity(String issuerDid) {
        log.debug("\t--> Finding Entity by DID: {}", issuerDid);
        return entityQueryService.findEntityByDid(issuerDid);
    }

    private void updateExistingCredentialDefinition(ListCredentialDefinition existingCredentialSchema, CredentialDefinition listCredentialSchema) {
        log.debug("\t--> Updating existing credential-definition");

        existingCredentialSchema.setCredentialDefinition(GsonWrapper.getGson().toJson(listCredentialSchema));

        listCredentialDefinitionRepository.save(existingCredentialSchema);
    }

    private void insertNewCredentialDefinition(String issuerDid, Entity entity, CredentialDefinition credentialDefinition) {
        log.debug("\t--> Inserting credential-definition");

        ListCredentialDefinition newSchema = ListCredentialDefinition.builder()
                .credentialDefinitionId(credentialDefinition.getId())
                .issuerDid(issuerDid)
                .issuerName(entity.getName())
                .credentialDefinition(credentialDefinition.toJson())
                .credentialSchemaId(credentialDefinition.getSchemaId())
                .credentialDefinitionTag(credentialDefinition.getTag())
                .build();

        listCredentialDefinitionRepository.save(newSchema);
    }

}
