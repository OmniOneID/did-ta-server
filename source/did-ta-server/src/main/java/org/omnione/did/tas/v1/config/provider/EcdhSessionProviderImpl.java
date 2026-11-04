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

import org.omnione.did.base.db.domain.Ecdh;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.tas.v1.agent.service.TransactionService;
import org.omnione.did.tas.v1.common.service.query.EcdhQueryService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.provider.EcdhSessionProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK EcdhSessionProvider interface, delegating to the server's EcdhQueryService.
 *
 * <p>Session persistence is handled by EcdhServiceImpl; saveEcdhSession is intentionally unsupported.</p>
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class EcdhSessionProviderImpl implements EcdhSessionProvider {

    private final EcdhQueryService ecdhQueryService;
    private final TransactionService transactionService;

    @Override
    public void saveEcdhSession(String clientDid, String sessionKey, String mergedNonce) {
        throw new UnsupportedOperationException(
                "ECDH session save is handled by EcdhServiceImpl, not the SDK provider.");
    }

    @Override
    public String getSessionKey(String txId) {
        return findEcdh(txId).getSessionKey();
    }

    @Override
    public String getCipher(String txId) {
        return findEcdh(txId).getCipher();
    }

    @Override
    public String getPadding(String txId) {
        return findEcdh(txId).getPadding();
    }

    @Override
    public String getClientDid(String txId) {
        return findEcdh(txId).getClientDid();
    }

    @Override
    public boolean existsEcdhSession(String txId) {
        try {
            findEcdh(txId);
            return true;
        } catch (OpenDidException e) {
            return false;
        }
    }

    private Ecdh findEcdh(String txId) {
        return ecdhQueryService.findEcdhByTransactionId(transactionService.findTransactionByTxId(txId).getId());
    }
}
