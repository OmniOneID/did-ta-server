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
package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.Wallet;
import org.omnione.did.list.v1.admin.dto.user.AppDto;
import org.omnione.did.list.v1.admin.dto.user.UserDto;
import org.omnione.did.list.v1.admin.dto.user.WalletDto;
import org.omnione.did.tas.v1.common.service.query.AppQueryService;
import org.omnione.did.tas.v1.common.service.query.UserQueryService;
import org.omnione.did.tas.v1.common.service.query.WalletQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserManagementService {
    private final UserQueryService userQueryService;
    private final AppQueryService appQueryService;
    private final WalletQueryService walletQueryService;

    public Page<UserDto> searchUsers(String searchKey, String searchValue, Pageable pageable) {
        return userQueryService.searchUserList(searchKey, searchValue, pageable);
    }

    /**
     * Find user by id
     *
     * @param id user id
     * @return UserDto
     */
    public UserDto findUser(Long id) {
        log.debug("=== Starting findUser ===");

        // Find user by id
        log.debug("find user by id: {}", id);
        User user = userQueryService.findById(id);

        log.debug("*** Finished findUser ***");

        return UserDto.fromUser(user);
    }

    /**
     * Find app by id
     *
     * @param id app id
     * @return AppDto
     */
    public AppDto findApp(Long id) {
        log.debug("=== Starting findApp ===");

        // Find app by id
        log.debug("find app by id: {}", id);
        App app = appQueryService.findById(id);

        log.debug("*** Finished findApp ***");

        return AppDto.fromApp(app);
    }

    /**
     * Search apps by search key and value
     *
     * @param searchKey search key
     * @param searchValue search value
     * @param pageable pageable
     * @return Page of AppDto
     */
    public Page<AppDto> searchApps(String searchKey, String searchValue, Pageable pageable) {
        return appQueryService.searchAppList(searchKey, searchValue, pageable);
    }

    /**
     * Find wallet by id
     *
     * @param id wallet id
     * @return WalletDto
     */
    public WalletDto findWallet(Long id) {
        log.debug("=== Starting findWallet ===");

        // Find wallet by id
        log.debug("find wallet by id: {}", id);
        Wallet wallet = walletQueryService.findById(id);

        log.debug("*** Finished findWallet ***");

        return WalletDto.fromWallet(wallet);
    }

    /**
     * Search wallets by search key and value
     *
     * @param searchKey search key
     * @param searchValue search value
     * @param pageable pageable
     * @return Page of WalletDto
     */
    public Page<WalletDto> searchWallets(String searchKey, String searchValue, Pageable pageable) {
        return walletQueryService.searchWalletList(searchKey, searchValue, pageable);
    }
}
