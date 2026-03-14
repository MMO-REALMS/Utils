package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface ThrowLambda<Thrown extends Throwable>{
	void run() throws Thrown;
}
