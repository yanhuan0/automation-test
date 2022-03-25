package com.qatest.functional.huwang.components.pages.organization.defender;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.huwang.components.pages.organization.defender.group.DefenderGroupPage;
import com.qatest.functional.huwang.components.pages.organization.defender.resource.DefenderResourcePage;
import com.qatest.functional.huwang.components.pages.organization.defender.team.DefenderTeamPage;

public class DefenderOrgPage extends WebPage {

	public DefenderOrgPage() {
		sync("elemHWDefenderTabTeamChecked");
		logMethod();
	}

	/**
	 * Open the defender team page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public DefenderTeamPage openTeamPage() {
		if (waitForObject("elemHWDefenderTabTeamNotChecked", 10))
			setObject("elemHWDefenderTabTeamNotChecked");
		return new DefenderTeamPage();
	}

	/**
	 * Open the defender group page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public DefenderGroupPage openGroupPage() {
		if (waitForObject("elemHWDefenderTabGroupNotChecked", 10))
			setObject("elemHWDefenderTabGroupNotChecked");
		return new DefenderGroupPage();
	}

	/**
	 * Open the defender resource page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public DefenderResourcePage openResoucePage() {
		if (waitForObject("elemHWDefenderTabResourceNotChecked", 10))
			setObject("elemHWDefenderTabResourceNotChecked");
		return new DefenderResourcePage();
	}
}
