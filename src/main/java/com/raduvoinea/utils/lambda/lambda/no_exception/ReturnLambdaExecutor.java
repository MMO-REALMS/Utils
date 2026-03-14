package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface ReturnLambdaExecutor<Result> {
	Result execute();
}