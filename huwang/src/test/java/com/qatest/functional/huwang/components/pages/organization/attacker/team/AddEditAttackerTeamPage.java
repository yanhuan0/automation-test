package com.qatest.functional.huwang.components.pages.organization.attacker.team;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.util.PageSyncUtil;
import com.qatest.functional.util.QAConstants;

public class AddEditAttackerTeamPage extends WebPage {

	public AddEditAttackerTeamPage() {
		sync("elemHWAttackerTeamNameTitle");
		logMethod();
	}

	/**
	 * Add a attacker team with the following properties
	 * 
	 * @param teamName
	 * @param adress
	 * @param longitude
	 * @param latitude
	 * @param logo
	 * @param group
	 * @param type
	 * @author liaochunxiang
	 */
	public void addTeam(String teamName, String abbrName, String unit, String adress, String longitude, String latitude, String logo, String group,
			String type) {
		setObject("editHWAttackerTeamName", teamName);
		setObject("editHWAttackerTeamAbbreviationName", abbrName);
		setObject("editHWAttackerTeamUnit", unit);
		
		setObject("editHWAttackerTeamAddress", adress);
		setObject("editHWAttackerTeamLongitude", longitude);
		setObject("editHWAttackerTeamLatitude", latitude);
		if (logo != null) {

		}
		if (group != null) {
			setObject("editHWAttackerTeamGroup");
			selectItem(group);
		}
		
		if (type != null) {
			setObject("elemHWAttackerTeamProps");
			selectItem(type);
		}
		
		setObject("btnHWAttackerAddOK" + "(" + "添加队伍信息" + "," + "确 定" + ")");
		PageSyncUtil.waitForAlertGone(getDriver(), QAConstants.DEFAULT_IMPLICIT_WAIT);
	}

	/**
	 * Check the team existing on the table.
	 * 
	 * @param teamName
	 * @return
	 * @author liaochunxiang
	 */
	public boolean isTeamExisting(String teamName) {
		if (waitForObject("elemHWDivText" + "(" + teamName + ")"))
			return true;
		else
			return false;
	}
	
	/**
	 * Select the item on page
	 * 
	 * @param itemName
	 * @author liaochunxiang
	 */
	private void selectItem(String itemName) {
		if (waitForObject("elemHWDefenderTeamPropsItem" + "(" + itemName + ")")) {
			setObject("elemHWDefenderTeamPropsItem" + "(" + itemName + ")");
			sleep(1000);
		}
		else
			logError("The item '" + itemName + "' does not exist.");
	}

}
