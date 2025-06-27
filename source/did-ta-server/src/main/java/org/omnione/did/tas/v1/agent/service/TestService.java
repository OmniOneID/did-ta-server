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

import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.repository.AppRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.tas.v1.common.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for testing purposes
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestService {

    private final UserQueryService userQueryService;
    private final AppRepository appRepository;

    /**
     * Updates the push token for a user
     *
     * @param did The DID of the user
     * @param pushToken The new push token
     */
    public void updatePushToken(String did, String pushToken) {
        try {
            log.debug("=== Starting updatePushToken ===");

            // Find user
            log.debug("\t-->Finding user by did: {}", did);
            User user = userQueryService.findByDid(did);

            // Update push token
            updatePushTokenByUserId(user.getId(), pushToken);

            log.debug("*** Finished updatePushToken ***");

        } catch (OpenDidException e) {
            log.error("Failed to update push token", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to update push token", e);
            throw new OpenDidException(ErrorCode.PUSH_TOKEN_UPDATE_FAILED);
        }
    }
    /**
     * Updates the push token for a user
     *
     * @param userId The ID of the user
     * @param pushToken The new push token
     */
    public void updatePushTokenByUserId(Long userId, String pushToken) {
        appRepository.updatePushTokenByUserId(userId, pushToken);
    }
    /**
     * Finds the latest user
     *
     * @return The latest user
     */
    public User findLatestUser() {
        return userQueryService.findLatestUser();
    }


}
