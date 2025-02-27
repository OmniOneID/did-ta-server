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

package org.omnione.did.tas.v1.agent.service;

import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityResDto;

/**
 * Entity service interface for managing entity enrollments.
 */
public interface EntityService {
    ProposeEnrollEntityResDto proposeEnrollEntity(ProposeEnrollEntityReqDto proposeEnrollEntityReqDto);
    RequestEnrollEntityResDto requestEnrollEntity(RequestEnrollEntityReqDto requestEnrollEntityReqDto);
    ConfirmEnrollEntityResDto confirmEnrollEntity(ConfirmEnrollEntityReqDto confirmEnrollEntityReqDto);
}
