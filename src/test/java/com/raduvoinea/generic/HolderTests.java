package com.raduvoinea.generic;

import com.raduvoinea.utils.generic.dto.Holder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HolderTests {

	@Test
	public void testSimpleHolder() {
		Holder<String> holder = Holder.of("test");
		assertEquals("test", holder.value());

		holder = holder.set("test2");
		assertEquals("test2", holder.value());
	}

	@Test
	public void testReferenceHolder() {
		Holder<String> holder = Holder.of("test");
		assertEquals("test", holder.value());

		HolderHolder holderHolder = new HolderHolder(holder);
		assertEquals("test", holderHolder.holder.value());

		holder = holder.set("test2");
		assertEquals("test2", holder.value());
		assertEquals("test2", holderHolder.holder.value());
	}

	@Test
	public void testEmpty() {
		Holder<String> holder = Holder.empty();
		assertNull(holder.value());
	}

	@Test
	public void testIsEmpty() {
		Holder<String> empty = Holder.empty();
		assertTrue(empty.isEmpty());

		Holder<String> nonEmpty = Holder.of("test");
		assertFalse(nonEmpty.isEmpty());
	}

	@Test
	public void testFluentApi() {
		Holder<String> holder = Holder.of("one")
				.set("two")
				.set("three");
		assertEquals("three", holder.value());
	}

	@Test
	public void testConcurrency() throws InterruptedException {
		Holder<Integer> holder = Holder.of(0);
		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 10000; i++) holder.set(i);
		});
		Thread t2 = new Thread(() -> {
			for (int i = 0; i < 10000; i++) holder.set(i + 100000);
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();

		Integer value = holder.value();
		assertTrue(value >= 0);
	}

	@AllArgsConstructor
	@Getter
	private static class HolderHolder {
		private final Holder<String> holder;
	}


}
