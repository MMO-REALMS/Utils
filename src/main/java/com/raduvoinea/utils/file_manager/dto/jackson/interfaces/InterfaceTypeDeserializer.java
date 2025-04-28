package com.raduvoinea.utils.file_manager.dto.jackson.interfaces;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import com.raduvoinea.utils.lambda.lambda.ReturnArgLambdaExecutor;

import java.io.IOException;

public class InterfaceTypeDeserializer extends JsonDeserializer<ISerializable> {

    private final ClassLoader classLoader;
    private final ReturnArgLambdaExecutor<String, String> classMapper;
    private final ObjectMapper delegateMapper;

    public InterfaceTypeDeserializer(ClassLoader classLoader, ReturnArgLambdaExecutor<String, String> classMapper) {
        this.classLoader = classLoader;
        this.classMapper = classMapper;
        this.delegateMapper = new ObjectMapper();
    }

    @Override
    public ISerializable deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec codec = parser.getCodec();
        ObjectNode node = codec.readTree(parser);

        String className = node.get("className").asText();
        JsonNode __dataNode = node.get("data");
        JsonNode dataNode;

        try {
            if(__dataNode.isTextual()){
                dataNode = delegateMapper.readTree(__dataNode.asText());
            } else {
                dataNode = __dataNode;
            }

            String mappedClassName = classMapper != null ? classMapper.execute(className) : className;
            Class<?> clazz = Class.forName(mappedClassName, true, classLoader);

            return  (ISerializable) delegateMapper.treeToValue(dataNode, clazz);
        } catch (ClassNotFoundException e) {
            throw new IOException("Class not found during deserialization: " + className, e);
        }
    }
}
