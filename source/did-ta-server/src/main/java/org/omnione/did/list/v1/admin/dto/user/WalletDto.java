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
package org.omnione.did.list.v1.admin.dto.user;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.WalletStatus;
import org.omnione.did.base.db.domain.Wallet;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class WalletDto {
    private final Long id;
    private final String walletId;
    private final String did;
    private final WalletStatus status;
    private final String registeredAt;
    private final String cancelledAt;
    private final Long userId;
    private final Long entityId;
    private final String createdAt;
    private final String updatedAt;

    public static WalletDto fromWallet(Wallet wallet) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return WalletDto.builder()
                .id(wallet.getId())
                .walletId(wallet.getWalletId())
                .did(wallet.getDid())
                .status(wallet.getStatus())
                .registeredAt(formatInstant(wallet.getRegisteredAt(), formatter))
                .cancelledAt(formatInstant(wallet.getCancelledAt(), formatter))
                .userId(wallet.getUserId())
                .entityId(wallet.getEntityId())
                .createdAt(formatInstant(wallet.getCreatedAt(), formatter))
                .updatedAt(formatInstant(wallet.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
