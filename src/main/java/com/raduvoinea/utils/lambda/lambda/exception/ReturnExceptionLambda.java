package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ReturnExceptionLambda<Result, Exception extends Throwable> {
	Result execute() throws Exception;
}