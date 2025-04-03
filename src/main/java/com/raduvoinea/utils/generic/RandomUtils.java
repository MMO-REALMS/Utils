package com.raduvoinea.utils.generic;

import com.raduvoinea.utils.generic.dto.IWeighted;
import com.raduvoinea.utils.generic.dto.Range;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RandomUtils {

	@SuppressWarnings("unused")
	public static int getRandom(Range range) {
		return getRandom(range.getMin(), range.getMax());
	}

	public static int getRandom(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	@SuppressWarnings("unused")
	public static @NotNull <T extends IWeighted> T getRandom(@NotNull List<T> items) {
		int totalWeight = items.stream().mapToInt(IWeighted::getWeight).sum();
		int random = getRandom(0, totalWeight);
		for (T item : items) {
			random -= item.getWeight();
			if (random <= 0) {
				return item;
			}
		}
		return items.getLast();
	}

}
