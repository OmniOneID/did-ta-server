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

import org.omnione.did.base.datamodel.data.ReqRevokeVc;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.provider.VcRevocationDataProvider;

/**
 * Request data for P220 VC Revocation Protocol, created per request by VcServiceImpl.
 *
 * <p>The TA server checks only that a VC is not revoked yet, and leaves the checks of the revocation request to the
 * Issuer. So this provider gives the SDK the VC meta that was already validated, reports every other status as ACTIVE,
 * and returns the requested vcId and issuerNonce: the Issuer compares them with the values it issued.</p>
 */
public class VcRevocationDataProviderImpl implements VcRevocationDataProvider {

    private static final String ACTIVE = VcStatus.ACTIVE.getRawValue();

    private final VcMeta vcMeta;
    private final ReqRevokeVc reqRevokeVc;

    private VcRevocationDataProviderImpl(VcMeta vcMeta, ReqRevokeVc reqRevokeVc) {
        this.vcMeta = vcMeta;
        this.reqRevokeVc = reqRevokeVc;
    }

    public static VcRevocationDataProviderImpl forPropose(VcMeta vcMeta) {
        return new VcRevocationDataProviderImpl(vcMeta, null);
    }

    public static VcRevocationDataProviderImpl forRequest(ReqRevokeVc reqRevokeVc) {
        return new VcRevocationDataProviderImpl(null, reqRevokeVc);
    }

    public static VcRevocationDataProviderImpl forConfirm() {
        return new VcRevocationDataProviderImpl(null, null);
    }

    @Override
    public org.omnione.did.tas.model.domain.VcMeta getVcMeta(String vcId) throws TaSdkException {
        return org.omnione.did.tas.model.domain.VcMeta.builder()
                .vcId(vcId)
                .issuer(vcMeta.getIssuer().getDid())
                .subject(vcMeta.getSubject())
                .status(VcStatus.fromString(vcMeta.getStatus()) == VcStatus.REVOKED ? VcStatus.REVOKED.getRawValue() : ACTIVE)
                .build();
    }

    @Override
    public String getIssuerDid(String txId) throws TaSdkException {
        return vcMeta == null ? null : vcMeta.getIssuer().getDid();
    }

    @Override
    public String getIssuerNonce(String txId) throws TaSdkException {
        return reqRevokeVc == null ? null : reqRevokeVc.getIssuerNonce();
    }

    @Override
    public String getVcId(String txId) throws TaSdkException {
        return reqRevokeVc == null ? null : reqRevokeVc.getVcId();
    }
}
