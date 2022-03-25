package com.qa.framework.webdriver;

import java.io.File;
import java.util.Date;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.qa.framework.Logger;
import com.qa.framework.ObjectNotFoundException;
import com.qa.framework.ParameterConstants;
import com.qa.framework.Sikuli;

import org.openqa.selenium.Keys;


public class WebPage extends ParameterConstants implements Page{
	private static UIAutomation auto;
	private String _pageName = getClass().getSimpleName();
	
	public WebPage(){
		getLogger().info("Creating page object: "+getClass().getSimpleName());
		if(auto != null){
			auto.setParentKey(null);
		}
	}

	static void init(UIAutomation autoWrapper){
		auto=autoWrapper;
	}


	@Override
	public void sync(String key) {
		auto.setParentKey(_pageName);
		sync(key, WAIT_TIMEOUT_MS);
		auto.setParentKey(null);
	}

	@Override
	public void sync(String key, int timeoutMS) {
		auto.setParentKey(_pageName);
		getLogger().debugMethod(key, timeoutMS);
		if(!auto.waitForObject(key, timeoutMS))
			logError(new ObjectNotFoundException("could not find object: "+key));
		autoScreenShot();
		auto.setParentKey(null);
	}
	
	private void autoScreenShot(){
		File file = new File(getLogger().getOutputDir(), 
				getClass().getSimpleName() + "_" + new Date().getTime()+ ".png");
		auto.saveScreenShot(file);
	}
	

	@Override
    public void sleep(int milliSec) {
        getLogger().sleep(milliSec, true);
    }
    
	@Override
	public Sikuli getSikuli() {
		return auto.getSikuli();
	}

	@Override
	public void logInfo(String msg) {
		getLogger().info(msg);	
	}

	@Override
	public void logError(String msg) {
		getLogger().error(msg);
	}
	@Override
	public void logError(RuntimeException e) {
		getLogger().error(e);
	}

	@Override
	public void logDebug(String msg) {
		getLogger().debug(msg);		
	}

	@Override
	public void logWarn(String msg) {
		getLogger().warn(msg);
	}

	public void logMethod(Object... args) {
        boolean findNext=false;
        for(StackTraceElement stElem : Thread.currentThread().getStackTrace()){
                String methodName=stElem.getMethodName();
                if(findNext){
                	getLogger().logMethod(methodName, 2, args);
                	break;
                }
                if(methodName.equals("logMethod"))
                	findNext=true;
        }		
	}

	@Override
	public WebElement getObject(String key) {
		auto.setParentKey(_pageName);
		WebElement ret = auto.getObject(key);
		auto.setParentKey(null);
		return ret;
	}

	@Override
	public void setObject(String key) {
		auto.setParentKey(_pageName);
		auto.setObject(key);
		auto.setParentKey(null);
	}

	@Override
	public void setObject(String key, String value) {
		auto.setParentKey(_pageName);
		auto.setObject(key, value);
		auto.setParentKey(null);
	}

	@Override
	public void setObject(String key, boolean check) {
		auto.setParentKey(_pageName);
		auto.setObject(key, check);
		auto.setParentKey(null);
	}

	@Override
	public boolean waitForObject(String key) {
		auto.setParentKey(_pageName);
		boolean ret = auto.waitForObject(key);
		auto.setParentKey(null);
		return ret;
	}

	@Override
	public boolean waitForObject(String key, int maxWaitMS) {
		auto.setParentKey(_pageName);
		boolean ret = auto.waitForObject(key, maxWaitMS);
		auto.setParentKey(null);
		return ret;
	}

	@Override
	public boolean waitForObjectGone(String key) {
		auto.setParentKey(_pageName);
		boolean ret = auto.waitForObjectGone(key);
		auto.setParentKey(null);
		return ret;
	}

	@Override
	public boolean waitForObjectGone(String key, int maxWaitMS) {
		auto.setParentKey(_pageName);
		boolean ret = auto.waitForObjectGone(key, maxWaitMS);
		auto.setParentKey(null);
		return ret;
	}

	@Override
	public void open(String url) {
		auto.open(url);	
	}

	@Override
	public void startWebDriver() {
		auto.startWebDriver();
	}

	@Override
	public void stopWebDriver() {
		auto.stopWebDriver();
	}

	@Override
	public WebDriver getDriver() {
		return auto.getDriver();
	}
	@Override
	public Actions getActions() {
		return auto.getActions();
	}

	@Override
	public Logger getLogger() {
		return auto.getLogger();
	}

	@Override
	public void saveScreenShot(File file) {
		auto.saveScreenShot(file);
	}

    @Override
    public void saveScreenShot(String key, File file) {
    	auto.setParentKey(_pageName);
        auto.saveScreenShot(key, file);
    	auto.setParentKey(null);
    }

	@Override
	public String getLocator(String key) {
    	auto.setParentKey(_pageName);
		String ret = auto.getLocator(key);
    	auto.setParentKey(null);
    	return ret;
	}

	@Override
	public String getAttribute(String key, String attribute) {
    	auto.setParentKey(_pageName);
		String ret = auto.getAttribute(key, attribute);
    	auto.setParentKey(null);
    	return ret;
	}

	@Override
	public String getPageText() {
		return auto.getPageText();
	}

	@Override
	public String getText(String key) {
    	auto.setParentKey(_pageName);
		String ret = auto.getText(key);
    	auto.setParentKey(null);
    	return ret;
	}

	@Override
	public String getCurrentUrl() {
		return auto.getCurrentUrl();
	}

	@Override
	public void closeBrowser() {
		auto.closeBrowser();
	}

	@Override
	public void resizeBrowser(int width, int height) {
		auto.resizeBrowser(width, height);
	}

	@Override
	public void maximizeBrowser() {
		auto.maximizeBrowser();
	}

	@Override
	public void mouseMove(String key) {
    	auto.setParentKey(_pageName);
		auto.mouseMove(key);
    	auto.setParentKey(null);
	}

    @Override
    public void mouseMove(String key, int xOffset, int yOffset) {
    	auto.setParentKey(_pageName);
        auto.mouseMove(key, xOffset, yOffset);
    	auto.setParentKey(null);
    }

    @Override
    public void mouseDown() {
        auto.mouseDown();
    }
    
    @Override
    public void mouseUp() {
        auto.mouseUp();
    }
    
    @Override
    public void click(String key) {
    	auto.setParentKey(_pageName);
        auto.click(key);
    	auto.setParentKey(null);
    }

	@Override
	public void doubleClick(String key) {
    	auto.setParentKey(_pageName);
		auto.doubleClick(key);
    	auto.setParentKey(null);
	}

	@Override
	public void dragAndDrop(String fromKey, String toKey) {
    	auto.setParentKey(_pageName);
		auto.dragAndDrop(fromKey, toKey);
    	auto.setParentKey(null);
	}
	
	@Override
	public void mouseOver(String key) {
    	auto.setParentKey(_pageName);
		auto.mouseOver(key);		
    	auto.setParentKey(null);
	}

	@Override
	public void rightClick(String key) {
    	auto.setParentKey(_pageName);
		auto.rightClick(key);		
    	auto.setParentKey(null);
	}

	@Override
	public boolean isVisible(String key) {
    	auto.setParentKey(_pageName);
		boolean ret = auto.isVisible(key);
    	auto.setParentKey(null);
    	return ret;
	}

	@Override
	public String getTitle() {
		return auto.getTitle();
	}

	@Override
	public String dismissAlert() {
		return auto.dismissAlert();
	}

	@Override
	public String acceptAlert() {
		return auto.acceptAlert();
	}
	
	@Override
	public File clickToDownload(String key) {
    	auto.setParentKey(_pageName);
		File ret = auto.clickToDownload(key);
    	auto.setParentKey(null);
    	return ret;
	}
	@Override
	public File clickToDownload(String key, int time){
    	auto.setParentKey(_pageName);
		File ret = auto.clickToDownload(key,time);
    	auto.setParentKey(null);
    	return ret;
	}

        @Override
        public boolean isTextPresent(String text){
                return auto.isTextPresent(text);
        }

    @Override
    public void clickToOpenPopup(String key) {
    	auto.setParentKey(_pageName);
        auto.clickToOpenPopup(key);
    	auto.setParentKey(null);
    }

    @Override
    public void clickToClosePopup(String key) {
    	auto.setParentKey(_pageName);
        auto.clickToClosePopup(key);
    	auto.setParentKey(null);
    }
    
    @Override
    public void selectFrame(String frame){
        auto.selectFrame(frame);
    }

    @Override
    public void selectFrameByIndex(int index){
        auto.selectFrameByIndex(index);
    }

    @Override
    public void returnToTopWindow(){
        auto.returnToTopWindow();
    }

    @Override
    public void dragAndDropMouseEvent(String fromKey, String toKey,
                      int to_offsetX, int to_offsetY) {
    	auto.setParentKey(_pageName);
        auto.dragAndDropMouseEvent(fromKey, toKey, to_offsetX, to_offsetY);
    	auto.setParentKey(null);
    }
    
    @Override
    public void tabKey(){
        getActions().sendKeys(Keys.TAB);
    }

    @Override
    public void setParentKey(String key){
        auto.setParentKey(key);
    }  
    
    public void robotMouseMove(int x, int y) {
        auto.robotMouseMove(x, y);
    }

    public void robotClick() {
        auto.robotClick();
    }

}
