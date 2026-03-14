package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface Return3ArgsLambda<Result, FirstArgument, SecondArgument, ThirdArgument> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument, ThirdArgument thirdArgument);
}