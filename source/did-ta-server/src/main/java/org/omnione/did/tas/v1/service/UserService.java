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

package org.omnione.did.tas.v1.service;

import org.omnione.did.tas.v1.dto.common.EmptyResDto;
import jakarta.validation.Valid;
import org.omnione.did.tas.v1.dto.user.ConfirmRegisterUserReqDto;
import org.omnione.did.tas.v1.dto.user.ConfirmRegisterUserResDto;
import org.omnione.did.tas.v1.dto.user.ConfirmRestoreDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ConfirmRestoreDidDocResDto;
import org.omnione.did.tas.v1.dto.user.ConfirmUpdateDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ConfirmUpdateDidDocResDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidEmailReqDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidEmailResDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidPushReqDto;
import org.omnione.did.tas.v1.dto.user.OfferRestoreDidPushResDto;
import org.omnione.did.tas.v1.dto.user.ProposeRegisterUserReqDto;
import org.omnione.did.tas.v1.dto.user.ProposeRegisterUserResDto;
import org.omnione.did.tas.v1.dto.user.ProposeRestoreDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ProposeRestoreDidDocResDto;
import org.omnione.did.tas.v1.dto.user.ProposeUpdateDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.ProposeUpdateDidDocResDto;
import org.omnione.did.tas.v1.dto.user.RequestRegisterUserReqDto;
import org.omnione.did.tas.v1.dto.user.RequestRegisterUserResDto;
import org.omnione.did.tas.v1.dto.user.RequestRestoreDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.RequestRestoreDidDocResDto;
import org.omnione.did.tas.v1.dto.user.RequestUpdateDidDocReqDto;
import org.omnione.did.tas.v1.dto.user.RequestUpdateDidDocResDto;
import org.omnione.did.tas.v1.dto.user.RetrieveKycReqDto;
import org.omnione.did.tas.v1.dto.user.RetrieveKycResDto;
import org.omnione.did.tas.v1.dto.user.UpdateDidDocDeactivatedReqDto;
import org.omnione.did.tas.v1.dto.user.UpdateDidDocRevokedReqDto;
import org.omnione.did.tas.v1.dto.user.UpdateUserStatusReqDto;
import org.omnione.did.tas.v1.dto.user.UpdateUserStatusResDto;
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
