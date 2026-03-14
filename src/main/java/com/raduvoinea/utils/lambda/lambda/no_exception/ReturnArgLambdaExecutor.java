package com.raduvoinea.utils.lambda.lambda.no_exception;


@FunctionalInterface
public interface ReturnArgLambdaExecutor<Result, Argument> {
	Result execute(Argument argument);
}