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
package org.omnione.did.list.v1.admin.dto.vcschema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.ListVcSchema;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.schema.VcSchema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Builder
public class ListVcSchemaDto {
    private final Long id;
    private final String schemaId;
    private final String issuerDid;
    private final String issuerName;
    private final String title;
    private final String description;
    private final Map<String, Object> vcSchema;
    private final String createdAt;
    private final String updatedAt;
    private final String entityName;

    public static ListVcSchemaDto fromListVcSchema(ListVcSchema listVcSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(listVcSchema.getSchema());

        Map<String, Object> parsedVcSchema = parseVcSchema(listVcSchema.getSchema());

        return ListVcSchemaDto.builder()
                .id(listVcSchema.getId())
                .schemaId(listVcSchema.getSchemaId())
                .issuerDid(listVcSchema.getIssuerDid())
                .issuerName(listVcSchema.getIssuerName())
                .title(listVcSchema.getTitle())
                .description(listVcSchema.getDescription())
                .vcSchema(parsedVcSchema)
                .createdAt(formatInstant(listVcSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(listVcSchema.getUpdatedAt(), formatter))
                .build();
    }

    private static Map<String, Object> parseVcSchema(String schemaJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(schemaJson, Map.class);
        } catch (JsonProcessingException e) {
            throw new OpenDidException(ErrorCode.INVALID_VC_SCHEMA);
        }
    }

    public static ListVcSchemaDto fromListVcSchemaForAgent(ListVcSchema listVcSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(listVcSchema.getSchema());

        Map<String, Object> parsedVcSchema = parseVcSchema(listVcSchema.getSchema());

        return ListVcSchemaDto.builder()
                .schemaId(listVcSchema.getSchemaId())
                .issuerDid(listVcSchema.getIssuerDid())
                .issuerName(listVcSchema.getIssuerName())
                .title(listVcSchema.getTitle())
                .description(listVcSchema.getDescription())
                .vcSchema(parsedVcSchema)
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
