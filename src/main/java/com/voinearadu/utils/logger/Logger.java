package com.voinearadu.utils.logger;

import com.voinearadu.utils.lambda.lambda.ReturnArgLambdaExecutor;
import com.voinearadu.utils.logger.dto.ConsoleColor;
import com.voinearadu.utils.logger.dto.Level;
import com.voinearadu.utils.logger.utils.StackTraceUtils;
import com.voinearadu.utils.message_builder.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Logger {

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static Level LOG_LEVEl = Level.TRACE;
    private static ReturnArgLambdaExecutor<String, String> PACKAGE_PARSER = packageName -> null;
    private static Handler LOG_HANDLER = System.out::println;

    public static void setLogLevel(Level logLevel) {
        Logger.LOG_LEVEl = logLevel;
    }

    @SuppressWarnings("unused")
    public static void setPackageParser(@NotNull ReturnArgLambdaExecutor<String, String> packageParser) {
        Logger.PACKAGE_PARSER = packageParser;
    }

    public static void setLogHandler(@NotNull Handler logHandler) {
        Logger.LOG_HANDLER = logHandler;
    }

    private static @Nullable String parsePackage(String packageName) {
        return PACKAGE_PARSER.execute(packageName);
    }

    private static @NotNull Class<?> getCallerClass(int steps) {
        Class<?> clazz = STACK_WALKER.walk(stack -> stack.map(StackWalker.StackFrame::getDeclaringClass).skip(steps).findFirst()).orElse(null);

        if (clazz == null) {
            System.out.println("<!> Failed to get caller class <!>");
            clazz = Logger.class;
        }

        return clazz;
    }

    public static void debug() {
        log(Level.DEBUG, "", ConsoleColor.BRIGHT_BLACK, 1);
    }

    public static void debug(Object object) {
        log(Level.DEBUG, object, ConsoleColor.BRIGHT_BLACK, 1);
    }

    @SuppressWarnings("unused")
    public static void debug(Object object, ConsoleColor color) {
        log(Level.DEBUG, object, color, 1);
    }

    public static void goodOrWarn(Object object, boolean goodCheck) {
        if (goodCheck) {
            good(object);
        } else {
            warn(object);
        }
    }

    public static void good(Object object) {
        log(Level.INFO, object, ConsoleColor.DARK_GREEN, 1);
    }

    public static void log() {
        log(Level.INFO, "", 1);
    }

    public static void log(Object object) {
        log(Level.INFO, object, 1);
    }

    public static void info(Object object) {
        log(Level.INFO, object, ConsoleColor.DARK_WHITE, 1);
    }

    public static void warn(Object object) {
        log(Level.WARN, object, ConsoleColor.DARK_YELLOW, 1);
    }

    public static void error(Object object) {
        log(Level.ERROR, object, ConsoleColor.DARK_RED, 1);
    }

    @SuppressWarnings("SameParameterValue")
    private static void log(Level level, Object object, int depth) {
        log(level, object, ConsoleColor.RESET, depth + 1);
    }

    private static void log(Level level, Object object, @NotNull ConsoleColor color, int depth) {
        if (level.getLevel() < LOG_LEVEl.getLevel()) {
            return;
        }

        Class<?> caller = getCallerClass(depth + 2);
        String id = parsePackage(caller.getPackageName());

        if (id == null || id.isEmpty()) {
            id = caller.getSimpleName() + ".java";
        }

        String log = switch (object) {
            case null -> "null";
            case MessageBuilder messageBuilder -> messageBuilder.parse();
            case Throwable throwable -> StackTraceUtils.toString(throwable);
            case StackTraceElement[] stackTraceElements -> StackTraceUtils.toString(stackTraceElements);
            default -> object.toString();
        };

        log = color + String.join("\n" + color, log.split("\n")) + ConsoleColor.RESET;

        LOG_HANDLER.log(log, id);
    }

    public static void printStackTrace() {
        try {
            throw new Exception();
        } catch (Exception e) {
            debug(StackTraceUtils.toString(e));
        }
    }

    public interface Handler {
        @SuppressWarnings("unused")
        default void log(String log, String id) {
            log(log);
        }

        void log(String log);
    }
}
