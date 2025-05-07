package com.raduvoinea.utils.message_builder.jackson.message_builder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.raduvoinea.utils.message_builder.MessageBuilder;

import java.io.IOException;

public class MessageBuilderDeserializer extends JsonDeserializer<MessageBuilder> {
    @Override
    public MessageBuilder deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return new MessageBuilder(p.getValueAsString());
    }
}
