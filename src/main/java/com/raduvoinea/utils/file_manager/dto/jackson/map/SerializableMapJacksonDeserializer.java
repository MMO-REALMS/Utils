package com.raduvoinea.utils.file_manager.dto.jackson.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableMap;
import com.raduvoinea.utils.logger.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SerializableMapJacksonDeserializer extends JsonDeserializer<SerializableMap> {

    private static final String KEY_CLASS_NAME = "key_class_name";
    private static final String VALUE_CLASS_NAME = "value_class_name";
    private static final String KEYS = "keys";
    private static final String VALUES = "values";

    private final ClassLoader classLoader;

    public SerializableMapJacksonDeserializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public SerializableMap deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode treeNode = codec.readTree(p);

        try {
            String keyClassName = treeNode.get(KEY_CLASS_NAME).asText();
            String valueClassName = treeNode.get(VALUE_CLASS_NAME).asText();

            Class<?> keyClass = classLoader.loadClass(keyClassName);
            Class<?> valueClass = classLoader.loadClass(valueClassName);

            ArrayNode keysNode = (ArrayNode) treeNode.get(KEYS);
            ArrayNode valuesNode = (ArrayNode) treeNode.get(VALUES);

            Map<Object, Object> map = new HashMap<>();

            Iterator<JsonNode> keyIterator = keysNode.elements();
            Iterator<JsonNode> valueIterator = valuesNode.elements();

            while (keyIterator.hasNext() && valueIterator.hasNext()) {
                Object key = codec.treeToValue(keyIterator.next(), keyClass);
                Object value = codec.treeToValue(valueIterator.next(), valueClass);
                map.put(key, value);
            }

            return new SerializableMap(keyClass, valueClass, map);

        } catch (ClassNotFoundException e) {
            Logger.error(e);
            return null;
        }
    }
}
