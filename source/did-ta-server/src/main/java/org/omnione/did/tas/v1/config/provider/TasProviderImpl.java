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

import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.property.TaAuthProperty;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.model.enums.TasStatus;
import org.omnione.did.tas.provider.TasProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK TasProvider interface, delegating to the server's existing services.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class TasProviderImpl implements TasProvider {

    private final TasQueryService tasQueryService;
    private final TaAuthProperty taAuthProperty;

    @Override
    public org.omnione.did.tas.model.domain.Tas getTasInfo() {
        Tas entity = tasQueryService.findTas();
        return org.omnione.did.tas.model.domain.Tas.builder()
                .did(entity.getDid())
                .name(entity.getName())
                .status(toSdkStatus(entity.getStatus()))
                .subjectDn(String.format("o=%s", entity.getName()))
                .version("1.0")
                .build();
    }

    @Override
    public String getTasPassword() {
        return taAuthProperty.getAuth().getRegistrationPassword();
    }

    @Override
    public TasStatus getTasStatus() {
        return toSdkStatus(tasQueryService.findTas().getStatus());
    }

    @Override
    public String getTasDid() {
        return tasQueryService.findTas().getDid();
    }

    /**
     * The SDK distinguishes only a completed TAS from one that is still being set up.
     */
    private TasStatus toSdkStatus(org.omnione.did.base.db.constant.TasStatus serverStatus) {
        return serverStatus == org.omnione.did.base.db.constant.TasStatus.COMPLETED ? TasStatus.COMPLETED : TasStatus.PENDING;
    }
}
