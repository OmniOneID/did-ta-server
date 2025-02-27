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

import org.omnione.did.base.db.domain.Token;
import org.omnione.did.base.db.repository.TokenRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for querying Token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenQueryService {
    private final TokenRepository tokenRepository;

    /**
     * Finds a Token by its transaction ID.
     *
     * @param transactionId Transaction ID to search for.
     * @return Found Token.
     * @throws OpenDidException if the Token is not found.
     */
    public Token findTokenByTransactionId(Long transactionId) {
        try {
            return tokenRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.TOKEN_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Token not found for transactionId {}: {}", transactionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Token for transactionId {}: {}", transactionId, e.getMessage());
            throw new OpenDidException(ErrorCode.TOKEN_INFO_NOT_FOUND);
        }
    }
}
