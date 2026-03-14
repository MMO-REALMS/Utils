package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface ArgLambdaExecutor<Argument> {
	void execute(Argument argument);
}