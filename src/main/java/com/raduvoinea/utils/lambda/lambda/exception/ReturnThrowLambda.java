package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ReturnThrowLambda<Result, Thrown extends Throwable> {
	Result run() throws Thrown;
}