package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface ArgsLambda<FirstArgument, SecondArgument> {
	void execute(FirstArgument firstArgument, SecondArgument secondArgument);
}