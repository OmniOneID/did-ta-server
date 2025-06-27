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

import org.omnione.did.base.db.domain.DidOffer;
import org.omnione.did.base.db.repository.DidOfferRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for querying DidOffer.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DidOfferQueryService {

    private final DidOfferRepository didOfferRepository;

    /**
     * save a DidOffer.
     *
     * @param didOffer The DidOffer to save
     * @return The saved DidOffer
     * @throws OpenDidException if the save operation fails
     */
    public DidOffer save(DidOffer didOffer) {
        return didOfferRepository.save(didOffer);
    }

    /**
     * Finds a DidOffer by its ID.
     *
     * @param id ID to search for.
     * @return Found DidOffer.
     * @throws OpenDidException if the DidOffer is not found.
     */
    public DidOffer findById(Long id) {
        return didOfferRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_OFFER_NOT_FOUND));
    }

    /**
     * Finds a DidOffer by its offer ID.
     *
     * @param offerId Offer ID to search for.
     * @return Found DidOffer.
     * @throws OpenDidException if the DidOffer is not found.
     */
    public DidOffer findByOfferId(String offerId) {
        return didOfferRepository.findByOfferId(offerId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_OFFER_NOT_FOUND));
    }

    /**
     * Finds a DidOffer by its transaction ID.
     *
     * @param transactionId Transaction ID to search for.
     * @return Found DidOffer.
     * @throws OpenDidException if the DidOffer is not found.
     */
    public DidOffer findByTransactionId(Long transactionId) {
        return didOfferRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_OFFER_NOT_FOUND));
    }

    /**
     * Updates the transaction ID of a DidOffer.
     *
     * @param id The ID of the DidOffer to update
     * @param transactionId transaction table's key
     * @return The updated DidOffer
     */
    public DidOffer updateTransactionId(Long id, Long transactionId) {
        DidOffer didOffer = findById(id);
        didOffer.setTransactionId(transactionId);
        return save(didOffer);
    }

}
