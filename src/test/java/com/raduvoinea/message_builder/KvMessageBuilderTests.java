package com.raduvoinea.message_builder;


import com.raduvoinea.utils.logger.KvLog;
import com.raduvoinea.utils.message_builder.KvMessageBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class KvMessageBuilderTests {

	@Test
	void replacesPlaceholderInType() {
		KvLog base = new KvLog("{event}.triggered");
		KvMessageBuilder builder = new KvMessageBuilder(base);

		KvLog result = builder.parse("{event}", "player_join").parse();

		assertEquals("player_join.triggered", result.getType());
	}

	@Test
	void replacesPlaceholderInType2() {
		KvLog base = new KvLog("{event}.triggered");
		KvMessageBuilder builder = new KvMessageBuilder(base);

		KvLog result = builder.parse("{event}", "player_join").parse();

		assertEquals("player_join.triggered", result.getType());
	}

	@Test
	void replacesMultiplePlaceholdersInType() {
		KvLog base = new KvLog("{module}.{action}");
		KvMessageBuilder builder = new KvMessageBuilder(base);

		KvLog result = builder
				.parse("{module}", "auth")
				.parse("{action}", "login")
				.parse();

		assertEquals("auth.login", result.getType());
	}

	@Test
	void noMatchingPlaceholderLeavesTypeUnchanged() {
		KvLog base = new KvLog("static.event");
		KvMessageBuilder builder = new KvMessageBuilder(base);

		KvLog result = builder.parse("{irrelevant}", "value").parse();

		assertEquals("static.event", result.getType());
	}


	@Test
	void replacesPlaceholderInStringValues() {
		KvLog base = new KvLog("event");
		base.add("player", "{name}");
		base.add("world", "{world}");

		KvMessageBuilder builder = new KvMessageBuilder(base);

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

		KvMessageBuilder builder = new KvMessageBuilder(base);
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

		KvLog result = new KvMessageBuilder(base).parse("{name}", "Alex").parse();

		assertEquals("user=Alex", result.getValues().get("label"));
		assertEquals(99, result.getValues().get("id"));
	}

	@Test
	void nullBaseReturnsNull() {
		KvMessageBuilder builder = new KvMessageBuilder((KvLog)null);

		assertNull(builder.parse("{x}", "y").parse());
	}

	@Test
	void parseMapsAllPlaceholdersAtOnce() {
		KvLog base = new KvLog("{module}.{action}");
		base.add("target", "{player}");

		KvLog result = new KvMessageBuilder(base)
				.parse(Map.of("{module}", "combat", "{action}", "kill", "{player}", "Notch"))
				.parse();

		assertEquals("combat.kill", result.getType());
		assertEquals("Notch", result.getValues().get("target"));
	}

	@Test
	void cloneProducesEqualButIndependentInstance() {
		KvLog base = new KvLog("{event}");
		base.add("key", "val");

		KvMessageBuilder original = new KvMessageBuilder(base);
		KvMessageBuilder cloned = (KvMessageBuilder) original.clone();

		KvLog origResult = original.parse("{event}", "click").parse();
		KvLog cloneResult = cloned.parse("{event}", "click").parse();
		assertEquals(origResult, cloneResult);
	}

	@Test
	void cloneIsIndependentFromOriginalPlaceholders() {
		KvLog base = new KvLog("{event}");

		KvMessageBuilder original = new KvMessageBuilder(base);
		KvMessageBuilder cloned = (KvMessageBuilder) original.clone();

		KvLog cloneResult = cloned.parse("{event}", "hover").parse();
		KvLog origResult = original.parse("{event}", "click").parse();

		assertEquals("hover", cloneResult.getType());
		assertEquals("click", origResult.getType());
	}

	@Test
	void clonePreservesAlreadyBoundPlaceholders() {
		KvLog base = new KvLog("{module}.{action}");

		KvMessageBuilder withModuleBound = new KvMessageBuilder(base)
				.parse("{module}", "auth");

		KvMessageBuilder cloned = (KvMessageBuilder) withModuleBound.clone();
		KvLog result = cloned.parse("{action}", "logout").parse();

		assertEquals("auth.logout", result.getType());
	}

}
