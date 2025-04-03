package com.raduvoinea.event_manager.dto;

import com.raduvoinea.event_manager.EventManagerTests;
import com.raduvoinea.utils.event_manager.dto.LocalEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TestEvent extends LocalEvent {

	private final int number1;
	private final int number2;
	private @Setter int result;

	public TestEvent(int number1, int number2) {
		super(EventManagerTests.getEventManager());
		this.number1 = number1;
		this.number2 = number2;
	}

}
