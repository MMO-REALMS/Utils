package com.raduvoinea.utils.lambda.lambda.exception;

@FunctionalInterface
public interface Return2ArgsThrowLambda<Result, FirstArgument, SecondArgument, Thrown extends Throwable> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument) throws Thrown;
}