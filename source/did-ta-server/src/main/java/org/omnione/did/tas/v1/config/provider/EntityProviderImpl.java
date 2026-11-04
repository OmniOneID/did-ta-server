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

package org.omnione.did.tas.v1.config.provider;

import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.Role;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.tas.exception.TaSdkErrorCode;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.provider.EntityProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of the SDK EntityProvider interface, delegating to the server's EntityQueryService.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class EntityProviderImpl implements EntityProvider {

    private final EntityQueryService entityQueryService;
    private final EntityRepository entityRepository;

    @Override
    public org.omnione.did.tas.model.domain.Entity getEntityByDid(String did) {
        return toSdkEntity(entityQueryService.findEntityByDid(did));
    }

    @Override
    public org.omnione.did.tas.model.domain.Entity getEntityByRole(RoleType roleType) {
        List<Entity> entities = entityRepository.findByRoleOrderByRole(Role.fromRoleType(roleType));
        if (entities.isEmpty()) {
            throw new TaSdkException(TaSdkErrorCode.SDK_ENTITY_NOT_FOUND,
                    "Entity not found for role: " + roleType);
        }
        return toSdkEntity(entities.get(0));
    }

    @Override
    public boolean existsEntityByDid(String did) {
        return entityQueryService.countByDid(did) > 0;
    }

    @Override
    public void validateEntityStatus(String did) {
        if (entityQueryService.findEntityByDid(did).getStatus() != EntityStatus.COMPLETED) {
            throw new TaSdkException(TaSdkErrorCode.ENTITY_NOT_COMPLETED,
                    "Entity is not in COMPLETED status: " + did);
        }
    }

    /**
     * The dn is the subject of the Certificate VC when a request has no DN. It is always "o=name", even when a DN
     * from an earlier request is stored for the Entity.
     */
    private org.omnione.did.tas.model.domain.Entity toSdkEntity(Entity entity) {
        return org.omnione.did.tas.model.domain.Entity.builder()
                .did(entity.getDid())
                .name(entity.getName())
                .role(entity.getRole().toRoleType())
                .dn(String.format("o=%s", entity.getName()))
                .certVcRef(entity.getPublishedCertificateUrl())
                .build();
    }
}
