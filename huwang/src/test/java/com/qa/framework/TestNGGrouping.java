package com.qa.framework;

import java.util.ArrayList;
import java.util.List;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

public class TestNGGrouping implements IMethodInterceptor {
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		List<IMethodInstance> results = new ArrayList<IMethodInstance>(methods);
		String TEST_GROUPS = System.getProperty("TEST_GROUPS");
		if (TEST_GROUPS == null || TEST_GROUPS.length() == 0) {
			results = methods;
		} else {
			String[] runGroups = TEST_GROUPS.split("\\,");
			for (IMethodInstance m : methods) {
				String[] groups = m.getMethod().getGroups();
				for (String runGroup : runGroups) {
					boolean foundGroup = false;
					for (String group : groups) {
						if (group.trim().equalsIgnoreCase(runGroup.trim())) {
							foundGroup = true;
							break;
						}
					}
					if (foundGroup == false) {
						results.remove(m);
						break;
					}
				}
			}
		}

		return results;
	}

}
