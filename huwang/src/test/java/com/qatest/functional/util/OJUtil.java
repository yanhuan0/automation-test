package com.qatest.functional.util;

import java.util.Collections;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.qa.framework.webdriver.UIAutomation;
import com.qatest.functional.util.QAConstants;

public class OJUtil {
	 private int _smallWait = QAConstants.SMALL_TIME;
	 private UIAutomation m_auto;
		
	 public OJUtil(UIAutomation auto) {
			m_auto = auto;
	}
	    
	 public void openContextMenu (WebDriver driver, WebElement elem,  String menu){
		 Actions oAction = new Actions(driver);        	
		 oAction.moveToElement(elem);        	
		 oAction.contextClick(elem).build().perform();
		 m_auto.sleep(_smallWait);
	         
		 if (m_auto.waitForObject("elemVAContextMenuItem" + "(" + menu + ")")) {
			 driver.findElement(By.linkText(menu)).click();            	
		 }
	 }
	 
	 public void openContextMenu_2 (WebDriver driver, WebElement elem, WebElement parentElem, String menu){
		 try {
			 elem.click();
		 } catch(Exception ex) {
			 elem.click();
		 }
		 m_auto.sleep(QAConstants.MINI_TIME);
		 Actions oAction = new Actions(driver);        	
	     oAction.moveToElement(parentElem);        	
	     oAction.contextClick(parentElem).build().perform();
	     m_auto.sleep(QAConstants.MINI_TIME);
	         
	     if (m_auto.waitForObject("elemVAContextMenuItem" + "(" + menu + ")")) {
	         driver.findElement(By.linkText(menu)).click();            	
	     }
	 }
	 
	 public void openContextHierarchyMenu (WebDriver driver, WebElement elem,  String options){     
		 String[] option = options.split("/");
	     Actions oAction = new Actions(driver);        	
	     oAction.moveToElement(elem); 
	     oAction.contextClick(elem).build().perform();
	     m_auto.sleep(_smallWait);
	         
		int len = option.length;
		if (len > 1) {
			for (int i = 0; i < len - 1; i++) {
				m_auto.mouseOver("elemVAContextItems" + "(" + option[i] + ")");
			}
		}
		if (m_auto.waitForObject("elemVAContextItems" + "(" + option[len - 1] + ")")) {
			m_auto.setObject("elemVAContextItems" + "(" + option[len - 1] + ")");
		} else {
			m_auto.logError("The action '" + option + "' does not exist.");
		}			
	}	 

	public void openContextHierarchyMenu_2 (WebDriver driver, WebElement elem, String options){
		String[] option = options.split("/");
	    Actions oAction = new Actions(driver);
	    oAction.moveToElement(elem);
	    oAction.contextClick(elem).build().perform();
	         
		int len = option.length;
		if (len > 1) {
			for (int i = 0; i < len - 1; i++) {
			   WebElement parentOption= m_auto.getObject("elemVAContextItems" + "(" + option[i] + ")");
            oAction.moveToElement(parentOption).perform();
            m_auto.sleep(QAConstants.MINI_TIME/2);
			}
		}
		if (m_auto.waitForObject("elemVAContextItems" + "(" + option[len - 1] + ")")) {
			m_auto.mouseOver("elemVAContextItems" + "(" + option[len - 1] + ")");
			m_auto.setObject("elemVAContextItems" + "(" + option[len - 1] + ")");
		} else {
		   if (len > 1) {
	         for (int i = 0; i < len - 1; i++) {
	            m_auto.waitForObject("elemVAContextItems" + "(" + option[i] + ")");
	            m_auto.mouseOver("elemVAContextItems" + "(" + option[i] + ")");
	            m_auto.setObject("elemVAContextItems" + "(" + option[i] + ")");
	         }
	         if (m_auto.waitForObject("elemVAContextItems" + "(" + option[len - 1] + ")")) {
	            m_auto.mouseOver("elemVAContextItems" + "(" + option[len - 1] + ")");
	            m_auto.setObject("elemVAContextItems" + "(" + option[len - 1] + ")");
	         }
		   }
		}			
	}	 
	    
	    
	    
	  public WebElement getJETWebElement(WebDriver driver, String locator, int waitTime){
		    WebElement elem=null;
			locator=locator.trim();
			
			By by = ByJet.locator(locator);
			elem = findElementBy (driver, by, waitTime);	
			if (elem instanceof WebElement){
			    return elem;			        
			}		
			return null;
		}
	    
	    
	    public WebElement findElementBy (WebDriver driver, By by, int timeout){
	    	WebElement elem = null;
	    	int i = 0;
	    	WebDriverWait wait = new WebDriverWait(driver, 10);
	    	
	    	try{
	    	    elem = wait.until(ExpectedConditions.elementToBeClickable(by));    	   
	    	    timeout = timeout/1000;    	
	    	    if (driver.findElement(by) != Collections.emptyList()){
	     	        elem = driver.findElement(by);  
	    	    }
	    	 }
	     	catch (Exception e) {
	     		System.out.println("===========no found : " + e.getMessage() );
	 		}	     
	        return elem;
	    }        
	
	   public String escapeQueryLocator(String locator, String pattern){
	    	if (pattern.equals(":"))
	    		return locator.replace(":", "\\\\:");
	    	else if (pattern.equals("!"))
	    		return locator.replace("!", "\\\\!");
	    	else 
	    		return null;         
	    }  
	    
	    //Works for Bar and Pie Chart
       public WebElement getOJChartItemObject(WebDriver driver,String OjChartID, String seriesIndex, String itemIndex){
    	   System.out.println("===========id: " + OjChartID);
		   //construct the correct string
		   String loc1 = escapeQueryLocator(OjChartID, ":");
		   
		   String loc2 = "{\"element\":\"" + "#" + loc1 + "\", \"subId\" :\"oj-chart-item\", \"seriesIndex\": " + seriesIndex+","+ " \"itemIndex\""+ ": " + itemIndex+"}";
		   System.out.println("===========set up subId: " + loc2);
		   
		   String loc3 = escapeQueryLocator(loc2, "!");
		   System.out.println("===========escapeQueryLocator for ! " + loc3);
		   
		   //Locate the particular Bar specified by seriesIndex and itemIndex
		   WebElement e1 = getJETWebElement(driver, loc3, 10000);
    	   
		   return e1;
       }
       
	    //Works for Table and Pivot Table
       public WebElement getOJTableCellObject(WebDriver driver,String OjTableID, String rowIndex, String columnIndex){
    	   System.out.println("=========== Element id: " + OjTableID);
    	   
    	   //Construct the correct JET locator		   
		   String loc_JET = "{\"element\":\"" + "#" + OjTableID + "\", \"subId\" :\"oj-datagrid-cell\", \"rowIndex\": " + rowIndex +","+ " \"columnIndex\""+ ": " + columnIndex +"}";
		   System.out.println("=========== Set up SubId: " + loc_JET);
		   
		   //Locate the particular Table or Pivot specified by rowIndex and columnIndex
		   WebElement element = getJETWebElement(driver, loc_JET, 10000);
    	   
		   return element;
       }
       
       // Works for List view
       public WebElement getOJLegendItemObject(WebDriver driver,String OjChartID, String sectionIndexPath, String itemIndex){
    	   System.out.println("===========id: " + OjChartID);
		   String loc1 = escapeQueryLocator(OjChartID, ":");		   
		   String loc2 = "{\"element\":\"" + "#" + loc1 + "\", \"subId\" :\"oj-legend-item\", \"sectionIndexPath\": " + sectionIndexPath+","+ " \"itemIndex\""+ ": " + itemIndex+"}";		   
		   String loc3 = escapeQueryLocator(loc2, "!");
		   WebElement e1 = getJETWebElement(driver, loc3, 10000);
    	   
		   return e1;
       }
}
