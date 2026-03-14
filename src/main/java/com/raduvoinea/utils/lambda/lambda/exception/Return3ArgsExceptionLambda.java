package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface Return3ArgsExceptionLambda<Result, FirstArgument, SecondArgument, ThirdArgument, Exception extends Throwable> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument, ThirdArgument thirdArgument) throws Exception;
}