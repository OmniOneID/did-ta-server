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

package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing operations on the App entity.
 * Extends JpaRepository to provide basic CRUD operations and defines custom query methods
 * for more specific database interactions with App entities.
 */
@Repository
public interface AppRepository extends JpaRepository<App, Long>, QuerydslPredicateExecutor<App>, AppRepositoryAdmin {
    Optional<App> findByAppId(String appId);
    Optional<App> findByUserId(Long userID);
    @Query("SELECT u FROM App u WHERE u.userId IN :userIds")
    List<App> findByUserIds(@Param("userIds") List<Long> userIds);
    @Modifying
    @Query("UPDATE App a SET a.pushToken = :pushToken WHERE a.userId = :userId")
    int updatePushTokenByUserId(@Param("userId") Long userId, @Param("pushToken") String pushToken);
}
