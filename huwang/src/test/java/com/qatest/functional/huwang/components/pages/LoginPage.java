package com.qatest.functional.huwang.components.pages;

import org.openqa.selenium.Point;

import com.qa.framework.webdriver.WebPage;

public class LoginPage extends WebPage {
	private HomePage _homePage = null;
	private int browserWidth = 1280;
	private int browserHeight = 1024;

	public LoginPage() {
		String m_username = System.getProperty("qa.user");
		String m_password = System.getProperty("qa.pwd");
		logMethod();
		logInfo("====qa.result.mode: " + System.getProperty("qa.result.mode"));
		resizeBrowserSize(m_username);
	}

	public LoginPage(boolean noResize) {

	}

	public HomePage login(String user, String pwd) {
		logMethod(user, pwd);
		_homePage = loginHW(user, pwd, null);
		if (_homePage == null) {
			try {
				stopWebDriver();
				startWebDriver();
				open(System.getProperty("qa.url"));
				resizeBrowserSize(user);
				_homePage = loginHW(user, pwd, null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return _homePage;
	}

	private HomePage loginHW(String user, String pwd, String code) {
		boolean logIn = false;
		setObject("elemHWLoginInput" + "(1)", user);
		setObject("elemHWLoginPasswd", pwd);
		if (code == null)
			setObject("elemHWLoginCode", "test");
		else
			setObject("elemHWLoginCode", code);
		if (waitForObject("elemHWLoginCheckPolocy")) {
			setObject("elemHWLoginCheckPolocy");
		}
		setObject("elemHWLoginOKButton");
		
		_homePage = new HomePage();
		return _homePage;
	}

	private void resizeBrowserSize(String m_username) {

		if (System.getProperty("qa.browser.width") != null)
			browserWidth = Integer.parseInt(System.getProperty("qa.browser.width"));
		if (System.getProperty("qa.browser.height") != null)
			browserHeight = Integer.parseInt(System.getProperty("qa.browser.height"));
		logInfo("====Setting browser size to: " + browserWidth + " x " + browserHeight);

		resizeBrowser(browserWidth, browserHeight);
		getDriver().manage().window().setPosition(new Point(0, 0));
	}

	public void resetLoginVA(String user, String pwd) {
		logMethod(user, pwd);

		try {
			stopWebDriver();
			startWebDriver();
			open(System.getProperty("qa.url"));
			resizeBrowserSize(user);
			loginHW(user, pwd, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
