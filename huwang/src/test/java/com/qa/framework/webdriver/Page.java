package com.qa.framework.webdriver;


public interface Page extends UIAutomation{	
	public void sync(String key);
	public void sync(String key, int timeoutSec);	
	    
}
