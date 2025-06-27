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

import org.omnione.did.base.db.constant.WalletStatus;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.Wallet;
import org.omnione.did.base.db.repository.WalletRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.list.v1.admin.dto.user.UserDto;
import org.omnione.did.list.v1.admin.dto.user.WalletDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for querying Wallet.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WalletQueryService {
    private final WalletRepository walletRepository;

    /**
     * Finds a Wallet by its ID.
     *
     * @param id ID to search for.
     * @return Found Wallet.
     * @throws OpenDidException if the Wallet is not found.
     */
    public Wallet findById(Long id) {
        try {
            return walletRepository.findById(id)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Wallet not found for id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Wallet for id {}: {}", id, e.getMessage());
            throw new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND);
        }
    }

    /**
     * Finds a Wallet by its wallet ID.
     *
     * @param walletId Wallet ID to search for.
     * @return Found Wallet.
     * @throws OpenDidException if the Wallet is not found.
     */
    public Wallet findByWalletId(String walletId) {
        try {
            return walletRepository.findByWalletId(walletId)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Wallet not found for walletId {}: {}", walletId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Wallet for walletId {}: {}", walletId, e.getMessage());
            throw new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND);
        }
    }

    /**
     * Finds a Wallet by its wallet ID.
     *
     * @param walletId Wallet ID to search for.
     * @return Found Wallet.
     * @throws OpenDidException if the Wallet is not found.
     */
    public long countByWalletId(String walletId) {
        return walletRepository.countByWalletId(walletId);
    }

    /**
     * Finds a Wallet by its wallet ID and DID.
     *
     * @param walletId Wallet ID to search for.
     * @param did DID to search for.
     * @return Found Wallet.
     * @throws OpenDidException if the Wallet is not found.
     */
    public long countByWalletIdAndDidAndStatus(String walletId, String did, WalletStatus walletStatus) {
        return walletRepository.countByWalletIdAndDidAndStatus(walletId, did, walletStatus);
    }

    /**
     * Finds a Wallet by its wallet ID and DID.
     *
     * @param walletId Wallet ID to search for.
     * @param did DID to search for.
     * @return Found Wallet.
     * @throws OpenDidException if the Wallet is not found.
     */
    public Wallet findByWalletIdAndDidAndStatus(String walletId, String did, WalletStatus walletStatus) {

        try {
            return walletRepository.findByWalletIdAndDidAndStatus(walletId, did, walletStatus)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Wallet not found for walletId {}, did {} : {}", walletId, did, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Wallet for walletId {}, did {}: {}", walletId, did, e.getMessage());
            throw new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND);
        }
    }

    public Wallet findByWalletIdAndUserIdAndStatus(String walletId, Long userId, WalletStatus walletStatus) {

        try {
            return walletRepository.findByWalletIdAndUserIdAndStatus(walletId, userId, walletStatus)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Wallet not found for walletId {}, userId {} : {}", walletId, userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Wallet for walletId {}, userId {}: {}", walletId, userId, e.getMessage());
            throw new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND);
        }
    }

    /**
     * Searches for a list of Wallets based on the given search key and value.
     *
     * @param searchKey Key to search for.
     * @param searchValue Value to search for.
     * @param pageable Pageable.
     * @return Page of WalletDto.
     */
    public Page<WalletDto> searchWalletList(String searchKey, String searchValue, Pageable pageable) {
        Page<Wallet> walletPage = walletRepository.searchWallets(searchKey, searchValue, pageable);

        List<WalletDto> walletDtos = walletPage.getContent().stream()
                .map(WalletDto::fromWallet)
                .collect(Collectors.toList());

        return new PageImpl<>(walletDtos, pageable, walletPage.getTotalElements());
    }
}
