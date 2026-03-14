package com.raduvoinea.utils.lambda.lambda.no_exception;


@FunctionalInterface
public interface ReturnArgLambda<Result, Argument> {
	Result execute(Argument argument);
}