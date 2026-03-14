package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ThrowLambda<Thrown extends Throwable>{
	void run() throws Thrown;
}
