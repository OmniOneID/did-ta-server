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
import org.omnione.did.base.db.repository.SubTransactionRepository;
import org.omnione.did.base.db.repository.TransactionRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.v1.common.service.query.ApiQueryService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Transaction service implementation for managing transactions and sub-transactions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final SubTransactionRepository subTransactionRepository;
    private final ApiQueryService apiQueryService;

    /**
     * Finds a transaction by its transaction ID.
     *
     * @param txId The transaction ID to search for
     * @return The found Transaction object
     * @throws OpenDidException if the transaction is not found
     */
    @Override
    public Transaction findTransactionByTxId(String txId) {
        Optional<Transaction> optionalTransaction = transactionRepository.findByTxId(txId);
        if (optionalTransaction.isEmpty()) {
            log.error("\t--> Transaction not found for txId: {}", txId);
            throw new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        return optionalTransaction.get();
    }

    /**
     * Inserts a new transaction into the repository.
     *
     * @param transaction The Transaction object to be inserted
     * @return The saved Transaction object
     */
    @Override
    public Transaction insertTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Updates an existing transaction.
     *
     * @param transaction The Transaction object to be updated
     */
    @Override
    public void updateTransaction(Transaction transaction) {
        // Implementation not provided
    }

    /**
     * Updates the certificate ID of a transaction.
     *
     * @param id The ID of the transaction to update
     * @param certificateId The new certificate ID
     * @throws OpenDidException if the transaction is not found
     */
    @Override
    public void updateTransactionCertificateId(Long id, String certificateId) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        Transaction transaction = optionalTransaction.get();
        transaction.setCertificateId(certificateId);

        transactionRepository.save(transaction);
    }

    /**
     * Updates the authentication nonce of a transaction.
     *
     * @param id The ID of the transaction to update
     * @param authNonce The new authentication nonce
     * @throws OpenDidException if the transaction is not found
     */
    @Override
    public void updateTransactionAuthNonce(Long id, String authNonce) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        Transaction transaction = optionalTransaction.get();
        transaction.setAuthNonce(authNonce);

        transactionRepository.save(transaction);
    }

    /**
     * Updates the PII (Personally Identifiable Information) of a transaction.
     *
     * @param id The ID of the transaction to update
     * @param pii The new PII
     * @throws OpenDidException if the transaction is not found
     */
    @Override
    public void updateTransactionPii(Long id, String pii) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        Transaction transaction = optionalTransaction.get();
        transaction.setPii(pii);

        transactionRepository.save(transaction);
    }

    /**
     * Updates the status of a transaction.
     *
     * @param id The ID of the transaction to update
     * @param transactionStatus The new transaction status
     * @throws OpenDidException if the transaction is not found
     */
    @Override
    public void updateTransactionStatus(Long id, TransactionStatus transactionStatus) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        Transaction transaction = optionalTransaction.get();
        transaction.setStatus(transactionStatus);

        transactionRepository.save(transaction);
    }

    /**
     * Finds a sub-transaction by transaction ID and step.
     *
     * @param txId The transaction ID
     * @param step The step of the sub-transaction
     * @return The found SubTransaction object
     */
    @Override
    public SubTransaction findSubTransactionByTxIdAndStep(String txId, Integer step) {
        return null; // Implementation not provided
    }

    /**
     * Finds a list of sub-transactions by transaction ID and step.
     *
     * @param txId The transaction ID
     * @param step The step of the sub-transactions
     * @return A list of SubTransaction objects
     */
    @Override
    public List<SubTransaction> findSubTransactionListByTxId(String txId, Integer step) {
        return null; // Implementation not provided
    }

    /**
     * Inserts a new sub-transaction into the repository.
     *
     * @param subTransaction The SubTransaction object to be inserted
     * @return The saved SubTransaction object
     */
    @Override
    public SubTransaction insertSubTransaction(SubTransaction subTransaction) {
        return subTransactionRepository.save(subTransaction);
    }

    /**
     * Updates an existing sub-transaction.
     *
     * @param subTransaction The SubTransaction object to be updated
     */
    @Override
    public void updateSubTransaction(SubTransaction subTransaction) {
        // Implementation not provided
    }

    /**
     * Finds the last sub-transaction for a given transaction ID.
     *
     * @param transactionId The ID of the transaction
     * @return The last SubTransaction object
     * @throws OpenDidException if no sub-transaction is found
     */
    @Override
    public SubTransaction findLastSubTransaction(Long transactionId) {
        Optional<SubTransaction> optionalSubTransaction = subTransactionRepository.findFirstByTransactionIdOrderByStepDesc(transactionId);
        if (optionalSubTransaction.isEmpty()) {
            throw new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND);
        }
        return optionalSubTransaction.get();
    }

    /**
     * Retrieves the expiration time for a transaction.
     *
     * @return The expiration time as an Instant object
     */
    @Override
    public Instant retrieveTransactionExpiredTime() {
        Duration seconds = Duration.ofSeconds(apiQueryService.findTransactionExpirationTime());
        return Instant.now().plus(seconds);
    }
}