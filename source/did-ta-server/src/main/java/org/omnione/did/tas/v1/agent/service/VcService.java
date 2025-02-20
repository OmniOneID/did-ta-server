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

import org.omnione.did.tas.v1.common.dto.agent.vc.ConfirmIssueVcReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.ConfirmIssueVcResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.ConfirmRevokeVcReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.ConfirmRevokeVcResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.OfferIssueVcEmailReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.OfferIssueVcNotiResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.OfferIssueVcPushReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.OfferIssueVcQrReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.OfferIssueVcResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.ProposeIssueVcReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.ProposeIssueVcResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.ProposeRevokeVcReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.ProposeRevokeVcResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.RequestIssueProfileReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.RequestIssueProfileResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.RequestIssueVcReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.RequestIssueVcResDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.RequestRevokeVcReqDto;
import org.omnione.did.tas.v1.common.dto.agent.vc.RequestRevokeVcResDto;

/**
 * VC service interface for handling Verifiable Credentials.
 *
 */
public interface VcService {
    ProposeIssueVcResDto proposeIssueVc(ProposeIssueVcReqDto proposeIssueVcReqDto);
    RequestIssueProfileResDto requestIssueProfile(RequestIssueProfileReqDto requestIssueProfileReqDto);
    RequestIssueVcResDto requestIssueVc(RequestIssueVcReqDto requestIssueVcReqDto);
    ConfirmIssueVcResDto confirmIssueVc(ConfirmIssueVcReqDto confirmIssueVcReqDto);
    OfferIssueVcResDto offerIssueVcQr(OfferIssueVcQrReqDto request);
    OfferIssueVcNotiResDto offerIssueVcEmail(OfferIssueVcEmailReqDto request);
    OfferIssueVcNotiResDto offerIssueVcPush(OfferIssueVcPushReqDto request);
    String requestCertificateVc();
    String requestVcSchema(String name);
    ProposeRevokeVcResDto proposeRevokeVc(ProposeRevokeVcReqDto proposeRevokeVcReqDto);
    RequestRevokeVcResDto requestRevokeVc(RequestRevokeVcReqDto requestRevokeVcReqDto);
    ConfirmRevokeVcResDto confirmRevokeVc(ConfirmRevokeVcReqDto confirmRevokeVcReqDto);
}
