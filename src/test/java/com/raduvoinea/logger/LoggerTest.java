package com.raduvoinea.logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.raduvoinea.logger.dto.PrintAsJsonTestObject;
import com.raduvoinea.logger.dto.TestLogger;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.dto.Level;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoggerTest {

	private static final Gson GSON = new Gson();

	@BeforeEach
	public void beforeEach() {
		Logger.reset();
		Logger.setInstance(new TestLogger(true));
	}

	private @NotNull TestLogger getInstance() {
		if (Logger.getInstance() instanceof TestLogger testLogger) {
			return testLogger;
		}
		fail();
		return null;
	}

	private void assertJsonEquals(String expected, String actualLogLine) {
		int jsonStart = actualLogLine.indexOf('{');
		int jsonEnd = actualLogLine.lastIndexOf('}');
		assertNotEquals(-1, jsonStart, "No JSON object found in log line: " + actualLogLine);
		assertNotEquals(-1, jsonEnd, "No closing brace found in log line: " + actualLogLine);

		String actualJson = actualLogLine.substring(jsonStart, jsonEnd + 1);

		JsonElement expectedElement = GSON.fromJson(expected, JsonElement.class);
		JsonElement actualElement = GSON.fromJson(actualJson, JsonElement.class);
		assertEquals(expectedElement, actualElement);
	}

	@Test
	public void testDebugLogger() {
		Logger.getInstance().setLogLevel(Level.DEBUG);

		Logger.debug("testDebugLogger#debug");
		Logger.log("testDebugLogger#log");
		Logger.good("testDebugLogger#good");
		Logger.warn("testDebugLogger#warn");
		Logger.error("testDebugLogger#error");

		assertEquals(5, this.getInstance().getBuffer().size());
	}

	@Test
	public void testInfoLogger() {
		Logger.getInstance().setLogLevel(Level.INFO);

		Logger.debug("testInfoLogger#debug");
		Logger.log("testInfoLogger#log");
		Logger.good("testInfoLogger#good");
		Logger.warn("testInfoLogger#warn");
		Logger.error("testInfoLogger#error");

		assertEquals(4, this.getInstance().getBuffer().size());
	}

	@Test
	public void testWarnLogger() {
		Logger.getInstance().setLogLevel(Level.WARN);

		Logger.debug("testWarnLogger#debug");
		Logger.log("testWarnLogger#log");
		Logger.good("testWarnLogger#good");
		Logger.warn("testWarnLogger#warn");
		Logger.error("testWarnLogger#error");

		assertEquals(2, this.getInstance().getBuffer().size());
	}

	@Test
	public void testErrorLogger() {
		Logger.getInstance().setLogLevel(Level.ERROR);

		Logger.debug("testErrorLogger#debug");
		Logger.log("testErrorLogger#log");
		Logger.good("testErrorLogger#good");
		Logger.warn("testErrorLogger#warn");
		Logger.error("testErrorLogger#error");

		assertEquals(1, this.getInstance().getBuffer().size());
	}

	@Test
	public void testFormatClass() {
		Logger.log("testErrorLogger#log");

		assertEquals(1, this.getInstance().getBuffer().size());
		assertEquals(
				ConsoleColor.RESET + "[LoggerTest] testErrorLogger#log" + ConsoleColor.RESET,
				this.getInstance().getBuffer().getFirst()
		);
	}

	@Test
	public void testFormatPackage() {
		Logger.setInstance(new TestLogger(true, true));
		Logger.log("testErrorLogger#log");

		assertEquals(1, this.getInstance().getBuffer().size());
		assertEquals(
				ConsoleColor.RESET + "[LoggerTestPackage] testErrorLogger#log" + ConsoleColor.RESET,
				this.getInstance().getBuffer().getFirst()
		);
	}

	@Test
	public void testSoutOverride() {
		{
			Logger.uninstallPrintStream();
			System.out.println("testSoutOverride");
			assertEquals(0, this.getInstance().getBuffer().size());
		}

		Logger.installPrintStream();

		{
			System.out.println("testSoutOverride");
			assertEquals(1, this.getInstance().getBuffer().size());
		}

		{
			Logger.setInstance(new TestLogger(true));
			System.out.println("testSoutOverride");
			assertEquals(1, this.getInstance().getBuffer().size());
		}
	}

	@Test
	public void testSimpleKv() {
		Logger.kv("simple")
				.add("a", "b")
				.add("c", 1)
				.commit();

		String expectedJson = "{"
				+ "\"__type\": \"simple\","
				+ "\"a\": \"b\","
				+ "\"c\": 1"
				+ "}";

		assertJsonEquals(expectedJson, this.getInstance().getBuffer().getFirst());
	}

	@Test
	public void testKvTypes() {
		UUID testUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
		Instant testInstant = Instant.parse("2024-01-15T10:30:00Z");
		LocalDateTime testLdt = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
		LocalDate testLd = LocalDate.of(2024, 1, 15);

		Logger.kv("simple_with_objects")
				.add("string", "hello")
				.add("number_int", 42)
				.add("number_double", 3.14)
				.add("bool_true", true)
				.add("bool_false", false)
				.add("null_val", null)
				.add("char", 'X')
				.add("enum", TimeUnit.SECONDS)
				.add("list", List.of("a", "b", "c"))
				.add("map", new HashMap<>() {{
					put("a", "b");
					put("c", "d");
				}})
				.add("object_array", new Object[]{"x", 1, true})
				.add("int_array", new int[]{1, 2, 3})
				.add("long_array", new long[]{100L, 200L, 300L})
				.add("double_array", new double[]{1.1, 2.2, 3.3})
				.add("bool_array", new boolean[]{true, false, true})
				.add("uuid", testUuid)
				.add("instant", testInstant)
				.add("local_date_time", testLdt)
				.add("local_date", testLd)
				.add("optional_present", Optional.of("value"))
				.add("optional_empty", Optional.empty())
				.add("set", new LinkedHashSet<>(List.of("x", "y", "z")))
				.commit();

		String expectedJson = "{"
				+ "\"__type\": \"simple_with_objects\","
				+ "\"string\": \"hello\","
				+ "\"number_int\": 42,"
				+ "\"number_double\": 3.14,"
				+ "\"bool_true\": true,"
				+ "\"bool_false\": false,"
				+ "\"null_val\": null,"
				+ "\"char\": \"X\","
				+ "\"enum\": \"SECONDS\","
				+ "\"list\": [\"a\",\"b\",\"c\"],"
				+ "\"map\": {\"a\":\"b\",\"c\":\"d\"},"
				+ "\"object_array\": [\"x\",1,true],"
				+ "\"int_array\": [1,2,3],"
				+ "\"long_array\": [100,200,300],"
				+ "\"double_array\": [1.1,2.2,3.3],"
				+ "\"bool_array\": [true,false,true],"
				+ "\"uuid\": \"550e8400-e29b-41d4-a716-446655440000\","
				+ "\"instant\": \"2024-01-15T10:30:00Z\","
				+ "\"local_date_time\": \"2024-01-15T10:30\","
				+ "\"local_date\": \"2024-01-15\","
				+ "\"optional_present\": \"value\","
				+ "\"optional_empty\": null,"
				+ "\"set\": [\"x\",\"y\",\"z\"]"
				+ "}";

		assertJsonEquals(expectedJson, this.getInstance().getBuffer().getFirst());
	}

	@Test
	public void testSimpleNestedKv() {
		Logger.kv("trade_event")
				.add("trade_id", "TRD-001")
				.add("buyer", Logger.kv("player")
						.add("uuid", "550e8400-e29b-41d4-a716-446655440000")
						.add("username", "Radu")
				)
				.add("seller", Logger.kv("player")
						.add("uuid", "660e8400-e29b-41d4-a716-446655440000")
						.add("username", "Steve")
				)
				.add("amount", 500)
				.commit();

		String expectedJson = "{"
				+ "\"__type\": \"trade_event\","
				+ "\"trade_id\": \"TRD-001\","
				+ "\"buyer\": {\"__type\": \"player\", \"uuid\": \"550e8400-e29b-41d4-a716-446655440000\", \"username\": \"Radu\"},"
				+ "\"seller\": {\"__type\": \"player\", \"uuid\": \"660e8400-e29b-41d4-a716-446655440000\", \"username\": \"Steve\"},"
				+ "\"amount\": 500"
				+ "}";

		assertJsonEquals(expectedJson, this.getInstance().getBuffer().getFirst());
	}

	@Test
	public void testComplexNestedKv() {
		Logger.kv("dungeon_run")
				.add("run_id", "RUN-999")
				.add("started_at", Instant.parse("2024-01-15T10:30:00Z"))
				.add("dungeon",
						Logger.kv("dungeon")
								.add("name", "Blackrock Depths")
								.add("difficulty", "HARD")
								.add("max_players", 5)
				)
				.add("party", List.of(
						Logger.kv("member")
								.add("uuid", "550e8400-e29b-41d4-a716-446655440000")
								.add("username", "Radu")
								.add("role", "TANK")
								.add("stats",
										Logger.kv("stats")
												.add("hp", 1000)
												.add("armor", 85)
								),
						Logger.kv("member")
								.add("uuid", "660e8400-e29b-41d4-a716-446655440000")
								.add("username", "Steve")
								.add("role", "HEALER")
								.add("stats",
										Logger.kv("stats")
												.add("hp", 600)
												.add("armor", 30)
								)
				))
				.add("rewards",
						Logger.kv("rewards")
								.add("xp", 5000)
								.add("gold", 1337)
								.add("items", List.of("Blackrock Sword", "Shield of Dawn"))
				)
				.add("completed", true)
				.commit();

		String expectedJson = "{"
				+ "\"__type\": \"dungeon_run\","
				+ "\"run_id\": \"RUN-999\","
				+ "\"started_at\": \"2024-01-15T10:30:00Z\","
				+ "\"dungeon\": {\"__type\": \"dungeon\", \"name\": \"Blackrock Depths\", \"difficulty\": \"HARD\", \"max_players\": 5},"
				+ "\"party\": ["
				+ "  {\"__type\": \"member\", \"uuid\": \"550e8400-e29b-41d4-a716-446655440000\", \"username\": \"Radu\", \"role\": \"TANK\", \"stats\": {\"__type\": \"stats\", \"hp\": 1000, \"armor\": 85}},"
				+ "  {\"__type\": \"member\", \"uuid\": \"660e8400-e29b-41d4-a716-446655440000\", \"username\": \"Steve\", \"role\": \"HEALER\", \"stats\": {\"__type\": \"stats\", \"hp\": 600, \"armor\": 30}}"
				+ "],"
				+ "\"rewards\": {\"__type\": \"rewards\", \"xp\": 5000, \"gold\": 1337, \"items\": [\"Blackrock Sword\",\"Shield of Dawn\"]},"
				+ "\"completed\": true"
				+ "}";

		assertJsonEquals(expectedJson, this.getInstance().getBuffer().getFirst());
	}

	@Test
	public void testLogAsJson() {
		PrintAsJsonTestObject printAsJsonTestObject = new PrintAsJsonTestObject(
				"a",
				1111,
				new HashMap<>() {{
					put("a", "b");
					put("c", "d");
					put("e", "f");
				}},
				List.of("a", "b", "c")
		);

		Logger.debug(printAsJsonTestObject);

		String expectedJson = "{\"a\":\"a\",\"b\":1111,\"c\":{\"a\":\"b\",\"c\":\"d\",\"e\":\"f\"},\"d\":[\"a\",\"b\",\"c\"]}";

		assertJsonEquals(expectedJson, this.getInstance().getBuffer().getFirst());
	}
}