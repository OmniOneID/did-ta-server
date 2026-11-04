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

package org.omnione.did.tas.v1.config;

import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.tas.core.vc.VcBuilder;
import org.omnione.did.tas.provider.VcSchemaProvider;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;

/**
 * Builds the Certificate VCs of the P110 and P120 protocols the way the TA server issues them.
 * <p>
 * A Certificate VC is valid for one year and its issuer name is the TAS name. The SDK protocols set no validity
 * period, and P120 passes the controller of the TAS DID Document as the issuer name.
 */
public class CertificateVcBuilder extends VcBuilder {

    private static final int VALIDITY_YEARS = 1;

    private final TasQueryService tasQueryService;

    public CertificateVcBuilder(VcSchemaProvider vcSchemaProvider, TasQueryService tasQueryService) {
        super(vcSchemaProvider, VcType.CERTIFICATE_VC.name());
        this.tasQueryService = tasQueryService;
    }

    @Override
    public BuildContext setIssuer(String did, String name, String certVcRef) {
        return super.setIssuer(did, tasQueryService.findTas().getName(), certVcRef)
                .setValidityPeriod(DateTimeUtil.getCurrentUTCTime(), DateTimeUtil.addYearsToCurrentTime(VALIDITY_YEARS));
    }
}
