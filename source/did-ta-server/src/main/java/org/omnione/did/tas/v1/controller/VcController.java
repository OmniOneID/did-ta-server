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

package org.omnione.did.tas.v1.controller;

import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.tas.v1.service.VcService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.v1.dto.vc.ConfirmIssueVcReqDto;
import org.omnione.did.tas.v1.dto.vc.ConfirmIssueVcResDto;
import org.omnione.did.tas.v1.dto.vc.ConfirmRevokeVcReqDto;
import org.omnione.did.tas.v1.dto.vc.ConfirmRevokeVcResDto;
import org.omnione.did.tas.v1.dto.vc.OfferIssueVcEmailReqDto;
import org.omnione.did.tas.v1.dto.vc.OfferIssueVcNotiResDto;
import org.omnione.did.tas.v1.dto.vc.OfferIssueVcPushReqDto;
import org.omnione.did.tas.v1.dto.vc.OfferIssueVcQrReqDto;
import org.omnione.did.tas.v1.dto.vc.OfferIssueVcResDto;
import org.omnione.did.tas.v1.dto.vc.ProposeIssueVcReqDto;
import org.omnione.did.tas.v1.dto.vc.ProposeIssueVcResDto;
import org.omnione.did.tas.v1.dto.vc.ProposeRevokeVcReqDto;
import org.omnione.did.tas.v1.dto.vc.ProposeRevokeVcResDto;
import org.omnione.did.tas.v1.dto.vc.RequestIssueProfileReqDto;
import org.omnione.did.tas.v1.dto.vc.RequestIssueProfileResDto;
import org.omnione.did.tas.v1.dto.vc.RequestIssueVcReqDto;
import org.omnione.did.tas.v1.dto.vc.RequestIssueVcResDto;
import org.omnione.did.tas.v1.dto.vc.RequestRevokeVcReqDto;
import org.omnione.did.tas.v1.dto.vc.RequestRevokeVcResDto;
import org.springframework.web.bind.annotation.*;

/**
 * The VcController class is a controller that handles requests related to vc.
 * It provides endpoints for issuing, proposing, requesting, confirming, and revoking vc.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.V1)
public class VcController {
    private final VcService vcService;

    /**
     * Requests to issue vc.
     *
     * @param request the vc to issue
     * @return the response of issue vc
     */
    @RequestMapping(value = "/offer-issue-vc/qr", method = RequestMethod.POST)
    public OfferIssueVcResDto offerIssueVcQr(@Valid @RequestBody OfferIssueVcQrReqDto request) {
        return vcService.offerIssueVcQr(request);
    }

    /**
     * Requests to issue vc.
     *
     * @param request the vc to issue
     * @return the response of issue vc
     */
    @RequestMapping(value = "/offer-issue-vc/push", method = RequestMethod.POST)
    public OfferIssueVcNotiResDto offerIssueVcPush(@Valid @RequestBody OfferIssueVcPushReqDto request) {
        return vcService.offerIssueVcPush(request);
    }

    /**
     * Requests to issue vc.
     *
     * @param request the vc to issue
     * @return the response of issue vc
     */
    @RequestMapping(value = "/offer-issue-vc/email", method = RequestMethod.POST)
    public OfferIssueVcNotiResDto offerIssueVcEmail(@Valid @RequestBody OfferIssueVcEmailReqDto request) {
        return vcService.offerIssueVcEmail(request);
    }

    /**
     * Proposes to issue vc.
     *
     * @param proposeIssueVcReqDto the vc to propose
     * @return the response of propose issue vc
     */
    @RequestMapping(value = "/propose-issue-vc", method = RequestMethod.POST)
    @ResponseBody
    public ProposeIssueVcResDto proposeIssueVc(@Valid @RequestBody ProposeIssueVcReqDto proposeIssueVcReqDto) {
        return vcService.proposeIssueVc(proposeIssueVcReqDto);
    }

    /**
     * Requests to issue profile.
     *
     * @param requestIssueProfileReqDto the profile to issue
     * @return the response of issue profile
     */
    @RequestMapping(value = "/request-issue-profile", method = RequestMethod.POST)
    @ResponseBody
    public RequestIssueProfileResDto requestIssueProfile(@Valid @RequestBody RequestIssueProfileReqDto requestIssueProfileReqDto) {
        return vcService.requestIssueProfile(requestIssueProfileReqDto);
    }

    /**
     * Requests to issue vc.
     *
     * @param requestIssueVcReqDto the vc to confirm
     * @return the response of confirm issue vc
     */
    @RequestMapping(value = "/request-issue-vc", method = RequestMethod.POST)
    @ResponseBody
    public RequestIssueVcResDto requestIssueVc(@Valid @RequestBody RequestIssueVcReqDto requestIssueVcReqDto) {
        return vcService.requestIssueVc(requestIssueVcReqDto);
    }

    /**
     * Confirms to issue vc.
     *
     * @param confirmIssueVcReqDto the vc to confirm
     * @return the response of confirm issue vc
     */
    @RequestMapping(value = "/confirm-issue-vc", method = RequestMethod.POST)
    @ResponseBody
    public ConfirmIssueVcResDto confirmIssueVc(@Valid @RequestBody ConfirmIssueVcReqDto confirmIssueVcReqDto) {
        return vcService.confirmIssueVc(confirmIssueVcReqDto);
    }

    /**
     * Requests Certificate VC.
     *
     * @return the response of request certificate vc
     */
    @GetMapping("/certificate-vc")
    public String requestCertificateVc() {
        return vcService.requestCertificateVc();
    }

    /**
     * Requests VC Schema.
     *
     * @param name the name of the schema
     * @return the response of request vc schema
     */
    @GetMapping("/vc-schema")
    public String requestVcSchema(@RequestParam(name = "name") String name) {
        // FIXME: Return Type VcSchema
        return vcService.requestVcSchema(name);
    }

    /**
     * propose revoke vc.
     *
     * @param proposeRevokeVcReqDto the schema of the vc
     * @return propose revoke vc response
     */
    @RequestMapping(value = "/propose-revoke-vc", method = RequestMethod.POST)
    @ResponseBody
    public ProposeRevokeVcResDto proposeRevokeVc(@Valid @RequestBody ProposeRevokeVcReqDto proposeRevokeVcReqDto) {
        return vcService.proposeRevokeVc(proposeRevokeVcReqDto);
    }

    /**
     * request revoke vc.
     *
     * @param requestRevokeVcReqDto the schema of the vc
     * @return request revoke vc response
     */
    @RequestMapping(value = "/request-revoke-vc", method = RequestMethod.POST)
    @ResponseBody
    public RequestRevokeVcResDto requestRevokeVc(@Valid @RequestBody RequestRevokeVcReqDto requestRevokeVcReqDto) {
        return vcService.requestRevokeVc(requestRevokeVcReqDto);
    }

    /**
     * confirm revoke vc.
     *
     * @param confirmRevokeVcReqDto the schema of the vc
     * @return confirm revoke vc response
     */
    @RequestMapping(value = "/confirm-revoke-vc", method = RequestMethod.POST)
    @ResponseBody
    public ConfirmRevokeVcResDto confirmRevokeVc(@Valid @RequestBody ConfirmRevokeVcReqDto confirmRevokeVcReqDto) {
        return vcService.confirmRevokeVc(confirmRevokeVcReqDto);
    }
}
