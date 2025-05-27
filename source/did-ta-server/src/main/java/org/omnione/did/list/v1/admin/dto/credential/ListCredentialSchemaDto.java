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
package org.omnione.did.list.v1.admin.dto.credential;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.omnione.did.base.db.domain.ListCredentialSchema;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.GsonSerializationUtil;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.omnione.did.zkp.datamodel.schema.AttributeDef.ATTR_TYPE;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class ListCredentialSchemaDto {
    private final Long id;
    private final String credentialSchemaId;
    private final String issuerDid;
    private final String issuerName;
    private final String name;
    private final String description;
    private final Map<String, Object> credentialSchema;
    private final String createdAt;
    private final String updatedAt;
    private final String entityName;
    private final List<ListCredentialDefinitionSimpleDto> credentialDefinitions;

    public static ListCredentialSchemaDto fromListCredentialSchema(ListCredentialSchema listCredentialSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Gson customGson = GsonSerializationUtil.createGsonWithAttributeTypeSerializer();
        Map<String, Object> parsedVcSchema = parseVcSchema(listCredentialSchema.getCredentialSchema());

        return ListCredentialSchemaDto.builder()
                .id(listCredentialSchema.getId())
                .credentialSchemaId(listCredentialSchema.getCredentialSchemaId())
                .issuerDid(listCredentialSchema.getIssuerDid())
                .issuerName(listCredentialSchema.getIssuerName())
                .name(listCredentialSchema.getName())
                .credentialSchema(parsedVcSchema)
                .createdAt(formatInstant(listCredentialSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(listCredentialSchema.getUpdatedAt(), formatter))
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

    public static ListCredentialSchemaDto fromListVcSchemaForAgent(ListCredentialSchema listCredentialSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(listCredentialSchema.getCredentialSchema());

        Map<String, Object> parsedVcSchema = parseVcSchema(listCredentialSchema.getCredentialSchema());

        return ListCredentialSchemaDto.builder()
                .credentialSchemaId(listCredentialSchema.getCredentialSchemaId())
                .issuerDid(listCredentialSchema.getIssuerDid())
                .issuerName(listCredentialSchema.getIssuerName())
                .name(listCredentialSchema.getName())
                .credentialSchema(parsedVcSchema)
                .createdAt(formatInstant(listCredentialSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(listCredentialSchema.getUpdatedAt(), formatter))
                .build();
    }

    public static ListCredentialSchemaDto fromListCredentialSchema(ListCredentialSchema listCredentialSchema, List<ListCredentialDefinitionSimpleDto> credentialDefinitions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Gson customGson = GsonSerializationUtil.createGsonWithAttributeTypeSerializer();
        CredentialSchema credentialSchema = GsonSerializationUtil.parseCredentialSchema(listCredentialSchema.getCredentialSchema());
        String credentialJson = customGson.toJson(credentialSchema, CredentialSchema.class);

        Map<String, Object> parsedVcSchema = parseVcSchema(credentialJson);
        return ListCredentialSchemaDto.builder()
                .id(listCredentialSchema.getId())
                .credentialSchemaId(listCredentialSchema.getCredentialSchemaId())
                .issuerDid(listCredentialSchema.getIssuerDid())
                .issuerName(listCredentialSchema.getIssuerName())
                .name(listCredentialSchema.getName())
                .credentialSchema(parsedVcSchema)
                .credentialDefinitions(credentialDefinitions)
                .createdAt(formatInstant(listCredentialSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(listCredentialSchema.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
