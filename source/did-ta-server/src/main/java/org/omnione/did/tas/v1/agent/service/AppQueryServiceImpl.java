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

import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.repository.AppRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for querying app information from the database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppQueryServiceImpl implements AppQueryService {
    private final AppRepository appRepository;

    /**
     * Find the app information associated with the given user ID.
     * Throws an OpenDidException if the app information cannot be retrieved.
     *
     * @param userId The user ID to retrieve the app information for
     * @return The app information associated with the given user ID
     * @throws OpenDidException if the app information cannot be retrieved
     */
    @Override
    public App findByUserId(Long userId) {
        try {
            return appRepository.findByUserId(userId)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.APP_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("App not found for userId {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding App for userId {}: {}", userId, e.getMessage());
            throw new OpenDidException(ErrorCode.APP_INFO_NOT_FOUND);
        }
    }

    /**
     * Find the app information associated with the given app ID.
     * Throws an OpenDidException if the app information cannot be retrieved.
     *
     * @param id The app ID to retrieve the app information for
     * @return The app information associated with the given app ID
     * @throws OpenDidException if the app information cannot be retrieved
     */
    @Override
    public App findById(Long id) {
        try {
            return appRepository.findById(id)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.APP_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("App not found for id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding App for id {}: {}", id, e.getMessage());
            throw new OpenDidException(ErrorCode.APP_INFO_NOT_FOUND);
        }
    }
}
