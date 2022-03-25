package com.qatest.functional.huwang.components.pages.organization;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.huwang.components.pages.StaticPageContants;
import com.qatest.functional.huwang.components.pages.organization.attacker.AttackerOrgPage;
import com.qatest.functional.huwang.components.pages.organization.defender.DefenderOrgPage;
import com.qatest.functional.util.HWPageUtil;

public class OrganizationPage extends WebPage {
	public HWPageUtil m_util = new HWPageUtil(this);
	
	public OrganizationPage() {
		sync("elemHWMenuNodeOpened" + "(" + StaticPageContants.ORGANIZATION_NODE + ")");
		logMethod("Opened organization page");
	}

	/**
	 * Select the attacker team organization page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public AttackerOrgPage openAttackerOrgPage() {

		boolean status = m_util.selectSubMenu(StaticPageContants.ATTACKER_TEAM_ORG_NODE);
		if(!status)
			logError("Fail to open attacker organization page.");
		
		return new AttackerOrgPage();
	}

	/**
	 * Select the defender team organization page¡£
	 * 
	 * @author liaochunxiang
	 * @return
	 */
	public DefenderOrgPage openDefenderOrgPage() {

		boolean status = m_util.selectSubMenu(StaticPageContants.DEFENDER_TEAM_ORG_NODE);
		if(!status)
			logError("Fail to open defender organization page.");
		
		return new DefenderOrgPage();
	}
}
