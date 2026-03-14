package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface ArgsLambdaExceptionExecutor<FirstArgument, SecondArgument, Exception extends Throwable> {
	void execute(FirstArgument firstArgument, SecondArgument secondArgument)throws Exception;
}