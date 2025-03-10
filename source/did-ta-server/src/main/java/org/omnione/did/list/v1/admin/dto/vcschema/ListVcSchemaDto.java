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

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.ListVcSchema;
import org.omnione.did.data.model.schema.VcSchema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ListVcSchemaDto {
    private final Long id;
    private final String schemaId;
    private final String issuerDid;
    private final String issuerName;
    private final String title;
    private final String description;
    private final VcSchema vcSchema;
    private final String createdAt;
    private final String updatedAt;
    private final String entityName;

    public static ListVcSchemaDto fromVcSchema(ListVcSchema listVcSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(listVcSchema.getSchema());

        return ListVcSchemaDto.builder()
                .id(listVcSchema.getId())
                .schemaId(listVcSchema.getSchemaId())
                .issuerDid(listVcSchema.getIssuerDid())
                .issuerName(listVcSchema.getIssuerName())
                .title(listVcSchema.getTitle())
                .description(listVcSchema.getDescription())
                .vcSchema(vcSchema)
                .createdAt(formatInstant(listVcSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(listVcSchema.getUpdatedAt(), formatter))
                .build();
    }

    public static ListVcSchemaDto fromVcSchemaForAgent(ListVcSchema listVcSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(listVcSchema.getSchema());

        return ListVcSchemaDto.builder()
                .schemaId(listVcSchema.getSchemaId())
                .issuerDid(listVcSchema.getIssuerDid())
                .issuerName(listVcSchema.getIssuerName())
                .title(listVcSchema.getTitle())
                .description(listVcSchema.getDescription())
                .vcSchema(vcSchema)
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
