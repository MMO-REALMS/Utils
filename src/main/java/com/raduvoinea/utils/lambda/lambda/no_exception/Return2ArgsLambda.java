package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface Return2ArgsLambda<Result, FirstArgument, SecondArgument> {
	Result execute(FirstArgument firstArgument, SecondArgument secondArgument);
}