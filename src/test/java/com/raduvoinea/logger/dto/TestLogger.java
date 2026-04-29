package com.raduvoinea.logger.dto;

import com.raduvoinea.utils.logger.LoggerInstance;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestLogger extends LoggerInstance {

	private final List<String> buffer;
	private final boolean parsePackage;
	private final boolean forwardToDefault;

	public TestLogger(boolean forwardToDefault) {
		this(forwardToDefault, false);
	}

	public TestLogger(boolean forwardToDefault, boolean parsePackage) {
		this.buffer = new ArrayList<>();
		this.forwardToDefault = forwardToDefault;
		this.parsePackage = parsePackage;
	}

	@Override
	protected void handleInfo(@NotNull String log) {
		this.buffer.add(log);

		if (this.forwardToDefault) {
			LoggerInstance.DEFAULT.info(log);
		}
	}

	@Override
	protected void handleError(@NotNull String log) {
		this.buffer.add(log);

		if (this.forwardToDefault) {
			LoggerInstance.DEFAULT.error(log);
		}
	}

	@Override
	protected void handleWarn(@NotNull String log) {
		this.buffer.add(log);

		if (this.forwardToDefault) {
			LoggerInstance.DEFAULT.warn(log);
		}
	}

	@Override
	protected void handleDebug(@NotNull String log) {
		this.buffer.add(log);

		if (this.forwardToDefault) {
			LoggerInstance.DEFAULT.debug(log);
		}
	}

	@Override
	protected @Nullable String parsePackage(String packageName) {
		if (!this.parsePackage) {
			return null;
		}

		return "LoggerTestPackage";
	}
}
