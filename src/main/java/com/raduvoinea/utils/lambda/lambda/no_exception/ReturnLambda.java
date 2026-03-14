package com.raduvoinea.utils.lambda.lambda.no_exception;

@FunctionalInterface
public interface ReturnLambda<Result> {
	Result execute();
}