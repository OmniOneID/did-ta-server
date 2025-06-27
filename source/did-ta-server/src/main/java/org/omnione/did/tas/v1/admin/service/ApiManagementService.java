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
package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.ApiType;
import org.omnione.did.base.db.domain.Api;
import org.omnione.did.base.db.repository.ApiRepository;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.tas.v1.admin.dto.api.ExpirationInfoDto;
import org.omnione.did.tas.v1.admin.dto.api.KeyExchangePolicyInfoDto;
import org.omnione.did.tas.v1.admin.dto.api.RegisterExpirationInfoReqDto;
import org.omnione.did.tas.v1.admin.dto.api.RegisterKeyExchangePolicyInfoReqDto;
import org.omnione.did.tas.v1.common.service.query.ApiQueryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApiManagementService {
    private final ApiQueryService apiQueryService;
    private final ApiRepository apiRepository;

    public ExpirationInfoDto findExpirationInfo() {
        Api api = apiQueryService.findApiByTypeOrNull(ApiType.EXPIRATION_TIME);
        if (api!= null) {
            return JsonUtil.deserializeFromJson(api.getConfig(), ExpirationInfoDto.class);
        }

        return ExpirationInfoDto.builder().build();
    }

    public ExpirationInfoDto registerExpirationInfo(RegisterExpirationInfoReqDto registerExpirationInfoReqDto) {
        Api api = apiQueryService.findApiByTypeOrNull(ApiType.EXPIRATION_TIME);

        if (api == null) {
            api = Api.builder()
                    .type(ApiType.EXPIRATION_TIME)
                    .config(JsonUtil.serializeToJson(registerExpirationInfoReqDto))
                    .build();
        } else {
            api.setConfig(JsonUtil.serializeToJson(registerExpirationInfoReqDto));
        }

        Api savedApi = apiRepository.save(api);
        return JsonUtil.deserializeFromJson(savedApi.getConfig(), ExpirationInfoDto.class);
    }

    public KeyExchangePolicyInfoDto findKeyExchangePolicy() {
        Api api = apiQueryService.findApiByTypeOrNull(ApiType.KEY_EXCHANGE_POLICY);
        if (api!= null) {
            return JsonUtil.deserializeFromJson(api.getConfig(), KeyExchangePolicyInfoDto.class);
        }

        return KeyExchangePolicyInfoDto.builder().build();
    }

    public KeyExchangePolicyInfoDto registerKeyExchangePolicy(RegisterKeyExchangePolicyInfoReqDto registerKeyExchangePolicyInfoReqDto) {
        Api api = apiQueryService.findApiByTypeOrNull(ApiType.KEY_EXCHANGE_POLICY);

        if (api == null) {
            api = Api.builder()
                    .type(ApiType.KEY_EXCHANGE_POLICY)
                    .config(JsonUtil.serializeToJson(registerKeyExchangePolicyInfoReqDto))
                    .build();
        } else {
            api.setConfig(JsonUtil.serializeToJson(registerKeyExchangePolicyInfoReqDto));
        }

        Api savedApi = apiRepository.save(api);
        return JsonUtil.deserializeFromJson(savedApi.getConfig(), KeyExchangePolicyInfoDto.class);
    }
}
