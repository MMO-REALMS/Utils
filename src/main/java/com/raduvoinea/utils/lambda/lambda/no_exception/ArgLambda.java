package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface ArgLambda<Argument> {
	void execute(Argument argument);
}