/*
 * Copyright 2024 OmniOne.
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


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * This class represents the AttestedDidDoc structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AttestedDidDoc {
    @NotNull(message = "attestedDidDoc.walletId cannot be null")
    private String walletId;
    @NotNull(message = "attestedDidDoc.ownerDiddoc cannot be null")
    private String ownerDidDoc;
    @Valid
    private Provider provider;
    @NotNull(message = "attestedDidDoc.nonce cannot be null")
    private String nonce;
    @Valid
    private Proof proof;
}
