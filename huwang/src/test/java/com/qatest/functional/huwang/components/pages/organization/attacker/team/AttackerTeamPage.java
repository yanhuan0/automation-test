package com.qatest.functional.huwang.components.pages.organization.attacker.team;

import com.qa.framework.webdriver.WebPage;

public class AttackerTeamPage extends WebPage {

	public AttackerTeamPage() {
		sync("elemHWAttackerTabTeamChecked");
		logMethod();
	}

	public AddEditAttackerTeamPage clickAddTeam() {
		setObject("btnHWAttackerAddTeam");
		return new AddEditAttackerTeamPage();
	}
}
