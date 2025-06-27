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
package org.omnione.did.tas.v1.admin.dto.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.tas.v1.common.service.JsonParseService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * DTO for requesting Entity information.
 */
@Getter
@Builder
public class EntityInfoDto {
    private final Long id;
    private final String did;
    private final String name;
    private final String role;
    private final String status;
    private final String serverUrl;
    private final String certificateUrl;
    private final String createdAt;
    private final String updatedAt;
    private Map<String, Object> didDocument;

    public static EntityInfoDto fromEntity(Entity entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return EntityInfoDto.builder()
                .id(entity.getId())
                .did(entity.getDid())
                .name(entity.getName())
                .role(entity.getRole().name())
                .status(entity.getStatus().name())
                .serverUrl(entity.getServerUrl())
                .certificateUrl(entity.getCertificateUrl())
                .createdAt(formatInstant(entity.getCreatedAt(), formatter))
                .updatedAt(formatInstant(entity.getUpdatedAt(), formatter))
                .build();
    }

    public static EntityInfoDto fromEntity(Entity entity, DidDocument didDocument) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return EntityInfoDto.builder()
                .id(entity.getId())
                .did(entity.getDid())
                .name(entity.getName())
                .role(entity.getRole().name())
                .status(entity.getStatus().name())
                .serverUrl(entity.getServerUrl())
                .certificateUrl(entity.getCertificateUrl())
                .didDocument(parseDidDocToMap(didDocument.toJson()))
                .createdAt(formatInstant(entity.getCreatedAt(), formatter))
                .updatedAt(formatInstant(entity.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }

    public static Map<String, Object> parseDidDocToMap(String didDocJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(didDocJson, Map.class);
        } catch (JsonProcessingException e) {
            throw new OpenDidException(ErrorCode.INVALID_DID_DOCUMENT);
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.INVALID_DID_DOCUMENT);
        }
    }
}
