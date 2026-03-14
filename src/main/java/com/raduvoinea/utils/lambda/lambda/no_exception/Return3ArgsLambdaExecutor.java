package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface Return3ArgsLambdaExecutor<Result, FirstArgument, SecondArgument, ThirdArgument> {
	Result execute(FirstArgument firstArgument, SecondArgument secondArgument, ThirdArgument thirdArgument);
}