package com.raduvoinea.utils.generic;

import com.raduvoinea.utils.message_builder.GenericMessageBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
			case int[] array -> serializeList(Arrays.stream(array).boxed().collect(Collectors.toList()), quoteStrings);
			case long[] array -> serializeList(Arrays.stream(array).boxed().collect(Collectors.toList()), quoteStrings);
			case double[] array -> serializeList(Arrays.stream(array).boxed().collect(Collectors.toList()), quoteStrings);
			case Map<?, ?> map -> serializeMap((Map<Object, Object>) map, quoteStrings);
			case boolean[] array -> {
				List<Object> bools = new ArrayList<>();

				for (boolean bool : array) {
					bools.add(bool);
				}

				yield serializeList(bools, quoteStrings);
			}
			case Iterable<?> iterable -> {
				List<Object> items = new ArrayList<>();
				iterable.forEach(items::add);
				yield serializeList(items, quoteStrings);
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

		return str.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

}
