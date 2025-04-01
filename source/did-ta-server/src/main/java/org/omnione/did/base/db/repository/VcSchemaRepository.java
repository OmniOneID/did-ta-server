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

import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.data.model.enums.vc.VcType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VcSchemaRepository extends JpaRepository<VcSchema, Long> {
    Optional<VcSchema> findByType(VcType vcType);
}
