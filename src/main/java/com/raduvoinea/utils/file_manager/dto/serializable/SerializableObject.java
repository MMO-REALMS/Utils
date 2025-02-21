package com.raduvoinea.utils.file_manager.dto.serializable;

public record SerializableObject<Object>(Class<Object> objectClass, Object object) {

}
