package com.qatest.functional.huwang.tests.orgnization.defender;

import org.testng.annotations.Test;

import com.qatest.functional.huwang.components.pages.HomePage;
import com.qatest.functional.huwang.components.pages.organization.OrganizationPage;
import com.qatest.functional.huwang.components.pages.organization.defender.DefenderOrgPage;
import com.qatest.functional.huwang.components.pages.organization.defender.group.AddEditDefenderGroupPage;
import com.qatest.functional.huwang.components.pages.organization.defender.group.DefenderGroupPage;
import com.qatest.functional.huwang.components.pages.organization.defender.team.AddEditDefenderTeamPage;
import com.qatest.functional.huwang.components.pages.organization.defender.team.DefenderTeamPage;
import com.qatest.functional.huwang.testbase.HWTestBase;

public class DefenderTest extends HWTestBase {
	private HomePage _homePage = null;

	/**
	 * Test steps: <br/>
	 * 1. New a Defender group.  <br/>
	 * 2. Check the created group is existing on the table. <br/>
	 * 3. New a attacker team with the created defender group. <br/>
	 * 4. Check the created team is existing on the table. <br/>
	 * 
	 * @author liaochunxiang
	 */
	@Test(groups = { "UI", "webdriver", "hw_mats" })
	public void testAddDefenderGroupNTeam() {
		String testID = getName();
		String teamName = getParam("teamName").toString();
		String abbreviation = getParam("abbreviation").toString();	
		String adress = getParam("adress").toString();
		String longitude = getParam("longitude").toString();
		String latitude = getParam("latitude").toString();
		String group = getParam("group").toString();
		_homePage = setUp(USERNAME, PASSWORD, testID);
		OrganizationPage orgPage = _homePage.openOrganizationPage();
		DefenderOrgPage defenderOrgPage = orgPage.openDefenderOrgPage();

		// Add a group
		DefenderGroupPage defenderGroupPage = defenderOrgPage.openGroupPage();
		AddEditDefenderGroupPage addEditDefenderGroupPage = defenderGroupPage.clickAddGroup();
		addEditDefenderGroupPage.addGroup(group, null, null);

		// Check the created group
		boolean statusGroup = addEditDefenderGroupPage.isGroupExisting(group);
		if (statusGroup)
			logInfo("The Group '" + group + "' has been created successfully.");
		else
			logError("Failed to create the Group '" + group + "'.");

		// Add three attacker teams
		DefenderTeamPage defenderTeamPage = defenderOrgPage.openTeamPage();
		AddEditDefenderTeamPage addEditDefenderTeamPage = defenderTeamPage.clickAddTeam();
		addEditDefenderTeamPage.addTeam(teamName, abbreviation, "北京市",adress, longitude, latitude,"政务", null, group, "政府机关");

		// Check the created team
		boolean statusTeam = addEditDefenderTeamPage.isTeamExisting(teamName);
		if (statusTeam)
			logInfo("The team '" + teamName + "' has been created successfully.");
		else
			logError("Failed to create the team '" + teamName + "'.");
	}
	
}
