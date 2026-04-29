package com.raduvoinea.utils.message_builder;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class GenericMessageBuilder<T> {

	@Getter
	protected T base;
	protected List<Object> placeholders = new ArrayList<>();
	protected List<Object> values = new ArrayList<>();

	public GenericMessageBuilder(T base) {
		this.base = base;
	}

	protected GenericMessageBuilder(T base, List<Object> placeholders, List<Object> values) {
		this.base = base;
		this.placeholders = placeholders;
		this.values = values;
	}

	public GenericMessageBuilder<T> parse(Map<?, ?> placeholders) {
		GenericMessageBuilder<T> working = this;
		for (Object placeholder : placeholders.keySet()) {
			Object value = placeholders.get(placeholder);
			working = working.parse(placeholder, value);
		}
		return working;
	}

	public GenericMessageBuilder<T> replace(Object placeholder, Object value) {
		return parse(placeholder, value);
	}

	public GenericMessageBuilder<T> parse(Object placeholder, Object value) {
		GenericMessageBuilder<T> working = clone();

		working.placeholders.add(placeholder);
		working.values.add(value);
		return working;
	}

	public T parse() {
		T parsed = base;
		T before = base;

		MessageBuilderManager manager = MessageBuilderManager.instance();
		boolean legacyMode = manager.isLegacyMode();
		boolean chatColor = manager.isChatColor();

		int limit = Math.min(placeholders.size(), values.size());
		for (int i = 0; i < limit; i++) {
			Object placeholderObj = placeholders.get(i);
			if (placeholderObj == null) {
				continue;
			}

			String placeholder = placeholderObj.toString();

			// TODO Remove at some point
			if (legacyMode) {
				if (placeholder.startsWith("%") && placeholder.endsWith("%")) {
					placeholder = placeholder.substring(1, placeholder.length() - 1);
				}
			}

			if (placeholder.startsWith("{") && placeholder.endsWith("}")) {
				placeholder = placeholder.substring(1, placeholder.length() - 1);
			}

			Object value = values.get(i);
			String valueString;
			if (value instanceof List<?> list) {
				StringBuilder sb = new StringBuilder(list.size() * 8);
				sb.append('[');
				for (int j = 0; j < list.size(); j++) {
					Object obj = list.get(j);
					if (j > 0) sb.append(", ");
					sb.append(obj == null ? "null" : obj.toString());
				}
				sb.append(']');
				valueString = sb.toString();
			} else {
				valueString = value == null ? "null" : value.toString();
			}

			// TODO Remove at some point
			if (legacyMode) {
				parsed = parsePlaceholder(parsed, "%" + placeholder + "%", valueString);
			}

			parsed = parsePlaceholder(parsed, "{" + placeholder + "}", valueString);
		}

		if (chatColor) {
			parsed = parsePlaceholder(parsed, "&", "§");
		}

		this.base = parsed;

		if (!equals(before, parsed)) {
			return parse();
		}

		return parsed;
	}

	@Override
	public String toString() {
		return convertToString();
	}

	/**
	 * Compares 2 instances of T and asserts if they are equal.
	 *
	 * @param o1 First instance of T.
	 * @param o2 Second instance of T.
	 * @return True if equal, false otherwise.
	 */
	@SuppressWarnings("unused")
	protected abstract boolean equals(T o1, T o2);

	protected abstract String convertToString();

	/**
	 * @param base        the base where to parse into
	 * @param placeholder The placeholder to parse. Already has the identifier with % at beginning and end
	 * @param value       The value to parse the placeholder with (already serialized)
	 * @return The parsed value
	 */
	protected abstract T parsePlaceholder(T base, String placeholder, String value);

	/**
	 * Clones the current object
	 */
	public abstract GenericMessageBuilder<T> clone();

	public String convertListToString(List<String> list) {
		return "[" + String.join(", ", list) + "]";
	}

}
