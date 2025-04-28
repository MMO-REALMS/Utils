package com.raduvoinea.utils.file_manager.dto.jackson.object;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;

import java.io.IOException;

@SuppressWarnings({"rawtypes"})
public class SerializableObjectJacksonSerializer extends JsonSerializer<SerializableObject> {

    private static final String CLASS_NAME = "class_name";
    private static final String DATA = "data";

    @Override
    public void serialize(SerializableObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(CLASS_NAME, value.getObjectClass().getName());
        gen.writeFieldName(DATA);
        gen.writeObject(value.getObject());
        gen.writeEndObject();
    }
}
