package com.qatest.functional.huwang.components.retapi;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import com.qa.framework.AbstractBaseTest;
import com.qa.framework.TestNGGrouping;

@Listeners(TestNGGrouping.class)
public class BaseRestAPI extends AbstractBaseTest implements ITest {
	// Fix the issue of afterClass
	protected String mTestCaseName = "";

	@BeforeClass(alwaysRun = true)
	public void setupClass() throws Exception {
		super.setupClass();
	}

	@BeforeMethod(alwaysRun = true)
	public void setupMthod(Method method, Object[] testData) {
		super.setupMethod(method);
		if (testData == null || testData.length == 0) {
			mTestCaseName = method.getName();
		} else {
			mTestCaseName = String.format("%s_%s", method.getName(), testData[0]);
		}

		System.setProperty("QA_CURRENT_TESTNAME", mTestCaseName);
	}

	@AfterMethod(alwaysRun = true)
	public void teardownMethod(ITestResult result) throws Exception {
		logger.debug("calling BaseRestAPI.teardownMethod");
		logger.endTest();
		super.teardownMethod();
		if (System.getProperty("qa.generate.sucdif", "true").equals("true")) {
			AbstractBaseTest.generateSucDif(result);
		}
	}

	@AfterClass(alwaysRun = true)
	public static void teardownClass() throws Exception {
		logger.debug("calling BaseRestAPI.teardownClass");
		logger.endSuite();
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

	@Override
	public String getTestName() {
		return mTestCaseName;
	}

}
