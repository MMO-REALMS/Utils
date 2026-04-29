package com.raduvoinea.utils.generic.dto;

import com.raduvoinea.utils.generic.Time;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ExpirableList<Data> {

	private final List<ExpirableData<Data>> datas = Collections.synchronizedList(new ArrayList<>());
	private final Time cooldownTime;
	private volatile long lastClear = 0;

	public ExpirableList(Time cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void clearExpired() {
		long now = System.currentTimeMillis();
		long threshold = cooldownTime.toMilliseconds();
		if (now - lastClear < Math.max(threshold / 10, 100)) {
			return; // Don't clear too frequently
		}
		lastClear = now;
		datas.removeIf(data -> now - data.timestamp > threshold);
	}

	public void add(Data data) {
		datas.add(new ExpirableData<>(data));
	}

	public List<Data> getData() {
		clearExpired();
		List<Data> result = new ArrayList<>(datas.size());
		for (ExpirableData<Data> expirableData : datas) {
			result.add(expirableData.data);
		}
		return result;
	}

	public void removeIf(Predicate<? super Data> filter) {
		datas.removeIf(data -> filter.test(data.data));
	}

	public void remove(Data data) {
		datas.removeIf(expirableData -> expirableData.data.equals(data));
	}

	public boolean contains(Data data) {
		clearExpired();
		for (ExpirableData<Data> expirableData : datas) {
			if (expirableData.data.equals(data)) {
				return true;
			}
		}
		return false;
	}

	@Getter
	public static class ExpirableData<Data> {
		private final Data data;
		private final long timestamp;

		public ExpirableData(Data data) {
			this.data = data;
			this.timestamp = System.currentTimeMillis();
		}
	}

}
