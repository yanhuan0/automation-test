package com.qatest.functional.huwang.testbase;

import org.openqa.selenium.WebDriver;

import com.qa.framework.webdriver.BaseWebDriverTest;
import com.qatest.functional.huwang.components.pages.HomePage;
import com.qatest.functional.huwang.components.pages.LoginPage;

public class HWTestBase extends BaseWebDriverTest {

	public WebDriver _driver = null;
	public String currentTestName = "";
	
	public HomePage setUp(String user, String pwd, String testID) {
		HomePage homePage = null;
		_driver = getDriver();
		currentTestName = testID;
		LoginPage logPage = new LoginPage();

		String clean = System.getProperty("qa.cleanup.after.test");
		if (clean.equals("true")) {
			homePage = logPage.login(user, pwd);
		} else {
			if (isFirstTest())
				homePage = logPage.login(user, pwd);
			else
				homePage = new HomePage();
		}

		_driver = getDriver();
		
		return homePage;
	}

}
