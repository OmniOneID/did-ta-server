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
package org.omnione.did.list.v1.admin.dto.vcplan;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.datamodel.data.VcPlan;
import org.omnione.did.base.db.domain.ListVcPlan;
import org.omnione.did.common.util.JsonUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ListVcPlanDto {
    private final Long id;
    private final String vcPlanId;
    private final String name;
    private final String description;
    private final String issuerDid;
    private final String issuerName;
    private final VcPlan vcPlan;
    private final String initiate;
    private final String createdAt;
    private final String updatedAt;

    public static ListVcPlanDto fromListVcPlan(ListVcPlan listVcPlan) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        VcPlan vcPlan = JsonUtil.deserializeFromJson(listVcPlan.getVcPlan(), VcPlan.class);

        return ListVcPlanDto.builder()
                .id(listVcPlan.getId())
                .vcPlanId(listVcPlan.getVcPlanId())
                .name(listVcPlan.getName())
                .description(listVcPlan.getDescription())
                .issuerDid(listVcPlan.getIssuerDid())
                .issuerName(listVcPlan.getIssuerName())
                .vcPlan(vcPlan)
                .initiate(listVcPlan.getInitiate())
                .createdAt(formatInstant(listVcPlan.getCreatedAt(), formatter))
                .updatedAt(formatInstant(listVcPlan.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
