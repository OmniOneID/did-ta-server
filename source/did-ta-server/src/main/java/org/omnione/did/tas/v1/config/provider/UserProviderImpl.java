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

import org.omnione.did.tas.v1.common.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.model.domain.User;
import org.omnione.did.tas.model.enums.UserStatus;
import org.omnione.did.tas.provider.UserProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK UserProvider interface, delegating to the server's UserQueryService.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class UserProviderImpl implements UserProvider {

    private final UserQueryService userQueryService;

    @Override
    public User findByDid(String did) throws TaSdkException {
        org.omnione.did.base.db.domain.User serverUser = userQueryService.findByDid(did);
        return User.builder()
                .did(serverUser.getDid())
                .status(UserStatus.valueOf(serverUser.getStatus().name()))
                .pii(serverUser.getPii())
                .build();
    }

    @Override
    public boolean existsUserByDid(String did) {
        return userQueryService.countByDid(did) > 0;
    }

    /**
     * No SDK protocol rejects a registration because of this result: re-registering with the same PII deactivates
     * the previous users, which UserServiceImpl does after the DID Document is registered.
     */
    @Override
    public boolean existsUserByPii(String pii) {
        return !userQueryService.findActiveUsersByPii(pii).isEmpty();
    }
}
