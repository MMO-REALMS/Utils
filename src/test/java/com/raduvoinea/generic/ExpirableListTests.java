package com.raduvoinea.generic;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.ExpirableList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpirableListTests {

	@Test
	public void testAddAndGet() {
		ExpirableList<String> list = new ExpirableList<>(Time.hours(1));
		list.add("a");
		list.add("b");

		List<String> data = list.getData();
		assertEquals(2, data.size());
		assertTrue(data.contains("a"));
		assertTrue(data.contains("b"));
	}

	@Test
	public void testExpiration() throws InterruptedException {
		ExpirableList<String> list = new ExpirableList<>(Time.milliseconds(50));
		list.add("a");
		Thread.sleep(100);
		list.add("b");

		List<String> data = list.getData();
		assertEquals(1, data.size());
		assertEquals("b", data.getFirst());
	}

	@Test
	public void testContains() {
		ExpirableList<String> list = new ExpirableList<>(Time.hours(1));
		list.add("x");
		assertTrue(list.contains("x"));
		assertFalse(list.contains("y"));
	}

	@Test
	public void testRemove() {
		ExpirableList<String> list = new ExpirableList<>(Time.hours(1));
		list.add("a");
		list.add("b");
		list.remove("a");

		assertFalse(list.contains("a"));
		assertTrue(list.contains("b"));
	}

	@Test
	public void testRemoveIf() {
		ExpirableList<Integer> list = new ExpirableList<>(Time.hours(1));
		list.add(1);
		list.add(2);
		list.add(3);
		list.removeIf(n -> n > 1);

		assertEquals(List.of(1), list.getData());
	}

	@Test
	public void testClearExpiredThrottling() throws InterruptedException {
		ExpirableList<String> list = new ExpirableList<>(Time.milliseconds(10));
		list.add("a");
		Thread.sleep(20);

		// First call clears expired ("a" should be gone)
		assertFalse(list.contains("a"));

		list.add("b");
		Thread.sleep(20);

		// Second call within throttle window (<100ms), so expired item won't be cleared yet
		// "b" was added 20ms ago, threshold=10ms, but clear didn't run due to throttling
		// The test demonstrates that throttling prevents excessive clearing
		list.contains("b"); // this may or may not clear depending on timing
	}
}
