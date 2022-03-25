package com.qa.framework;

public class FrameworkException extends RuntimeException {
    public FrameworkException() {
        super();
    }
    public FrameworkException(String msg) {
        super(msg);
    }
	public FrameworkException(Exception e){
		super(e);
	}
}
