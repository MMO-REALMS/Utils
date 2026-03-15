package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.logger.dto.Level;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class ChildLoggerInstance extends LoggerInstance {

	private final LoggerInstance parent;

	public ChildLoggerInstance(LoggerInstance parent) {
		super(
			parent.getLogLevel(),
			parent.isPrintSourceClass()
		);
		this.parent = parent;
	}

	protected void handleInfo(@NotNull String log) {
		this.parent.handleInfo(log);
	}

	protected void handleError(@NotNull String log) {
		this.parent.handleError(log);
	}

	protected void handleWarn(@NotNull String log) {
		this.parent.handleWarn(log);
	}

	protected void handleDebug(@NotNull String log) {
		this.parent.handleDebug(log);
	}

	protected String finalProcessor(String log) {
		return this.parent.finalProcessor(log);
	}

	protected @Nullable String parsePackage(String packageName) {
		return this.parent.parsePackage(packageName);
	}

	@Override
	public void setPrintSourceClass(boolean printSourceClass) {
		super.setPrintSourceClass(printSourceClass);
		this.parent.setPrintSourceClass(printSourceClass);
	}

	@Override
	public void setLogLevel(Level logLevel) {
		super.setLogLevel(logLevel);
		this.parent.setLogLevel(logLevel);
	}
}
