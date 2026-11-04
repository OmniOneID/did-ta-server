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

package org.omnione.did.tas.v1.agent.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant.Issuer;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.exception.ProviderOpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.tas.client.IssuerApiClient;
import org.omnione.did.tas.model.request.issuer.CompleteRevokeApiRequest;
import org.omnione.did.tas.model.request.issuer.CompleteVcApiRequest;
import org.omnione.did.tas.model.request.issuer.GenerateIssueProfileApiRequest;
import org.omnione.did.tas.model.request.issuer.InspectIssueProposeApiRequest;
import org.omnione.did.tas.model.request.issuer.InspectProposeRevokeApiRequest;
import org.omnione.did.tas.model.request.issuer.IssueVcApiRequest;
import org.omnione.did.tas.model.request.issuer.OfferIssueVcApiRequest;
import org.omnione.did.tas.model.request.issuer.RevokeVcApiRequest;
import org.omnione.did.tas.model.response.issuer.CompleteRevokeApiResponse;
import org.omnione.did.tas.model.response.issuer.CompleteVcApiResponse;
import org.omnione.did.tas.model.response.issuer.GenerateIssueProfileApiResponse;
import org.omnione.did.tas.model.response.issuer.InspectIssueProposeApiResponse;
import org.omnione.did.tas.model.response.issuer.InspectProposeRevokeApiResponse;
import org.omnione.did.tas.model.response.issuer.IssueVcApiResponse;
import org.omnione.did.tas.model.response.issuer.OfferIssueVcApiResponse;
import org.omnione.did.tas.model.response.issuer.RevokeVcApiResponse;
import org.omnione.did.tas.v1.agent.api.dto.ApiHolder;
import org.omnione.did.tas.v1.agent.api.dto.CompleteRevokeApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.CompleteRevokeApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.CompleteVcApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.CompleteVcApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.GenerateIssueProfileApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.GenerateIssueProfileApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.InspectIssueProposeApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.InspectIssueProposeApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.InspectProposeRevokeApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.InspectProposeRevokeApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.IssueVcApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.IssueVcApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.OfferIssueVcApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.OfferIssueVcApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.RevokeVcApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.RevokeVcApiResDto;
import org.omnione.did.tas.v1.agent.helper.TaSdkDataConverter;

/**
 * HTTP implementation of the SDK's IssuerApiClient for the P210 and P220 protocols.
 * <p>
 * Created per request by VcServiceImpl. Requests and responses use the server's Issuer API DTOs, which are the format
 * the Issuer server is deployed with. A failure is reported as a {@link ProviderOpenDidException}, which the SDK
 * passes through, so the client keeps receiving the Issuer's error response.
 */
@Slf4j
public class IssuerHttpApiClientImpl implements IssuerApiClient {

    private final String issuerServerUrl;

    /** The Issuer's transaction ID, used by the P220 steps after propose-revoke-vc. */
    private final String externalTxId;

    /**
     * Issuer response of inspect-propose-revoke. The SDK result does not carry the Issuer's transaction ID, and
     * VcServiceImpl stores it in the transaction.
     */
    @Getter
    private InspectProposeRevokeApiResDto inspectProposeRevokeResponse;

    public IssuerHttpApiClientImpl(String issuerServerUrl) {
        this(issuerServerUrl, null);
    }

    public IssuerHttpApiClientImpl(String issuerServerUrl, String externalTxId) {
        this.issuerServerUrl = issuerServerUrl;
        this.externalTxId = externalTxId;
    }

    @Override
    public String getIssuerServerUrl() {
        return issuerServerUrl;
    }

    @Override
    public OfferIssueVcApiResponse offerIssueVc(OfferIssueVcApiRequest request) {
        OfferIssueVcApiReqDto reqDto = OfferIssueVcApiReqDto.builder()
                .vcPlanId(request.getVcPlanId())
                .build();
        OfferIssueVcApiResDto resDto = post(Issuer.REQUEST_OFFER, reqDto, OfferIssueVcApiResDto.class);
        return OfferIssueVcApiResponse.builder()
                .offerId(resDto.getOfferId())
                .validUntil(resDto.getIssueOfferPayload() == null ? null : resDto.getIssueOfferPayload().getValidUntil())
                .build();
    }

    @Override
    public InspectIssueProposeApiResponse inspectProposeIssue(InspectIssueProposeApiRequest request) {
        InspectIssueProposeApiReqDto reqDto = InspectIssueProposeApiReqDto.builder()
                .id(request.getId())
                .vcPlanId(request.getVcPlanId())
                .issuer(request.getIssuer())
                .offerId(request.getOfferId())
                .build();
        InspectIssueProposeApiResDto resDto = post(Issuer.INSPECT_PROPOSE_ISSUE, reqDto, InspectIssueProposeApiResDto.class);
        return InspectIssueProposeApiResponse.builder()
                .txId(resDto.getTxId())
                .refId(resDto.getRefId())
                .build();
    }

    @Override
    public GenerateIssueProfileApiResponse generateIssueProfile(GenerateIssueProfileApiRequest request) {
        GenerateIssueProfileApiReqDto reqDto = GenerateIssueProfileApiReqDto.builder()
                .id(request.getId())
                .txId(request.getTxId())
                .holder(ApiHolder.builder()
                        .did(request.getHolder().getDid())
                        .pii(request.getHolder().getPii())
                        .build())
                .build();
        GenerateIssueProfileApiResDto resDto = post(Issuer.GENERATE_ISSUE_PROFILE, reqDto, GenerateIssueProfileApiResDto.class);
        return GenerateIssueProfileApiResponse.builder()
                .profile(resDto.getProfile())
                .build();
    }

    @Override
    public IssueVcApiResponse issueVc(IssueVcApiRequest request) {
        IssueVcApiReqDto reqDto = IssueVcApiReqDto.builder()
                .id(request.getId())
                .txId(request.getTxId())
                .accE2e(TaSdkDataConverter.toServerAccE2e(request.getAccE2e()))
                .encReqVc(request.getEncReqVc())
                .build();
        IssueVcApiResDto resDto = post(Issuer.ISSUE_VC, reqDto, IssueVcApiResDto.class);
        return IssueVcApiResponse.builder()
                .e2e(TaSdkDataConverter.toSdkE2e(resDto.getE2e()))
                .build();
    }

    @Override
    public CompleteVcApiResponse completeVc(CompleteVcApiRequest request) {
        CompleteVcApiReqDto reqDto = CompleteVcApiReqDto.builder()
                .id(request.getId())
                .txId(request.getTxId())
                .vcId(request.getVcId())
                .build();
        post(Issuer.COMPLETE_VC, reqDto, CompleteVcApiResDto.class);
        return new CompleteVcApiResponse();
    }

    @Override
    public InspectProposeRevokeApiResponse inspectProposeRevoke(InspectProposeRevokeApiRequest request) {
        InspectProposeRevokeApiReqDto reqDto = InspectProposeRevokeApiReqDto.builder()
                .id(request.getId())
                .vcId(request.getVcId())
                .build();
        inspectProposeRevokeResponse = post(Issuer.INSPECT_PROPOSE_REVOKE, reqDto, InspectProposeRevokeApiResDto.class);
        return InspectProposeRevokeApiResponse.builder()
                .issuerNonce(inspectProposeRevokeResponse.getIssuerNonce())
                .authType(inspectProposeRevokeResponse.getAuthType() == null ? null
                        : inspectProposeRevokeResponse.getAuthType().getAuthType())
                .build();
    }

    @Override
    public RevokeVcApiResponse revokeVc(RevokeVcApiRequest request) {
        RevokeVcApiReqDto reqDto = RevokeVcApiReqDto.builder()
                .id(request.getId())
                .txId(externalTxId)
                .request(TaSdkDataConverter.toServerReqRevokeVc(request.getReqRevokeVc()))
                .build();
        post(Issuer.REVOKE_VC, reqDto, RevokeVcApiResDto.class);
        return new RevokeVcApiResponse();
    }

    @Override
    public CompleteRevokeApiResponse completeRevoke(CompleteRevokeApiRequest request) {
        // The SDK passes the TA transaction ID here. The Issuer expects its own transaction ID.
        CompleteRevokeApiReqDto reqDto = CompleteRevokeApiReqDto.builder()
                .id(request.getId())
                .txId(externalTxId)
                .build();
        post(Issuer.COMPLETE_REVOKE, reqDto, CompleteRevokeApiResDto.class);
        return new CompleteRevokeApiResponse();
    }

    private <T> T post(String path, Object requestDto, Class<T> responseType) {
        String url = issuerServerUrl + Issuer.V1 + path;
        try {
            String request = JsonUtil.serializeToJson(requestDto);
            return HttpClientUtil.postData(url, request, responseType);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending request to {}: {}", path, e.getResponseBody(), e);
            throw new ProviderOpenDidException(new OpenDidException(convertExternalErrorResponse(e.getResponseBody())));
        } catch (Exception e) {
            log.error("An unknown error occurred while sending request to {}", path, e);
            throw new ProviderOpenDidException(new OpenDidException(ErrorCode.ISSUER_COMMUNICATION_ERROR));
        }
    }

    private ErrorResponse convertExternalErrorResponse(String responseBody) {
        try {
            return new ObjectMapper().readValue(responseBody, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse external error response: {}", responseBody, e);
            throw new ProviderOpenDidException(new OpenDidException(ErrorCode.ISSUER_UNKNOWN_RESPONSE));
        }
    }
}
