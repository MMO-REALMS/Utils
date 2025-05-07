package com.raduvoinea.utils.message_builder.jackson.message_builder_list;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageBuilderListDeserializer extends JsonDeserializer<MessageBuilderList> {
    @Override
    public MessageBuilderList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> list = new ArrayList<>();
        while (p.nextToken() != null && !p.currentToken().isStructEnd()) {
            list.add(p.getValueAsString());
        }
        return new MessageBuilderList(list);
    }
}
