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

package org.omnione.did.base.datamodel.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

/**
 * Enum class for the LANGUAGE in Data Specification documentation.
 */
public enum Language {
    @SerializedName("ko")
    KO("ko"),
    @SerializedName("fr")
    FR("fr"),
    @SerializedName("jp")
    JP("jp"),
    @SerializedName("en")
    EN("en");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }
    @Override
    @JsonValue
    public String toString() {
        return displayName;
    }
}
