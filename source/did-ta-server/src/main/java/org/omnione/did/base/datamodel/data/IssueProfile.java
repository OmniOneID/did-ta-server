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

import org.omnione.did.base.datamodel.enums.Encoding;
import org.omnione.did.base.datamodel.enums.Language;
import org.omnione.did.base.datamodel.enums.ProfileType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * This class represents the IssueProfile structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class IssueProfile {
    @NotNull(message = "id cannot be null")
    private String id;
    @NotNull(message = "type cannot be null")
    private ProfileType type;
    private String title;
    private String description;
    private LogoImage logo;
    @NotNull(message = "encoding cannot be null")
    private Encoding encoding;
    @NotNull(message = "language cannot be null")
    private Language language;
    @Valid
    private VcProfile profile;
    @Valid
    private Proof proof;
}
