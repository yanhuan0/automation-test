package com.qatest.functional.huwang.restapitests.organization.deployments;

import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.testng.annotations.Test;

import com.qatest.functional.huwang.components.retapi.RestAPIPath;
import com.qatest.functional.huwang.testbase.RestAPITestBase;
import com.qatest.functional.util.JSONUtil;

public class DeploymentsAPITest extends RestAPITestBase {
	/**
	 * Query Department in Organization
	 * 
	 * @author liaochunxiang
	 */
	@Test(groups = { "hw_api" })
	public void testDepartments() {
		String testID = getName();
		String[] checkValues = getParam(testID, "checkValues").split("/");
		Response response_department = doGet(RestAPIPath.GET_DEPARTMENTS_LIST);
		JSONObject jsonObject = getJSONObject(response_department);
		logInfo(jsonObject.toString());
		boolean result = JSONUtil.checkParameterValue(jsonObject, checkValues[0], checkValues[1]);
		if (result) {
			logInfo("Department '" + checkValues[1] + "' exists.");
		} else {
			logError("Department '" + checkValues[1] + "' does not exist.");
		}
	}

}
