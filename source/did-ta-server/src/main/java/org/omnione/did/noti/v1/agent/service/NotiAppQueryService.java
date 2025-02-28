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

import java.util.List;

/**
 * Service interface for querying App information.
 */
public interface NotiAppQueryService {
    /**
     * Finds an App entity by the user's ID.
     *
     * @param userId The ID of the user.
     * @return The App entity associated with the given user ID.
     */
    App findByUserId(Long userId);

    /**
     * Finds an App entity by its ID.
     *
     * @param id The ID of the App.
     * @return The App entity with the given ID.
     */
    App findById(Long id);

    /**
     * Finds a list of App entities by a list of user IDs.
     *
     * @param userIds The list of user IDs.
     * @return A list of App entities associated with the given user IDs.
     */
    List<App> findByUserIds(List<Long> userIds);
}
