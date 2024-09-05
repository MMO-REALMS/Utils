package com.voinearadu.logger;

import com.voinearadu.lambda.lambda.ArgLambdaExecutor;
import com.voinearadu.lambda.lambda.ReturnArgLambdaExecutor;
import com.voinearadu.logger.dto.ConsoleColor;
import com.voinearadu.logger.utils.StackTraceUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

public class Logger {

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static Level LOG_LEVEl = Level.TRACE;
    private static ReturnArgLambdaExecutor<String, String> PACKAGE_PARSER = packageName -> null;
    private static ArgLambdaExecutor<String> LOG_HANDLER = log -> {
    };

    public static void setLogLevel(Level logLevel) {
        Logger.LOG_LEVEl = logLevel;
    }

    @SuppressWarnings("unused")
    public static void setPackageParser(@NotNull ReturnArgLambdaExecutor<String, String> packageParser) {
        Logger.PACKAGE_PARSER = packageParser;
    }

    public static void setLogHandler(@NotNull ArgLambdaExecutor<String> logHandler) {
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

    public static void debug(Object object) {
        log(Level.DEBUG, object, ConsoleColor.WHITE, 1);
    }

    public static void good(Object object) {
        log(Level.INFO, object, ConsoleColor.GREEN, 1);
    }

    public static void log(Object object) {
        log(Level.INFO, object, 1);
    }

    public static void warn(Object object) {
        log(Level.WARN, object, ConsoleColor.YELLOW, 1);
    }

    public static void error(Object object) {
        log(Level.ERROR, object, ConsoleColor.RED, 1);
    }

    @SuppressWarnings("SameParameterValue")
    private static void log(Level level, Object object, int depth) {
        log(level, object, ConsoleColor.RESET, depth + 1);
    }

    private static void log(Level level, Object object, @NotNull ConsoleColor color, int depth) {
        if (level.toInt() < LOG_LEVEl.toInt()) {
            return;
        }

        Class<?> caller = getCallerClass(depth + 2);
        String id = parsePackage(caller.getPackageName());

        if (id == null || id.isEmpty()) {
            id = caller.getSimpleName() + ".java";
        }

        String log = switch (object) {
            case null -> "null";
            case Throwable throwable -> StackTraceUtils.toString(throwable);
            case StackTraceElement[] stackTraceElements -> StackTraceUtils.toString(stackTraceElements);
            default -> object.toString();
        };

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(id);
        logger.info(color + log + ConsoleColor.RESET);
        LOG_HANDLER.execute(log);
    }
}
