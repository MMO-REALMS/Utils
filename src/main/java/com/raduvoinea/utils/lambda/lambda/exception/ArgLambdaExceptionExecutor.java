package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ArgLambdaExceptionExecutor<Argument, Exception extends Throwable> {
	void execute(Argument argument) throws Exception;
}