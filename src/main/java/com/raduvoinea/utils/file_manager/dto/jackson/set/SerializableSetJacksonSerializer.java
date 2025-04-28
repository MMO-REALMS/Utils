package com.raduvoinea.utils.file_manager.dto.jackson.set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableSet;

import java.io.IOException;

@SuppressWarnings({"rawtypes"})
public class SerializableSetJacksonSerializer extends JsonSerializer<SerializableSet> {

    private static final String CLASS_NAME_FIELD = "class_name";
    private static final String VALUES = "values";

    @Override
    public void serialize(SerializableSet value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(CLASS_NAME_FIELD, value.getValueClass().getName());

        gen.writeArrayFieldStart(VALUES);
        for (Object element : value) {
            gen.writeObject(element);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
