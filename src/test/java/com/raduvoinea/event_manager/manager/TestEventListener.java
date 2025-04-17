package com.raduvoinea.event_manager.manager;

import com.raduvoinea.event_manager.dto.ExternalEvent;
import com.raduvoinea.event_manager.dto.TestEvent;
import com.raduvoinea.event_manager.dto.TestLocalEvent;
import com.raduvoinea.event_manager.dto.TestLocalRequest;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;

public class TestEventListener {

	@EventHandler
	public void onTestEvent(TestEvent event) {
		event.setResult(event.getNumber1() + event.getNumber2());
	}

	@EventHandler
	public void onTestLocalEvent(TestLocalEvent event) {
		event.setResult(event.getNumber1() + event.getNumber2());
	}

	@EventHandler
	public void onTestLocalRequest(TestLocalRequest event) {
event.setResult(event.getNumber1() + event.getNumber2());
	}

	@EventHandler
	public void onExternalEvent(ExternalEvent event) {
		int a=event.getNumber1() + event.getNumber2();// TODO: Implement this method
	}


}
