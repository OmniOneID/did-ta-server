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

package org.omnione.did.base.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.omnione.did.base.exception.CommonSdkErrorMapper;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.exception.ProviderOpenDidException;
import org.omnione.did.common.exception.CommonSdkException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Keeps server errors intact when they pass through the TA SDK.
 * <p>
 * Providers delegate to the server's services, which report failures with {@link OpenDidException}. The SDK would
 * replace that exception with a generic SDK error, so it is wrapped into a {@link ProviderOpenDidException} when it
 * leaves a provider.
 */
@Aspect
@Component
@Profile("!sample")
public class TaSdkProviderExceptionAspect {

    @Around("within(org.omnione.did.tas.v1.config.provider..*)")
    public Object preserveServerError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (OpenDidException e) {
            throw new ProviderOpenDidException(e);
        } catch (CommonSdkException e) {
            throw new ProviderOpenDidException(CommonSdkErrorMapper.toOpenDidException(e));
        }
    }
}
