package com.qatest.functional.huwang.restapitests.organization.attacker;

import javax.ws.rs.core.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;
import com.qatest.functional.huwang.components.retapi.RestAPIPath;
import com.qatest.functional.huwang.testbase.RestAPITestBase;
import com.qatest.functional.util.JSONUtil;

public class AttackerAPITest extends RestAPITestBase {

	/**
	 * 1. Add attacker team with api <br/>
	 * 2. Query attacker team with api <br/>
	 * 
	 * @author liaochunxiang
	 */
	@Test(groups = { "hw_api" })
	public void testAddAttackerTeam() {
		String testID = getName();
		String attackerTeamFile = getParam(testID, "attackerTeamFile");
		String[] checkValues = getParam(testID, "checkValues").split("/");
		Response response_addAttackTeam = doPost(RestAPIPath.ATTACKER_TEAM_INFO, attackerTeamFile);
		if (response_addAttackTeam.getStatus() == 200) {
			logInfo("Add attack team successfully.");
		} else {
			logError("Failed to add attack team.");
		}

		Response response_attackTeamList = doGet(RestAPIPath.GET_ATTACKER_GROUP_LIST);
		JSONObject jsonObject = getJSONObject(response_attackTeamList);
		logInfo(jsonObject.toString());
		boolean result = JSONUtil.checkParameterValue(jsonObject, checkValues[0], checkValues[1]);
		if (result) {
			logInfo("Attacker team '" + checkValues[1] + "' exists.");
		} else {
			logError("Attacker team '" + checkValues[1] + "' does not exist.");
		}

	}

}
