package com.raduvoinea.utils.lambda.lambda.throwing;


@FunctionalInterface
public interface ReturnArgThrowLambda<Result, Argument, Thorwn extends Throwable> {
	Result run(Argument argument) throws Thorwn;
}