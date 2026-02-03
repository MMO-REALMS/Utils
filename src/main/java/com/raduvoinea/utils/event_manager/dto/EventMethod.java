package com.raduvoinea.utils.event_manager.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.event_manager.exceptions.RuntimeEventException;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

@Getter
public class EventMethod {
	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private final Method method;
	private final Object parentObject;
	private final EventHandler annotation;

	public EventMethod(Object parentObject, @NotNull Method method) {
		this.parentObject = parentObject;
		this.method = method;
		this.method.setAccessible(true);
		this.annotation = method.getAnnotation(EventHandler.class);
	}

	public void fire(Object event, boolean suppressExceptions) {
		try {
			method.invoke(parentObject, event);
		} catch (Exception error) {
			String eventData = "";

			try {
				eventData = GSON.toJson(event);
			} catch (Exception e) {
				eventData = event.toString();
			}

			Logger.error(
					new MessageBuilder("Error while invoking event method {method} from class {class} for event {event}\nJSON (debug only json): {json}")
							.parse("method", method.getName())
							.parse("class", method.getDeclaringClass().getName())
							.parse("event", event.getClass().getSimpleName())
							.parse("json", eventData)
							.parse()
			);

			if (suppressExceptions) {
				Logger.error(error);
			} else {
				throw new RuntimeEventException(error);
			}
		}
	}

	public static class Comparator implements java.util.Comparator<EventMethod> {

		private static Comparator instance;

		public static Comparator getInstance() {
			if (instance == null) {
				instance = new Comparator();
			}
			return instance;
		}

		@Override
		public int compare(@NotNull EventMethod object1, @NotNull EventMethod object2) {
			return object1.annotation.order() - object2.annotation.order();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EventMethod eventMethod)) {
			return false;
		}

		return eventMethod.method.equals(method);
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}
}