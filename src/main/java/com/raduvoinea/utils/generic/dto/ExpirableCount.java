package com.raduvoinea.utils.generic.dto;

import com.raduvoinea.utils.generic.Time;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpirableCount {

	private final List<Long> datas = Collections.synchronizedList(new ArrayList<>());
	private final Time cooldownTime;
	private volatile long lastClear = 0;

	public ExpirableCount(Time cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void clearExpired() {
		long now = System.currentTimeMillis();
		long threshold = cooldownTime.toMilliseconds();
		if (now - lastClear < Math.max(threshold / 10, 100)) {
			return;
		}
		lastClear = now;
		datas.removeIf(data -> now - data > threshold);
	}

	public void add() {
		datas.add(System.currentTimeMillis());
	}

	public int size() {
		clearExpired();
		return datas.size();
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
