package com.qatest.functional.huwang.components.pages.organization.defender.group;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.util.PageSyncUtil;
import com.qatest.functional.util.QAConstants;

public class AddEditDefenderGroupPage extends WebPage {

	public AddEditDefenderGroupPage() {
		sync("elemHWDefenderGroupName");
		logMethod();
	}

	/**
	 * Add a defender group with the following properties
	 * 
	 * @param groupName
	 * @param description
	 * @param teamNames
	 * @author liaochunxiang
	 */
	public void addGroup(String groupName, String description, String teamNames) {
		setObject("elemHWDefenderGroupName", groupName);
		if (description != null)
			setObject("elemHWDefenderGroupDes", description);
		if (teamNames != null) {
			String[] names = teamNames.split(";");
			// TODO
		}

		setObject("btnHWDefenderAddOK" + "(" + "添加防守组" + "," + "确 定" + ")");
		PageSyncUtil.waitForAlertGone(getDriver(), QAConstants.DEFAULT_IMPLICIT_WAIT);
	}

	/**
	 * Check if defender group is existing.
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
