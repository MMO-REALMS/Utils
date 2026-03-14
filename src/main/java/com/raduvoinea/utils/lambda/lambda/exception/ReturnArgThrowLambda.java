package com.raduvoinea.utils.lambda.lambda.exception;


@FunctionalInterface
public interface ReturnArgThrowLambda<Result, Argument, Thorwn extends Throwable> {
	Result run(Argument argument) throws Thorwn;
}