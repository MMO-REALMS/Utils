package com.raduvoinea.utils.message_builder;

import com.raduvoinea.utils.logger.KvLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KVMessageBuilder extends GenericMessageBuilder<KvLog> {

	public KVMessageBuilder(KvLog base) {
		super(base);
	}

	protected KVMessageBuilder(KvLog base, List<Object> placeholders, List<Object> values) {
		super(base, placeholders, values);
	}

	@Override
	protected boolean equals(KvLog o1, KvLog o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	@Override
	protected String convertToString() {
		return parse().toString();
	}

	@Override
	protected KvLog parsePlaceholder(KvLog base, String placeholder, String value) {
		if (base == null) {
			return null;
		}

		String newType = base.getType().replace(placeholder, value);
		Map<String, Object> originalValues = base.getValues();
		HashMap<String, Object> resultValues = new HashMap<>(originalValues.size());

		for (Map.Entry<String, Object> entry : originalValues.entrySet()) {
			Object entryValue = entry.getValue();

			if (entryValue instanceof String stringValue) {
				entryValue = stringValue.replace(placeholder, value);
			}

			resultValues.put(entry.getKey(), entryValue);
		}

		return new KvLog(newType, resultValues);
	}

	@Override
	public GenericMessageBuilder<KvLog> clone() {
		return new KVMessageBuilder(
				base == null ? null : new KvLog(base),
				new ArrayList<>(placeholders),
				new ArrayList<>(values)
		);
	}

	public KVMessageBuilder parse(Map<?, ?> placeholders) {
		return (KVMessageBuilder) super.parse(placeholders);
	}

	public KVMessageBuilder parse(Object placeholder, Object value) {
		return (KVMessageBuilder) super.parse(placeholder, value);
	}
}
