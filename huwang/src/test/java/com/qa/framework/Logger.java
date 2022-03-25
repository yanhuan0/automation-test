package com.qa.framework;

import java.awt.image.BufferedImage;
import java.io.File;

public interface Logger {
	
	public File getResultDir();
	public File getBaseDir();
	public File getOutputDir();
	public void setCurrentTestName(String testName);
	public void infoMethod(Object ... args);
	public void debugMethod(Object ... args);
	public void logMethod(String methodName, int level, Object ... args);

	public String debugReturn(String ret);
	public boolean debugReturn(boolean ret);
	public int debugReturn(int ret);

	public void debug(String msg);
	public void info(String msg);
	public void warn(String msg);
	public void error(String msg);
	public void error(Exception e);
	public void sideBySide(String text1, String text2);
	public void sideBySide(BufferedImage img1, BufferedImage img2);
	
	
	public void beginTest();
	public void endTest();
	public void beginSuite();
	public void endSuite();

	public void startPerfTimer(String description);
	public void stopPerfTimer();
	
	public void logScreenShot(File imgFile);

	public boolean saveResult(String name, String value);
	public boolean saveResult(String name, String result, String benchmark);
	
    public void sleep(int milliSec, boolean withLog);

}
	
