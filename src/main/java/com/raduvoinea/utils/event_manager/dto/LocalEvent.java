package com.raduvoinea.utils.event_manager.dto;

import com.raduvoinea.utils.event_manager.EventManager;

public abstract class LocalEvent extends LocalRequest<Void> {

	public LocalEvent(EventManager eventManager) {
		super(null);
	}

}
