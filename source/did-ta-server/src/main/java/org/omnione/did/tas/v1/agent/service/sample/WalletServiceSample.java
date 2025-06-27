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

package org.omnione.did.tas.v1.agent.service.sample;

import org.omnione.did.tas.v1.agent.dto.wallet.RegisterWalletReqDto;
import org.omnione.did.tas.v1.agent.dto.wallet.RegisterWalletResDto;
import org.omnione.did.tas.v1.agent.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * this is a sample implementation of the WalletService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("sample")
public class WalletServiceSample implements WalletService {
    @Override
    @ResponseBody
    public RegisterWalletResDto RequestRegisterWallet(@Valid @RequestBody RegisterWalletReqDto registerWalletReqDto) {
        return RegisterWalletResDto.builder()
                .txId("83edab98-b704-4303-b7fa-0d46e55d163b")
                .build();
    }
}
