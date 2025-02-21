package com.raduvoinea.lambda;

import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.*;
import com.raduvoinea.utils.lambda.lambda.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LambdaRunnableExecutorTest {

    @Test
    public void testLambdaExecutors() {
        ArgLambdaExecutor<List<String>> addEmpty = (list) -> list.add("empty");
        ArgsLambdaExecutor<List<String>, String> add = List::add;

        ReturnLambdaExecutor<String> getHelloWorld = () -> "Hello World";
        ReturnArgLambdaExecutor<String, String> getHello = (arg) -> "Hello " + arg;
        ReturnArgsLambdaExecutor<String, String, String> concatenateStrings = (arg1, arg2) -> arg1 + arg2;

        List<String> list = new ArrayList<>();

        addEmpty.execute(list);
        //noinspection ConstantValue
        assertEquals(1, list.size());
        assertEquals("empty", list.getFirst());

        add.execute(list, "test");
        assertEquals(2, list.size());
        assertEquals("test", list.get(1));

        assertEquals("Hello World", getHelloWorld.execute());
        assertEquals("Hello test", getHello.execute("test"));
        assertEquals("testtest", concatenateStrings.execute("test", "test"));
    }

    @SneakyThrows
    @Test
    public void testRunTaskLater() {
        AtomicBoolean executed = new AtomicBoolean(false);

        ScheduleUtils.runTaskLater(() -> executed.set(true), 1000);

        Thread.sleep(1500);

        assertTrue(executed.get());
    }

    @SneakyThrows
    @Test
    public void testRunTaskTimer() {
        AtomicInteger executed = new AtomicInteger(0);

        ScheduleUtils.runTaskTimer(new CancelableTimeTask() {
            @Override
            public void execute() {
                executed.getAndAdd(1);

                if (executed.get() == 5) {
                    this.cancel();
                }
            }
        }, 1000);

        Thread.sleep(7000);

        assertEquals(5, executed.get());
    }

}
