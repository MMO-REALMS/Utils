package com.raduvoinea.utils.lambda.lambda.exception;


@FunctionalInterface
public interface ReturnArgExceptionLambda<Result, Argument, Exception extends Throwable> {
	Result execute(Argument argument) throws Exception;
}