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
import org.omnione.did.base.db.domain.ListVcSchema;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.list.v1.admin.dto.vcschema.ListVcSchemaDto;
import org.omnione.did.list.v1.admin.dto.vcschema.RegisterVcSchemaFromIssuerReqDto;
import org.omnione.did.list.v1.admin.service.query.ListVcSchemaQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListVcSchemaManagementService {
    private final ListVcSchemaQueryService listVcSchemaQueryService;

    public Page<ListVcSchemaDto> searchVcSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return listVcSchemaQueryService.searchVcSchemaList(searchKey, searchValue, pageable);
    }

    public ListVcSchemaDto findById(Long id) {
        ListVcSchema listVcSchema = listVcSchemaQueryService.findById(id);
        return ListVcSchemaDto.fromVcSchema(listVcSchema);
    }

    public ListVcSchema findBySchemaId(String schemaId) {
        return listVcSchemaQueryService.findBySchemaId(schemaId);
    }


    public EmptyResDto registerVcSchemaFromIssuer(RegisterVcSchemaFromIssuerReqDto registerVcSchemaFromIssuerReqDto) {

        byte[] decodedVcSchema = BaseMultibaseUtil.decode(registerVcSchemaFromIssuerReqDto.getVcSchema());
        log.debug("decodedVcSchema: {}", new String(decodedVcSchema));

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(new String(decodedVcSchema));

        log.debug("vcSchema: {}", vcSchema.toString());


        return EmptyResDto.builder().build();
    }
}
