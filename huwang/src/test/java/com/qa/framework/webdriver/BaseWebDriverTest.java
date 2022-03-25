package com.qa.framework.webdriver;

import java.io.File;
import java.lang.reflect.Method;
import java.io.StringReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.DataProvider;

import com.qa.framework.AbstractBaseTest;
import com.qa.framework.Sikuli;
import com.qa.framework.SikuliWrapper;
import com.qa.framework.TestNGGrouping;

import org.testng.ITestResult;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;

@Listeners(TestNGGrouping.class)
public class BaseWebDriverTest extends AbstractBaseTest implements UIAutomation, ITest {
	protected String mTestCaseName = "";
	private static boolean initWebDriverDone;	
	private static UIAutomation auto;
	private static boolean urlOpened;

	@BeforeClass
	public void setupClass() throws Exception {
		super.setupClass();
	}

	@BeforeMethod(alwaysRun = true)
	public void setupMthod(Method method, Object[] testData) {
		super.setupMethod(method);
		logger.debug("calling BaseWebDriverTest.setUpMethod");
		if (testData == null || testData.length == 0) {
			mTestCaseName = method.getName();
		} else {
			mTestCaseName = String.format("%s_%s", method.getName(), testData[0]);
		}
		// Set system property "VA_CURRENT_TESTNAME" for va GAT NLS test
		System.setProperty("VA_CURRENT_TESTNAME", mTestCaseName);

		if (isFirstTest()) {
			logger.debug("=========First test:init webdriver");
			initWebDriver();
		} else if (System.getProperty("qa.cleanup.after.test").equalsIgnoreCase("true")
				&& BROWSER_TYPE.contains("nw")) {
			if (auto != null)
				auto.startWebDriver();
			else {
				logger.debug("auto=========null:init webdriver");
				initWebDriver();
			}

		} else if (CLEANUP_AFTER_TEST && !urlOpened) {
			if (auto != null)
				auto.startWebDriver();
			else {
				logger.debug("auto=========null:init webdriver");
				initWebDriver();
			}
		}

		if (!urlOpened && URL != null) {
			open(URL);
			if (System.getProperty("qa.jscover", "false").equalsIgnoreCase("true")) {
			}
			urlOpened = true;
			if (BROWSER_WIDTH > 0 && BROWSER_HEIGHT > 0) {
				auto.resizeBrowser(BROWSER_WIDTH, BROWSER_HEIGHT);
			} else {
				maximizeBrowser();
			}
		}
	}

	protected void initWebDriver() {

		Sikuli sikuli = null;
		sikuli = new SikuliWrapper();
		sikuli.setLogger(logger);
		sikuli.setImageRootDir(getOutputDir());
		String[] dorRootDirs = DOR_ROOT_DIRS == null ? new String[] { "../resources/dor" }
				: DOR_ROOT_DIRS.split(",");
		for (int i = 0; i < dorRootDirs.length; i++)
			dorRootDirs[i] = getRootDir() + "/" + dorRootDirs[i];
		auto = new WebDriverWrapper(logger, sikuli, dorRootDirs);
		WebPage.init(auto);

	}

	@AfterMethod(alwaysRun = true)
	public void teardownMethod(ITestResult result) throws Exception {

		logger.debug("calling BaseWebDriverTest.tearDownMethod");
		if (System.getProperty("qa.jscover", "false").equalsIgnoreCase("true")) {
			((JavascriptExecutor) (auto.getDriver())).executeScript("if(window.jscoverage_report)jscoverage_report();");
		}
		try {

			if (System.getProperty("qa.cleanup.after.test").equalsIgnoreCase("false")) {
				logger.debug("====qa.cleanup.after.test: false");
			} else if (System.getProperty("qa.cleanup.after.test").equalsIgnoreCase("true")) {
				auto.stopWebDriver();
				urlOpened = false;
			} else if (CLEANUP_AFTER_TEST) {
				auto.stopWebDriver();
				urlOpened = false;
			}
		} catch (Exception e) {
			String os = "";
			if (System.getProperty("os.name") != null)
				os = System.getProperty("os.name").toLowerCase();
			logger.warn("teardownMethod():" + e.toString());
		} finally {
			logger.endTest();
		}

		super.teardownMethod();
		if (System.getProperty("qa.generate.sucdif", "true").equals("true")) {
			AbstractBaseTest.generateSucDif(result);
		}
	}

	@AfterClass(alwaysRun = true)
	public static void teardownClass() throws Exception {
		logger.debug("calling BaseWebDriverTest.teardownClass");
		try {

			if (auto != null)
				auto.stopWebDriver();
			else
				logger.info("auto is null...");
		} catch (Exception e) {
			String os = "";
			if (System.getProperty("os.name") != null)
				os = System.getProperty("os.name").toLowerCase();
			logger.error(e);
		} finally {
			logger.endSuite();
			auto = null;
		}
		// super.teardownClass();
	}

	
	@Override
	public String getTestName() {
		return mTestCaseName;
	}

	@DataProvider(name = "xmldata")
	public Object[][] provideXMLData() {
		List<String> tcList = getXmlTcList();
		Object[][] testData = new Object[tcList.size()][2];
		for (int i = 0; i < tcList.size(); i++) {
			String tcId = tcList.get(i);
			Properties params = getParams(tcId);
			testData[i][0] = tcId;
			testData[i][1] = params;
		}
		return testData;
	}

	@DataProvider(name = "stepdata")
	public Object[][] provideStepData() {
		List<String> tcList = null;
		String[] runTestCaseList = null;

		boolean hasRunList = false;

		if (isFirstTest()) {
			firstTimeSetup();
		}

		// Get all list
		tcList = getXmlTcList();
		logInfo("########################################");
		logInfo("Default tcList: " + tcList);
		logInfo("########################################");

		// Update tcList
		if (hasRunList) {
			for (int i = 0; i < tcList.size(); i++) {
				String id = tcList.get(i);
				boolean foundTestCase = false;
				for (int index = 0; index < runTestCaseList.length; index++) {
					if (id.equalsIgnoreCase(runTestCaseList[index])) {
						foundTestCase = true;
						break;
					}
				}
				// If not in the list, removed from tcList
				if (!foundTestCase) {
					tcList.remove(i);
					i--;
				}
			}
			logInfo("########################################");
			logInfo("Final tcList: " + tcList);
			logInfo("########################################");
		}

		Object[][] testData = new Object[tcList.size()][2];
		for (int i = 0; i < tcList.size(); i++) {
			String tcId = tcList.get(i);
			Properties params = getParams(tcId);
			testData[i][0] = tcId;
			List<Properties> steps = new ArrayList<Properties>();
			for (int step = 1; step < 100; step++) {
				String stepText = params.getProperty("step" + step);
				if (stepText == null) {
					stepText = params.getProperty("Step" + step);
				}
				if (stepText == null) {
					stepText = params.getProperty("STEP" + step);
				}
				if (stepText == null) {
					stepText = params.getProperty("step " + step);
				}
				if (stepText == null) {
					stepText = params.getProperty("Step " + step);
				}
				if (stepText == null) {
					stepText = params.getProperty("STEP " + step);
				}
				if (stepText == null) {
					break;
				}
				Properties stepData = new Properties();
				try {
					stepData.load(new StringReader(stepText));
				} catch (IOException e) {
					e.printStackTrace();
					logError("Failed to load step data from data xml file");
				}
				steps.add(stepData);
			}
			testData[i][1] = steps;
		}
		return testData;
	}

	public void saveScreenshot(String name) {
		File file = new File(getOutputDir(), name + ".png");
		auto.saveScreenShot(file);
	}

	@Override
	public WebElement getObject(String key) {

		if (auto.getObject(key) == null) {
			logError("The WebElement got by getObject() is Null!");
		}
		return auto.getObject(key);
	}

	@Override
	public void setObject(String key) {
		auto.setObject(key);
	}

	@Override
	public void setObject(String key, String value) {
		auto.setObject(key, value);
	}

	@Override
	public void setObject(String key, boolean check) {
		auto.setObject(key, check);
	}

	@Override
	public boolean waitForObject(String key) {
		return auto.waitForObject(key);
	}

	@Override
	public boolean waitForObject(String key, int maxWaitMS) {
		return auto.waitForObject(key, maxWaitMS);
	}

	@Override
	public boolean waitForObjectGone(String key) {
		return auto.waitForObjectGone(key);
	}

	@Override
	public boolean waitForObjectGone(String key, int maxWaitMS) {
		return auto.waitForObjectGone(key, maxWaitMS);
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
	public Sikuli getSikuli() {
		return auto.getSikuli();
	}

	@Override
	public void saveScreenShot(File file) {
		auto.saveScreenShot(file);
	}

	@Override
	public void saveScreenShot(String key, File file) {
		auto.saveScreenShot(key, file);
	}

	@Override
	public String getLocator(String key) {
		return auto.getLocator(key);
	}

	@Override
	public String getAttribute(String key, String attribute) {
		return auto.getAttribute(key, attribute);
	}

	@Override
	public String getPageText() {
		return auto.getPageText();
	}

	@Override
	public String getText(String key) {
		return auto.getText(key);
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
		auto.mouseMove(key);
	}

	@Override
	public void mouseMove(String key, int xOffset, int yOffset) {
		auto.mouseMove(key, xOffset, yOffset);
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
		auto.click(key);
	}

	@Override
	public void doubleClick(String key) {
		auto.doubleClick(key);
	}

	@Override
	public void dragAndDrop(String fromKey, String toKey) {
		auto.dragAndDrop(fromKey, toKey);
	}

	@Override
	public void dragAndDropMouseEvent(String fromKey, String toKey, int to_offsetX, int to_offsetY) {
		auto.dragAndDropMouseEvent(fromKey, toKey, to_offsetX, to_offsetY);
	}

	@Override
	public void mouseOver(String key) {
		auto.mouseOver(key);
	}

	@Override
	public void rightClick(String key) {
		auto.rightClick(key);
	}

	@Override
	public boolean isVisible(String key) {
		return auto.isVisible(key);
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
		return auto.clickToDownload(key);
	}

	@Override
	public File clickToDownload(String key, int time) {
		return auto.clickToDownload(key, time);
	}

	@Override
	public boolean isTextPresent(String text) {
		return auto.isTextPresent(text);
	}

	@Override
	public void clickToOpenPopup(String key) {
		auto.clickToOpenPopup(key);
	}

	@Override
	public void clickToClosePopup(String key) {
		auto.clickToClosePopup(key);
	}

	@Override
	public void selectFrame(String frame) {
		auto.selectFrame(frame);
	}

	@Override
	public void selectFrameByIndex(int index) {
		auto.selectFrameByIndex(index);
	}

	@Override
	public void returnToTopWindow() {
		auto.returnToTopWindow();
	}

	@Override
	public void setParentKey(String key) {
		auto.setParentKey(key);
	}

	@Override
	public void tabKey() {
		getActions().sendKeys(Keys.TAB);
	}

	public void robotMouseMove(int x, int y) {
		auto.robotMouseMove(x, y);
	}

	public void robotClick() {
		auto.robotClick();
	}

}
