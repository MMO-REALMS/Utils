package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface Return2ArgsLambdaExecutor<Result, FirstArgument, SecondArgument> {
	Result execute(FirstArgument firstArgument, SecondArgument secondArgument);
}