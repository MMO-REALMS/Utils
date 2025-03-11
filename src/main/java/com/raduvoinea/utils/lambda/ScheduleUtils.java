package com.raduvoinea.utils.lambda;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.lambda.LambdaExecutor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

@Getter
public class ScheduleUtils {

    public static CancelableTimeTask runTaskLater(@NotNull LambdaExecutor executor, long delay) {
        CancelableTimeTask task = new CancelableTimeTask() {
            @Override
            public void execute() {
                executor.execute();
            }
        };

        return runTaskLater(task, delay);
    }

    public static CancelableTimeTask runTaskLater(@NotNull CancelableTimeTask task, long delay) {
        Timer timer = new Timer();
        timer.schedule(task, delay);

        return task;
    }

    @Deprecated(forRemoval = true)
    public static @NotNull CancelableTimeTask runTaskTimer(@NotNull LambdaExecutor executor, long period) {
        return runTaskTimer(new CancelableTimeTask() {
            @Override
            public void execute() {
                executor.execute();
            }
        }, period);
    }

    @Deprecated(forRemoval = true)
    public static @NotNull CancelableTimeTask runTaskTimer(@NotNull CancelableTimeTask task, long period) {
        Timer timer = new Timer();
        timer.schedule(task, 0, period);

        return task;
    }

    public static @NotNull CancelableTimeTask runTaskLaterAsync(@NotNull LambdaExecutor executor, long delay) {
        CancelableTimeTask task = new CancelableTimeTask() {
            @Override
            public void execute() {
                executor.execute();
            }
        };

        Thread thread = Thread.ofVirtual().start(() -> runTaskLater(task, delay));
        task.setThread(thread);
        return task;
    }

    public static @NotNull CancelableTimeTask runTaskTimerAsync(@NotNull LambdaExecutor executor, Time period) {
        return runTaskTimerAsync(executor, period.toMilliseconds());
    }

    public static @NotNull CancelableTimeTask runTaskTimerAsync(@NotNull LambdaExecutor executor, long period) {
        CancelableTimeTask task = new CancelableTimeTask() {
            @Override
            public void execute() {
                executor.execute();
            }
        };

        Thread thread = Thread.ofVirtual().start(() -> runTaskTimer(task, period));
        task.setThread(thread);
        return task;
    }

    public static @NotNull CancelableTimeTask runTaskAsync(@NotNull LambdaExecutor executor) {
        CancelableTimeTask task = new CancelableTimeTask() {
            @Override
            public void execute() {
                executor.execute();
            }
        };

        Thread thread = Thread.ofVirtual().start(task::execute);
        task.setThread(thread);
        return task;
    }
}
