package com.qatest.functional.huwang.components.pages.organization.defender.group;

import com.qa.framework.webdriver.WebPage;

public class DefenderGroupPage extends WebPage {

	public DefenderGroupPage() {
		sync("elemHWDefenderTabGroupChecked");
		logMethod();
	}
	
	public AddEditDefenderGroupPage clickAddGroup() {
		setObject("btnHWDefenderAddGroup");
		return new AddEditDefenderGroupPage();
	}
	
}
