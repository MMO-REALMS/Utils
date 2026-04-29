package com.raduvoinea.logger.dto;

import com.raduvoinea.utils.logger.annotations.LogAsJson;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@LogAsJson
@AllArgsConstructor
public class PrintAsJsonTestObject {

	private String a;
	private int b;
	private Map<String, Object> c;
	private List<String> d;

}
