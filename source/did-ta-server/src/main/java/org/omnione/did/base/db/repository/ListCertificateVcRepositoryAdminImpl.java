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
import org.omnione.did.base.db.domain.ListCertificateVc;
import org.omnione.did.base.db.domain.QListCertificateVc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ListCertificateVcRepositoryAdminImpl implements ListCertificateVcRepositoryAdmin {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ListCertificateVc> searchCertificateVc(String searchKey, String searchValue, Pageable pageable) {
        QListCertificateVc qListCertificateVc = QListCertificateVc.listCertificateVc;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qListCertificateVc.count())
                .from(qListCertificateVc)
                .where(predicate)
                .fetchOne();

        List<ListCertificateVc> results = queryFactory
                .selectFrom(qListCertificateVc)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qListCertificateVc))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QListCertificateVc qListCertificateVc = QListCertificateVc.listCertificateVc;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "did":
                    predicate = predicate.and(qListCertificateVc.did.containsIgnoreCase(searchValue));
                    break;
                case "name":
                    predicate = predicate.and(qListCertificateVc.name.containsIgnoreCase(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QListCertificateVc qListCertificateVc) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.DESC, qListCertificateVc.publishedAt));
        }

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "did":
                    orders.add(new OrderSpecifier<>(direction, qListCertificateVc.did));
                    break;
                case "name":
                    orders.add(new OrderSpecifier<>(direction, qListCertificateVc.name));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.DESC, qListCertificateVc.publishedAt));
                    break;
            }
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}
