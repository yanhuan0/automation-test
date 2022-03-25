package com.qatest.functional.huwang.components.pages.organization.defender.team;

import org.openqa.selenium.JavascriptExecutor;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.util.PageSyncUtil;
import com.qatest.functional.util.QAConstants;

public class AddEditDefenderTeamPage extends WebPage {

	public AddEditDefenderTeamPage() {
		sync("elemHWDefenderTeamNameTitle");
		logMethod();
	}

	/**
	 * Add a defender team with the following properties
	 * 
	 * @param teamName
	 * @param abbreviation
	 * @param location
	 * @param adress
	 * @param longitude
	 * @param latitude
	 * @param department
	 * @param logo
	 * @param group
	 * @param type
	 * @author liaochunxiang
	 */
	public void addTeam(String teamName, String abbreviation, String location, String adress, String longitude,
			String latitude, String department, String logo, String group, String type) {
		setObject("editHWDefenderTeamName", teamName);
		setObject("editHWDefenderTeamAbbreviation", abbreviation);
		if (location != null) {
			setObject("editHWDefenderTeamLocation");
			selectItem(location);
		}

		setObject("editHWDefenderTeamAddress", adress);
		setObject("editHWDefenderTeamLongitude", longitude);
		setObject("editHWDefenderTeamLatitude", latitude);

		if (department != null) {
			setObject("elemHWDefenderTeamDepartment");
			selectItem(department);
		}

		if (logo != null) {

		}

		if (group != null) {
			setObject("editHWDefenderTeamGroup");
			selectItem(group);
		}

		if (type != null) {
			setObject("elemHWDefenderTeamProps");
			selectItem(type);
		}
		setObject("btnHWDefenderAddOK" + "(" + "添加队伍信息" + "," + "确 定" + ")");
		PageSyncUtil.waitForAlertGone(getDriver(), QAConstants.DEFAULT_IMPLICIT_WAIT);
	}

	/**
	 * Check the defender team existing on the table.
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
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			js.executeScript("arguments[0].scrollIntoViewIfNeeded(true);",
					getObject("elemHWDefenderTeamPropsItem" + "(" + itemName + ")"));
			if (isVisible("elemHWDefenderTeamPropsItem" + "(" + itemName + ")")) {
				setObject("elemHWDefenderTeamPropsItem" + "(" + itemName + ")");
				sleep(1000);
			} else
				logError("The item '" + itemName + "' does not exist.");
		} else
			logError("The item '" + itemName + "' does not exist.");
	}

}
