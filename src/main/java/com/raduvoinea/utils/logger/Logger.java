package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.lambda.lambda.ArgLambdaExecutor;
import com.raduvoinea.utils.lambda.lambda.ReturnArgLambdaExecutor;
import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.dto.Level;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Logger {

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static @Setter Level logLevel;
    private static @Setter ReturnArgLambdaExecutor<String, String> packageParser;
    private static @Setter @Getter Handler logHandler;

    static {
        reset();
    }

    public static void reset() {
        logLevel = Level.TRACE;
        packageParser = packageName -> null;
        logHandler = Handler.defaultHandler();
    }

    private static @NotNull Class<?> getCallerClass() {
        Class<?> clazz = STACK_WALKER.walk(stack -> stack.map(StackWalker.StackFrame::getDeclaringClass)
                .filter(c -> !c.equals(Logger.class))
                .findFirst()
        ).orElse(null);

        if (clazz == null) {
            System.out.println("<!> Failed to get caller class <!>");
            clazz = Logger.class;
        }

        return clazz;
    }

    public static void debug(@NotNull Object object) {
        log(Level.DEBUG, object, ConsoleColor.BRIGHT_BLACK, logHandler::debug);
    }

    public static void log(@Nullable Object object) {
        info(object);
    }

    public static void info(@Nullable Object object) {
        log(Level.INFO, object, ConsoleColor.RESET, logHandler::info);
    }

    public static void good(@Nullable Object object) {
        log(Level.INFO, object, ConsoleColor.DARK_GREEN, logHandler::info);
    }

    public static void warn(@NotNull Object object) {
        log(Level.WARN, object, ConsoleColor.DARK_YELLOW, logHandler::warn);
    }

    public static void error(@NotNull Object object) {
        log(Level.ERROR, object, ConsoleColor.DARK_RED, logHandler::error);
    }

    public static void goodOrWarn(Object object, boolean goodCheck) {
        if (goodCheck) {
            good(object);
        } else {
            warn(object);
        }
    }


    private static void genericHandle(@Nullable String log, ArgLambdaExecutor<String> logger) {
        if (log != null) {
            logger.execute(log);
        }
    }

    private static @Nullable void log(@NotNull Level level, @Nullable Object object, @NotNull ConsoleColor color, ArgLambdaExecutor<String> logger) {
        if (level.getLevel() < logLevel.getLevel()) {
            return;
        }

        Class<?> caller = getCallerClass();
        String id = packageParser.execute(caller.getPackageName());

        if (id == null || id.isEmpty()) {
            id = caller.getSimpleName();
        }

        id = "[" + id + "] ";


        String log = switch (object) {
            case null -> "null";
            case MessageBuilder messageBuilder -> messageBuilder.parse();
            case Throwable throwable -> StackTraceUtils.toString(throwable);
            case StackTraceElement[] stackTraceElements -> StackTraceUtils.toString(stackTraceElements);
            default -> object.toString();
        };

        log = color + id + String.join("\n" + color, log.split("\n")) + ConsoleColor.RESET;
        logger.execute(log);
    }

    public static void printStackTrace() {
        try {
            throw new Exception();
        } catch (Exception e) {
            debug(StackTraceUtils.toString(e));
        }
    }

    public interface Handler {
        void info(@NotNull String log);

        void error(@NotNull String log);

        void warn(@NotNull String log);

        void debug(@NotNull String log);

        static Handler defaultHandler() {
            return new Handler() {
                @Override
                public void info(@NotNull String log) {
                    System.out.println(log);
                }

                @Override
                public void error(@NotNull String log) {
                    System.err.println(log);
                }

                @Override
                public void warn(@NotNull String log) {
                    System.out.println(log);
                }

                @Override
                public void debug(@NotNull String log) {
                    System.out.println(log);
                }
            };
        }
    }
}
