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

package org.omnione.did.tas.v1.agent.dto.user;

import org.omnione.did.base.datamodel.data.SignedDidDoc;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for requesting register user.
 */
//@TODO: data-model SDK 사용하는 것으로 변경되면 해당 클래스 삭제할 것
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RequestRegisterUserReqDto {
    @NotNull(message = "id cannot be null")
    private String id;
    @NotNull(message = "txId cannot be null")
    private String txId;
    @NotNull(message = "serverToken cannot be null")
    private String serverToken;
    @Valid
    private SignedDidDoc signedDidDoc;
}
