package com.raduvoinea.utils.event_manager.dto;

import com.raduvoinea.utils.event_manager.EventManager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LocalRequest<Result> extends LocalEvent implements IRequest<Result> {

	private EventManager eventManager;
	private Result result;

	public LocalRequest(EventManager eventManager, Result defaultResult) {
		super(eventManager);
		this.result = defaultResult;
	}

	public void respond(Result result) {
		this.result = result;
	}

}
