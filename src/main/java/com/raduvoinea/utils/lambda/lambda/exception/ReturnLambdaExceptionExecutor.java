package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ReturnLambdaExceptionExecutor<Result, Exception extends Throwable> {
	Result execute() throws Exception;
}