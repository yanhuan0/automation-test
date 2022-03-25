package com.qatest.functional.huwang.components.pages;

import com.qa.framework.webdriver.WebPage;
import com.qatest.functional.huwang.components.pages.organization.OrganizationPage;
import com.qatest.functional.util.HWPageUtil;

public class HomePage extends WebPage {

	public HWPageUtil m_util = new HWPageUtil(this);

	public HomePage() {
		sync("elemHWLayoutHeaderInfo");
		logMethod();
	}

	public void changeUserPwd(String password) {

	}

	public void logOut() {

	}

	/**
	 * Open organization page on home page.
	 * 
	 * @return
	 * @author liaochunxiang
	 */
	public OrganizationPage openOrganizationPage() {
		boolean status = m_util.selectFirstMenu(StaticPageContants.ORGANIZATION_NODE);
		if(!status)
			logError("Fail to open Organization page.");
		return new OrganizationPage();
	}
}
