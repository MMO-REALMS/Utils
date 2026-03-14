package com.raduvoinea.utils.lambda.lambda.throwing;

@FunctionalInterface
public interface Exceptionambda<ThrownException extends Exception>{
	void run() throws ThrownException;
}
