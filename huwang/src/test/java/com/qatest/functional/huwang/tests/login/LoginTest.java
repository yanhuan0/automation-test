package com.qatest.functional.huwang.tests.login;

import org.testng.annotations.Test;

import com.qatest.functional.huwang.testbase.HWTestBase;

public class LoginTest extends HWTestBase {

	@Test(groups = { "UI", "webdriver", "hw_mats1" })
	public void testLogin() {
		String testID = getName();
		setUp(USERNAME, PASSWORD, testID);
	}
}
