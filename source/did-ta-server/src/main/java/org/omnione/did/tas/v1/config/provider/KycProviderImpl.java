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

package org.omnione.did.tas.v1.config.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.omnione.did.base.db.domain.Kyc;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.tas.v1.agent.api.dto.RetrievePiiApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.RetrievePiiApiResDto;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.KycQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.common.util.token.BaseAccessTokenUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.tas.model.domain.PiiData;
import org.omnione.did.tas.provider.KycProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK KycProvider interface, delegating to the server's KYC infrastructure.
 *
 * <p>Failures are reported with the server's error codes. A KYC server error response is returned as is.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!sample")
public class KycProviderImpl implements KycProvider {

    private final KycQueryService kycQueryService;
    private final StorageService storageService;

    /**
     * Extracts the PII from an OP server-issued JWT access token.
     * If signerDid is configured, the token signer is validated against it.
     * If signerDid is null, the signer DID is extracted from the JWT header.
     */
    @Override
    public PiiData getPiiFromKycToken(String kycToken) {
        try {
            Kyc kyc = kycQueryService.findKyc();
            String signerDid = kyc.getSignerDid() != null ? kyc.getSignerDid() : extractSignerDid(kycToken);
            DidDocument signerDidDocument = storageService.findDidDoc(signerDid);
            try {
                return PiiData.builder()
                        .pii(BaseAccessTokenUtil.validateTokenAndExtractPii(kycToken, signerDidDocument))
                        .build();
            } catch (CommonSdkException e) {
                log.error("Failed to extract PII from KYC token: {}", e.getMessage(), e);
                throw new OpenDidException(isExpiredWithValidSignature(kycToken, signerDidDocument)
                        ? ErrorCode.TOKEN_EXPIRED : ErrorCode.KYC_COMMUNICATION_ERROR);
            }
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to extract PII from KYC token: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    /**
     * Retrieves the PII from the KYC server using the transaction ID.
     */
    @Override
    public PiiData retrieveKycData(String kycTxId) {
        RetrievePiiApiReqDto apiRetrievePiiReqDto = RetrievePiiApiReqDto.builder()
                .userId(kycTxId)
                .build();

        Kyc kyc = kycQueryService.findKyc();

        try {
            String request = JsonUtil.serializeToJson(apiRetrievePiiReqDto);
            RetrievePiiApiResDto retrievePiiApiResDto = HttpClientUtil.postData(kyc.getServerUrl() + "/api/v1/retrieve-pii", request, RetrievePiiApiResDto.class);

            return PiiData.builder().pii(retrievePiiApiResDto.getPii()).build();
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending retrieve-pii request:: {}", e.getMessage(), e);
            throw new OpenDidException(convertExternalErrorResponse(e.getResponseBody()));
        } catch (Exception e) {
            log.error("Failed to retrieve PII information: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }

    private String extractSignerDid(String kycToken) {
        BaseAccessTokenUtil.JwtComponents jwtComponents = BaseAccessTokenUtil.splitJwtToken(kycToken);
        String kid = BaseAccessTokenUtil.extractKidFromHeader(jwtComponents.getHeader());
        return DidUtil.extractDid(kid);
    }

    /**
     * The Common SDK reports every token failure as INVALID_ACCESS_TOKEN. An authentic but expired token
     * is reported to the client as TOKEN_EXPIRED.
     */
    private boolean isExpiredWithValidSignature(String kycToken, DidDocument signerDidDocument) {
        try {
            BaseAccessTokenUtil.JwtComponents jwtComponents = BaseAccessTokenUtil.splitJwtToken(kycToken);
            BaseAccessTokenUtil.validateSignerDid(BaseAccessTokenUtil.extractKidFromHeader(jwtComponents.getHeader()), signerDidDocument);
            BaseAccessTokenUtil.verifyJwtSignature(jwtComponents, BaseAccessTokenUtil.extractPublicKeyFromDidDocument(signerDidDocument));
            return BaseAccessTokenUtil.isTokenExpired(kycToken);
        } catch (Exception e) {
            return false;
        }
    }

    private ErrorResponse convertExternalErrorResponse(String resBody) {
        try {
            return new ObjectMapper().readValue(resBody, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse external error response: {}", resBody, e);
            throw new OpenDidException(ErrorCode.KYC_COMMUNICATION_ERROR);
        }
    }
}
