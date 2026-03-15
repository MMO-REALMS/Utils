package com.raduvoinea.logger.dto;

import com.raduvoinea.utils.logger.ChildLoggerInstance;
import lombok.Getter;

@Getter
public class ChildTestLogger extends ChildLoggerInstance {

	public ChildTestLogger() {
		this(new TestLogger());
	}

	public ChildTestLogger(TestLogger testLogger) {
		super(testLogger);
	}

}
