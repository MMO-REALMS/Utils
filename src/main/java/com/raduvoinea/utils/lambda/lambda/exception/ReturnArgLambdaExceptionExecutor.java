package com.raduvoinea.utils.lambda.lambda.exception;


@FunctionalInterface
public interface ReturnArgLambdaExceptionExecutor<Result, Argument, Exception extends Throwable> {
	Result execute(Argument argument) throws Exception;
}