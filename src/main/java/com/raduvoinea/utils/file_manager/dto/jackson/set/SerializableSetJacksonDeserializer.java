package com.raduvoinea.utils.file_manager.dto.jackson.set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableSet;
import com.raduvoinea.utils.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SerializableSetJacksonDeserializer extends JsonDeserializer<SerializableSet> {

    private static final String CLASS_NAME_FIELD = "class_name";
    private static final String VALUES = "values";

    private final ClassLoader classLoader;

    public SerializableSetJacksonDeserializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public SerializableSet deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode treeNode = codec.readTree(p);

        try {
            String className = treeNode.get(CLASS_NAME_FIELD).asText();
            Class<?> clazz = classLoader.loadClass(className);

            ArrayNode valuesNode = (ArrayNode) treeNode.get(VALUES);

            List<Object> elements = new ArrayList<>();
            Iterator<JsonNode> iterator = valuesNode.elements();
            while (iterator.hasNext()) {
                JsonNode elementNode = iterator.next();
                elements.add(codec.treeToValue(elementNode, clazz));
            }

            return new SerializableSet(clazz, elements);
        } catch (ClassNotFoundException e) {
            Logger.error(e);
            return null;
        }
    }
}
