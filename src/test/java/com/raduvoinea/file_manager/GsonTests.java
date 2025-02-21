package com.raduvoinea.file_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.file_manager.dto.interface_serialization.CustomObject1;
import com.raduvoinea.file_manager.dto.interface_serialization.CustomObject2;
import com.raduvoinea.utils.file_manager.dto.gson.InterfaceGsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableListGsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableMapGsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableObjectTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableList;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableMap;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.dto.Level;
import com.raduvoinea.utils.reflections.Reflections;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GsonTests {

    private final static String listJson = "{\"class_name\":\"java.lang.String\",\"values\":[\"test1\",\"test2\",\"test3\"]}";
    private final static String objectJson = "{\"class_name\":\"java.lang.String\",\"data\":\"test\"}";
    private final static String object1Json = "{\"class_name\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject1\",\"data\":\"test\"}";
    private final static String object2Json = "{\"class_name\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject2\",\"data\":1000}";
    private static @Getter Gson gson;

    @BeforeAll
    public static void init() {
        ClassLoader classLoader = GsonTests.class.getClassLoader();
        Logger.setLogLevel(Level.TRACE);

        GsonBuilder gsonBuilder = new GsonBuilder();
        new SerializableListGsonTypeAdapter(classLoader).register(gsonBuilder);
        new SerializableMapGsonTypeAdapter(classLoader).register(gsonBuilder);
        new SerializableObjectTypeAdapter(classLoader).register(gsonBuilder);

        for (InterfaceGsonTypeAdapter<? extends ISerializable> typeAdapter : InterfaceGsonTypeAdapter.generate(classLoader, CustomObject1.class, CustomObject2.class)) {
            typeAdapter.register(gsonBuilder);
        }

        gson = gsonBuilder.create();
    }

    @Test
    public void testReflectionsIntegration() {
        Reflections.Crawler reflectionsCrawler = Reflections.simple(GsonTests.class.getClassLoader(), CustomObject1.class, CustomObject2.class);
        List<InterfaceGsonTypeAdapter<? extends ISerializable>> serializers = InterfaceGsonTypeAdapter.generate(GsonTests.class.getClassLoader(), reflectionsCrawler);
        for (InterfaceGsonTypeAdapter<? extends ISerializable> serializer : serializers) {
            Logger.debug(serializer.getSerializedClass());
        }
        assertEquals(2, serializers.size());
    }

    @Test
    public void serializeList() {
        SerializableList<String> serializableList = new SerializableList<>(
                String.class,
                List.of("test1", "test2", "test3")
        );

        String json = gson.toJson(serializableList);

        assertEquals(listJson, json);
    }

    @Test
    public void deserializeList() {
        //noinspection unchecked
        SerializableList<String> serializableList = gson.fromJson(listJson, SerializableList.class);

        assertNotNull(serializableList);
        assertEquals(String.class, serializableList.getValueClass());
        assertEquals(3, serializableList.size());
        assertEquals("test1", serializableList.get(0));
        assertEquals("test2", serializableList.get(1));
        assertEquals("test3", serializableList.get(2));
    }

    @Test
    public void serializeDeserializeList() {
        SerializableList<String> serializableList = new SerializableList<>(String.class, List.of("test1", "test2", "test3"));

        String json = gson.toJson(serializableList);

        //noinspection unchecked
        SerializableList<String> serializableList2 = gson.fromJson(json, SerializableList.class);

        assertNotNull(serializableList2);
        assertEquals(String.class, serializableList2.getValueClass());
        assertEquals(3, serializableList2.size());
        assertEquals("test1", serializableList2.get(0));
        assertEquals("test2", serializableList2.get(1));
        assertEquals("test3", serializableList2.get(2));
    }

    @Test
    public void serializeMap() {
        SerializableMap<String, Integer> serializableMap = new SerializableMap<>(String.class, Integer.class, new HashMap<>() {{
            put("test1", 1);
            put("test2", 2);
            put("test3", 3);
        }});

        String json = gson.toJson(serializableMap);

        //noinspection unchecked
        SerializableMap<String, Integer> deserializedMap = (SerializableMap<String, Integer>) gson.fromJson(json, SerializableMap.class);

        assertNotNull(deserializedMap);

        assertEquals(String.class, serializableMap.getKeyClass());
        assertEquals(String.class, deserializedMap.getKeyClass());

        assertEquals(Integer.class, serializableMap.getValueClass());
        assertEquals(Integer.class, deserializedMap.getValueClass());

        assertEquals(3, serializableMap.size());
        assertEquals(3, deserializedMap.size());

        assertEquals(serializableMap.getOrDefault("test1", 0), deserializedMap.getOrDefault("test1", 1));
        assertEquals(serializableMap.getOrDefault("test2", 0), deserializedMap.getOrDefault("test2", 1));
        assertEquals(serializableMap.getOrDefault("test3", 0), deserializedMap.getOrDefault("test3", 1));
    }

    @Test
    public void serializeDeserializeMap() {
        SerializableMap<String, Integer> serializableMap = new SerializableMap<>(String.class, Integer.class, new HashMap<>() {{
            put("test1", 1);
            put("test2", 2);
            put("test3", 3);
        }});

        String json = gson.toJson(serializableMap);

        //noinspection unchecked
        SerializableMap<String, Integer> serializableMap2 = gson.fromJson(json, SerializableMap.class);

        assertNotNull(serializableMap2);
        assertEquals(String.class, serializableMap2.getKeyClass());
        assertEquals(Integer.class, serializableMap.getValueClass());
        assertEquals(3, serializableMap.size());
        assertEquals(1, serializableMap.get("test1"));
        assertEquals(2, serializableMap.get("test2"));
        assertEquals(3, serializableMap.get("test3"));
    }


    @Test
    public void serializeObject() {
        SerializableObject<String> serializableObject = new SerializableObject<>(String.class, "test");

        String json = gson.toJson(serializableObject);

        assertEquals(objectJson, json);
    }

    @Test
    public void deserializeObject() {
        //noinspection unchecked
        SerializableObject<String> serializableObject = gson.fromJson(objectJson, SerializableObject.class);

        assertNotNull(serializableObject);
        assertEquals(String.class, serializableObject.objectClass());
        assertEquals("test", serializableObject.object());
    }

    @Test
    public void serializeDeserializeObject() {
        SerializableObject<String> serializableObject1 = new SerializableObject<>(String.class, "test");

        String json = gson.toJson(serializableObject1);

        //noinspection unchecked
        SerializableObject<String> serializableObject2 = gson.fromJson(json, SerializableObject.class);

        assertNotNull(serializableObject2);
        assertEquals(String.class, serializableObject2.objectClass());
    }

    @Test
    public void serializeInterface() {
        ISerializable customInterface1 = new CustomObject1("test");
        ISerializable customInterface2 = new CustomObject2(1000);

        String json1 = gson.toJson(customInterface1);
        String json2 = gson.toJson(customInterface2);

        assertEquals(object1Json, json1);
        assertEquals(object2Json, json2);
    }

    @Test
    public void deserializeInterface() {

    }

    @Test
    public void serializeDeserializeInterface() {

    }


}
