package com.raduvoinea.utils.file_manager.dto.jackson.interfaces;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;

import java.io.IOException;

public class InterfaceTypeSerializer extends JsonSerializer<ISerializable> {

    private final ClassLoader classLoader;
    private final ObjectMapper delegateMapper;

    public InterfaceTypeSerializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.delegateMapper = new ObjectMapper();
    }

    @Override
    public void serialize(ISerializable value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("className", value.getClass().getName());
        generator.writeFieldName("data");
        generator.writeRawValue(delegateMapper.writeValueAsString(value));

        generator.writeEndObject();
    }
}
