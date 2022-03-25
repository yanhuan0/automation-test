package com.qa.framework.webdriver;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import com.qa.framework.Logger;
import com.qa.framework.Sikuli;

public interface UIAutomation {
	public WebElement getObject(String key);

	public void setObject(String key);

	public void setObject(String key, String value);

	public void setObject(String key, boolean check);

	public void mouseMove(String key);

	public void mouseMove(String key, int xOffset, int yOffset);

	public void mouseDown();

	public void mouseUp();

	public void click(String key);

	public void doubleClick(String key);

	public void dragAndDrop(String fromKey, String toKey);

	public void dragAndDropMouseEvent(String fromKey, String toKey, int to_offsetX, int to_offsetY);

	public void mouseOver(String key);

	public void rightClick(String key);

	public void robotMouseMove(int x, int y);

	public void robotClick();

	public boolean waitForObject(String key);

	public boolean waitForObject(String key, int maxWaitMS);

	public boolean waitForObjectGone(String key);

	public boolean waitForObjectGone(String key, int maxWaitMS);

	public void sleep(int timeMS);

	public void open(String url);

	public void startWebDriver();

	public void stopWebDriver();

	public WebDriver getDriver();

	public Actions getActions();

	public Sikuli getSikuli();

	public Logger getLogger();

	public File clickToDownload(String key);

	public File clickToDownload(String key, int time);

	public void clickToOpenPopup(String key);

	public void clickToClosePopup(String key);

	public void saveScreenShot(File file);

	public void saveScreenShot(String key, File file);

	public String getLocator(String key);

	public String getAttribute(String key, String attribute);

	public String getPageText();

	public String getText(String key);

	public String getCurrentUrl();

	public boolean isVisible(String key);

	public String getTitle();

	public String dismissAlert();

	public String acceptAlert();

	public void closeBrowser();

	public void resizeBrowser(int width, int height);

	public void maximizeBrowser();

	public void logInfo(String msg);

	public void logError(String msg);

	public void logError(RuntimeException e);

	public void logDebug(String msg);

	public void logWarn(String msg);

	public boolean isTextPresent(String text);

	public void selectFrame(String frame);

	public void selectFrameByIndex(int index);

	public void returnToTopWindow();

	public void tabKey();

	public void setParentKey(String key);
}
