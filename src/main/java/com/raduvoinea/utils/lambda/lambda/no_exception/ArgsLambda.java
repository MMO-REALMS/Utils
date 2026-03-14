package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface ArgsLambda<FirstArgument, SecondArgument> {
	void run(FirstArgument firstArgument, SecondArgument secondArgument);
}