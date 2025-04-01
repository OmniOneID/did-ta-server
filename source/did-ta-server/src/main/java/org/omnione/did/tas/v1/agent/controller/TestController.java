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
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.tas.v1.agent.service.EntityService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.v1.agent.service.validator.CertificateVcValidator;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcNotiResDto;
import org.omnione.did.tas.v1.agent.dto.vc.OfferIssueVcPushReqDto;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.agent.service.IssueVcService;
import org.omnione.did.tas.v1.agent.service.TestService;
import org.omnione.did.tas.v1.agent.service.VcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * The TestController class is a controller that handles requests related to testing.
 * This controller is intended to be used only in test environments.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.AGENT_V1 + "/test")
@Profile("test")
public class TestController {
    private final DidDocService didDocService;
    private final TestService testService;
    private final VcService vcService;
    private final CertificateVcValidator certificateVcValidator;
    private final EntityQueryService entityQueryService;
    private final EntityService entityService;
    private final TasQueryService tasQueryService;
    private final IssueVcService issueVcService;

    /**
     * Finds a DID document based on the provided DID.
     *
     * @param did The DID to search for.
     * @return The DID document in JSON format.
     */
    @GetMapping("/diddoc")
    public String findDidDocument(@RequestParam String did) {
        DidDocument didDocument = didDocService.getDidDocument(did);
        return didDocument.toJson();
    }

    /**
     * Updates the push token associated with the given DID.
     *
     * @param params A map containing the DID and the new push token.
     * @return EmptyResDto A response indicating that the push token was updated successfully.
     */
    @PostMapping("/update-push-token")
    public EmptyResDto updatePushToken(@RequestParam Map<String, String> params) {
        String did = params.get("did");
        String pushToken = params.get("pushtoken");

        testService.updatePushToken(did, pushToken);

        return new EmptyResDto();

    }

    /**
     * Sends a push notification for an offer to issue a Verifiable Credential (VC).
     *
     * @param params A map containing the ID, VC plan ID, issuer, and holder information.
     * @return OfferIssueVcNotiResDto The response of the push notification.
     */
    @PostMapping("/offer-issue-vc/push")
    public OfferIssueVcNotiResDto sendPush(@RequestParam Map<String, String> params) {
        String id = params.get("id");
        String vcPlanId = params.get("vcPlanId");
        String issuer = params.get("issuer");
        String holder = params.get("holder");

        return vcService.offerIssueVcPush(OfferIssueVcPushReqDto.builder()
                .id(id)
                .vcPlanId(vcPlanId)
                .issuer(issuer)
                .holder(holder)
                .build());
    }

    /**
     * Retrieves the most recently registered user in the system.
     *
     * @return User The most recently registered user.
     */
    @GetMapping("/user/latest")
    public User findLatestUser() {
        return testService.findLatestUser();
    }

    /**
     * Validates a certificate Verifiable Credential (VC) based on the provided URL and provider DID.
     *
     * @param params A map containing the certificate VC URL and the provider DID.
     * @return EmptyResDto A response indicating that the certificate VC was validated successfully.
     */
    @PostMapping("/verify-certificate-vc")
    public EmptyResDto validateCertificateVc(@RequestBody Map<String, String> params) {
        String certificateVcUrl = params.get("certificateVcUrl");
        String providerDid = params.get("providerDid");

        certificateVcValidator.validateCertificateVc(certificateVcUrl, providerDid);

        return new EmptyResDto();
    }

    @PostMapping("/generate-certificate-vc")
    public String generateCertificateVc() {

        Entity entity = entityQueryService.findEntityByDid("did:omn:backup");
        VerifiableCredential verifiableCredential = generateEntityCertificateVc(entity);

        log.debug(verifiableCredential.toJson());
        return verifiableCredential.toJson();
    }

    private VerifiableCredential generateEntityCertificateVc(Entity entity) {
        Tas tas = tasQueryService.findTas();
        IssueVcParam issueVcParam = new IssueVcParam();

        issueVcService.setCertificateVcSchema(issueVcParam);
        issueVcService.setIssuer(issueVcParam, tas, tas.getCertificateUrl());
        issueVcService.setEntityClaimInfo(issueVcParam, entity);
        issueVcService.setCertificateVcTypes(issueVcParam);
        issueVcService.setCertificateEvidence(issueVcParam, tas);
        issueVcService.setValidateUntil(issueVcParam,1);

        return issueVcService.generateEntityCertificateVc(issueVcParam, entity);
    }
}
