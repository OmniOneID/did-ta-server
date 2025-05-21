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
import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.ListCredentialDefinition;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.schema.VcSchema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Builder
public class ListCredentialDefinitionDto {
    private final Long id;
    private final String credentialDefinitionId;
    private final String issuerDid;
    private final String issuerName;
    private final String name;
    private final String description;
    private final Map<String, Object> vcSchema;
    private final String createdAt;
    private final String updatedAt;
    private final String entityName;

    public static ListCredentialDefinitionDto fromListCredentialDefinition(ListCredentialDefinition credentialDefinition) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(credentialDefinition.getCredentialDefinition());

        Map<String, Object> parsedVcSchema = parseVcSchema(credentialDefinition.getCredentialDefinition());

        return ListCredentialDefinitionDto.builder()
                .id(credentialDefinition.getId())
                .credentialDefinitionId(credentialDefinition.getCredentialDefinitionId())
                .issuerDid(credentialDefinition.getIssuerDid())
                .issuerName(credentialDefinition.getIssuerName())
                .vcSchema(parsedVcSchema)
                .createdAt(formatInstant(credentialDefinition.getCreatedAt(), formatter))
                .updatedAt(formatInstant(credentialDefinition.getUpdatedAt(), formatter))
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

    public static ListCredentialDefinitionDto fromListCredentialDefinitionForAgent(ListCredentialDefinition listCredentialDefinitiona) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(listCredentialDefinitiona.getCredentialDefinition());

        Map<String, Object> parsedVcSchema = parseVcSchema(listCredentialDefinitiona.getCredentialDefinition());

        return ListCredentialDefinitionDto.builder()
                .credentialDefinitionId(listCredentialDefinitiona.getCredentialDefinitionId())
                .issuerDid(listCredentialDefinitiona.getIssuerDid())
                .issuerName(listCredentialDefinitiona.getIssuerName())
                .vcSchema(parsedVcSchema)
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
