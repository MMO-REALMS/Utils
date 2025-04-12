package com.raduvoinea.utils.file_manager.dto.serializable;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SerializableObject<Object> {

	private final Class<Object> objectClass;
	private final Object object;

	public SerializableObject(Object object) {
		this.objectClass = object == null ? null : (Class<Object>) object.getClass();
		this.object = object;
	}
}