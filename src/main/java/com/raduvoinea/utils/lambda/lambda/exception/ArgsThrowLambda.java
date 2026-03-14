package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ArgsThrowLambda<FirstArgument, SecondArgument, Throwed extends Throwable> {
	void run(FirstArgument firstArgument, SecondArgument secondArgument)throws Throwed;
}