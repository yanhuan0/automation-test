package com.qatest.functional.huwang.components.pages.organization.attacker.group;

import com.qa.framework.webdriver.WebPage;

public class AttackerGroupPage extends WebPage {

	public AttackerGroupPage() {
		sync("elemHWAttackerTabGroupChecked");
		logMethod();
	}
	
	public AddEditAttackerGroupPage clickAddGroup() {
		setObject("btnHWAttackerAddGroup");
		return new AddEditAttackerGroupPage();
	}
	
}
