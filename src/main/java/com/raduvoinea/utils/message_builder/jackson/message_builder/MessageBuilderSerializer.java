package com.raduvoinea.utils.message_builder.jackson.message_builder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.raduvoinea.utils.message_builder.MessageBuilder;

import java.io.IOException;

public class MessageBuilderSerializer extends JsonSerializer<MessageBuilder> {
    @Override
    public void serialize(MessageBuilder value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getBase());
    }
}
