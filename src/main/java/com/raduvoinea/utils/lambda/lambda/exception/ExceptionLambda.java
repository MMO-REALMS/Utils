package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ExceptionLambda<Exception extends Throwable>{
	void execute() throws Exception;
}
