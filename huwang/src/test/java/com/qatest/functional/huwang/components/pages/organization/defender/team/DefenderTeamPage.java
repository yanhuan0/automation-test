package com.qatest.functional.huwang.components.pages.organization.defender.team;

import com.qa.framework.webdriver.WebPage;

public class DefenderTeamPage extends WebPage {

	public DefenderTeamPage() {
		sync("elemHWDefenderTabTeamChecked");
		logMethod();
	}

	public AddEditDefenderTeamPage clickAddTeam() {
		setObject("btnHWDefenderAddTeam");
		return new AddEditDefenderTeamPage();
	}
}
