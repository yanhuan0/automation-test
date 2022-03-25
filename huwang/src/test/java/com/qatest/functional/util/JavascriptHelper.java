package com.qatest.functional.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class JavascriptHelper {
	
	
	public static Object executeAsynchronousScript (WebDriver driver, String scriptFileName, Object... args) {
        
        try {
            System.out.println("====Begin: Load javascript file " + scriptFileName);          
            BufferedReader reader = new BufferedReader(new FileReader(scriptFileName));
            StringBuilder buf = new StringBuilder();
            String line = new String();
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append("\n");
            }
            reader.close();        
            driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
            Object result = ((JavascriptExecutor)driver).executeAsyncScript(buf.toString(), args);
            System.out.println("====End: Load javascript file " + scriptFileName);
            return result;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }  
}

