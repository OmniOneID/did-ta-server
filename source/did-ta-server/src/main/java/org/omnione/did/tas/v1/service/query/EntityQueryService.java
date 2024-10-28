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

package org.omnione.did.tas.v1.service.query;

import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for querying Entity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityQueryService {
    private final EntityRepository entityRepository;

    /**
     * Finds an Entity by its DID.
     *
     * @param did DID to search for.
     * @return Found Entity.
     * @throws OpenDidException if the Entity is not found.
     */
    public Entity findEntityByDid(String did) {
        try {
            return entityRepository.findByDid(did)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.ENTITY_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Entity not found for did {}: {}", did, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Entity for did {}: {}", did, e.getMessage());
            throw new OpenDidException(ErrorCode.ENTITY_INFO_NOT_FOUND);
        }
    }

    /**
     * Finds an Entity by its ID.
     *
     * @param id ID to search for.
     * @return Found Entity.
     * @throws OpenDidException if the Entity is not found.
     */
    public Entity findEntityById(Long id) {
        try {
            return entityRepository.findById(id)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.ENTITY_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Entity not found for id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Entity for id {}: {}", id, e.getMessage());
            throw new OpenDidException(ErrorCode.ENTITY_INFO_NOT_FOUND);
        }
    }

    /**
     * Counts the number of entities by DID.
     *
     * @param did DID to search for.
     * @return Number of entities.
     */
    public long countByDid(String did) {
        return entityRepository.countByDid(did);
    }

    /**
     * Counts the number of entities by DID and status.
     *
     * @param did DID to search for.
     * @param status Status to search for.
     * @return Number of entities.
     */
    public long countByDidAndStatus(String did, EntityStatus status) {
        return entityRepository.countByDidAndStatus(did, status);
    }
}
