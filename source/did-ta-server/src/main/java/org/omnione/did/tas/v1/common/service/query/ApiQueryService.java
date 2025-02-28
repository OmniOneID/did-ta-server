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
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.db.constant.ApiType;
import org.omnione.did.base.db.domain.Api;
import org.omnione.did.base.db.repository.ApiRepository;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.tas.v1.admin.dto.api.ExpirationInfoDto;
import org.omnione.did.tas.v1.admin.dto.api.KeyExchangePolicyInfoDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiQueryService {

    private final Integer DEFAULT_TOKEN_EXPIRATION_TIME = 300;
    private final Integer DEFAULT_TRANSACTION_EXPIRATION_TIME = 60;
    private final SymmetricCipherType DEFAULT_CIPHER_TYPE = SymmetricCipherType.AES_256_CBC;
    private final SymmetricPaddingType DEFAULT_PADDING_TYPE = SymmetricPaddingType.PKCS5;

    private final ApiRepository apiRepository;

    public Api findApiByTypeOrNull(ApiType type) {
        return apiRepository.findByType(type).orElse(null);
    }

    public Integer findTokenExpirationTime() {
        Api api = findApiByTypeOrNull(ApiType.EXPIRATION_TIME);
        if (api != null) {
            ExpirationInfoDto expirationInfoDto = JsonUtil.deserializeFromJson(api.getConfig(), ExpirationInfoDto.class);
            return expirationInfoDto.getTokenExpirationSeconds();
        }
        return DEFAULT_TOKEN_EXPIRATION_TIME;
    }

    public Integer findTransactionExpirationTime() {
        Api api = findApiByTypeOrNull(ApiType.EXPIRATION_TIME);
        if (api != null) {
            ExpirationInfoDto expirationInfoDto = JsonUtil.deserializeFromJson(api.getConfig(), ExpirationInfoDto.class);
            return expirationInfoDto.getTransactionExpirationSeconds();
        }
        return DEFAULT_TRANSACTION_EXPIRATION_TIME;
    }

    public SymmetricCipherType findCipherType() {
        Api api = findApiByTypeOrNull(ApiType.KEY_EXCHANGE_POLICY);
        if (api != null) {
            KeyExchangePolicyInfoDto keyExchangePolicyInfoDto = JsonUtil.deserializeFromJson(api.getConfig(), KeyExchangePolicyInfoDto.class);
            return SymmetricCipherType.fromDisplayName(keyExchangePolicyInfoDto.getCipherType());
        }
        return DEFAULT_CIPHER_TYPE;
    }

    public SymmetricPaddingType findPaddingType() {
        Api api = findApiByTypeOrNull(ApiType.KEY_EXCHANGE_POLICY);
        if (api != null) {
            KeyExchangePolicyInfoDto keyExchangePolicyInfoDto = JsonUtil.deserializeFromJson(api.getConfig(), KeyExchangePolicyInfoDto.class);
            return SymmetricPaddingType.fromDisplayName(keyExchangePolicyInfoDto.getPaddingType());
        }
        return DEFAULT_PADDING_TYPE;
    }
}
