package com.raduvoinea.generic;

import com.raduvoinea.utils.generic.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SerializationUtilsTests {

	@Test
	public void testEscapeStringNoEscapes() {
		String input = "Hello World";
		assertSame(input, SerializationUtils.escapeString(input)); // fast path returns same ref
	}

	@Test
	public void testEscapeStringWithEscapes() {
		assertEquals("Hello \\\"World\\\"", SerializationUtils.escapeString("Hello \"World\""));
		assertEquals("line1\\nline2", SerializationUtils.escapeString("line1\nline2"));
		assertEquals("tab\\there", SerializationUtils.escapeString("tab\there"));
		assertEquals("carriage\\rreturn", SerializationUtils.escapeString("carriage\rreturn"));
		assertEquals("path\\\\to\\\\file", SerializationUtils.escapeString("path\\to\\file"));
	}

	@Test
	public void testSerializeNull() {
		assertEquals("null", SerializationUtils.serializeObject(null, true));
	}

	@Test
	public void testSerializePrimitives() {
		assertEquals("42", SerializationUtils.serializeObject(42, true));
		assertEquals("3.14", SerializationUtils.serializeObject(3.14, true));
		assertEquals("true", SerializationUtils.serializeObject(true, true));
		assertEquals("false", SerializationUtils.serializeObject(false, true));
	}

	@Test
	public void testSerializeString() {
		assertEquals("\"hello\"", SerializationUtils.serializeObject("hello", true));
		assertEquals("hello", SerializationUtils.serializeObject("hello", false));
	}

	@Test
	public void testSerializeArray() {
		assertEquals("[1,2,3]", SerializationUtils.serializeObject(new int[]{1, 2, 3}, true));
		assertEquals("[1,2,3]", SerializationUtils.serializeObject(new long[]{1L, 2L, 3L}, true));
		assertEquals("[1.1,2.2,3.3]", SerializationUtils.serializeObject(new double[]{1.1, 2.2, 3.3}, true));
		assertEquals("[true,false,true]", SerializationUtils.serializeObject(new boolean[]{true, false, true}, true));
	}

	@Test
	public void testSerializeOptional() {
		assertEquals("\"value\"", SerializationUtils.serializeObject(Optional.of("value"), true));
		assertEquals("null", SerializationUtils.serializeObject(Optional.empty(), true));
	}

	@Test
	public void testSerializeUUID() {
		UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
		assertEquals("\"550e8400-e29b-41d4-a716-446655440000\"", SerializationUtils.serializeObject(uuid, true));
	}

	@Test
	public void testSerializeList() {
		List<Object> list = List.of("a", 1, true);
		assertEquals("[\"a\",1,true]", SerializationUtils.serializeList(list, true));
	}

	@Test
	public void testSerializeMap() {
		Map<Object, Object> map = Map.of("key", "value", "num", 42);
		String result = SerializationUtils.serializeMap(map, true);
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
		assertTrue(result.contains("\"key\":\"value\""));
		assertTrue(result.contains("\"num\":42"));
	}
}
