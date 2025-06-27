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

package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.domain.Tas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing operations on the Tas entity.
 * Extends JpaRepository to provide basic CRUD operations and defines custom query methods
 * for more specific database interactions with Tas entities.
 */
@Repository
public interface TasRepository extends JpaRepository<Tas, Long> {
    Optional<Tas> findByDid(String did);
    long countByDid(String did);
    long countByDidAndStatus(String did, TasStatus status);
    Optional<Tas> findTop1ByOrderByIdAsc();
}
