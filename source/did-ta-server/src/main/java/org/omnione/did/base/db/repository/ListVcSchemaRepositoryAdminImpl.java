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
import org.omnione.did.base.db.domain.ListVcSchema;
import org.omnione.did.base.db.domain.QListAllowedCa;
import org.omnione.did.base.db.domain.QListVcSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ListVcSchemaRepositoryAdminImpl implements ListVcSchemaRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ListVcSchema> searchListVcSchemas(String searchKey, String searchValue, Pageable pageable) {
        QListVcSchema qListVcSchema = QListVcSchema.listVcSchema;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qListVcSchema.count())
                .from(qListVcSchema)
                .where(predicate)
                .fetchOne();

        List<ListVcSchema> results = queryFactory
                .selectFrom(qListVcSchema)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qListVcSchema))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QListVcSchema qListVcSchema = QListVcSchema.listVcSchema;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "title":
                    predicate = predicate.and(qListVcSchema.title.eq(searchValue));
                    break;
                case "issuerDid":
                    predicate = predicate.and(qListVcSchema.issuerDid.eq(searchValue));
                    break;
                case "issuerName":
                    predicate = predicate.and(qListVcSchema.issuerName.eq(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QListVcSchema qListVcSchema) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qListVcSchema.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "issuerDid":
                    orders.add(new OrderSpecifier<>(direction, qListVcSchema.issuerDid));
                    break;
                case "issuerName":
                    orders.add(new OrderSpecifier<>(direction, qListVcSchema.issuerName));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qListVcSchema.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

}
