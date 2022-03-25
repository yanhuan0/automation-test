package com.qatest.functional.huwang.testbase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;

import com.google.gson.JsonObject;
import com.qatest.functional.huwang.components.retapi.BaseRestAPI;
import com.qatest.functional.huwang.components.retapi.RestAPIPath;
import com.qatest.functional.util.CompareUtil;
import com.qatest.functional.util.JSONUtil;
import com.qatest.functional.util.SSLFix;

public class RestAPITestBase extends BaseRestAPI {

	private Client client = null;
	private String baseURL = null;
	private CompareUtil m_compUtil = new CompareUtil();
	private NewCookie cookieT = null;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		if (isFirstTest())
			logIn(USERNAME, PASSWORD);
		else
			logInfo("Using the existed 'Client'.");
	}

	public void logIn(String userName, String password) {
		this.baseURL = System.getProperty("base.url");
		String loginAPIPath = this.baseURL + RestAPIPath.loginPath;
		SSLFix.execute();
		this.client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", userName);
		jsonObject.addProperty("password", password);
		jsonObject.addProperty("captcha", "testapi");
		jsonObject.addProperty("checked", true);

		WebTarget target = client.target(loginAPIPath);
		Response response = target.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(jsonObject.toString(), MediaType.APPLICATION_JSON));
		cookieT = response.getCookies().get("T");

		if (cookieT == null)
			logError("The cookie 'cookieT' is null.");

		int status = response.getStatus();
		String statusInfo = response.getStatusInfo().toString();

		if (status == 200 || statusInfo.equalsIgnoreCase("OK"))
			logInfo("Log into the server is successful");
		else
			logError("Log into the server is failed");
	}

	
	private Invocation.Builder build(String apiPath) {		
		WebTarget webTarget = client.target(this.baseURL + apiPath);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.cookie(cookieT);
		return invocationBuilder;
	}
	
	public Response doPost(String apiPath, String bodyTxtFile) {
		logInfo("Test Rest API: " + apiPath);
		Invocation.Builder builder = build(apiPath);
		String txtFile = null;
		Response response = null;

		try {
			txtFile = readFile(getDatDir() + "/" + bodyTxtFile);
			logInfo("txtFile=" + txtFile);
			response = builder.post(Entity.entity(txtFile, MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			logError("Post action is failed:\n" + e);
		}

		return response;
	}

	public Response doGet(String apiPath, Map<String, String> parms) {
		logInfo("Test Rest API: " + apiPath);
		Invocation.Builder builder = build(apiPath);
		Response response = null;

		try {
			if (parms != null) {
				for (Map.Entry<String, String> entry : parms.entrySet()) {

					builder.property(entry.getKey(), entry.getValue());
				}
			}
			response = builder.get();
		} catch (Exception e) {
			logError("Get action is failed:\n" + e);
		}

		return response;
	}

	public Response doGet(String apiPath) {
		Response response = doGet(apiPath, null);
		return response;
	}

	
	public JSONObject getJSONObject(Response response) {
		JSONObject jObject = null;

		try {
			if (response != null) {
				String responseString = response.readEntity(String.class);
				jObject = new JSONObject(responseString);
			} else {
				return null;
			}

		} catch (Exception e) {
			logError(e.toString());
		}
		return jObject;
	}

	public String readFile(String reportFile) throws IOException {
		File file = new File(reportFile);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufRead = new BufferedReader(fileReader);
		StringBuffer buf = new StringBuffer();

		String line;
		int count = 0;

		// Read first line
		line = bufRead.readLine();
		while (line != null) {
			buf.append(line);
			count++;
			line = bufRead.readLine();
		}
		bufRead.close();

		return buf.toString();
	}

	public Client getClient() {
		return this.client;
	}

	public String getBaseURL() {
		return this.baseURL;
	}

	public String getSrcDir() {
		String[] srcDirs = getTestDir().split("/");
		String srcDir = "";
		for (int i = 0; i < srcDirs.length - 1; i++) {
			if (i < srcDirs.length - 2)
				srcDir = srcDir + srcDirs[i] + "/";
			else
				srcDir = srcDir + srcDirs[i];
		}
		return srcDir;
	}

	public String getExcelDir() {
		return getSrcDir() + "/" + "dat";
	}

	public String getDatDir() {
		return getTestDir() + "/" + "dat";
	}

	public void writeToFile(String text, File fileName) {
		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write(text);
			bw.newLine();
			bw.newLine();
			bw.close();
		} catch (Exception e) {
		}
	}

		public boolean findAString(File f, String searchString) {
		boolean result = false;
		Scanner in = null;
		try {
			in = new Scanner(new FileReader(f));
			while (in.hasNextLine() && !result) {
				result = in.nextLine().indexOf(searchString) >= 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) { /* ignore */
			}
		}
		return result;
	}

	public void saveJSONObject(String testID, JSONObject jo) {
		BufferedWriter writer = null;
		File file = new File(getResultDir() + "/" + testID + ".json");
		// if (!file.exists()) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// }
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(jo.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void compareJSONFiles(String testID, String jsonFile) {
		compareJSONFiles(testID, jsonFile, null, null);
	}

	public void compareJSONFiles(String testID, String BaseJOFile, String sourceJOFile) {
		compareJSONFiles(testID, BaseJOFile, sourceJOFile, null);
	}

	public void compareJSONFiles(String testID, String BaseJOFile, String[] replaceKeys) {
		compareJSONFiles(testID, BaseJOFile, null, replaceKeys);
	}

	public void compareJSONFiles(String testID, String baseJOFile, String sourceJOFile, String[] replaceKeys) {

		String outputJOFilePath = getResultDir() + "/" + testID + "_Output.json";
		JSONObject outputJO = JSONUtil.readJSONFile(new File(outputJOFilePath));

		if (replaceKeys != null) {
			for (String key : replaceKeys) {
				String replaceStr = JSONUtil.getKeysValue(outputJO, key);
				outputJO = JSONUtil.replaceKeyValue(outputJO, replaceStr, "repaceValue");
			}
		}

		String sourceFileName = testID;
		if (sourceJOFile != null)
			sourceFileName = sourceJOFile.split("\\.")[0];

		saveJSONObject(sourceFileName, outputJO);

		System.setProperty("biee.result.mode", "true");
		String baseJOFilePath = getBaseDir() + "/" + baseJOFile;
		String sourceFilePath = getResultDir() + "/" + sourceFileName + ".json";

		try {
			m_compUtil.compareTextFiles(baseJOFilePath, sourceFilePath);
		} catch (IOException e) {
			logger.error("JSON data is not as expected and log is: " + e.toString());
			;
		} catch (Exception ex) {
			logger.error("JSON data is not as expected and log is: " + ex.toString());
			;
		}

	}

	public boolean checkValuesInFile(String testID, String values) {
		String[] valueList = values.split(";");
		String outputJOFilePath = getResultDir() + "/" + testID + "_Output.json";
		String outputJOStr = JSONUtil.readJSONFile(new File(outputJOFilePath)).toString();
		boolean result = false;
		for (String value : valueList) {
			if (outputJOStr.indexOf(value) > 0) {
				result = true;
				break;
			}
		}
		return result;

	}
}
