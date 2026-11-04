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

import org.omnione.did.base.db.domain.Ecdh;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.tas.v1.agent.service.TransactionService;
import org.omnione.did.tas.v1.common.service.query.EcdhQueryService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.exception.TaSdkErrorCode;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.model.domain.HolderInfo;
import org.omnione.did.tas.model.domain.VcPlanInfo;
import org.omnione.did.tas.provider.VcIssuanceDataProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of VcIssuanceDataProvider for P210 VC Issuance Protocol.
 *
 * <p>Delegates to server-side services for data access.</p>
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class VcIssuanceDataProviderImpl implements VcIssuanceDataProvider {

    private final TransactionService transactionService;
    private final EcdhQueryService ecdhQueryService;
    private final UserQueryService userQueryService;
    private final EntityQueryService entityQueryService;

    @Override
    public String getAuthNonce(String txId) throws TaSdkException {
        return transactionService.findTransactionByTxId(txId).getAuthNonce();
    }

    @Override
    public String getExternalTxId(String txId) throws TaSdkException {
        return transactionService.findTransactionByTxId(txId).getExternalTxId();
    }

    @Override
    public String getRefId(String txId) throws TaSdkException {
        throw new UnsupportedOperationException("getRefId is not supported: refId is not stored in Transaction");
    }

    @Override
    public String getIssuerDid(String txId) throws TaSdkException {
        return transactionService.findTransactionByTxId(txId).getExternalDid();
    }

    @Override
    @Deprecated
    public String getIssuerServerUrl(String issuerDid) throws TaSdkException {
        return entityQueryService.findEntityByDid(issuerDid).getServerUrl();
    }

    @Override
    public HolderInfo getHolderInfo(String txId) throws TaSdkException {
        Transaction transaction = transactionService.findTransactionByTxId(txId);
        Ecdh ecdh = ecdhQueryService.findEcdhByTransactionId(transaction.getId());
        User user = userQueryService.findByDid(ecdh.getClientDid());
        return HolderInfo.builder()
                .did(user.getDid())
                .pii(user.getPii())
                .build();
    }

    @Override
    public String getPushToken(String holderDid) throws TaSdkException {
        throw new TaSdkException(TaSdkErrorCode.PUSH_TOKEN_NOT_FOUND,
                "Push token is not supported: User entity has no pushToken field");
    }

    /**
     * The TA server does not check the VC plan. The Issuer decides in inspect-propose-issue whether it is valid,
     * so a plan that is not registered in the List provider must not fail here.
     */
    @Override
    public VcPlanInfo getVcPlan(String vcPlanId) throws TaSdkException {
        return VcPlanInfo.builder()
                .vcPlanId(vcPlanId)
                .build();
    }

    /**
     * The issuer of a proposal comes from the request and is validated by the server.
     */
    @Override
    public String getVcPlanIssuerDid(String vcPlanId) throws TaSdkException {
        return null;
    }
}
