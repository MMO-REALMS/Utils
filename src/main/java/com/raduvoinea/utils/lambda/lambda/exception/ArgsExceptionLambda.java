package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ArgsExceptionLambda<FirstArgument, SecondArgument, Exception extends Throwable> {
	void run(FirstArgument firstArgument, SecondArgument secondArgument)throws Exception;
}