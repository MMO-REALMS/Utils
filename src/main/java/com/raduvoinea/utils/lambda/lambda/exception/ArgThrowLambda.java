package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ArgThrowLambda<Argument, Throwed extends Throwable> {
	void run(Argument argument) throws Throwed;
}