package com.raduvoinea.message_builder;


import com.raduvoinea.utils.logger.KvLog;
import com.raduvoinea.utils.message_builder.KVMessageBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class KVMessageBuilderTests {

	@Test
	void replacesPlaceholderInType() {
		KvLog base = new KvLog("{event}.triggered");
		KVMessageBuilder builder = new KVMessageBuilder(base);

		KvLog result = builder.parse("{event}", "player_join").parse();

		assertEquals("player_join.triggered", result.getType());
	}

	@Test
	void replacesPlaceholderInType2() {
		KvLog base = new KvLog("{event}.triggered");
		KVMessageBuilder builder = new KVMessageBuilder(base);

		KvLog result = builder.parse("{event}", "player_join").parse();

		assertEquals("player_join.triggered", result.getType());
	}

	@Test
	void replacesMultiplePlaceholdersInType() {
		KvLog base = new KvLog("{module}.{action}");
		KVMessageBuilder builder = new KVMessageBuilder(base);

		KvLog result = builder
				.parse("{module}", "auth")
				.parse("{action}", "login")
				.parse();

		assertEquals("auth.login", result.getType());
	}

	@Test
	void noMatchingPlaceholderLeavesTypeUnchanged() {
		KvLog base = new KvLog("static.event");
		KVMessageBuilder builder = new KVMessageBuilder(base);

		KvLog result = builder.parse("{irrelevant}", "value").parse();

		assertEquals("static.event", result.getType());
	}


	@Test
	void replacesPlaceholderInStringValues() {
		KvLog base = new KvLog("event");
		base.add("player", "{name}");
		base.add("world", "{world}");

		KVMessageBuilder builder = new KVMessageBuilder(base);

		KvLog result = builder
				.parse("{name}", "Steve")
				.parse("{world}", "overworld")
				.parse();

		assertEquals("Steve", result.getValues().get("player"));
		assertEquals("overworld", result.getValues().get("world"));
	}

	@Test
	void nonStringValuesPassThroughUnchanged() {
		KvLog base = new KvLog("event");
		base.add("count", 42);
		base.add("ratio", 3.14);
		base.add("flag", true);

		KVMessageBuilder builder = new KVMessageBuilder(base);
		KvLog result = builder.parse("{unused}", "x").parse();

		assertEquals(42, result.getValues().get("count"));
		assertEquals(3.14, result.getValues().get("ratio"));
		assertEquals(true, result.getValues().get("flag"));
	}

	@Test
	void mixedValuesOnlyStringValuesReplaced() {
		KvLog base = new KvLog("event");
		base.add("label", "user={name}");
		base.add("id", 99);

		KvLog result = new KVMessageBuilder(base).parse("{name}", "Alex").parse();

		assertEquals("user=Alex", result.getValues().get("label"));
		assertEquals(99, result.getValues().get("id"));
	}

	@Test
	void nullBaseReturnsNull() {
		KVMessageBuilder builder = new KVMessageBuilder(null);

		assertNull(builder.parse("{x}", "y").parse());
	}

	@Test
	void parseMapsAllPlaceholdersAtOnce() {
		KvLog base = new KvLog("{module}.{action}");
		base.add("target", "{player}");

		KvLog result = new KVMessageBuilder(base)
				.parse(Map.of("{module}", "combat", "{action}", "kill", "{player}", "Notch"))
				.parse();

		assertEquals("combat.kill", result.getType());
		assertEquals("Notch", result.getValues().get("target"));
	}

	@Test
	void cloneProducesEqualButIndependentInstance() {
		KvLog base = new KvLog("{event}");
		base.add("key", "val");

		KVMessageBuilder original = new KVMessageBuilder(base);
		KVMessageBuilder cloned = (KVMessageBuilder) original.clone();

		KvLog origResult = original.parse("{event}", "click").parse();
		KvLog cloneResult = cloned.parse("{event}", "click").parse();
		assertEquals(origResult, cloneResult);
	}

	@Test
	void cloneIsIndependentFromOriginalPlaceholders() {
		KvLog base = new KvLog("{event}");

		KVMessageBuilder original = new KVMessageBuilder(base);
		KVMessageBuilder cloned = (KVMessageBuilder) original.clone();

		KvLog cloneResult = cloned.parse("{event}", "hover").parse();
		KvLog origResult = original.parse("{event}", "click").parse();

		assertEquals("hover", cloneResult.getType());
		assertEquals("click", origResult.getType());
	}

	@Test
	void clonePreservesAlreadyBoundPlaceholders() {
		KvLog base = new KvLog("{module}.{action}");

		KVMessageBuilder withModuleBound = new KVMessageBuilder(base)
				.parse("{module}", "auth");

		KVMessageBuilder cloned = (KVMessageBuilder) withModuleBound.clone();
		KvLog result = cloned.parse("{action}", "logout").parse();

		assertEquals("auth.logout", result.getType());
	}

}
