package com.raduvoinea.utils.file_manager.dto.jackson.list;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableList;

import java.io.IOException;

@SuppressWarnings({"rawtypes"})
public class SerializableListJacksonSerializer extends JsonSerializer<SerializableList> {

    private static final String CLASS_NAME_FIELD = "class_name";
    private static final String VALUES_FIELD = "values";

    @Override
    public void serialize(SerializableList value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(CLASS_NAME_FIELD, value.getValueClass().getName());
        gen.writeArrayFieldStart(VALUES_FIELD);

        for (Object element : value) {
            gen.writeObject(element);
        }

        gen.writeEndArray();
        gen.writeEndObject();
    }
}
