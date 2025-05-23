package org.omnione.did.base.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;

import java.io.IOException;

/**
 * Utility class for custom Gson serialization/deserialization
 */
public class GsonSerializationUtil {

    /** Gson with custom serializer/deserializer for AttributeDef.ATTR_TYPE */
    public static Gson createGsonWithAttributeTypeSerializer() {
        return new GsonBuilder()
                .registerTypeAdapter(AttributeDef.ATTR_TYPE.class, new AttributeTypeAdapter())
                .create();
    }

    /** TypeAdapter for ATTR_TYPE: serialize using getValue(), parse case-insensitively */
    private static class AttributeTypeAdapter extends TypeAdapter<AttributeDef.ATTR_TYPE> {
        @Override
        public void write(JsonWriter out, AttributeDef.ATTR_TYPE value) throws IOException {
            out.value(value != null ? value.getValue() : null);
        }

        @Override
        public AttributeDef.ATTR_TYPE read(JsonReader in) throws IOException {
            String value = in.peek() == JsonToken.NULL ? null : in.nextString().toUpperCase();
            return value != null ? AttributeDef.ATTR_TYPE.valueOf(value) : null;
        }
    }

    /** Parse JSON string to CredentialSchema with flexible ATTR_TYPE values */
    public static CredentialSchema parseCredentialSchema(String json) {
        return createGsonWithAttributeTypeSerializer().fromJson(json, CredentialSchema.class);
    }

}
