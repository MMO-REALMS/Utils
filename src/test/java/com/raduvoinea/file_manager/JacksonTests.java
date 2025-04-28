package com.raduvoinea.file_manager;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.raduvoinea.file_manager.dto.interface_serialization.CustomInterface;
import com.raduvoinea.file_manager.dto.interface_serialization.CustomObject1;
import com.raduvoinea.file_manager.dto.interface_serialization.CustomObject2;
import com.raduvoinea.utils.file_manager.dto.jackson.InheritableDeserializerModule;
import com.raduvoinea.utils.file_manager.dto.jackson.interfaces.InterfaceTypeDeserializer;
import com.raduvoinea.utils.file_manager.dto.jackson.interfaces.InterfaceTypeSerializer;
import com.raduvoinea.utils.file_manager.dto.jackson.list.SerializableListJacksonDeserializer;
import com.raduvoinea.utils.file_manager.dto.jackson.list.SerializableListJacksonSerializer;
import com.raduvoinea.utils.file_manager.dto.jackson.map.SerializableMapJacksonDeserializer;
import com.raduvoinea.utils.file_manager.dto.jackson.map.SerializableMapJacksonSerializer;
import com.raduvoinea.utils.file_manager.dto.jackson.object.SerializableObjectJacksonDeserializer;
import com.raduvoinea.utils.file_manager.dto.jackson.object.SerializableObjectJacksonSerializer;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableList;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableMap;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.dto.Level;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonTests {

	private final static String listJson = "{\"class_name\":\"java.lang.String\",\"values\":[\"test1\",\"test2\",\"test3\"]}";
	private final static String objectJson = "{\"class_name\":\"java.lang.String\",\"data\":\"test\"}";
	private final static String CUSTOM_OBJECT_1_JSON = "{\"className\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject1\",\"data\":{\"data\":\"object1\"}}";
	private final static String CUSTOM_OBJECT_1_JSON_OLD = "{\"className\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject1\",\"data\":\"{\\\"data\\\":\\\"object1\\\"}\"}";
	private final static String CUSTOM_OBJECT_2_JSON = "{\"className\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject2\",\"data\":{\"data\":\"object2\",\"otherData\":\"some other data\"}}";
	private final static String CUSTOM_OBJECT_2_JSON_OLD = "{\"className\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject2\",\"data\":\"{\\\"data\\\":\\\"object2\\\",\\\"otherData\\\":\\\"some other data\\\"}\"}";

	private static @Getter ObjectMapper objectMapper;

	@BeforeAll
	public static void init() {
		Logger.setLogLevel(Level.TRACE);

		ClassLoader classLoader = JacksonTests.class.getClassLoader();

		JsonFactory factory = JsonFactory.builder()
				.enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
				.build();

		objectMapper = new ObjectMapper(factory);

		SimpleModule module = new SimpleModule();
		module.addSerializer(SerializableList.class, new SerializableListJacksonSerializer());
		module.addDeserializer(SerializableList.class, new SerializableListJacksonDeserializer(classLoader));
		module.addSerializer(SerializableMap.class, new SerializableMapJacksonSerializer());
		module.addDeserializer(SerializableMap.class, new SerializableMapJacksonDeserializer(classLoader));
		module.addSerializer(SerializableObject.class, new SerializableObjectJacksonSerializer());
		module.addDeserializer(SerializableObject.class, new SerializableObjectJacksonDeserializer(classLoader));
		module.addSerializer(ISerializable.class, new InterfaceTypeSerializer(classLoader));
//		module.addDeserializer(ISerializable.class, new InterfaceTypeDeserializer(classLoader, className -> className));
//		module.setDeserializerModifier(new BeanDeserializerModifier() {
//			@Override
//			public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
//			                                              BeanDescription beanDesc,
//			                                              JsonDeserializer<?> defaultDeserializer)
//			{
//				Class<?> raw = beanDesc.getBeanClass();
//				if ( raw.isInterface()
//						&& ISerializable.class.isAssignableFrom(raw)  // any interface extending ISerializable
//				) {
//					return new InterfaceTypeDelegatingDeserializer(defaultDeserializer,
//							classLoader,
//							className -> className);
//				}
//				return defaultDeserializer;
//			}
//		});
//		module.addSerializer(ISerializable.class, new InterfaceTypeSerializer(classLoader));
		objectMapper.registerModule(
				new InheritableDeserializerModule(
						ISerializable.class,
						new InterfaceTypeDeserializer(classLoader, classname -> classname)
				)
		);
//		objectMapper.setDefaultTyping(new ClassNamePolymorphicTypeResolver()
//				.init(JsonTypeInfo.Id.CLASS, null)
//				.inclusion(JsonTypeInfo.As.PROPERTY)
//				.typeProperty("className")
//		);

		objectMapper.registerModule(module);
	}

	@Test
	public void serializeList() throws Exception {
		SerializableList<String> serializableList = new SerializableList<>(
				String.class,
				List.of("test1", "test2", "test3")
		);

		String json = objectMapper.writeValueAsString(serializableList);

		assertEquals(listJson, json);
	}

	@Test
	public void deserializeList() throws Exception {
		SerializableList<String> serializableList = objectMapper.readValue(listJson, SerializableList.class);

		assertNotNull(serializableList);
		assertEquals(String.class, serializableList.getValueClass());
		assertEquals(3, serializableList.size());
		assertEquals("test1", serializableList.get(0));
		assertEquals("test2", serializableList.get(1));
		assertEquals("test3", serializableList.get(2));
	}

	@Test
	public void serializeDeserializeList() throws Exception {
		SerializableList<String> serializableList = new SerializableList<>(String.class, List.of("test1", "test2", "test3"));

		String json = objectMapper.writeValueAsString(serializableList);

		SerializableList<String> serializableList2 = objectMapper.readValue(json, SerializableList.class);

		assertNotNull(serializableList2);
		assertEquals(String.class, serializableList2.getValueClass());
		assertEquals(3, serializableList2.size());
		assertEquals("test1", serializableList2.get(0));
		assertEquals("test2", serializableList2.get(1));
		assertEquals("test3", serializableList2.get(2));
	}

	@Test
	public void serializeMap() throws Exception {
		SerializableMap<String, Integer> serializableMap = new SerializableMap<>(String.class, Integer.class, new HashMap<>() {{
			put("test1", 1);
			put("test2", 2);
			put("test3", 3);
		}});

		String json = objectMapper.writeValueAsString(serializableMap);

		SerializableMap<String, Integer> deserializedMap = objectMapper.readValue(json, SerializableMap.class);

		assertNotNull(deserializedMap);

		assertEquals(String.class, deserializedMap.getKeyClass());
		assertEquals(Integer.class, deserializedMap.getValueClass());

		assertEquals(3, deserializedMap.size());
		assertEquals(1, deserializedMap.get("test1"));
		assertEquals(2, deserializedMap.get("test2"));
		assertEquals(3, deserializedMap.get("test3"));
	}

	@Test
	public void serializeDeserializeMap() throws Exception {
		SerializableMap<String, Integer> serializableMap = new SerializableMap<>(String.class, Integer.class, new HashMap<>() {{
			put("test1", 1);
			put("test2", 2);
			put("test3", 3);
		}});

		String json = objectMapper.writeValueAsString(serializableMap);

		SerializableMap<String, Integer> serializableMap2 = objectMapper.readValue(json, SerializableMap.class);

		assertNotNull(serializableMap2);
		assertEquals(String.class, serializableMap2.getKeyClass());
		assertEquals(Integer.class, serializableMap2.getValueClass());
	}

	@Test
	public void serializeObject() throws Exception {
		SerializableObject<String> serializableObject = new SerializableObject<>(String.class, "test");

		String json = objectMapper.writeValueAsString(serializableObject);

		assertEquals(objectJson, json);
	}

	@Test
	public void deserializeObject() throws Exception {
		SerializableObject<String> serializableObject = objectMapper.readValue(objectJson, SerializableObject.class);

		assertNotNull(serializableObject);
		assertEquals(String.class, serializableObject.getObjectClass());
		assertEquals("test", serializableObject.getObject());
	}

	@Test
	public void serializeDeserializeObject() throws Exception {
		SerializableObject<String> serializableObject1 = new SerializableObject<>(String.class, "test");

		String json = objectMapper.writeValueAsString(serializableObject1);

		SerializableObject<String> serializableObject2 = objectMapper.readValue(json, SerializableObject.class);

		assertNotNull(serializableObject2);
		assertEquals(String.class, serializableObject2.getObjectClass());
	}

	@Test
	public void serializeInterface() throws Exception {
		ISerializable customInterface1 = new CustomObject1("object1");
		ISerializable customInterface2 = new CustomObject2("object2", "some other data");

		String json1 = objectMapper.writeValueAsString(customInterface1);
		String json2 = objectMapper.writeValueAsString(customInterface2);

		assertEquals(CUSTOM_OBJECT_1_JSON, json1);
		assertEquals(CUSTOM_OBJECT_2_JSON, json2);
	}

	@Test
	public void deserializeInterface() throws Exception {
		CustomInterface object1 = objectMapper.readValue(CUSTOM_OBJECT_1_JSON, CustomInterface.class);
		CustomInterface object2 = objectMapper.readValue(CUSTOM_OBJECT_2_JSON, CustomInterface.class);

		assertEquals("object1", object1.getData());
		assertEquals("object2", object2.getData());
	}

	@Test
	public void serializeDeserializeInterface() throws Exception {
		CustomObject1 class1 = new CustomObject1("object1");
		CustomObject2 class2 = new CustomObject2("object2", "some other data");

		String json1 = objectMapper.writeValueAsString(class1);
		String json2 = objectMapper.writeValueAsString(class2);

		assertTrue(json1.contains("\"className\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject1\""));
		assertTrue(json2.contains("\"className\":\"com.raduvoinea.file_manager.dto.interface_serialization.CustomObject2\""));

		CustomInterface deserialized1 = objectMapper.readValue(json1, CustomInterface.class);
		CustomInterface deserialized2 = objectMapper.readValue(json2, CustomInterface.class);

		assertEquals("object1", deserialized1.getData());
		assertEquals("object2", deserialized2.getData());
	}

	@Test
	public void deserializeInterfaceLegacy() throws Exception {
		CustomInterface object1 = objectMapper.readValue(CUSTOM_OBJECT_1_JSON_OLD, CustomInterface.class);
		CustomInterface object2 = objectMapper.readValue(CUSTOM_OBJECT_2_JSON_OLD, CustomInterface.class);

		assertEquals("object1", object1.getData());
		assertEquals("object2", object2.getData());
	}
}
