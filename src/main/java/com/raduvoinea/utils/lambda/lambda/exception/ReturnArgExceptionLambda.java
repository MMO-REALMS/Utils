package com.raduvoinea.utils.lambda.lambda.exception;


@FunctionalInterface
public interface ReturnArgExceptionLambda<Result, Argument, Exception extends Throwable> {
	Result run(Argument argument) throws Exception;
}