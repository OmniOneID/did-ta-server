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

import org.omnione.did.base.datamodel.enums.ServerTokenPurpose;
import org.omnione.did.tas.v1.agent.service.TransactionService;
import org.omnione.did.tas.v1.agent.service.validator.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.provider.TokenProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK TokenProvider interface, delegating to the server's TokenValidator.
 *
 * <p>The SDK does not know which purpose each API requires. The services validate the token with the required
 * purposes before they call the SDK.</p>
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class TokenProviderImpl implements TokenProvider {

    private final TokenValidator tokenValidator;
    private final TransactionService transactionService;

    @Override
    public void validateServerToken(String serverToken, String txId) {
        tokenValidator.validateServerToken(serverToken, transactionService.findTransactionByTxId(txId).getId(),
                ServerTokenPurpose.values());
    }
}
