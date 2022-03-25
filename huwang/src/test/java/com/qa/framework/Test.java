package com.qa.framework;

import java.util.Map;
import java.io.File;

public interface Test {

	public String getName() ;

	public String getBaseDir() ;

	public String getResultDir() ;

	public String getOutputDir() ;

	public String getTestDir() ;
	
	public Logger getLogger() ;

	public void logInfo(String msg) ;

	public void logError(String msg) ;

	public void logDebug(String msg) ;

	public void logWarn(String msg) ;

	public void sleep(int timeMS) ;

	public void logError(RuntimeException e) ;

	public String getParam(String key) ;

	public Map<String, String> getParamPairs(String key);

	public String getParam(String methodName, String key) ;

	public Map<String, String> getParamPairs(String methodName, String key);

	/**
     * Save test result as file with file name: testId_result.txt or testId_result.png
     * If in base mode, result is saved in base folder.
     * If in result mode, result is saved in result folder, then compared with 
     * base file. The test will fail when the result file does not match base
     * file.
     * @param name to identify the result file. the result file will be prefixed 
     * with test case id.
     * @param result if the name parameter ends with '.png', then this result parameter 
     * is the key or locator of the web object to capture bitmap; if the name parameter 
     * does not ends with '.png', the result parameter is the actual text result
     * @throws FrameworkException
     */
    public void saveResult(String name,String result);
    public void saveResult(String name,File file);
}
