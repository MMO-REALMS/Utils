package com.voinearadu.utils.message_builder.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.voinearadu.utils.file_manager.dto.GsonTypeAdapter;
import com.voinearadu.utils.message_builder.MessageBuilderList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MessageBuilderListTypeAdapter extends GsonTypeAdapter<MessageBuilderList> {

    public MessageBuilderListTypeAdapter(ClassLoader classLoader) {
        super(classLoader, MessageBuilderList.class);
    }

    @Override
    public MessageBuilderList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {

        JsonArray array = jsonElement.getAsJsonArray();
        List<String> list = new ArrayList<>();

        for (JsonElement element : array) {
            list.add(element.getAsString());
        }

        return new MessageBuilderList(list);
    }


    @Override
    public JsonElement serialize(MessageBuilderList src, Type type, JsonSerializationContext context) {
        JsonArray array = new JsonArray();

        for (String line : src.getBase()) {
            array.add(line);
        }

        return array;
    }

}
