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
import org.omnione.did.base.db.constant.WalletStatus;
import org.omnione.did.base.db.domain.QWallet;
import org.omnione.did.base.db.domain.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryAdminImpl implements  WalletRepositoryAdmin  {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Wallet> searchWallets(String searchKey, String searchValue, Pageable pageable) {
        QWallet qWallet = QWallet.wallet;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = queryFactory
                .select(qWallet.count())
                .from(qWallet)
                .where(predicate)
                .fetchOne();

        List<Wallet> results = queryFactory
                .selectFrom(qWallet)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qWallet))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QWallet qWallet = QWallet.wallet;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "walletId":
                    predicate = predicate.and(qWallet.walletId.eq(searchValue));
                    break;
                case "did":
                    predicate = predicate.and(qWallet.did.eq(searchValue));
                    break;
                case "status":
                    predicate = predicate.and(qWallet.status.eq(WalletStatus.valueOf(searchValue)));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QWallet qWallet) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qWallet.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "walletId":
                    orders.add(new OrderSpecifier<>(direction, qWallet.walletId));
                    break;
                case "did":
                    orders.add(new OrderSpecifier<>(direction, qWallet.did));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qWallet.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
