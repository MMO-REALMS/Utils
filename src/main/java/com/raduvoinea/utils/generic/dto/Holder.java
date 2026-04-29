package com.raduvoinea.utils.generic.dto;

public class Holder<T> {
	private volatile T value;

	private Holder(T value) {
		this.value = value;
	}

	public static <T> Holder<T> of(T value) {
		return new Holder<>(value);
	}

	public static <T> Holder<T> empty() {
		return new Holder<>(null);
	}

	public T value() {
		return value;
	}

	public Holder<T> set(T newValue) {
		this.value = newValue;
		return this;
	}

	public boolean isEmpty() {
		return value == null;
	}

}
