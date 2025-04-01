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

package org.omnione.did.tas.v1.common.service.query;

import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.repository.TasRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for querying TAS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TasQueryService {
    private final TasRepository tasRepository;

    /**
     * Finds a TAS by its DID.
     *
     * @return Found TAS.
     * @throws OpenDidException if the TAS is not found.
     */
    public Tas findTas() {
        try {
            return tasRepository.findTop1ByOrderByIdAsc()
                    .orElseThrow(() -> new OpenDidException(ErrorCode.TAS_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("TAS not found : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding TAS : {}", e.getMessage());
            throw new OpenDidException(ErrorCode.TAS_INFO_NOT_FOUND);
        }
    }

    /**
     * Finds a TAS by its DID.
     *
     * @param did DID to search for.
     * @return Found TAS.
     * @throws OpenDidException if the TAS is not found.
     */
    public long countByDid(String did) {
        return tasRepository.countByDid(did);
    }

    /**
     * Finds a TAS by its DID and status.
     *
     * @param did DID to search for.
     * @param status Status to search for.
     * @return Found TAS.
     * @throws OpenDidException if the TAS is not found.
     */
    public long countByDidAndStatus(String did, TasStatus status) {
        return tasRepository.countByDidAndStatus(did, status);
    }

    /**
     * Finds a TAS by its DID. Returns null if not found.
     *
     * @return Found TAS or null if not found.
     */
    public Tas findTasOrNull() {
        return tasRepository.findTop1ByOrderByIdAsc().orElse(null);
    }
}
