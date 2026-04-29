package com.raduvoinea.utils.message_builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageBuilderManager {

	private static MessageBuilderManager instance;
	private boolean chatColor;

	/**
	 * Enables the replacement of %placeholder%
	 */
	// TODO Remove at some point
	private boolean legacyMode;

	public MessageBuilderManager(boolean chatColor) {
		instance = this;

		this.chatColor = chatColor;
	}

	public static void init(boolean chatColor) {
		new MessageBuilderManager(chatColor);
	}

	public static MessageBuilderManager instance() {
		if (instance == null) {
			new MessageBuilderManager(false);
		}
		return instance;
	}

}
