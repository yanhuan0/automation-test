package com.qatest.functional.util;

import com.qa.framework.webdriver.UIAutomation;

/**
 * Simple JUnit test file used for testing framework.
 * 
 */

public class HWPageUtil {

	private UIAutomation m_auto;

	public HWPageUtil(UIAutomation auto) {
		m_auto = auto;
	}

	/**
	 * Select the first level menu
	 * 
	 * @param firstMenu is the first level menu
	 * @author liaochunxiang
	 */
	public boolean selectFirstMenu(String firstMenu) {

		if (m_auto.waitForObject("elemHWMenuNodeNotOpened" + "(" + firstMenu + ")", 10))
			m_auto.setObject("elemHWMenuNodeNotOpened" + "(" + firstMenu + ")");
		if (m_auto.waitForObject("elemHWMenuNodeOpened" + "(" + firstMenu + ")"))
			return true;
		else
			return false;

	}
	
	/**
	 * Select sub menu
	 * 
	 * @param subMenu is the sub menu
	 * @author liaochunxiang
	 */
	public boolean selectSubMenu(String subMenu) {

		if (m_auto.waitForObject("elemHWSubMenuNotSelected" + "(" + subMenu + ")", 10))
			m_auto.setObject("elemHWSubMenuNotSelected" + "(" + subMenu + ")");
		if (m_auto.waitForObject("elemHWSubMenuSelected" + "(" + subMenu + ")"))
			return true;
		else
			return false;

	}
}
