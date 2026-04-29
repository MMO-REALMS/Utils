package com.raduvoinea.utils.generic;

import com.raduvoinea.utils.message_builder.GenericMessageBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class SerializationUtils {

	public static String serializeMap(Map<Object, Object> map, boolean quoteStrings) {
		if (map == null) {
			return "null";
		}

		StringBuilder stringBuilder = new StringBuilder("{");
		Iterator<Map.Entry<Object, Object>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Object, Object> entry = iterator.next();
			stringBuilder.append("\"").append(escapeString(entry.getKey().toString())).append("\":");
			stringBuilder.append(serializeObject(entry.getValue(), quoteStrings));

			if (iterator.hasNext()) {
				stringBuilder.append(",");
			}
		}

		return stringBuilder.append("}").toString();
	}

	public static String serializeList(List<Object> list, boolean quoteStrings) {
		if (list == null) {
			return "null";
		}

		StringBuilder stringBuilder = new StringBuilder("[");

		for (int i = 0; i < list.size(); i++) {
			stringBuilder.append(serializeObject(list.get(i), quoteStrings));

			if (i < list.size() - 1) {
				stringBuilder.append(",");
			}
		}

		return stringBuilder.append("]").toString();
	}

	@SuppressWarnings("unchecked")
	public static String serializeObject(Object value, boolean quoteStrings) {
		return switch (value) {
			case null -> "null";
			case Boolean bool -> bool.toString();
			case Number number -> number.toString();
			case String str -> format(str, quoteStrings, true);
			case Character chr -> format(chr.toString(), quoteStrings, true);
			case Enum<?> e -> format(e.name(), quoteStrings, false);
			case UUID uuid -> format(uuid.toString(), quoteStrings, false);
			case Instant instant -> format(instant.toString(), quoteStrings, false);
			case LocalDateTime localDateTime -> format(localDateTime.toString(), quoteStrings, false);
			case LocalDate localDate -> format(localDate.toString(), quoteStrings, false);
			case Optional<?> optional -> serializeObject(optional.orElse(null), quoteStrings);
			case GenericMessageBuilder<?> mb -> serializeObject(mb.toString(), quoteStrings);
			case Object[] array -> serializeList(Arrays.asList(array), quoteStrings);
			case int[] array -> {
				StringBuilder sb = new StringBuilder("[");
				for (int i = 0; i < array.length; i++) {
					if (i > 0) sb.append(",");
					sb.append(array[i]);
				}
				yield sb.append("]").toString();
			}
			case long[] array -> {
				StringBuilder sb = new StringBuilder("[");
				for (int i = 0; i < array.length; i++) {
					if (i > 0) sb.append(",");
					sb.append(array[i]);
				}
				yield sb.append("]").toString();
			}
			case double[] array -> {
				StringBuilder sb = new StringBuilder("[");
				for (int i = 0; i < array.length; i++) {
					if (i > 0) sb.append(",");
					sb.append(array[i]);
				}
				yield sb.append("]").toString();
			}
			case Map<?, ?> map -> serializeMap((Map<Object, Object>) map, quoteStrings);
			case boolean[] array -> {
				StringBuilder sb = new StringBuilder("[");
				for (int i = 0; i < array.length; i++) {
					if (i > 0) sb.append(",");
					sb.append(array[i]);
				}
				yield sb.append("]").toString();
			}
			case Iterable<?> iterable -> {
				StringBuilder sb = new StringBuilder("[");
				boolean first = true;
				for (Object item : iterable) {
					if (!first) sb.append(",");
					sb.append(serializeObject(item, quoteStrings));
					first = false;
				}
				yield sb.append("]").toString();
			}
			default -> value.toString();
		};
	}

	private static String format(String value, boolean quoteStrings, boolean escapable) {
		boolean escapeExistent = quoteStrings && escapable;

		String result = escapeExistent ? escapeString(value) : value;
		return quoteStrings ? "\"" + result + "\"" : result;
	}

	public static String escapeString(String str) {
		if (str == null) {
			return "";
		}

		// Fast path: check if escaping is needed
		boolean needsEscape = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\\' || c == '"' || c == '\n' || c == '\r' || c == '\t') {
				needsEscape = true;
				break;
			}
		}

		if (!needsEscape) {
			return str;
		}

		StringBuilder sb = new StringBuilder(str.length() + 8);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
				case '\\' -> sb.append("\\\\");
				case '"' -> sb.append("\\\"");
				case '\n' -> sb.append("\\n");
				case '\r' -> sb.append("\\r");
				case '\t' -> sb.append("\\t");
				default -> sb.append(c);
			}
		}
		return sb.toString();
	}

}
