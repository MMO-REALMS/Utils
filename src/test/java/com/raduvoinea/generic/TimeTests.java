package com.raduvoinea.generic;

import com.raduvoinea.utils.generic.Time;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


}
