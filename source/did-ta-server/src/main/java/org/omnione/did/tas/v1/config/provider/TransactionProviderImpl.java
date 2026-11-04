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

import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.tas.v1.agent.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.model.domain.Transaction;
import org.omnione.did.tas.provider.TransactionProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK TransactionProvider interface, delegating to the server's TransactionService.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class TransactionProviderImpl implements TransactionProvider {

    private final TransactionService transactionService;

    @Override
    public Transaction getTransaction(String txId) {
        org.omnione.did.base.db.domain.Transaction entity =
                transactionService.findTransactionByTxId(txId);
        return Transaction.builder()
                .authNonce(entity.getAuthNonce())
                .did(entity.getDid())
                .build();
    }

    @Override
    public boolean existsTransaction(String txId) {
        try {
            transactionService.findTransactionByTxId(txId);
            return true;
        } catch (OpenDidException e) {
            return false;
        }
    }
}
