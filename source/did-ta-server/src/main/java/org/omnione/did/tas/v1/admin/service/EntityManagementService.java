/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.tas.v1.common.dto.admin.entity.EntityInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EntityManagementService {

    private final EntityRepository entityRepository;

    public Page<EntityInfoDto> searchEntities(String searchKey, String searchValue, Pageable pageable) {
        Page<Entity> entityPage = entityRepository.searchEntities(searchKey, searchValue, pageable);

        List<EntityInfoDto> entityDtos = entityPage.getContent().stream()
                .map(EntityInfoDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(entityDtos, pageable, entityPage.getTotalElements());
    }


}
