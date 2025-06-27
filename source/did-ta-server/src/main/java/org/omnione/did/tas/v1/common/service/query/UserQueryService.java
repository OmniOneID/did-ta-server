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

package org.omnione.did.tas.v1.common.service.query;

import org.omnione.did.base.db.constant.UserStatus;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.repository.UserRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.list.v1.admin.dto.user.UserDto;
import org.omnione.did.list.v1.admin.dto.vcplan.ListVcPlanDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for querying User.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryService {
    private final UserRepository userRepository;

    /**
     * UserRepository countByDid
     * @param did DID to search for
     * @return Found User count
     */
    public long countByDid(String did) {
        return userRepository.countByDid(did);
    }

    /**
     * Finds the latest User.
     *
     * @return The latest User
     * @throws OpenDidException if the User is not found
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
            throw new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND);
        }

    }

    /**
     * Finds a User by its DID and status.
     *
     * @param did DID to search for
     * @param userStatus Status to search for
     * @return Found User
     * @throws OpenDidException if the User is not found
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
            throw new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND);
        }
    }
    public List<Long> findIdsByDids(List<String> dids) {
        return null;
    }

    /**
     * Finds a User by its ID.
     *
     * @param userId ID to search for
     * @return Found User
     * @throws OpenDidException if the User is not found
     */
    public User findById(Long userId) {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("User not found for userId {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding User for userId {}", userId);
            throw new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND);
        }
    }

    /**
     * Finds the latest User.
     *
     * @return The latest User
     * @throws OpenDidException if the User is not found
     */
    public User findLatestUser() {
        try {
            return userRepository.findTopByOrderByIdDesc()
                    .orElseThrow(() -> new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("User not found", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("User not found", e.getMessage());
            throw new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND);
        }
    }

    /**
     * Finds a User by its PII.
     *
     * @param pii PII to search for
     * @return Found User
     * @throws OpenDidException if the User is not found
     */
    public User findByPiiAndStatus(String pii, UserStatus status) {
        try {
            return userRepository.findByPiiAndStatus(pii, status)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("User not found", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("User not found", e.getMessage());
            throw new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND);
        }
    }

    /**
     * Searches for a list of Users based on the given search key and value.
     *
     * @param searchKey Key to search for.
     * @param searchValue Value to search for.
     * @param pageable Pageable.
     * @return Page of UserDto.
     */
    public Page<UserDto> searchUserList(String searchKey, String searchValue, Pageable pageable) {
        Page<User> userPage = userRepository.searchUsers(searchKey, searchValue, pageable);

        List<UserDto> userDtos = userPage.getContent().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());

        return new PageImpl<>(userDtos, pageable, userPage.getTotalElements());
    }
}
