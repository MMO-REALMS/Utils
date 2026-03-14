package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface Return3ArgsLambdaExceptionExecutor<Result, FirstArgument, SecondArgument, ThirdArgument, Exception extends Throwable> {
	Result execute(FirstArgument firstArgument, SecondArgument secondArgument, ThirdArgument thirdArgument) throws Exception;
}