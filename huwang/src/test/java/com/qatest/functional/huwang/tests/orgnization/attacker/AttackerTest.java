package com.qatest.functional.huwang.tests.orgnization.attacker;

import org.testng.annotations.Test;

import com.qatest.functional.huwang.components.pages.HomePage;
import com.qatest.functional.huwang.components.pages.organization.OrganizationPage;
import com.qatest.functional.huwang.components.pages.organization.attacker.AttackerOrgPage;
import com.qatest.functional.huwang.components.pages.organization.attacker.group.AddEditAttackerGroupPage;
import com.qatest.functional.huwang.components.pages.organization.attacker.group.AttackerGroupPage;
import com.qatest.functional.huwang.components.pages.organization.attacker.team.AddEditAttackerTeamPage;
import com.qatest.functional.huwang.components.pages.organization.attacker.team.AttackerTeamPage;
import com.qatest.functional.huwang.testbase.HWTestBase;

public class AttackerTest extends HWTestBase {
	private HomePage _homePage = null;

	/**
	 * Test steps: <br/>
	 * 1. New a Attacker group.  <br/>
	 * 2. Check the created group is existing on the table. <br/>
	 * 3. New a attacker team with the created attacker group. <br/>
	 * 4. Check the created team is existing on the table. <br/>
	 * 
	 * @author liaochunxiang
	 */
	@Test(groups = { "UI", "webdriver", "hw_mats" })
	public void testAddAttackerGroupNTeam() {
		String testID = getName();
		String teamName = getParam("teamName").toString();
		String abbrName = getParam("abbrName").toString();
		String unit = getParam("unit").toString();
		String adress = getParam("adress").toString();
		String longitude = getParam("longitude").toString();
		String latitude = getParam("latitude").toString();
		String group = getParam("group").toString();
		String type = getParam("type").toString();
		_homePage = setUp(USERNAME, PASSWORD, testID);
		OrganizationPage orgPage = _homePage.openOrganizationPage();
		AttackerOrgPage attacherorgPage = orgPage.openAttackerOrgPage();

		// Add a group
		AttackerGroupPage groupPage = attacherorgPage.openGroupPage();
		AddEditAttackerGroupPage addEditGroupPage = groupPage.clickAddGroup();
		addEditGroupPage.addGroup(group, null, null);

		// Check the created group
		boolean statusGroup = addEditGroupPage.isGroupExisting(group);
		if (statusGroup)
			logInfo("The Group '" + group + "' has been created successfully.");
		else
			logError("Failed to create the Group '" + group + "'.");

		// Add three attacker teams
		AttackerTeamPage teamPage = attacherorgPage.openTeamPage();
		AddEditAttackerTeamPage addEditTeamPage = teamPage.clickAddTeam();
		addEditTeamPage.addTeam(teamName, abbrName, unit, adress, longitude, latitude, null, group, type);

		// Check the created team
		boolean statusTeam = addEditTeamPage.isTeamExisting(teamName);
		if (statusTeam)
			logInfo("The team '" + teamName + "' has been created successfully.");
		else
			logError("Failed to create the team '" + teamName + "'.");
	}
	
}
