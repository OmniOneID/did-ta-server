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

package org.omnione.did.noti.v1.agent.service;

import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.repository.AppRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the NotiAppQueryService interface.
 * This service provides methods for querying App entities from the database
 * based on various criteria such as user ID and App ID.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotiAppQueryService {
    private final AppRepository appRepository;

    /**
     * Finds an App entity by its associated user ID.
     *
     * @param userId The user ID of the app to search for.
     * @return The App entity associated with the given user ID.
     * @throws OpenDidException If the App entity is not found for the given user ID.
     */
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
     * Finds an App entity by its associated ID.
     *
     * @param id The ID of the app to search for.
     * @return The App entity associated with the given ID.
     * @throws OpenDidException If the App entity is not found for the given ID.
     */
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

    /**
     * Finds a list of App entities by their associated user IDs.
     *
     * @param userIds The list of user IDs to search for.
     * @return The list of App entities associated with the given user IDs.
     *         Returns an empty list if no App entities are found.
     */
    public List<App> findByUserIds(List<Long> userIds) {
        return appRepository.findByUserIds(userIds);
    }
}
