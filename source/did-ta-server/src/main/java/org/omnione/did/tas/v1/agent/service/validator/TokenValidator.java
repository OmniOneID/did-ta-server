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

package org.omnione.did.tas.v1.agent.service.validator;

import org.omnione.did.base.datamodel.enums.ServerTokenPurpose;
import org.omnione.did.base.db.domain.Token;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.tas.v1.common.service.query.TokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Service for validating tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidator {
    private final TokenQueryService tokenQueryService;

    /**
     * Validates a server token.
     *
     * @param requestedServerToken The server token to validate
     * @param transactionId The transaction ID associated with the token
     * @param serverTokenPurposes The purposes for which the token is valid
     * @return The validated token
     * @throws OpenDidException if validation fails or the token is invalid or expired
     */
    public Token validateServerToken(String requestedServerToken, long transactionId, ServerTokenPurpose... serverTokenPurposes) {
        // Decode requested server token.
        byte[] requestServerTokenBytes = BaseMultibaseUtil.decode(requestedServerToken);

        // Retrieve token information
        Token token = tokenQueryService.findTokenByTransactionId(transactionId);
        byte[] savedServerTokenBytes = BaseMultibaseUtil.decode(token.getToken());

        // Verify token purpose.
        boolean isValidPurpose = Arrays.stream(serverTokenPurposes)
                .map(ServerTokenPurpose::toString)
                .anyMatch(token.getPurpose()::equals);

        if (!isValidPurpose) {
            log.error("\t--> Unsupported token purpose for transactionId: {}", transactionId);
            throw new OpenDidException(ErrorCode.UNSUPPORTED_PURPOSE);
        }

        // Validate if the token has expired.
        if (DateTimeUtil.isExpired(token.getExpiredAt())) {
            log.error("\t--> Token has expired for transactionId: {}", transactionId);
            throw new OpenDidException(ErrorCode.TOKEN_EXPIRED);
        }

        if (!Arrays.equals(requestServerTokenBytes, savedServerTokenBytes)) {
            log.error("\t--> Invalid token for transactionId: {}", transactionId);
            throw new OpenDidException(ErrorCode.INVALID_TOKEN);
        }

        return token;
    }
}