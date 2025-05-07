package com.raduvoinea.message_builder;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import com.raduvoinea.utils.message_builder.jackson.message_builder.MessageBuilderDeserializer;
import com.raduvoinea.utils.message_builder.jackson.message_builder.MessageBuilderSerializer;
import com.raduvoinea.utils.message_builder.jackson.message_builder_list.MessageBuilderListDeserializer;
import com.raduvoinea.utils.message_builder.jackson.message_builder_list.MessageBuilderListSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageBuilderTests {

	private static ObjectMapper objectMapper;

	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();

		SimpleModule module = new SimpleModule();

		module.addSerializer(MessageBuilder.class, new MessageBuilderSerializer());
		module.addDeserializer(MessageBuilder.class, new MessageBuilderDeserializer());
		module.addSerializer(MessageBuilderList.class, new MessageBuilderListSerializer());
		module.addDeserializer(MessageBuilderList.class, new MessageBuilderListDeserializer());

		objectMapper.registerModule(module);
	}

	@Test
	public void testMessageBuilder() {
		MessageBuilder builder1 = new MessageBuilder("This is a %placeholder%");
		MessageBuilder builder2 = new MessageBuilder("This is a %placeholder-1% with %placeholder-2%");

		String result1 = builder1
				.parse("placeholder", "banana")
				.parse();

		String result2 = builder2
				.parse("placeholder-1", "banana")
				.parse("%placeholder-2%", "1000 calories")
				.parse();

		assertEquals("This is a banana", result1);
		assertEquals("This is a banana with 1000 calories", result2);
	}

	@Test
	public void testMessageBuilderList() {
		MessageBuilderList builder = new MessageBuilderList(Arrays.asList(
				"This is a %placeholder-1%",
				"This %placeholder-1% has %placeholder-2%"
		));

		List<String> result = builder
				.parse("placeholder-1", "banana")
				.parse("%placeholder-2%", "1000 calories")
				.parse();

		List<String> expected = Arrays.asList(
				"This is a banana",
				"This banana has 1000 calories"
		);
		assertArrayEquals(expected.toArray(), result.toArray());
	}

	@Test
	public void testSerializeDeserializeMessageBuilder() throws JsonProcessingException {
		String base = "This is a {placeholder}";
		MessageBuilder builder = new MessageBuilder(base);

		String json = objectMapper.writeValueAsString(builder);

		MessageBuilder deserializedBuilder = objectMapper.readValue(json, MessageBuilder.class);

		assertEquals(json, "\"" + base + "\"");
		assertEquals(builder.getBase(), deserializedBuilder.getBase());
	}

	@Test
	public void testSerializeDeserializeMessageBuilderList() throws JsonProcessingException {
		List<String> base = List.of(
				"This is a {placeholder} - L1",
				"This is a {placeholder} - L2"
		);
		MessageBuilderList builder = new MessageBuilderList(base);

		String json = objectMapper.writeValueAsString(builder);

		MessageBuilderList deserializedBuilder = objectMapper.readValue(json, MessageBuilderList.class);

		assertEquals(json, "[\"" + base.get(0) + "\",\"" + base.get(1) + "\"]");
		assertEquals(builder.getBase(), deserializedBuilder.getBase());
	}


}
