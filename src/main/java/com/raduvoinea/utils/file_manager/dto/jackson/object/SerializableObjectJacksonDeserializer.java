package com.raduvoinea.utils.file_manager.dto.jackson.object;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;
import com.raduvoinea.utils.logger.Logger;

import java.io.IOException;

@SuppressWarnings({"rawtypes"})
public class SerializableObjectJacksonDeserializer extends JsonDeserializer<SerializableObject> {

    private static final String CLASS_NAME = "class_name";
    private static final String DATA = "data";

    private final ClassLoader classLoader;

    public SerializableObjectJacksonDeserializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public SerializableObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        String className = node.get(CLASS_NAME).asText(null);
        JsonNode dataNode = node.get(DATA);

        if (className == null) {
            return new SerializableObject<>(null);
        }

        try {
            Class<?> clazz = classLoader.loadClass(className);
            Object object = codec.treeToValue(dataNode, clazz);

            return new SerializableObject<>(object);
        } catch (ClassNotFoundException e) {
            Logger.error(e);
            return new SerializableObject<>(null);
        }
    }
}
