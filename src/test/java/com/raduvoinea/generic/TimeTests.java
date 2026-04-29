package com.raduvoinea.generic;

import com.raduvoinea.utils.generic.Time;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimeTests {

	@Test
	public void testSimple(){
		Time time = Time.days(1);

		assertEquals("1d", time.toString());
	}

	@Test
	public void testComplex(){
		Time time1 = Time.milliseconds(87315468725L);
		Time time2 = Time.milliseconds(87315725L);
		assertEquals("2y 9M 1w", time1.toString());
		assertEquals("1d 15m 15s", time2.toString());
	}

	@Test
	public void testParseSeconds() {
		Time time = Time.parse("5s");
		Assertions.assertNotNull(time);
		assertEquals(5000, time.toMilliseconds());
	}

	@Test
	public void testParseMinutes() {
		Time time = Time.parse("10m");
		Assertions.assertNotNull(time);
		assertEquals(600000, time.toMilliseconds());
	}

	@Test
	public void testParseHours() {
		Time time = Time.parse("2h");
		Assertions.assertNotNull(time);
		assertEquals(7200000, time.toMilliseconds());
	}

	@Test
	public void testParseDays() {
		Time time = Time.parse("7d");
		Assertions.assertNotNull(time);
		assertEquals(604800000, time.toMilliseconds());
	}

	@Test
	public void testParseMilliseconds() {
		Time time = Time.parse("150ms");
		Assertions.assertNotNull(time);
		assertEquals(150, time.toMilliseconds());
	}

	@Test
	public void testParseInvalid() {
		assertNull(Time.parse("invalid"));
		assertNull(Time.parse(""));
		assertNull(Time.parse("x"));
	}

	@Test
	public void testToSeconds() {
		assertEquals(60, Time.minutes(1).toSeconds());
		assertEquals(3600, Time.hours(1).toSeconds());
	}

	@Test
	public void testMillisecondsToString() {
		assertEquals("1 seconds", Time.millisecondsToString(1000));
		assertEquals("1 minutes", Time.millisecondsToString(60000));
		assertEquals("0 milliseconds", Time.millisecondsToString(0));
	}

	@Test
	public void testGetReversedValues() {
		var reversed = Time.Unit.getReversedValues();
		assertEquals(Time.Unit.YEARS, reversed.getFirst());
		assertEquals(Time.Unit.MILLISECONDS, reversed.getLast());
	}


}
