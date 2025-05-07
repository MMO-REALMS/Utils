package com.raduvoinea.utils.message_builder.jackson.message_builder_list;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.io.IOException;

public class MessageBuilderListSerializer extends JsonSerializer<MessageBuilderList> {
    @Override
    public void serialize(MessageBuilderList value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (String line : value.getBase()) {
            gen.writeString(line);
        }
        gen.writeEndArray();
    }
}
