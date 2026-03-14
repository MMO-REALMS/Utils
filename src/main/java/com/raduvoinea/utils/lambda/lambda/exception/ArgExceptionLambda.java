package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ArgExceptionLambda<Argument, Exception extends Throwable> {
	void execute(Argument argument) throws Exception;
}