package com.raduvoinea.file_manager.dto.interface_serialization;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomObject2 implements CustomInterface {

	public String data;
	public String otherData;

}
