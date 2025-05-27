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
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Builder
public class ListCredentialDefinitionSimpleDto {
    private final Long id;
    private final String credentialDefinitionId;
    private final String credentialDefinitionTag;
    private final String credentialSchemaId;
    private final String issuerDid;
    private final String issuerName;
    private final String name;
    private final String description;
    private final String createdAt;
    private final String updatedAt;
    private final String entityName;

    public static ListCredentialDefinitionSimpleDto fromListCredentialDefinition(ListCredentialDefinition listCredentialDefinition) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ListCredentialDefinitionSimpleDto.builder()
                .id(listCredentialDefinition.getId())
                .credentialDefinitionId(listCredentialDefinition.getCredentialDefinitionId())
                .credentialDefinitionTag(listCredentialDefinition.getCredentialDefinitionTag())
                .credentialSchemaId(listCredentialDefinition.getCredentialSchemaId())
                .issuerDid(listCredentialDefinition.getIssuerDid())
                .issuerName(listCredentialDefinition.getIssuerName())
                .createdAt(formatInstant(listCredentialDefinition.getCreatedAt(), formatter))
                .updatedAt(formatInstant(listCredentialDefinition.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
