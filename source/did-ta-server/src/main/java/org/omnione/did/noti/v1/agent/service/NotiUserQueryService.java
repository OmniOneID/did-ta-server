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

import org.omnione.did.base.db.constant.UserStatus;
import org.omnione.did.base.db.domain.User;

import java.util.List;

/**
 * Service interface for querying User information.
 */
public interface NotiUserQueryService {
    /**
     * Counts the number of users associated with a specific DID.
     *
     * @param did The DID (Decentralized Identifier) of the user.
     * @return The number of users associated with the provided DID.
     */
    long countByDid(String did);

    /**
     * Retrieves a user entity based on the provided DID.
     *
     * @param did The DID of the user to retrieve.
     * @return The User entity associated with the given DID, or null if not found.
     */
    User findByDid(String did);

    /**
     * Retrieves a user entity based on the provided DID and user status.
     *
     * @param did The DID of the user to retrieve.
     * @param userStatus The status of the user to filter by.
     * @return The User entity matching the provided DID and status, or null if not found.
     */
    User findByDidAndStatus(String did, UserStatus userStatus);

    /**
     * Retrieves a list of user IDs associated with the given list of DIDs.
     *
     * @param dids A list of DIDs to search for.
     * @return A list of user IDs associated with the provided DIDs.
     */
    List<Long> findIdsByDids(List<String> dids);
}
