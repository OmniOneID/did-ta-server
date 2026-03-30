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
import org.omnione.did.base.db.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ListCredentialDefinitionRepositoryAdminImpl implements ListCredentialDefinitionRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ListCredentialDefinition> searchListCredentialDefinition(String searchKey, String searchValue, Pageable pageable) {
        QListCredentialDefinition qListCredentialDefinition = QListCredentialDefinition.listCredentialDefinition;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qListCredentialDefinition.count())
                .from(qListCredentialDefinition)
                .where(predicate)
                .fetchOne();

        List<ListCredentialDefinition> results = queryFactory
                .selectFrom(qListCredentialDefinition)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qListCredentialDefinition))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QListCredentialDefinition qListCredentialDefinition = QListCredentialDefinition.listCredentialDefinition;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "credentialDefinitionId":
                    predicate = predicate.and(qListCredentialDefinition.credentialDefinitionId.eq(searchValue));
                    break;
                case "credentialSchemaId":
                    predicate = predicate.and(qListCredentialDefinition.credentialSchemaId.eq(searchValue));
                    break;
                case "issuerDid":
                    predicate = predicate.and(qListCredentialDefinition.issuerDid.eq(searchValue));
                    break;
                case "issuerName":
                    predicate = predicate.and(qListCredentialDefinition.issuerName.eq(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QListCredentialDefinition qListCredentialDefinition) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qListCredentialDefinition.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "issuerDid":
                    orders.add(new OrderSpecifier<>(direction, qListCredentialDefinition.issuerDid));
                    break;
                case "issuerName":
                    orders.add(new OrderSpecifier<>(direction, qListCredentialDefinition.issuerName));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qListCredentialDefinition.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

}
