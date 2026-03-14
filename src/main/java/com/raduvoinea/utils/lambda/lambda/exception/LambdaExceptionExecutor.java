package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface LambdaExceptionExecutor <Exception extends Throwable>{
	void execute() throws Exception;
}
