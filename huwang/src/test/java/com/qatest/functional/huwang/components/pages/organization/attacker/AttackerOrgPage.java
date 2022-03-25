package com.qatest.functional.huwang.components.pages.organization.attacker;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.huwang.components.pages.organization.attacker.group.AttackerGroupPage;
import com.qatest.functional.huwang.components.pages.organization.attacker.resource.AttackerResourcePage;
import com.qatest.functional.huwang.components.pages.organization.attacker.team.AttackerTeamPage;

public class AttackerOrgPage extends WebPage {

	public AttackerOrgPage() {
		sync("elemHWAttackerTabTeamChecked");
		logMethod();
	}
	
	/**
	 * Open the attacker team page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public AttackerTeamPage openTeamPage() {
		if(waitForObject("elemHWAttackerTabTeamNotChecked", 10))
			setObject("elemHWAttackerTabTeamNotChecked");
		return new AttackerTeamPage();
	}
	
	/**
	 * Open the attacker group page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public AttackerGroupPage openGroupPage() {
		if(waitForObject("elemHWAttackerTabGroupNotChecked", 10))
			setObject("elemHWAttackerTabGroupNotChecked");
		return new AttackerGroupPage();
	}
	
	/**
	 * Open the attacker resource page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public AttackerResourcePage openResoucePage() {
		if(waitForObject("elemHWAttackerTabResourceNotChecked", 10))
			setObject("elemHWAttackerTabResourceNotChecked");
		return new AttackerResourcePage();
	}
}
