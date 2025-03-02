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
import org.omnione.did.base.db.repository.UserRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for querying User information.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotiUserQueryService {
    private final UserRepository userRepository;

    /**
     * Counts the number of User entities with the given DID.
     *
     * @param did The DID to search for.
     * @return The number of User entities with the given DID.
     */
    public long countByDid(String did) {
        return userRepository.countByDid(did);
    }

    /**
     * Retrieves a User entity based on the provided DID.
     *
     * @param did The DID of the user to retrieve.
     * @return The User entity associated with the given DID.
     * @throws OpenDidException If the User entity is not found.
     */
    public User findByDid(String did) {
        try {
            return userRepository.findByDid(did)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("User not found for did {}: {}", did, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding User for did {}: {}", did, e.getMessage());
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }

    }

    /**
     * Retrieves a User entity based on the provided DID and user status.
     *
     * @param did The DID of the user to retrieve.
     * @param userStatus The status of the user to filter by.
     * @return The User entity matching the provided DID and status.
     * @throws OpenDidException If the User entity is not found.
     */
    public User findByDidAndStatus(String did, UserStatus userStatus) {
        try {
            return userRepository.findByDidAndStatus(did, userStatus)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("User not found for did {}: {}", did, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding User for did {}: {}", did, e.getMessage());
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of user IDs associated with the provided DIDs.
     *
     * @param dids The DIDs of the users to retrieve.
     * @return A list of user IDs associated with the provided DIDs.
     */
    public List<Long> findIdsByDids(List<String> dids) {
        return userRepository.findIdsByDids(dids);
    }
}
