package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface Return2ArgsLambdaExceptionExecutor<Result, FirstArgument, SecondArgument, Exception extends Throwable> {
	Result execute(FirstArgument firstArgument, SecondArgument secondArgument) throws Exception;
}