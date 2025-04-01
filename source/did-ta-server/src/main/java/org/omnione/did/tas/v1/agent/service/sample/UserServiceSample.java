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

import org.omnione.did.tas.v1.agent.service.UserService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRegisterUserResDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRestoreDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmUpdateDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmUpdateDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidEmailReqDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidEmailResDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidPushReqDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidPushResDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRegisterUserResDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRestoreDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeUpdateDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeUpdateDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRegisterUserResDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRestoreDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestUpdateDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestUpdateDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.RetrieveKycReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RetrieveKycResDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateDidDocDeactivatedReqDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateDidDocRevokedReqDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateUserStatusReqDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateUserStatusResDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * this is a sample implementation of the UserService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("sample")
public class UserServiceSample implements UserService {
    @Override
    public ProposeRegisterUserResDto proposeRegisterUser(ProposeRegisterUserReqDto proposeRegisterUserReqDto) {
        return ProposeRegisterUserResDto.builder()
                .txId("61e4164d-939d-4252-b2f4-5026c8225a3b")
                .build();
    }

    @Override
    public RetrieveKycResDto retrieveKyc(RetrieveKycReqDto retrieveKycReqDto) {
        return RetrieveKycResDto.builder()
                .txId("61e4164d-939d-4252-b2f4-5026c8225a3b")
                .build();
    }

    @Override
    public RequestRegisterUserResDto requestRegisterUser(RequestRegisterUserReqDto requestRegisterUserReqDto) {
        return RequestRegisterUserResDto.builder()
                .txId("61e4164d-939d-4252-b2f4-5026c8225a3b")
                .build();
    }

    @Override
    public ConfirmRegisterUserResDto confirmRegisterUser(ConfirmRegisterUserReqDto confirmRegisterUserReqDto) {
        return ConfirmRegisterUserResDto.builder()
                .txId("61e4164d-939d-4252-b2f4-5026c8225a3b")
                .build();
    }

    @Override
    public ProposeUpdateDidDocResDto proposeUpdateDidDoc(ProposeUpdateDidDocReqDto proposeUpdateDidDocReqDto) {
        return ProposeUpdateDidDocResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .build();
    }

    @Override
    public RequestUpdateDidDocResDto requestUpdateDidDoc(RequestUpdateDidDocReqDto requestUpdateDidDocReqDto) {
        return RequestUpdateDidDocResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .build();
    }

    @Override
    public ConfirmUpdateDidDocResDto confirmUpdateDidDoc(ConfirmUpdateDidDocReqDto confirmUpdateDidDocReqDto) {
        return ConfirmUpdateDidDocResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .build();
    }

    @Override
    public UpdateUserStatusResDto updateUserStatus(UpdateUserStatusReqDto updateUserStatusReqDto) {
        return UpdateUserStatusResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .build();
    }

    @Override
    public EmptyResDto updateDidDocDeactivated(UpdateDidDocDeactivatedReqDto updateDidDocDeactivatedReqDto) {
        return new EmptyResDto();
    }

    @Override
    public EmptyResDto updateDidDocRevoked(UpdateDidDocRevokedReqDto updateDidDocRevokedReqDto) {
        return new EmptyResDto();
    }

    @Override
    public OfferRestoreDidPushResDto offerRestoreDidPush(OfferRestoreDidPushReqDto offerRestoreDidPushReqDto) {
        return OfferRestoreDidPushResDto.builder()
                .offerId("aae54cdf-0412-4878-bd32-b9745dd60482")
                .build();
    }

    @Override
    public OfferRestoreDidEmailResDto offerRestoreDidEmail(OfferRestoreDidEmailReqDto offerRestoreDidEmailReqDto) {
        return OfferRestoreDidEmailResDto.builder()
                .offerId("aae54cdf-0412-4878-bd32-b9745dd60482")
                .build();
    }

    @Override
    public ProposeRestoreDidDocResDto proposeRestoreDidDoc(ProposeRestoreDidDocReqDto proposeRestoreDidDocReqDto) {
        return ProposeRestoreDidDocResDto.builder()
                .txId("cad7a1e8-0e27-47f3-b9dd-b6590a349852")
                .authNonce("mXx4ZICWZNczC1jpFHXyxDA")
                .build();
    }

    @Override
    public RequestRestoreDidDocResDto requestRestoreDidDoc(RequestRestoreDidDocReqDto requestRestoreDidDocReqDto) {
        return RequestRestoreDidDocResDto.builder()
                .txId("cad7a1e8-0e27-47f3-b9dd-b6590a349852")
                .build();
    }

    @Override
    public ConfirmRestoreDidDocResDto confirmRestoreDidDoc(ConfirmRestoreDidDocReqDto confirmRestoreDidDocReqDto) {
        return ConfirmRestoreDidDocResDto.builder()
                .txId("cad7a1e8-0e27-47f3-b9dd-b6590a349852")
                .build();
    }
}
