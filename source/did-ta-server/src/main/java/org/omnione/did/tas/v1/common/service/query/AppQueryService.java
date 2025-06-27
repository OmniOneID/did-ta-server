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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.repository.AppRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.user.AppDto;
import org.omnione.did.list.v1.admin.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppQueryService {
    private final AppRepository appRepository;

    /**
     * Searches for a list of Apps based on the given search key and value.
     *
     * @param searchKey Key to search for.
     * @param searchValue Value to search for.
     * @param pageable Pageable.
     * @return Page of AppDto.
     */
    public Page<AppDto> searchAppList(String searchKey, String searchValue, Pageable pageable) {
        Page<App> userPage = appRepository.searchApps(searchKey, searchValue, pageable);

        List<AppDto> userDtos = userPage.getContent().stream()
                .map(AppDto::fromApp)
                .collect(Collectors.toList());

        return new PageImpl<>(userDtos, pageable, userPage.getTotalElements());
    }

    /**
     * Finds an App by its ID.
     *
     * @param appId App ID to search for.
     * @return AppDto.
     */
    public App findById(Long appId) {
        try {
            return appRepository.findById(appId)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.APP_INFO_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("App not found for userId {}", appId);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding App for appId {}", appId);
            throw new OpenDidException(ErrorCode.APP_INFO_NOT_FOUND);
        }
    }
}
