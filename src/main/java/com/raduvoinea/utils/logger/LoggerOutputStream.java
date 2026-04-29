package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class LoggerOutputStream extends OutputStream {
	private final ArgLambda<String> logger;
	private final StringBuilder buffer = new StringBuilder();
	private final boolean autoFlush;

	LoggerOutputStream(ArgLambda<String> logger, boolean autoFlush) {
		this.logger = logger;
		this.autoFlush = autoFlush;
	}

	@Override
	public void write(int b) {
		if (b == '\n') {
			flush();
		} else if (b != '\r') {
			buffer.append((char) b);
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public void write(byte @NotNull [] b, int off, int len) {
		int start = off;
		for (int i = off; i < off + len; i++) {
			byte ch = b[i];
			if (ch == '\n') {
				if (i > start) {
					buffer.append(new String(b, start, i - start, java.nio.charset.StandardCharsets.UTF_8));
				}
				flush();
				start = i + 1;
			} else if (ch != '\r') {
				// continue
			} else {
				// skip \r - need to append segment before it
				if (i > start) {
					buffer.append(new String(b, start, i - start, java.nio.charset.StandardCharsets.UTF_8));
				}
				start = i + 1;
			}
		}
		if (start < off + len) {
			buffer.append(new String(b, start, off + len - start, java.nio.charset.StandardCharsets.UTF_8));
		}
	}

	@Override
	public void flush() {
		if (!buffer.isEmpty()) {
			logger.run(buffer.toString());
			buffer.setLength(0);
		}
		if (autoFlush) {
			try {
				super.flush();
			} catch (IOException ignored) {
			}
		}
	}
}