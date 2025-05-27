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
import org.omnione.did.base.db.constant.AppStatus;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.domain.QApp;
import org.omnione.did.base.db.domain.QUser;
import org.omnione.did.base.db.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AppRepositoryAdminImpl implements AppRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<App> searchApps(String searchKey, String searchValue, Pageable pageable) {
        QApp qApp = QApp.app;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qApp.count())
                .from(qApp)
                .where(predicate)
                .fetchOne();

        List<App> results = queryFactory
                .selectFrom(qApp)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qApp))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QApp qApp = QApp.app;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "appId":
                    predicate = predicate.and(qApp.appId.eq(searchValue));
                    break;
                case "pushToken":
                    predicate = predicate.and(qApp.pushToken.eq(searchValue));
                    break;
                case "status":
                    predicate = predicate.and(qApp.status.eq(AppStatus.valueOf(searchValue)));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QApp qApp) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qApp.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "predicate":
                    orders.add(new OrderSpecifier<>(direction, qApp.appId));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qApp.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
