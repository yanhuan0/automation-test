package com.qatest.functional.huwang.components.pages.organization.attacker.group;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.util.PageSyncUtil;
import com.qatest.functional.util.QAConstants;

public class AddEditAttackerGroupPage extends WebPage {

	public AddEditAttackerGroupPage() {
		sync("elemHWAttackerGroupName");
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
	public void addGroup(String groupName, String description, String teamNames) {
		setObject("elemHWAttackerGroupName", groupName);
		if (description != null)
			setObject("elemHWAttackerGroupDes", description);
		if (teamNames != null) {
			String[] names = teamNames.split(";");
			// TODO
		}

		setObject("btnHWAttackerAddOK" + "(" + "添加攻击组" + "," + "确 定" + ")");
		PageSyncUtil.waitForAlertGone(getDriver(), QAConstants.DEFAULT_IMPLICIT_WAIT);
	}

	/**
	 * Check if the attacker group is existing.
	 * 
	 * @param groupName
	 * @return
	 * @author liaochunxiang
	 */
	public boolean isGroupExisting(String groupName) {
		if (waitForObject("elemHWDivText" + "(" + groupName + ")"))
			return true;
		else
			return false;
	}

}
