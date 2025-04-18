package com.raduvoinea.utils.generic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pair2<First, Second> {

	private final First first;
	private final Second second;

	public static <First, Second> Pair2<First, Second> of(First first, Second second) {
		return new Pair2<>(first, second);
	}
}
