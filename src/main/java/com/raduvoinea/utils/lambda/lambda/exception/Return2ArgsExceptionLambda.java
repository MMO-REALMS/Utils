package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface Return2ArgsExceptionLambda<Result, FirstArgument, SecondArgument, Exception extends Throwable> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument) throws Exception;
}