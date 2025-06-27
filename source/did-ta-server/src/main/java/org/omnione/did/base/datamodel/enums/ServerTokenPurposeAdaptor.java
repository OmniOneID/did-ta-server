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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * TypeAdapter for ServerTokenPurpose enum.
 *
 * This adapter is necessary to correctly serialize and deserialize enums that use custom values,
 * such as integers or other non-standard representations, in JSON. By default, Gson serializes enums
 * using their name, but in some cases, an enum might be represented by a specific value (e.g., an integer)
 * in JSON. This adapter ensures that the correct value is written to and read from JSON, allowing
 * for proper handling of these enums in API requests and responses.
 */
public class ServerTokenPurposeAdaptor extends TypeAdapter<ServerTokenPurpose> {
    @Override
    public void write(JsonWriter out, ServerTokenPurpose value) throws IOException {
        if (value != null) {
            out.value(value.getPurpose());
        } else {
            out.nullValue();
        }
    }

    @Override
    public ServerTokenPurpose read(JsonReader in) throws IOException {
        int purposeValue = in.nextInt();
        for (ServerTokenPurpose purpose : ServerTokenPurpose.values()) {
            if (purpose.getPurpose().equals(purposeValue)) {
                return purpose;
            }
        }
        throw new IOException("Unknown purpose value: " + purposeValue);
    }
}
