package com.raduvoinea.logger.dto;

import com.raduvoinea.utils.logger.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestLoggerHandler implements Logger.Handler {

    private final List<String> buffer;

    public TestLoggerHandler() {
        this.buffer = new ArrayList<>();
    }

    @Override
    public void info(@NotNull String log) {
        buffer.add(log);
    }

    @Override
    public void error(@NotNull String log) {
        buffer.add(log);
    }

    @Override
    public void warn(@NotNull String log) {
        buffer.add(log);
    }

    @Override
    public void debug(@NotNull String log) {
        buffer.add(log);
    }
}
