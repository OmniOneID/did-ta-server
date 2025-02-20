/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.tas.v1.agent.service;

import org.omnione.did.tas.v1.common.dto.agent.common.EmptyResDto;
import jakarta.validation.Valid;
import org.omnione.did.tas.v1.common.dto.agent.user.ConfirmRegisterUserReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ConfirmRegisterUserResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ConfirmRestoreDidDocReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ConfirmRestoreDidDocResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ConfirmUpdateDidDocReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ConfirmUpdateDidDocResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.OfferRestoreDidEmailReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.OfferRestoreDidEmailResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.OfferRestoreDidPushReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.OfferRestoreDidPushResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ProposeRegisterUserReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ProposeRegisterUserResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ProposeRestoreDidDocReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ProposeRestoreDidDocResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ProposeUpdateDidDocReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.ProposeUpdateDidDocResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RequestRegisterUserReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RequestRegisterUserResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RequestRestoreDidDocReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RequestRestoreDidDocResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RequestUpdateDidDocReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RequestUpdateDidDocResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RetrieveKycReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.RetrieveKycResDto;
import org.omnione.did.tas.v1.common.dto.agent.user.UpdateDidDocDeactivatedReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.UpdateDidDocRevokedReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.UpdateUserStatusReqDto;
import org.omnione.did.tas.v1.common.dto.agent.user.UpdateUserStatusResDto;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * User service interface for handling user registration and DID document management.
 */
public interface UserService {
    ProposeRegisterUserResDto proposeRegisterUser(ProposeRegisterUserReqDto proposeRegisterUserReqDto);
    RetrieveKycResDto retrieveKyc(RetrieveKycReqDto retrieveKycReqDto);
    RequestRegisterUserResDto requestRegisterUser(RequestRegisterUserReqDto requestRegisterUserReqDto);
    ConfirmRegisterUserResDto confirmRegisterUser(ConfirmRegisterUserReqDto confirmRegisterUserReqDto);
    ProposeUpdateDidDocResDto proposeUpdateDidDoc(ProposeUpdateDidDocReqDto proposeUpdateDidDocReqDto);
    RequestUpdateDidDocResDto requestUpdateDidDoc(RequestUpdateDidDocReqDto requestUpdateDidDocReqDto);
    ConfirmUpdateDidDocResDto confirmUpdateDidDoc(ConfirmUpdateDidDocReqDto confirmUpdateDidDocReqDto);
    UpdateUserStatusResDto updateUserStatus(UpdateUserStatusReqDto updateUserStatusReqDto);
    EmptyResDto updateDidDocDeactivated(UpdateDidDocDeactivatedReqDto updateDidDocDeactivatedReqDto);
    EmptyResDto updateDidDocRevoked(UpdateDidDocRevokedReqDto updateDidDocRevokedReqDto);
    OfferRestoreDidPushResDto offerRestoreDidPush(OfferRestoreDidPushReqDto offerRestoreDidPushReqDto);
    OfferRestoreDidEmailResDto offerRestoreDidEmail(OfferRestoreDidEmailReqDto offerRestoreDidEmailReqDto);
    ProposeRestoreDidDocResDto proposeRestoreDidDoc(@Valid @RequestBody ProposeRestoreDidDocReqDto proposeRestoreDidDocReqDto);
    RequestRestoreDidDocResDto requestRestoreDidDoc(@Valid @RequestBody RequestRestoreDidDocReqDto requestRestoreDidDocReqDto);
    ConfirmRestoreDidDocResDto confirmRestoreDidDoc(@Valid @RequestBody ConfirmRestoreDidDocReqDto confirmRestoreDidDocReqDto);
}
