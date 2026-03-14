package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface ReturnThrowLambda<Result, Thrown extends Throwable> {
	Result run() throws Thrown;
}