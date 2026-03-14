package com.raduvoinea.utils.lambda.lambda.no_exception;


@FunctionalInterface
public interface ReturnArgLambda<Result, Argument> {
	Result run(Argument argument);
}