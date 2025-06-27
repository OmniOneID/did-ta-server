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

package org.omnione.did.tas.v1.agent.service;

import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;

import java.time.Instant;
import java.util.List;

/**
 * Transaction service interface for managing transactions and sub-transactions
 */
public interface TransactionService {
    public Transaction findTransactionByTxId (String txId);
    public Transaction insertTransaction(Transaction transaction);
    public void updateTransaction(Transaction transaction);
    public void updateTransactionCertificateId(Long id, String certificateId);
    public void updateTransactionStatus(Long id, TransactionStatus transactionStatus);
    public void updateTransactionAuthNonce(Long id, String authNonce);
    public void updateTransactionPii(Long id, String pii);

    public SubTransaction findSubTransactionByTxIdAndStep (String txId, Integer steo);
    public List<SubTransaction> findSubTransactionListByTxId(String txId, Integer steo);
    public SubTransaction insertSubTransaction(SubTransaction subTransaction);
    public void updateSubTransaction(SubTransaction subTransaction);
    public SubTransaction findLastSubTransaction(Long transactionId);

    public Instant retrieveTransactionExpiredTime();
}
