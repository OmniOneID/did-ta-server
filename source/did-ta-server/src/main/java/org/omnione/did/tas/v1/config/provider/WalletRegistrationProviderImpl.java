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

import org.omnione.did.base.db.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.provider.WalletRegistrationProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK WalletRegistrationProvider interface, delegating to the server's WalletRepository.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class WalletRegistrationProviderImpl implements WalletRegistrationProvider {

    private final WalletRepository walletRepository;

    @Override
    public boolean checkWalletIdExists(String walletId) {
        return walletRepository.countByWalletId(walletId) > 0;
    }

    @Override
    public boolean checkWalletDidExists(String did) {
        return walletRepository.existsByDid(did);
    }
}
