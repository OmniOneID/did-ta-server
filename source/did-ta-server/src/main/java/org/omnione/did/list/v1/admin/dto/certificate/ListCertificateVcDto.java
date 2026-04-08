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
package org.omnione.did.list.v1.admin.dto.certificate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.ListCertificateVc;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Builder
public class ListCertificateVcDto {
    private final Long id;
    private final String did;
    private final String name;
    private final Map<String, Object> certificateVc;
    private final String publishedUrl;
    private final String publishedAt;
    private final String expiredAt;
    private final String createdAt;
    private final String updatedAt;

    public static ListCertificateVcDto fromListCertificateVc(ListCertificateVc entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ListCertificateVcDto.builder()
                .id(entity.getId())
                .did(entity.getDid())
                .name(entity.getName())
                .certificateVc(parseCertificateVcToMap(entity.getCertificateVc()))
                .publishedUrl(entity.getPublishedUrl())
                .publishedAt(formatInstant(entity.getPublishedAt(), formatter))
                .expiredAt(formatInstant(entity.getExpiredAt(), formatter))
                .createdAt(formatInstant(entity.getCreatedAt(), formatter))
                .updatedAt(formatInstant(entity.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }

    private static Map<String, Object> parseCertificateVcToMap(String certificateVc) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(certificateVc, Map.class);
        } catch (JsonProcessingException e) {
            throw new OpenDidException(ErrorCode.INVALID_CERTIFICATE_VC_JSON_FORMAT);
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.INVALID_CERTIFICATE_VC_JSON_FORMAT);
        }
    }
}
