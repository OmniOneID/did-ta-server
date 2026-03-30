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
import org.omnione.did.base.db.domain.ListAllowedCa;
import org.omnione.did.base.db.domain.ListVcPlan;
import org.omnione.did.base.db.domain.QListAllowedCa;
import org.omnione.did.base.db.domain.QListVcPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ListVcPlanRepositoryAdminImpl implements ListVcPlanRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ListVcPlan> searchVcPlans(String searchKey, String searchValue, Pageable pageable) {
        QListVcPlan qListVcPlan = QListVcPlan.listVcPlan;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qListVcPlan.count())
                .from(qListVcPlan)
                .where(predicate)
                .fetchOne();

        List<ListVcPlan> results = queryFactory
                .selectFrom(qListVcPlan)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qListVcPlan))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QListVcPlan qListVcPlan = QListVcPlan.listVcPlan;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "vcPlanId":
                    predicate = predicate.and(qListVcPlan.vcPlanId.eq(searchValue));
                    break;
                case "name":
                    predicate = predicate.and(qListVcPlan.name.eq(searchValue));
                    break;
                case "issuerDid":
                    predicate = predicate.and(qListVcPlan.issuerDid.eq(searchValue));
                    break;
                case "issuerName":
                    predicate = predicate.and(qListVcPlan.issuerName.eq(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable,  QListVcPlan qListVcPlan) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qListVcPlan.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "issuerDid":
                    orders.add(new OrderSpecifier<>(direction, qListVcPlan.issuerDid));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qListVcPlan.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }


}
