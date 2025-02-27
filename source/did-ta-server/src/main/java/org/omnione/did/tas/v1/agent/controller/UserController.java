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

package org.omnione.did.tas.v1.agent.controller;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.tas.v1.agent.service.UserService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRestoreDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmUpdateDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmUpdateDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRestoreDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRegisterUserResDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateUserStatusReqDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateUserStatusResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRegisterUserReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRegisterUserResDto;
import org.omnione.did.tas.v1.agent.dto.user.ConfirmRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidEmailReqDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidEmailResDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidPushReqDto;
import org.omnione.did.tas.v1.agent.dto.user.OfferRestoreDidPushResDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeRegisterUserResDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeUpdateDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.ProposeUpdateDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRestoreDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestRestoreDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestUpdateDidDocReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestUpdateDidDocResDto;
import org.omnione.did.tas.v1.agent.dto.user.RetrieveKycReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RetrieveKycResDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateDidDocDeactivatedReqDto;
import org.omnione.did.tas.v1.agent.dto.user.UpdateDidDocRevokedReqDto;
import org.springframework.web.bind.annotation.*;

/**
 * The UserController class is a controller that handles requests related to users.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1)
public class UserController {
    private final UserService userService;

    /**
     * Handles the proposal for user registration.
     *
     * @param proposeRegisterUserReqDto The request DTO for proposing user registration.
     * @return ProposeRegisterUserResDto The response DTO for the registration proposal.
     */
    @RequestMapping(value = "/propose-register-user", method = RequestMethod.POST)
    @ResponseBody
    public ProposeRegisterUserResDto proposeRegisterUser(@Valid @RequestBody ProposeRegisterUserReqDto proposeRegisterUserReqDto) {
        return userService.proposeRegisterUser(proposeRegisterUserReqDto);
    }

    /**
     * Retrieves KYC (Know Your Customer) information.
     *
     * @param retrieveKycReqDto The request DTO for retrieving KYC information.
     * @return RetrieveKycResDto The response DTO containing KYC information.
     */
    @RequestMapping(value = "/retrieve-kyc", method = RequestMethod.POST)
    @ResponseBody
    public RetrieveKycResDto retrieveKyc(@Valid @RequestBody RetrieveKycReqDto retrieveKycReqDto) {
        return userService.retrieveKyc(retrieveKycReqDto);
    }

    /**
     * Handles the request for user registration.
     *
     * @param requestRegisterUserReqDto The request DTO for user registration.
     * @return RequestRegisterUserResDto The response DTO for the registration request.
     */
    @RequestMapping(value = "/request-register-user", method = RequestMethod.POST)
    @ResponseBody
    public RequestRegisterUserResDto requestRegisterUser(@Valid @RequestBody RequestRegisterUserReqDto requestRegisterUserReqDto) {
        return userService.requestRegisterUser(requestRegisterUserReqDto);
    }

    /**
     * Confirms the user registration process.
     *
     * @param confirmRegisterUserReqDto The request DTO for confirming user registration.
     * @return ConfirmRegisterUserResDto The response DTO for the registration confirmation.
     */
    @RequestMapping(value = "/confirm-register-user", method = RequestMethod.POST)
    @ResponseBody
    public ConfirmRegisterUserResDto confirmRegisterDidDoc(@Valid @RequestBody ConfirmRegisterUserReqDto confirmRegisterUserReqDto) {
        return userService.confirmRegisterUser(confirmRegisterUserReqDto);
    }

    /**
     * Handles the proposal for updating a DID document.
     *
     * @param proposeUpdateDidDocReqDto The request DTO for proposing a DID document update.
     * @return ProposeUpdateDidDocResDto The response DTO for the update proposal.
     */
    @RequestMapping(value = "/propose-update-diddoc", method = RequestMethod.POST)
    @ResponseBody
    public ProposeUpdateDidDocResDto proposeUpdateDidDoc(@Valid @RequestBody ProposeUpdateDidDocReqDto proposeUpdateDidDocReqDto) {
        return userService.proposeUpdateDidDoc(proposeUpdateDidDocReqDto);
    }

    /**
     * Handles the request for updating a DID document.
     *
     * @param requestUpdateDidDocReqDto The request DTO for updating a DID document.
     * @return RequestUpdateDidDocResDto The response DTO for the update request.
     */
    @RequestMapping(value = "/request-update-diddoc", method = RequestMethod.POST)
    @ResponseBody
    public RequestUpdateDidDocResDto requestUpdateDidDoc(@Valid @RequestBody RequestUpdateDidDocReqDto requestUpdateDidDocReqDto) {
        return userService.requestUpdateDidDoc(requestUpdateDidDocReqDto);
    }

    /**
     * Confirms the update of a DID document.
     *
     * @param confirmUpdateDidDocReqDto The request DTO for confirming a DID document update.
     * @return ConfirmUpdateDidDocResDto The response DTO for the update confirmation.
     */
    @RequestMapping(value = "/confirm-update-diddoc", method = RequestMethod.POST)
    @ResponseBody
    public ConfirmUpdateDidDocResDto confirmUpdateDidDoc(@Valid @RequestBody ConfirmUpdateDidDocReqDto confirmUpdateDidDocReqDto) {
        return userService.confirmUpdateDidDoc(confirmUpdateDidDocReqDto);
    }

    /**
     * Updates the status of a user.
     *
     * @param updateUserStatusReqDto The request DTO for updating user status
     * @return UpdateUserStatusResDto The response DTO for the status update
     */
    @RequestMapping(value = "/update-user-status", method = RequestMethod.POST)
    @ResponseBody
    public UpdateUserStatusResDto updateUserStatus(@Valid @RequestBody UpdateUserStatusReqDto updateUserStatusReqDto) {
        return userService.updateUserStatus(updateUserStatusReqDto);
    }

    /**
     * Updates a DID document to a deactivated state.
     *
     * @param updateDidDocDeactivatedReqDto The request DTO for deactivating a DID document
     * @return EmptyResDto An empty response DTO
     */
    @RequestMapping(value = "/update-diddoc-deactivated", method = RequestMethod.POST)
    public EmptyResDto updateDidDocDeactivated(@Valid @RequestBody UpdateDidDocDeactivatedReqDto updateDidDocDeactivatedReqDto) {
        return userService.updateDidDocDeactivated(updateDidDocDeactivatedReqDto);
    }

    /**
     * Updates a DID document to a revoked state.
     *
     * @param updateDidDocRevokedReqDto The request DTO for revoking a DID document
     * @return EmptyResDto An empty response DTO
     */
    @RequestMapping(value = "/update-diddoc-revoked", method = RequestMethod.POST)
    public EmptyResDto updateDidDocRevoked(@Valid @RequestBody UpdateDidDocRevokedReqDto updateDidDocRevokedReqDto) {
        return userService.updateDidDocRevoked(updateDidDocRevokedReqDto);
    }

    /**
     * Offers DID restoration via push notification.
     *
     * @param offerRestoreDidPushReqDto The request DTO for offering DID restoration via push
     * @return OfferRestoreDidPushResDto The response DTO for the push-based restoration offer
     */
    @RequestMapping(value = "/offer-restore-did/push", method = RequestMethod.POST)
    public OfferRestoreDidPushResDto offerRestoreDidPush(@Valid @RequestBody OfferRestoreDidPushReqDto offerRestoreDidPushReqDto) {
        return userService.offerRestoreDidPush(offerRestoreDidPushReqDto);
    }

    /**
     * Offers DID restoration via email.
     *
     * @param offerRestoreDidEmailReqDto The request DTO for offering DID restoration via email
     * @return OfferRestoreDidEmailResDto The response DTO for the email-based restoration offer
     */
    @RequestMapping(value = "/offer-restore-did/email", method = RequestMethod.POST)
    public OfferRestoreDidEmailResDto offerRestoreDidEmail(@Valid @RequestBody OfferRestoreDidEmailReqDto offerRestoreDidEmailReqDto) {
        return userService.offerRestoreDidEmail(offerRestoreDidEmailReqDto);
    }

    /**
     * Handles the proposal for restoring a DID document.
     *
     * @param proposeRestoreDidDocReqDto The request DTO for proposing DID document restoration
     * @return ProposeRestoreDidDocResDto The response DTO for the restoration proposal
     */
    @RequestMapping(value = "/propose-restore-diddoc", method = RequestMethod.POST)
    public ProposeRestoreDidDocResDto requestRestoreDidDoc(@Valid @RequestBody ProposeRestoreDidDocReqDto proposeRestoreDidDocReqDto) {
        return userService.proposeRestoreDidDoc(proposeRestoreDidDocReqDto);
    }

    /**
     * Handles the request for restoring a DID document.
     *
     * @param requestRestoreDidDocReqDto The request DTO for restoring a DID document
     * @return RequestRestoreDidDocResDto The response DTO for the restoration request
     */
    @RequestMapping(value = "/request-restore-diddoc", method = RequestMethod.POST)
    public RequestRestoreDidDocResDto requestRestoreDidDoc(@Valid @RequestBody RequestRestoreDidDocReqDto requestRestoreDidDocReqDto) {
        return userService.requestRestoreDidDoc(requestRestoreDidDocReqDto);
    }

    /**
     * Confirms the restoration of a DID document.
     *
     * @param confirmRestoreDidDocReqDto The request DTO for confirming DID document restoration
     * @return ConfirmRestoreDidDocResDto The response DTO for the restoration confirmation
     */
    @RequestMapping(value = "/confirm-restore-diddoc", method = RequestMethod.POST)
    public ConfirmRestoreDidDocResDto confirmRestoreDidDoc(@Valid @RequestBody ConfirmRestoreDidDocReqDto confirmRestoreDidDocReqDto) {
        return userService.confirmRestoreDidDoc(confirmRestoreDidDocReqDto);
    }
}