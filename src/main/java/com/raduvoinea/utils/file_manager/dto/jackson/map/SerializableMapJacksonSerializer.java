package com.raduvoinea.utils.file_manager.dto.jackson.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableMap;

import java.io.IOException;

@SuppressWarnings({"rawtypes"})
public class SerializableMapJacksonSerializer extends JsonSerializer<SerializableMap> {

    private static final String KEY_CLASS_NAME = "key_class_name";
    private static final String VALUE_CLASS_NAME = "value_class_name";
    private static final String KEYS = "keys";
    private static final String VALUES = "values";

    @Override
    public void serialize(SerializableMap value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(KEY_CLASS_NAME, value.getKeyClass().getName());
        gen.writeStringField(VALUE_CLASS_NAME, value.getValueClass().getName());

        gen.writeArrayFieldStart(KEYS);
        for (Object key : value.keySet()) {
            gen.writeObject(key);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart(VALUES);
        for (Object val : value.values()) {
            gen.writeObject(val);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
