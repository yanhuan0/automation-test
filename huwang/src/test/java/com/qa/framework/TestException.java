package com.qa.framework;

public class TestException extends FrameworkException{
	public TestException(){
		super();
	}
	public TestException(Exception e){
		super(e);
	}
	public TestException(String msg){
		super(msg);
	}
}
