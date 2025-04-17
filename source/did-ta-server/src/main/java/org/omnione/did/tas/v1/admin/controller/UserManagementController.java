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
package org.omnione.did.tas.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.list.v1.admin.dto.user.AppDto;
import org.omnione.did.list.v1.admin.dto.user.UserDto;
import org.omnione.did.list.v1.admin.dto.user.WalletDto;
import org.omnione.did.tas.v1.admin.dto.entity.EntityInfoDto;
import org.omnione.did.tas.v1.admin.service.UserManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.ADMIN_V1)
public class UserManagementController {
    private final UserManagementService userManagementService;

    @GetMapping(value = "/users/list")
    public Page<UserDto> searchUsers(String searchKey, String searchValue, Pageable pageable) {
        return userManagementService.searchUsers(searchKey, searchValue, pageable);
    }

    @GetMapping(value = "/users")
    public UserDto findUser(@RequestParam Long id) {
        return userManagementService.findUser(id);
    }

    @GetMapping(value = "/apps/list")
    public Page<AppDto> searchApps(String searchKey, String searchValue, Pageable pageable) {
        return userManagementService.searchApps(searchKey, searchValue, pageable);
    }

    @GetMapping(value = "/apps")
    public AppDto findApp(@RequestParam Long id) {
        return userManagementService.findApp(id);
    }

    @GetMapping(value = "/wallets/list")
    public Page<WalletDto> searchWallets(String searchKey, String searchValue, Pageable pageable) {
        return userManagementService.searchWallets(searchKey, searchValue, pageable);
    }

    @GetMapping(value = "/wallets")
    public WalletDto findWallet(@RequestParam Long id) {
        return userManagementService.findWallet(id);
    }
}
