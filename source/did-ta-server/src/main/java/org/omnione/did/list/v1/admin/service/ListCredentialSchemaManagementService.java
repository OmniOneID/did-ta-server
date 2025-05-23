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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListCredentialDefinition;
import org.omnione.did.base.db.domain.ListCredentialSchema;
import org.omnione.did.base.db.repository.ListCredentialSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.list.v1.admin.dto.credential.ListCredentialDefinitionDto;
import org.omnione.did.list.v1.admin.dto.credential.ListCredentialDefinitionSimpleDto;
import org.omnione.did.list.v1.admin.dto.credential.ListCredentialSchemaDto;
import org.omnione.did.list.v1.admin.dto.credential.RegisterCredentialSchemaFromIssuerReqDto;
import org.omnione.did.list.v1.admin.service.query.ListCredentialDefinitionQueryService;
import org.omnione.did.list.v1.admin.service.query.ListCredentialSchemaQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.omnione.did.zkp.datamodel.schema.AttributeDef.ATTR_TYPE;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListCredentialSchemaManagementService {
    private final ListCredentialSchemaQueryService listCredentialSchemaQueryService;
    private final ListCredentialSchemaRepository listCredentialSchemaRepository;
    private final EntityQueryService entityQueryService;
    private final ListCredentialDefinitionQueryService listCredentialDefinitionQueryService;

    public Page<ListCredentialSchemaDto> searchCredentialSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return listCredentialSchemaQueryService.searchCredentialSchemaList(searchKey, searchValue, pageable);
    }

    public ListCredentialSchemaDto findById(Long id) {
        ListCredentialSchema listVcSchema = listCredentialSchemaQueryService.findById(id);
        return ListCredentialSchemaDto.fromListCredentialSchema(listVcSchema);
    }

    public ListCredentialSchema findByCredentialSchemaId(String schemaId) {
        return listCredentialSchemaQueryService.findByCredentialSchemaId(schemaId);
    }

    public EmptyResDto registerCredentialSchemaFromIssuer(RegisterCredentialSchemaFromIssuerReqDto request) {
        try {
            log.debug("=== Starting registerCredentialSchemaFromIssuer ===");

            CredentialSchema credentialSchema = decodeCredentialSchema(request.getCredentialSchema());
            Entity entity = findIssuerEntity(request.getIssuerDid());

            ListCredentialSchema existingSchema = listCredentialSchemaQueryService.findByCredentialSchemaIdAndIssuerDid(credentialSchema.getId(), request.getIssuerDid());

            if (existingSchema != null) {
                updateExistingCredentialSchema(existingSchema, credentialSchema);
            } else {
                insertNewCredentialSchema(request.getIssuerDid(), entity, credentialSchema);
            }

            log.debug("*** Finished registerCredentialSchemaFromIssuer ***");
            return EmptyResDto.builder().build();
        } catch (OpenDidException e) {
            log.error("OpenDidException occurred during registerCredentialSchemaFromIssuer: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register credential schema from issuer", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_VC_SCHEMA_FROM_ISSUER);
        }
    }

    private CredentialSchema decodeCredentialSchema(String encodedVcSchema) {
        log.debug("\t--> Decoding Credential Schema");
        byte[] decodedData = BaseMultibaseUtil.decode(encodedVcSchema);
        CredentialSchema credentialSchema = GsonWrapper.getGson().fromJson(new String(decodedData), CredentialSchema.class);
        log.debug("\t--> Decoded Credential Schema: {}", credentialSchema);
        return credentialSchema;
    }

    private Entity findIssuerEntity(String issuerDid) {
        log.debug("\t--> Finding Entity by DID: {}", issuerDid);
        return entityQueryService.findEntityByDid(issuerDid);
    }

    private void updateExistingCredentialSchema(ListCredentialSchema existingCredentialSchema, CredentialSchema listCredentialSchema) {
        log.debug("\t--> Updating existing credential-schema");

        existingCredentialSchema.setName(listCredentialSchema.getName());
        existingCredentialSchema.setCredentialSchema(GsonWrapper.getGson().toJson(listCredentialSchema));

        listCredentialSchemaRepository.save(existingCredentialSchema);
    }

    private void insertNewCredentialSchema(String issuerDid, Entity entity, CredentialSchema credentialSchema) {
        log.debug("\t--> Inserting credential-schema");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AttributeDef.ATTR_TYPE.class,
                        (JsonSerializer<ATTR_TYPE>) (src, typeOfSrc, context) ->
                                new JsonPrimitive(
                                        src.name().charAt(0) + src.name().substring(1).toLowerCase()
                                )
                )
                .create();

        ListCredentialSchema newSchema = ListCredentialSchema.builder()
                .name(credentialSchema.getName())
                .credentialSchemaId(credentialSchema.getId())
                .issuerDid(issuerDid)
                .issuerName(entity.getName())
                .credentialSchema(gson.toJson(credentialSchema))
                .build();

        listCredentialSchemaRepository.save(newSchema);
    }

    public List<ListCredentialSchemaDto> getAllCredentialSchemas() {
        // Fetch all credential schemas
        log.debug("Fetching all credential schemas");
        List<ListCredentialSchema> allCredentialSchemas = listCredentialSchemaQueryService.getAllCredentialSchemas();

        // Fetch all credential definitions
        log.debug("Fetching all credential definitions");
        List<ListCredentialSchemaDto> listCredentialSchemaDtos = allCredentialSchemas.stream()
                .map(listCredentialSchema -> {
                    List<ListCredentialDefinition> credentialDefinitions = listCredentialDefinitionQueryService.findByCredentialSchemaId(listCredentialSchema.getCredentialSchemaId());
                    List<ListCredentialDefinitionSimpleDto> listCredentialDefinitionDtos = credentialDefinitions.stream()
                            .map(ListCredentialDefinitionSimpleDto::fromListCredentialDefinition)
                            .toList();
                    return ListCredentialSchemaDto.fromListCredentialSchema(listCredentialSchema, listCredentialDefinitionDtos);
                })
                .collect(Collectors.toList());

        return listCredentialSchemaDtos;
    }
}
