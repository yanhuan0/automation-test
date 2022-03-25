package com.qatest.functional.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import com.qa.framework.ParameterConstants;
import com.qatest.functional.util.JavascriptHelper;


public class HWJSUtil {
	
	private static String FILE_SEPARATOR  = System.getProperty("file.separator");	
	

	public static String loadJS (WebDriver driver, String jsfile) {
	    String rootDir = System.getProperty ("root.dir");
	    String pkgPath = getPackagePath();
	    	
	    String path = null;	    	
	    	
	    path = rootDir + FILE_SEPARATOR + pkgPath + FILE_SEPARATOR + "javascript" + FILE_SEPARATOR;    	
	    System.out.println("====Loading JavaScript: " + path);
		String result = (String) JavascriptHelper.executeAsynchronousScript (driver, path + jsfile);     
	    return result;
	}

    
    public static String loadActionStatusJS (WebDriver driver) {

    	String result = new String();
        return result;
    }
    
   
      
    public static void printStatus(WebDriver driver) {
        StringBuilder buf = new StringBuilder();
        buf.append("var actionstatus = require('va-selenium/actionstatus');");
        buf.append("var oActionStatus = actionstatus.getInstance();");
        buf.append("return oActionStatus.getDebugStatus();");
        String sDebug = buf.toString();
        String result = (String) ((JavascriptExecutor)driver).executeScript(sDebug);
        System.out.println(result);
    }    
    
  
   
 
    public static void beginAction(WebDriver driver, String action) {
    	System.out.println("====beginAction: " + action);
    	
    	try {    	
    		Thread.sleep(1000);
	        StringBuilder buf = new StringBuilder();
	        buf.append("var actionstatus = require('va-selenium/actionstatus');");
	        buf.append("var oActionStatus = actionstatus.getInstance();");
	        buf.append("oActionStatus.beginAction();");
	        String sBeginActionScript = buf.toString();         
	        String result = (String)((JavascriptExecutor)driver).executeScript(sBeginActionScript);  
	        System.out.println(result);
    	}  
	    catch (Exception e) {
	        e.printStackTrace();
	    }
    	
    }
    
    

   
    
    public static void closeProductTour(WebDriver driver) {
        StringBuilder buf = new StringBuilder();
        buf.append("var bootstrap = require('obitech-application/bootstrap');");
        buf.append("var oApplication = bootstrap.getApplicationInstance();");
        buf.append("var bSuccess = false;");
        buf.append("if(oApplication){");
        buf.append("    var oHomepage = oApplication._getService('obitech-homepage/homepage');");
        buf.append("    if(oHomepage){");
        buf.append("       var oAppState = oHomepage.getAppState();");
        buf.append("       oAppState.set('obitech-homepage/homepage#producttourshown', true);");
        buf.append("       var oProductTour = oHomepage._getHostedComponent('obitech-academy/producttour.ProductTour');");
        buf.append("       if(oProductTour){");
        //buf.append("          oProductTour.destroyComponent();");
        buf.append("          oProductTour._koModel.exit();");
        buf.append("       }");
        buf.append("       bSuccess = true;");
        buf.append("    }");
        buf.append("}");
        buf.append("return bSuccess;");
        
        String sCloseTourScript = buf.toString();         
        boolean bResult = (boolean)((JavascriptExecutor)driver).executeScript(sCloseTourScript);  
        System.out.println(bResult);
    }
    
    public static void endAction(WebDriver driver, int waitTime) {
    	waitForActionToComplete(driver, waitTime);
    }
  
  
  
    public static void waitForActionToComplete(WebDriver driver, int waitTime) {
    	//Todo
 
	}
  
  
    //DO NOT USE FOR NOW
    public static void waitForReportReady(WebDriver driver) {
    	//Todo
	}
    
    
	  
    public static void waitForViewReady (WebDriver driver, int waitTime) {     
    	//Todo
    }       
  
    
  public static void sendTextToColumnExpressionEditor(WebDriver driver, String sExpression) {
      	//change username
      	String[] userinfo = ParameterConstants.USERNAME.split("/");
      	if (System.getProperty("biee.browser")!=null&&System.getProperty("biee.browser").equalsIgnoreCase("nw")) {
      		System.out.println("For DVD tests, user should be weblogic");
      		sExpression = sExpression.replaceAll("'" + userinfo[0] + "'", "'weblogic'");
      	}
      	else if (!userinfo[0].equals("weblogic"))
	    	sExpression = sExpression.replaceAll("'weblogic'", "'" + userinfo[0] + "'");
	
		StringBuilder buf = new StringBuilder();
		buf.append("var callback = arguments[arguments.length-1];");
		buf.append("var elCodeMirror = arguments[0];");
		buf.append("var sText = arguments[1];");
		buf.append("elCodeMirror.CodeMirror.setValue(sText);");
		String sBeginActionScript = buf.toString();

		Object result = ((JavascriptExecutor) driver).executeScript(sBeginActionScript,
				driver.findElement(By.className("CodeMirror")), sExpression);
    }
  /**
   * add expression in data flow
   * @param driver
   * @param sExpression
   * @author jatao
   */
    public static void sendTextToColumnExpressionEditorInDataFlow(WebDriver driver, String sExpression) {
      	//change username
      	//String[] userinfo = ParameterConstants.USERNAME.split("/");
  		StringBuilder buf = new StringBuilder();
  		buf.append("var callback = arguments[arguments.length-1];");
  		buf.append("var elCodeMirror = arguments[0];");
  		buf.append("var sText = arguments[1];");
  		buf.append("elCodeMirror.CodeMirror.setValue(sText);");
  		String sBeginActionScript = buf.toString();

  		Object result = ((JavascriptExecutor) driver).executeScript(sBeginActionScript,
  				driver.findElement(By.className("CodeMirror")), sExpression);
    }
	/**
	 * Set value for expression filter
	 * 
	 * @param driver
	 * @param sExpression
	 * @author elliao
	 */
	public static void sendTextToExpressionFilter(WebDriver driver, String sExpression) {
      	//change username
      	String[] userinfo = ParameterConstants.USERNAME.split("/");
	    if (!userinfo[0].equals("weblogic"))
	    	sExpression = sExpression.replaceAll("'weblogic'", "'" + userinfo[0] + "'");
	
		StringBuilder buf = new StringBuilder();
		buf.append("var callback = arguments[arguments.length-1];");
		buf.append("var elCodeMirror = arguments[0];");
		buf.append("var sText = arguments[1];");
		buf.append("elCodeMirror.CodeMirror.setValue(sText);");
		String sBeginActionScript = buf.toString();

		Object result = ((JavascriptExecutor) driver).executeScript(sBeginActionScript,
				driver.findElement(By.className("CodeMirror")), sExpression);
	}
  
  public static String exportAReport(WebDriver driver)  {
 	 StringBuilder buf = new StringBuilder();
      buf.append("var actionstatus  = require('va-selenium/actionstatus');\n");
      buf.append("return actionstatus.exportAReport(eExportType);\n");
      String sBeginActionScript = buf.toString();  
      System.out.println("sBeginActionScript ="+sBeginActionScript);
      Object result=((JavascriptExecutor)driver).executeScript(sBeginActionScript); 
      System.out.println("result ="+result);
      return result.toString();	
   }
    
    private static String getPackagePath() {    
  	    String pkgPath = HWJSUtil.class.getPackage().getName();
  	    
  	    pkgPath = pkgPath.replace(".", FILE_SEPARATOR);    	
        return pkgPath; 
    }   
  
    
    private enum ExportType {
        CSV(1);
        private int value;

        private ExportType(int value) {
           this.value = value;
        }

     };
}
