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

package org.omnione.did.base.datamodel.data;

import org.omnione.did.base.datamodel.enums.EccCurveType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * This class represents the EcdhReqData structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EcdhReqData {
    @NotNull(message = "reqEcdh.client cannot be null")
    private String client;
    @NotNull(message = "reqEcdh.clientNonce cannot be null")
    private String clientNonce;
    @NotNull(message = "reqEcdh.curve cannot be null")
    private EccCurveType curve;
    @NotNull(message = "reqEcdh.publicKey cannot be null")
    private String publicKey;
    @Valid
    private Candidate candidate;
    @Valid
    private Proof proof;
}
