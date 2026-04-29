package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.generic.SerializationUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class KvLog {

	private final String type;
	private final Map<String, Object> values;

	public KvLog(String type) {
		this(type, new HashMap<>(10));
	}

	public KvLog(KvLog source) {
		this.type = source.type;
		this.values = new HashMap<>(source.values);
	}

	public KvLog(String type, Map<String, Object> values) {
		this.type = type;
		this.values = values;
	}

	public KvLog add(String key, Object value) {
		values.put(key, value);
		return this;
	}

	public void commit() {
		Logger.log(this);
	}

	@Override
	public String toString() {
		int estimatedSize = (type == null ? 4 : type.length()) + values.size() * 24 + 32;
		StringBuilder builder = new StringBuilder(estimatedSize);

		builder.append("{");
		builder.append("\"__type\": \"").append(SerializationUtils.escapeString(type)).append("\"");

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			builder.append(", ");
			builder.append("\"").append(SerializationUtils.escapeString(entry.getKey())).append("\": ");
			builder.append(SerializationUtils.serializeObject(entry.getValue(), true));
		}

		builder.append("}");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KvLog other)) {
			return false;
		}

		return Objects.equals(type, other.type) &&
				Objects.equals(values, other.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, values);
	}
}
