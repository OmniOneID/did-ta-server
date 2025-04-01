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

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.Role;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.QEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EntityRepositoryAdminImpl implements EntityRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Entity> searchEntities(String searchKey, String searchValue, Pageable pageable) {
        QEntity entity = QEntity.entity;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(entity.count())
                .from(entity)
                .where(predicate)
                .fetchOne();

        List<Entity> results = queryFactory
                .selectFrom(entity)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, entity))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QEntity entity = QEntity.entity;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "did":
                    predicate = predicate.and(entity.did.eq(searchValue));
                    break;
                case "name":
                    predicate = predicate.and(entity.name.eq(searchValue));
                    break;
                case "role":
                    predicate = predicate.and(entity.role.eq(Role.valueOf(searchValue)));
                    break;
                case "status":
                    predicate = predicate.and(entity.status.eq(EntityStatus.valueOf(searchValue)));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QEntity entity) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, entity.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "did":
                    orders.add(new OrderSpecifier<>(direction, entity.did));
                    break;
                case "status":
                    orders.add(new OrderSpecifier<>(direction, entity.status));
                    break;
                case "name":
                    orders.add(new OrderSpecifier<>(direction, entity.name));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, entity.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
