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
import org.omnione.did.base.db.domain.ListVcSchema;
import org.omnione.did.base.db.repository.ListVcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.list.v1.admin.dto.vcschema.ListVcSchemaDto;
import org.omnione.did.list.v1.admin.dto.vcschema.RegisterVcSchemaFromIssuerReqDto;
import org.omnione.did.list.v1.admin.service.query.ListVcSchemaQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListVcSchemaManagementService {
    private final ListVcSchemaQueryService listVcSchemaQueryService;
    private final ListVcSchemaRepository listVcSchemaRepository;
    private final EntityQueryService entityQueryService;

    public Page<ListVcSchemaDto> searchVcSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return listVcSchemaQueryService.searchVcSchemaList(searchKey, searchValue, pageable);
    }

    public ListVcSchemaDto findById(Long id) {
        ListVcSchema listVcSchema = listVcSchemaQueryService.findById(id);
        return ListVcSchemaDto.fromListVcSchema(listVcSchema);
    }

    public ListVcSchema findBySchemaId(String schemaId) {
        return listVcSchemaQueryService.findBySchemaId(schemaId);
    }

    public EmptyResDto registerVcSchemaFromIssuer(RegisterVcSchemaFromIssuerReqDto request) {
        try {
            log.debug("=== Starting registerVcSchemaFromIssuer ===");

            VcSchema vcSchema = decodeVcSchema(request.getVcSchema());
            Entity entity = findIssuerEntity(request.getIssuerDid());

            ListVcSchema existingSchema = listVcSchemaQueryService.findBySchemaIdAndIssuerDidOrNull(vcSchema.getId(), request.getIssuerDid());

            if (existingSchema != null) {
                updateExistingVcSchema(existingSchema, vcSchema);
            } else {
                insertNewVcSchema(request.getIssuerDid(), entity, vcSchema);
            }

            log.debug("*** Finished registerVcSchemaFromIssuer ***");
            return EmptyResDto.builder().build();
        } catch (OpenDidException e) {
            log.error("OpenDidException occurred during registerVcSchemaFromIssuer: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register vc schema from issuer", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_VC_SCHEMA_FROM_ISSUER);
        }
    }

    private VcSchema decodeVcSchema(String encodedVcSchema) {
        log.debug("\t--> Decoding VC Schema");
        byte[] decodedData = BaseMultibaseUtil.decode(encodedVcSchema);
        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(new String(decodedData));
        log.debug("\t--> Decoded VC Schema: {}", vcSchema);
        return vcSchema;
    }

    private Entity findIssuerEntity(String issuerDid) {
        log.debug("\t--> Finding Entity by DID: {}", issuerDid);
        return entityQueryService.findEntityByDid(issuerDid);
    }

    private void updateExistingVcSchema(ListVcSchema existingSchema, VcSchema vcSchema) {
        log.debug("\t--> Updating existing vc-schema");

        existingSchema.setTitle(vcSchema.getTitle());
        existingSchema.setDescription(vcSchema.getDescription());
        existingSchema.setSchema(vcSchema.toJson());

        listVcSchemaRepository.save(existingSchema);
    }

    private void insertNewVcSchema(String issuerDid, Entity entity, VcSchema vcSchema) {
        log.debug("\t--> Inserting vc-schema");

        ListVcSchema newSchema = ListVcSchema.builder()
                .title(vcSchema.getTitle())
                .description(vcSchema.getDescription())
                .schemaId(vcSchema.getId())
                .issuerDid(issuerDid)
                .issuerName(entity.getName())
                .schema(vcSchema.toJson())
                .build();

        listVcSchemaRepository.save(newSchema);
    }

}
