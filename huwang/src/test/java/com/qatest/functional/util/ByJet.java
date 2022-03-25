package com.qatest.functional.util;


import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * Jet Sub Id Locator based implementation of By. The sole objective of this class is to locate and return
 * element(s) in the current document using Jet's location strategy (id search, in certain cases) as a
 * WebElement instance.
 *
 */
public final class ByJet extends By {

    /**
	 * Returns a new By object that locates DOM elements using Jet locator
	 * @param jetLocator The locator. 
	 * @return By to locate elements using Jet location strategy.
	 */
    public static By locator(String jetLocator) {
	    return locator(jetLocator, false);
    }
    
  
    /**
     * Returns a new By object that locates DOM elements using Jet locator, with id strategy as an optional fallback
     * @param jetLocator The locator.
     * @param fallBackToIdSearch True to augment Jet location strategy with id search in case the
     * former fails to locate the element.
     * @return By to locate elements using Jet location strategy.
     */
    public static By locator(String jetLocator, boolean fallBackToIdSearch) {
    	if (jetLocator == null)
    		throw new IllegalArgumentException("Locator string is null. Cannot find elements with a null locator. " +
    				"Please provide a locator string");

    	return new ByJet(jetLocator, fallBackToIdSearch);
    }
    
    

    /**
     * Returns a list of WebElements identified by the Jet locator.
     * @param searchContext
     * @return
     */
    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
    	Object retVal;

    	try
    	{
    		retVal = ((JavascriptExecutor) searchContext).executeScript(_LOCATE_ELEMENT_BY_JET_JS,
    				_jetLocator,
    				_fallBackOnIdSearch);
    	}
    	catch (WebDriverException wEx)
    	{
    		// Logging the exception, so that we can find out what actually went wrong
    		// when this is called in a WebDriverWait and WebDriverException is ignored.
    		System.out.println("ByJet:findElements() - Exception while trying to locate element by Jet for locator '"
    				+ _jetLocator + "'");
    		//      wEx.printStackTrace();
    		retVal = null;
    		//      throw wEx;
    	}

    	if (retVal == null) 
    	{
    		return Collections.emptyList();
    	}

    	// verify if we have a list of WebElements
    	if (retVal instanceof List) 
    	{
    		return (List<WebElement>) retVal;   
    	}

    	if (retVal instanceof WebElement)
    	{
    		// Single element, wrap it in a collection
    		WebElement jetElement = (WebElement) retVal;
    		return Collections.singletonList(jetElement);
    	}

    	throw new WebDriverException("Unexpected return type '" + retVal.getClass() + 
    			"'encountered for locator " + 
    			_jetLocator + 
    			". Expected types are WebElement or List<WebElement>.");
    }

    @Override
    public String toString() {
    	return "By.jet: " + _jetLocator;
    }


    private ByJet(String jetLocator, boolean fallBackToIdSearch) {
    	this._jetLocator = jetLocator;
    	_fallBackOnIdSearch = fallBackToIdSearch;
    }
    

    private final String _jetLocator;
    private final boolean _fallBackOnIdSearch;

    private static final String _LOCATE_ELEMENT_BY_JET_JS =
	    "var locator = arguments[0];" +
	    "var fallBackOnId = arguments[1];" +
	    "var elem = null;" +
	    "if (typeof oj !== \"undefined\" && typeof oj.Test !== \"undefined\")" +
	    "{" +
	    "  elem = oj.Test.domNodeForLocator(locator);" +
	    "}" +
	    "if (!elem && fallBackOnId == true)" +
	    "{" +
	    "  elem = document.getElementById(locator);" +
	    "}" +
	    "return elem;";
}