
package com.qa.framework;

public class ParameterConstants {

	public static final String ROOT_DIR = "root.dir";
	public static final String TESTDATA = "testdata";
	public static final String PARAM = "Param";
	public static final String NAME = "name";
	public static final String METHOD = "method";
	public static String QA_WORK = null;// = System.getProperty("qa.t.work");
	public static String AUTO_WORK = null;// = System.getProperty("qa.auto.home");
	public static String HOSTNAME = "myhost";;// = System.getProperty("qa.hostname");
	public static String URL = "myurl"; // = System.getProperty("qa.url", "url");
	public static String USERNAME = "username";// = System.getProperty("qa.user", "username");
	public static String PASSWORD = "password";// = System.getProperty("qa.pwd", "password");
	public static String MAIL_USER = null;// = System.getProperty("qa.mail.user","bitest");
	public static String MAIL_PWD = null;// = System.getProperty("qa.mail.pwd","bitest");
	public static String QA_HOME = null;// = System.getProperty("qa.qa.home");
	public static String TESTOUT = null;   // = System.getProperty("qa.testout");
	public static String DOR_ROOT_DIRS = null;// = System.getProperty("qa.dor.dirs");
	public static String RESULT_MODE = null;// = System.getProperty("qa.result.mode");
	public static boolean CLEANUP_AFTER_TEST = true;// = Boolean.parseBoolean(
//                                                System.getProperty("qa.cleanup.after.test", "false"));

	public static double IMG_MATCH_THRESHOLD = 0.95;// =
													// Double.parseDouble(System.getProperty("qa.image.threshold","0.95"));

	public static int WAIT_TIME_UNIT = 1000;// =Integer.parseInt(System.getProperty("qa.wait.time.unit","1000"));
	public static int m_smallTime = WAIT_TIME_UNIT * 2;
	public static int m_mediamTime = WAIT_TIME_UNIT * 4;
	public static int m_mediumTime = WAIT_TIME_UNIT * 4;
	public static int m_largeTime = WAIT_TIME_UNIT * 6;
	public static int WAIT_TIMEOUT_MS = 90000;
	public static int m_waitTime = WAIT_TIMEOUT_MS;
	public static String DELIMITATE_REGEXP = ",";

	public static final String BROWSER_TYPE_FF = "firefox";
	public static final String BROWSER_TYPE_IE = "iexplore";
	public static String BROWSER_TYPE = null; // System.getProperty("qa.browser", BROWSER_TYPE_FF);

	public static String LINE_SEPARATOR = null; // System.getProperty("line.separator");

	public static int BROWSER_OFFSET_X = 1; // Integer.parseInt(System.getProperty("qa.browser.offset.x", "1"));
	public static int BROWSER_OFFSET_Y = 112; // Integer.parseInt(System.getProperty("qa.browser.offset.x", "112"));
	public static int BROWSER_HEIGHT = 0;
	public static int BROWSER_WIDTH = 0;

	public static void setProperties() {

		QA_WORK = System.getProperty("qa.t.work");
		AUTO_WORK = System.getProperty("qa.auto.home");
		HOSTNAME = System.getProperty("qa.hostname");
		URL = System.getProperty("qa.url");
		USERNAME = System.getProperty("qa.user")==null?"admin":System.getProperty("qa.user");	
		PASSWORD = System.getProperty("qa.pwd")==null?"123456":System.getProperty("qa.pwd");
		TESTOUT = System.getProperty("qa.testout");
		DOR_ROOT_DIRS = System.getProperty("qa.dor.dirs");
		RESULT_MODE = System.getProperty("qa.result.mode");
		CLEANUP_AFTER_TEST = Boolean.parseBoolean(System.getProperty("qa.cleanup.after.test", "false"));
		IMG_MATCH_THRESHOLD = Double.parseDouble(System.getProperty("qa.image.threshold", "0.95"));
		WAIT_TIME_UNIT = Integer.parseInt(System.getProperty("qa.wait.time.unit", "1000"));
		BROWSER_TYPE = System.getProperty("qa.browser", BROWSER_TYPE_FF);
		LINE_SEPARATOR = System.getProperty("line.separator");
		BROWSER_OFFSET_X = Integer.parseInt(System.getProperty("qa.browser.offset.x", "1"));
		BROWSER_OFFSET_Y = Integer.parseInt(System.getProperty("qa.browser.offset.x", "112"));
		BROWSER_WIDTH = Integer.parseInt(System.getProperty("qa.browser.width", "0"));
		BROWSER_HEIGHT = Integer.parseInt(System.getProperty("qa.browser.height", "0"));
	}
}
