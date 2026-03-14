package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ReturnExceptionLambda<Result, Exception extends Throwable> {
	Result run() throws Exception;
}