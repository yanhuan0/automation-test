package com.qa.framework.webdriver;

import java.io.File;

import java.io.IOException;
import java.util.Date;
import org.jdom2.Document;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.Select;
import com.qa.framework.DOR;
import com.qa.framework.FileUtil;
import com.qa.framework.FrameworkException;
import com.qa.framework.Logger;
import com.qa.framework.ObjectNotFoundException;
import com.qa.framework.ParameterConstants;
import com.qa.framework.Sikuli;
import com.qa.framework.StringUtil;
import com.qa.framework.TestData;
import com.qa.framework.TestException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.openqa.selenium.Point;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.*;

public class WebDriverWrapper extends ParameterConstants implements UIAutomation {
	private static Logger logger;
	private static DOR s_dor = new DOR();
	private static WebDriver s_driver;
	private static Sikuli s_sikuli = null;
	private String DOWNLOAD_DIR = QA_WORK + "/SELENIUM/downloads/";
	private static boolean useGrid;
	private String _parentKey;

	private static Actions s_action;
	private String parentWindowHandle;

	public WebDriverWrapper(Logger log, Sikuli sikuli, String[] dorRootDirs) {

		init(log, sikuli, dorRootDirs);

		startWebDriver();
	}

	static void init(Logger log, Sikuli sikuli, String[] dorRootDirs) {
		logger = log;
		if (s_sikuli == null) {
			s_sikuli = sikuli;
			for (String dorRootDir : dorRootDirs)
				loadAllDors(dorRootDir);

		}

		if (!"false".equals(System.getProperty("geckodriver.cleanup"))) {
			logger.debug("geckodriver.cleanup is not set to false, set to true then.");
			System.setProperty("geckodriver.cleanup", "true");
		}

	}

	private static void loadAllDors(String rootDir) {
		logger.debugMethod(rootDir);
		File root = new File(rootDir);
		if (!root.exists()) {
			logger.warn("dir not found: " + rootDir);
			return;
		}
		File[] files = null;
		files = root.listFiles();

		boolean foundDupKeys = false;
		for (File file : files) {
			if (!loadDor(file, false))
				foundDupKeys = true;
			String overrideStr = System.getProperty("qatest.dor.override");
			if (overrideStr != null) {
				overrideStr = overrideStr.trim();
				File[] subDirs = file.listFiles();
				for (File subDir : subDirs) {
					String name = subDir.getName();
					if (!subDir.isDirectory())
						continue;
					if (overrideStr.equals("*"))
						loadDor(subDir, true);
					else {
						String[] overrideComponents = overrideStr.split(",");
						for (String overrideComponent : overrideComponents) {
							if (name.equals(overrideComponent))
								loadDor(subDir, true);
						}
					}
				}
			}
		}
		logger.debug("All DOR files loaded. Total number of objects in DOR: " + s_dor.size());
		if (foundDupKeys)
			throw new FrameworkException("found duplicated keys");
	}

	private static boolean loadDor(File dorDir, boolean allowDupKeys) {
		if (!dorDir.exists())
			return true;
		File[] files = new File[] { dorDir }; // if dorDir is a dor xml file
		if (dorDir.isDirectory())
			files = dorDir.listFiles();
		boolean foundDupKeys = false;
		for (int j = 0; j < files.length; j++) {
			String filePath = files[j].getAbsolutePath();
			if (!filePath.endsWith(".ade_path")) {
				if (!filePath.trim().toLowerCase().endsWith(".xml"))
					continue;
				if (allowDupKeys)
					logger.debug("Overriding DOR");
				logger.debug(filePath);
				Document dorDoc = null;
				try {
					dorDoc = TestData.getXMLDoc(filePath);
				} catch (IOException e) {
					throw new FrameworkException(e);
				}
				try {
					s_dor.add(dorDoc, allowDupKeys);
				} catch (FrameworkException e) {
					if (e.getMessage().contains("duplicated keys")) {
						foundDupKeys = true;
					} else
						throw e;
				}
			}
		}
		return !foundDupKeys;
	}

	@Override
	public void startWebDriver() {
		logger.info("Starting WebDriver...");
		logger.info("BROWSER_TYPE =" + BROWSER_TYPE);

		if (System.getProperty("webdriver.gecko.driver") != null)
			System.setProperty("webdriver.gecko.driver", System.getProperty("webdriver.gecko.driver"));

		if (BROWSER_TYPE.contains("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--lang=en-us");
			s_driver = new ChromeDriver(options);
			s_action = new Actions(s_driver);
		}
	}

	@Override
	public void stopWebDriver() {
		logger.debugMethod();
		String os = "";
		if (System.getProperty("os.name") != null)
			os = System.getProperty("os.name").toLowerCase();
		s_driver.quit();
	}
	
	@Override
	public void open(String url) {
		logger.debugMethod(url);
		try {
			getDriver().navigate().to(url);
		} catch (UnreachableBrowserException e) {
			logger.warn("browser not found, so open a new browser");
			startWebDriver();
			getDriver().navigate().to(url);
		}
	}

	private void autoScreenShot() {
		File file = new File(logger.getOutputDir(), getClass().getSimpleName() + "_" + new Date().getTime() + ".png");
		saveScreenShot(file);
	}

	@Override
	public void setObject(String key) {
		logger.debugMethod(key);
		setObject(key, null, null);
	}

	@Override
	public void setObject(String key, String value) {
		if (value == null) {
			setObject(key);
			return;
		}
		logger.debugMethod(key, value);
		if (key.startsWith("lst") || key.startsWith("list")) {
			setObject(key, value, "select");
		} else if (key.startsWith("valueList")) {
			setObject(key, value, "selectValue");
		} else if (key.startsWith("file"))
			setObject(key, value, "file");
		else
			setObject(key, value, "type");
	}

	@Override
	public void setObject(String key, boolean check) {
		logger.debugMethod(key, check);
		setObject(key, null, check ? "check" : "uncheck");
	}

	private void setObject(String key, String value, String action) {
		String locator = getLocator(key);
		if (!existsOrDisappears(locator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + key + "]" + locator));
		WebElement elem = getWebElement(locator);
		if (action == null || action.equals("click")) {
			try {
				elem.click();
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
		} else if (action.equals("select") && value != null) {
			Select select = new Select(elem);
			select.selectByVisibleText(value);
		} else if (action.equals("selectValue") && value != null) {
			Select select = new Select(elem);
			select.selectByValue(value);
		} else if (action.equals("file") && value != null) {
			logger.debug("enter file path into the file selection box");
			elem.sendKeys(value);
		} else if (action.equals("type") && value != null) {
			logger.debug("in setobject type branch*************");
			elem.click();
			elem.clear(); 
			// elem.sendKeys(Keys.chord(Keys.CONTROL,"a"));
			if (value.length() > StringUtil.MAX_SHORT_MSG_LEN) {
				logger.debug("input text too long, using javascript to set edit box");
				try {
					((JavascriptExecutor) getDriver()).executeScript("arguments[0].value = arguments[1]", elem, value);
				} catch (Exception e) {
					logger.warn(e.getMessage());
					logger.warn("Got error in Javascript, redo with sendKeys");
					elem.sendKeys(value);
				}
			} else
				elem.sendKeys(value);
		} else if (action.equals("check")) {
			if (!elem.isSelected())
				elem.click();
		} else if (action.equals("uncheck")) {
			if (elem.isSelected())
				elem.click();
		} else if (action.equals("clear")) {
			elem.clear();
		} else
			logger.error("Unknow action: " + action);
	}

	@Override
	public void mouseMove(String key) {
		logger.debugMethod(key);
		String locator = getLocator(key);
		if (!existsOrDisappears(locator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + key + "]" + locator));
		getActions().moveToElement(getWebElement(locator)).perform();
	}

	@Override
	public void mouseMove(String key, int xOffset, int yOffset) {
		logger.debugMethod(key, xOffset, yOffset);
		String locator = getLocator(key);
		if (!existsOrDisappears(locator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + key + "]" + locator));
		getActions().moveToElement(getWebElement(locator), xOffset, yOffset).perform();
	}

	public void mouseDown() {
		logger.debugMethod();
		getActions().clickAndHold().perform();
	}

	public void mouseUp() {
		logger.debugMethod();
		getActions().release().perform();
	}

	public void click(String key) {
		logger.debugMethod(key);
		if (key == null)
			getActions().click().perform();
		else
			getActions().click(getObject(key)).perform();
	}

	@Override
	public void doubleClick(String key) {
		logger.debugMethod(key);
		if (key == null) {
			getActions().doubleClick().perform();
			return;
		}
		String locator = getLocator(key);
		if (!existsOrDisappears(locator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + key + "]" + locator));
		getActions().doubleClick(getWebElement(locator)).perform();
	}

	@Override
	public void dragAndDrop(String fromKey, String toKey) {
		logger.debugMethod(fromKey, toKey);
		String fromLocator = getLocator(fromKey);
		if (!existsOrDisappears(fromLocator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + fromKey + "]" + fromLocator));
		String toLocator = getLocator(toKey);
		if (!existsOrDisappears(toLocator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + toKey + "]" + toLocator));
		getActions().dragAndDrop(getWebElement(fromLocator), getWebElement(toLocator)).perform();
	}

	@Override
	public void dragAndDropMouseEvent(String fromKey, String toKey, int to_offsetX, int to_offsetY) {
		logger.debugMethod(fromKey, toKey);
		mouseMove(fromKey, 5, 10);
		mouseDown();
		sleep(300);
		mouseMove(toKey, to_offsetX, to_offsetY);
		sleep(300);
		setObject(toKey);
	}

	@Override
	public void mouseOver(String key) {
		logger.debugMethod(key);
		String locator = getLocator(key);
		if (!existsOrDisappears(locator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + key + "]" + locator));
		getActions().moveToElement(getWebElement(locator)).perform();
	}

	@Override
	public void rightClick(String key) {
		logger.debugMethod(key);
		if (key == null) {
			getActions().contextClick().perform();
			return;
		}
		String locator = getLocator(key);
		if (!existsOrDisappears(locator, WAIT_TIMEOUT_MS / 1000, true))
			logger.error(new ObjectNotFoundException("Object not found: [" + key + "]" + locator));
		getActions().contextClick(getWebElement(locator)).perform();
	}

	@Override
	public String getLocator(String key) {
		if (key.contains("=")) {
			logger.debug("locator = " + key);
			return key;
		}
		String locator = s_dor.getLocator(key);
		if (locator == null) {
			if (_parentKey != null && !key.startsWith(_parentKey + ".")) {
				locator = s_dor.getLocator(_parentKey + "." + key);
			}
		}
		if (locator == null) {
			logger.error("locator is not found: " + key);
		}
		locator = s_dor.replaceNestedDorKey(locator);
		logger.debug("locator[" + key + "] = " + locator);
		return locator;
	}

	private boolean existsOrDisappears(String locator, int timeoutSec, boolean checkExists) {
		getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		if (timeoutSec < 0)
			timeoutSec = WAIT_TIMEOUT_MS / 1000;
		for (int i = 0; i <= timeoutSec; i++) {
			WebElement elem = getWebElement(locator);
			if (checkExists && elem != null && elem.isDisplayed()
					|| !checkExists && (elem == null || !elem.isDisplayed())) {
				return true;
			}
			if (timeoutSec > 0) {
				int remainder = (timeoutSec - i) % 10;
				System.out.print(remainder == 0 ? "" + (timeoutSec - i) : ".");
				logger.sleep(1000, false);
			}
		}
		return false;
	}

	private boolean existsOrDisappearsByKey(String keyStr, int timeoutSec, boolean checkExists) {
		getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		if (timeoutSec < 0)
			timeoutSec = WAIT_TIMEOUT_MS / 1000;
		boolean hasOrOperator = keyStr.contains("||");
		boolean hasAndOperator = keyStr.contains("&&");
		String[] keys = new String[] { keyStr };
		if (hasOrOperator)
			keys = keyStr.split("\\|\\|");
		if (hasAndOperator)
			keys = keyStr.split("&&");
		String[] locators = new String[keys.length];
		for (int i = 0; i < keys.length; i++)
			locators[i] = getLocator(keys[i]);
		boolean andConditionMet = true;
		for (int i = 0; i <= timeoutSec; i++) {
			andConditionMet = true;
			for (String locator : locators) {
				WebElement elem = getWebElement(locator);
				if (checkExists && elem != null && elem.isDisplayed()
						|| !checkExists && (elem == null || !elem.isDisplayed())) {
					if (hasOrOperator || !hasOrOperator && !hasAndOperator)
						return true;
				} else {
					andConditionMet = false;
					break;
				}
			}
			if (hasAndOperator && andConditionMet)
				return true;
			if (timeoutSec > 0) {
				int remainder = (timeoutSec - i) % 10;
				System.out.print(remainder == 0 ? "" + (timeoutSec - i) : ".");
				logger.sleep(1000, false);
			}
		}
		return false;
	}

	@Override
	public boolean waitForObject(String key) {
		logger.debugMethod(key);
		try {
			return logger.debugReturn(existsOrDisappearsByKey(key, WAIT_TIMEOUT_MS / 1000, true));
		} catch (StaleElementReferenceException e) {
			logger.info("selenium exception:StaleElementReferenceException");
			sleep(m_smallTime);
			return logger.debugReturn(existsOrDisappearsByKey(key, WAIT_TIMEOUT_MS / 1000, true));
		}
	}

	@Override
	public boolean waitForObject(String key, int maxWaitMS) {
		logger.debugMethod(key, maxWaitMS);
		try {
			return logger.debugReturn(existsOrDisappearsByKey(key, maxWaitMS / 1000, true));
		} catch (StaleElementReferenceException e) {
			logger.info("selenium exception:StaleElementReferenceException");
			sleep(m_smallTime);
			return logger.debugReturn(existsOrDisappearsByKey(key, maxWaitMS / 1000, true));
		}
	}

	@Override
	public boolean waitForObjectGone(String key) {
		logger.debugMethod(key);
		return logger.debugReturn(existsOrDisappearsByKey(key, WAIT_TIMEOUT_MS / 1000, false));
	}

	@Override
	public boolean waitForObjectGone(String key, int maxWaitMS) {
		logger.debugMethod(key, maxWaitMS);
		return logger.debugReturn(existsOrDisappearsByKey(key, maxWaitMS / 1000, false));
	}

	@Override
	public WebDriver getDriver() {
		return s_driver;
	}

	@Override
	public Actions getActions() {
		return s_action;
	}

	@Override
	public Sikuli getSikuli() {
		return s_sikuli;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void saveScreenShot(File file) {
		logger.debugMethod(file.getAbsolutePath());

		File tempfile = null;
		try {
			if (useGrid) {
				WebDriver augmentedDriver = new Augmenter().augment(getDriver());
				tempfile = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
			} else
				tempfile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		} catch (Throwable t) {
			logger.warn("saveScreenShot(): Exception is caught when taking a screenshot.");
		}

		try {
			FileUtil.copyFile(tempfile, file);
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
		logger.logScreenShot(file);
	}

	@Override
	public void saveScreenShot(String key, File file) {
		logger.debugMethod(key, file.getAbsolutePath());
		WebElement element = getObject(key);
		WrapsDriver wrapsDriver = (WrapsDriver) element;
		File screen = ((TakesScreenshot) wrapsDriver.getWrappedDriver()).getScreenshotAs(OutputType.FILE);
		BufferedImage img;
		try {
			img = ImageIO.read(screen);
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		int width = element.getSize().getWidth();
		int height = element.getSize().getHeight();
		Rectangle rect = new Rectangle(width, height);
		Point point = element.getLocation();
		BufferedImage eleScreenshot = img.getSubimage(point.getX(), point.getY(), width, height);
		try {
			ImageIO.write(eleScreenshot, "png", file);
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public WebElement getObject(String key) {
		String locator = getLocator(key);
		if (locator == null)
			logger.error("locator is not found: " + key);
		return getWebElement(locator);
	}

	private WebElement getWebElement(String locator) {
		WebElement elem = null;
		locator = locator.trim();
		try {
			if (locator.toLowerCase().startsWith("id="))
				elem = getDriver().findElement(By.id(locator.substring(3).trim()));
			else if (locator.toLowerCase().startsWith("name="))
				elem = getDriver().findElement(By.name(locator.substring(5).trim()));
			else if (locator.toLowerCase().startsWith("linkText="))
				elem = getDriver().findElement(By.linkText(locator.substring(9).trim()));
			else if (locator.toLowerCase().startsWith("link="))
				elem = getDriver().findElement(By.linkText(locator.substring(5).trim()));
			else if (locator.toLowerCase().startsWith("partialLinkText="))
				elem = getDriver().findElement(By.partialLinkText(locator.substring(16).trim()));
			else if (locator.toLowerCase().startsWith("css="))
				elem = getDriver().findElement(By.cssSelector(locator.substring(4).trim()));
			else if (locator.toLowerCase().startsWith("xpath="))
				elem = getDriver().findElement(By.xpath(locator.substring(6).trim()));
			else if (locator.toLowerCase().startsWith("class="))
				elem = getDriver().findElement(By.className(locator.substring(6).trim()));
			else {
				elem = getDriver().findElement(By.xpath(locator));
			}
		} catch (NoSuchElementException e) {
			return null;
		} catch (Throwable e) {
			logger.warn(e.getMessage());
			return null;
		}
		return elem;
	}

	@Override
	public String getAttribute(String key, String attribute) {
		logger.debugMethod(key, attribute);
		WebElement elem = getObject(key);
		if (elem == null)
			return null;
		return logger.debugReturn(elem.getAttribute(attribute));

	}

	@Override
	public String getText(String key) {
		logger.debugMethod(key);
		WebElement elem = getObject(key);
		if (elem == null)
			return logger.debugReturn(null);
		return logger.debugReturn(elem.getText());
	}

	@Override
	public String getCurrentUrl() {
		logger.debugMethod();
		return s_driver.getCurrentUrl();
	}

	@Override
	public void maximizeBrowser() {
		logger.debugMethod();
		if (BROWSER_TYPE.contains("htmlunit")) {
			logger.warn("HtmlUnit does not support this action");
		} else if (BROWSER_TYPE.contains("phantomjs")) {
			s_driver.manage().window().setSize(new Dimension(1280, 1024));
		} else
			getDriver().manage().window().maximize();
	}

	@Override
	public void closeBrowser() {
		logger.debugMethod();
		getDriver().close();
	}

	@Override
	public void resizeBrowser(int width, int height) {
		logger.debugMethod(width, height);
		getDriver().manage().window().setSize(new Dimension(width, height));
	}

	@Override
	public String getPageText() {
		logger.debugMethod();
		return getDriver().findElement(By.tagName("body")).getText();
	}

	@Override
	public void sleep(int timeMS) {
		logger.sleep(timeMS, true);
	}

	@Override
	public void logInfo(String msg) {
		logger.info(msg);
	}

	@Override
	public void logError(String msg) {
		logger.error(msg);
	}

	@Override
	public void logError(RuntimeException e) {
		logger.error(e);
	}

	@Override
	public void logDebug(String msg) {
		logger.debug(msg);
	}

	@Override
	public void logWarn(String msg) {
		logger.warn(msg);
	}

	@Override
	public boolean isVisible(String key) {
		logger.debugMethod();
		WebElement elem = getObject(key);
		if (elem == null)
			return logger.debugReturn(false);
		return logger.debugReturn(elem.isDisplayed());
	}

	@Override
	public String getTitle() {
		logger.debugMethod();
		return logger.debugReturn(getDriver().getTitle());
	}

	@Override
	public String dismissAlert() {
		logger.debugMethod();
		Alert alert = null;
		try {
			alert = getDriver().switchTo().alert();
		} catch (NoAlertPresentException e) {
			logger.warn(e.getMessage());
			return logger.debugReturn(null);
		}
		logger.warn("Found alert box");
		String msg = alert.getText();
		logger.warn(msg);
		alert.dismiss();
		return logger.debugReturn(msg);
	}

	@Override
	public String acceptAlert() {
		logger.debugMethod();
		Alert alert = null;
		try {
			alert = getDriver().switchTo().alert();
		} catch (NoAlertPresentException e) {
			logger.warn(e.getMessage());
			return logger.debugReturn(null);
		}
		String msg = alert.getText();
		alert.accept();
		return logger.debugReturn(msg);
	}

	@Override
	public File clickToDownload(String key) {
		return clickToDownload(key, 60);
	}

	@Override
	public File clickToDownload(String key, int time) {
		logger.debugMethod(key);
		FileUtil.deleteAllFiles(DOWNLOAD_DIR);

		logger.debug("clickToDownload(): start...");
		setObject(key);
		logger.sleep(5000, false);

		logger.debug("DOWNLOAD_DIR: " + DOWNLOAD_DIR);
		for (int t = 0; t < time; t++) {
			if (FileUtil.listAllFiles(DOWNLOAD_DIR).length > 0) {
				break;
			}
			logger.sleep(1000, false);
		}
		if (FileUtil.listAllFiles(DOWNLOAD_DIR).length == 0)
			throw new TestException("Download did not start after " + time + " seconds");
		long startTime = new Date().getTime();
		System.out.print("downloading");
		while (FileUtil.listAllFiles(DOWNLOAD_DIR).length > 1) {
			logger.sleep(2000, false);
			System.out.print(".");
			long currentTime = new Date().getTime();
			if (currentTime - startTime > 10 * 60 * 1000)
				throw new TestException("download did not complete after 10 minutes");
		}
		System.out.println();
		File file = FileUtil.listAllFiles(DOWNLOAD_DIR)[0];
		logger.debug("download completed: " + file.getAbsolutePath() + " (size=" + file.length() + ")");
		return file;
	}

	@Override
	public void clickToOpenPopup(String key) {
		logger.debugMethod(key);
		Set<String> oldHandles = getDriver().getWindowHandles();
		parentWindowHandle = getDriver().getWindowHandle();
		setObject(key);
		for (int i = 0; i < WAIT_TIMEOUT_MS / 1000; i++) {
			Set<String> handles = getDriver().getWindowHandles();
			for (String handle : handles) {
				if (!oldHandles.contains(handle)) {
					getDriver().switchTo().window(handle);
					return;
				}
			}
			sleep(1000);
		}
		logError("Popup not found");
	}

	@Override
	public void clickToClosePopup(String key) {
		logger.debugMethod(key);
		Set<String> oldHandles = getDriver().getWindowHandles();
		if (key != null)
			setObject(key);
		else
			closeBrowser();
		for (int i = 0; i < WAIT_TIMEOUT_MS / 1000; i++) {
			Set<String> handles = getDriver().getWindowHandles();
			if (handles.size() < oldHandles.size()) {
				if (parentWindowHandle != null)
					getDriver().switchTo().window(parentWindowHandle);
				return;
			}
			sleep(1000);
		}
		logError("No popup was closed");
	}

	@Override
	public boolean isTextPresent(String text) {
		logger.debugMethod("In isTextPresent method");
		// search text on page
		if (getDriver().getPageSource().contains(text))
			return true;
		else
			return false;
	}

	@Override
	public void selectFrame(String frame) {
		getDriver().switchTo().frame(frame);
	}

	@Override
	public void selectFrameByIndex(int index) {
		getDriver().switchTo().frame(0);
	}

	@Override
	public void returnToTopWindow() {
		getDriver().switchTo().defaultContent();
	}

	@Override
	public void tabKey() {
		getActions().sendKeys(Keys.TAB);
	}

	public void robotMouseMove(int x, int y) {
		logger.debugMethod(x, y);
		int x0 = getDriver().manage().window().getPosition().x + BROWSER_OFFSET_X;
		int y0 = getDriver().manage().window().getPosition().y + BROWSER_OFFSET_Y;
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (Exception e) {
			logger.error(e);
		}
		robot.mouseMove(x0 + x, y0 + y);
	}

	public void robotClick() {
		logger.debugMethod();
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (Exception e) {
			logger.error(e);
		}
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	@Override
	public void setParentKey(String key) {
		_parentKey = key;
	}

}
