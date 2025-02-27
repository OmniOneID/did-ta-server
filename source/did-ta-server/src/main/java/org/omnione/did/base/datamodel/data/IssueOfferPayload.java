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

import org.omnione.did.base.datamodel.enums.OfferType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * This class represents the IssueOfferPayload structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class IssueOfferPayload {
    @NotNull(message = "IssueOfferPayload.offerId cannot be null")
    private String offerId;
    @NotNull(message = "IssueOfferPayload.type cannot be null")
    private OfferType type;
    @NotNull(message = "IssueOfferPayload.vcPlanId cannot be null")
    private String vcPlanId;
    @NotNull(message = "IssueOfferPayload.issuer cannot be null")
    private String issuer;
    @NotNull(message = "IssueOfferPayload.validUntil cannot be null")
    private String validUntil;
}

