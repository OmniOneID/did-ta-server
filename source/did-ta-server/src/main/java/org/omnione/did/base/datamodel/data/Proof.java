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

import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * This class represents the Proof structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Proof {
    @NotNull(message = "proof.type cannot be null")
    private ProofType type;
    @NotNull(message = "proof.created cannot be null")
    private String created;
    @NotNull(message = "proof.verificationMethod cannot be null")
    private String verificationMethod;
    @NotNull(message = "proof.proofPurpose cannot be null")
    private ProofPurpose proofPurpose;
    private String proofValue;
}
