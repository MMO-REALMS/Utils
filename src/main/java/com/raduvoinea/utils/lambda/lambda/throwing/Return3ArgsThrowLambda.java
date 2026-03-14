package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface Return3ArgsThrowLambda<Result, FirstArgument, SecondArgument, ThirdArgument, Thrown extends Throwable> {
	Result run(FirstArgument firstArgument, SecondArgument secondArgument, ThirdArgument thirdArgument) throws Thrown;
}