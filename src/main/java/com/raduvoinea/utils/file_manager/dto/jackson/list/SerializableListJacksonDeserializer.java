package com.raduvoinea.utils.file_manager.dto.jackson.list;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableList;
import com.raduvoinea.utils.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SerializableListJacksonDeserializer extends JsonDeserializer<SerializableList> {

    private static final String CLASS_NAME_FIELD = "class_name";
    private static final String VALUES_FIELD = "values";

    private final ClassLoader classLoader;

    public SerializableListJacksonDeserializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public SerializableList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode treeNode = codec.readTree(p);

        try {
            String className = treeNode.get(CLASS_NAME_FIELD).asText();
            Class<?> clazz = classLoader.loadClass(className);

            ArrayNode valuesNode = (ArrayNode) treeNode.get(VALUES_FIELD);

            List<Object> elements = new ArrayList<>();

            Iterator<JsonNode> iterator = valuesNode.elements();
            while (iterator.hasNext()) {
                JsonNode elementNode = iterator.next();
                elements.add(codec.treeToValue(elementNode, clazz));
            }

            return new SerializableList(clazz, elements);
        } catch (ClassNotFoundException e) {
            Logger.error(e);
            return null;
        }
    }
}
