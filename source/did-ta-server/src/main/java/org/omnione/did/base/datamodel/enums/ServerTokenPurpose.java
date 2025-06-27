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

package org.omnione.did.base.datamodel.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum class for the SERVER_TOKEN_PURPOSE in Data Specification documentation.
 */
public enum ServerTokenPurpose {
    CREATE_DID(5),
    UPDATE_DID(6),
    RESTORE_DID(7),
    ISSUE_VC(8),
    REVOKE_VC(9),
    PRESENT_VP(10),
    CREATE_DID_AND_ISSUE_VC(13),
    ;
    private final Integer purpose;
    ServerTokenPurpose(Integer purpose) { this.purpose = purpose; }

    @JsonValue
    public Integer getPurpose() {
        return purpose;
    }
}
