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

import org.omnione.did.base.db.domain.Ecdh;
import org.omnione.did.base.db.repository.EcdhRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for querying ECDH.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EcdhQueryService {

    private final EcdhRepository ecdhRepository;

    /**
     * Finds an ECDH by its transaction ID.
     *
     * @param transactionId Transaction ID to search for.
     * @return Found ECDH.
     * @throws OpenDidException if the ECDH is not found.
     */
    public Ecdh findEcdhByTransactionId(Long transactionId) {
        try {
            return ecdhRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.ECDH_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("ECDH not found for transactionId {}: {}", transactionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding ECDH for transactionId {}: {}", transactionId, e.getMessage());
            throw new OpenDidException(ErrorCode.ECDH_NOT_FOUND);
        }
    }
}
