package com.qa.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.net.InetAddress;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.qa.framework.imagetool.ImageCompareFinal;

public class AbstractBaseTest extends ParameterConstants implements Test {
	protected static Logger logger = new TestLogger();
	private static boolean _testdataLoaded;

	private static Map<String, Properties> m_param = new HashMap<String, Properties>();
	private static Map<String, String> g_param = new HashMap<String, String>();
	private static boolean initLoggerDone;
	private static Properties props = new Properties();
	private String currentTestName;
	private static String _currentTestClassName;
	private static String _previousTestName;
	private static List<String> _xmlTcList;

	static {
		Properties props = new Properties();
		File propFile = new File("local_debug.properties");
		if (propFile.exists()) {
			System.out.println("***Loading the file local_debug.properties***");
			System.out.println("Using local debugging environment: " + propFile.getAbsolutePath());
			try {
				props.load(new FileInputStream(propFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Object key : props.keySet()) {
				String keyStr = key.toString();
				String value = props.getProperty(keyStr);
				System.setProperty(keyStr, value);
				System.out.println(keyStr + " = " + value);
			}
			System.out.println("***Finished***");
		}
	}

	@BeforeClass
	public void setupClass() throws Exception {
		logger.debug("calling AbstractBaseTest.setupClass");
		_testdataLoaded = false;
		initLoggerDone = false;
	}

	protected boolean isFirstTest() {
		String className = getClass().getName();
		if (_currentTestClassName == null || !_currentTestClassName.equals(className)) {
			_currentTestClassName = className;
			_previousTestName = null;
			return true;
		}
		if (_previousTestName == null) {
			return true;
		}
		return false;
	}

	protected void firstTimeSetup() {
		String className = getClass().getName();
		System.out.println("***The first time***");
		System.out.println("Test Suite: " + className);
		_testdataLoaded = false;
		initLoggerDone = false;
		initLogger(true);
		String currentClassName = getClass().getSimpleName();
		File testdataXmlFile = new File(getTestDir(), currentClassName + ".xml");
		if (testdataXmlFile.exists())
			loadTestData(testdataXmlFile.getAbsolutePath());
		else
			logger.warn("data xml file not found: " + testdataXmlFile.getAbsolutePath());
	}

	@BeforeMethod
	public void setupMethod(Method method) {
		logger.debug("calling AbstractBaseTest.setupMethod");
		currentTestName = method.getName();
		setProperties();
		if (!initLoggerDone || isFirstTest()) {
			firstTimeSetup();
		}
		logger.setCurrentTestName(currentTestName);
		logger.beginTest();
		logger.debug("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.debug("Test method: " + currentTestName);
	}

	/**
	 * Call ant target
	 * @param target
	 * @param propertyStr
	 */
	public static void callAnt(String target, String propertyStr) {
		System.out.println("callAnt(" + target + ", " + propertyStr + ")");
		File buildXmlFile = new File(getRootDir() + "/../resources/ant/build.xml");
		Map<String, String> properties = StringUtil.toMap(propertyStr);
		properties.put("QA_WORK", getRootDir() + "/../../../build");
		properties.put("ROOT_DIR", getRootDir());
		String testHost = "localhost";
		try {
			testHost = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		properties.put("testHostname", testHost);
		String bieeHostname = System.getProperty("hostname");
		String bieeHostUser = System.getProperty("qaHostUser");
		String bieeHostPwd = System.getProperty("qaHostPwd");
		if (bieeHostname != null) {
			properties.put("qaHostname", bieeHostname);
			try {
				if (!InetAddress.getByName(bieeHostname).equals(InetAddress.getByName(testHost))) {
					properties.put("qaHostIsRemote", "true");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (bieeHostUser != null) {
			properties.put("qaHostUser", bieeHostUser);
		}
		if (bieeHostPwd != null) {
			properties.put("qaHostPwd", bieeHostPwd);
		}
		String filerRoot = System.getProperty("qa.filer.root");
		properties.put("FILER_ROOT", filerRoot != null ? filerRoot : "/home/files");
		AntUtil.callAnt(buildXmlFile, target, properties);
		System.out.println("callAnt() done");
	}

	public static void setURL(String url) {
		System.out.println("Reset URL to " + url);
		System.setProperty("qa.url", url);
	}

	protected void initLogger(boolean beginSuite) {
		if (initLoggerDone)
			return;
		logger = new TestLogger(new File(getOutputDir()), this.getClass());
		logger.setCurrentTestName(currentTestName);
		if (!this.getClass().toString().contains("restapitests")) {
			copyAndUnzip("base");
			copyAndUnzip("image");
		}
		if (beginSuite)
			logger.beginSuite();
		initLoggerDone = true;
	}

	public void teardownMethod() throws Exception {
		logger.debug(("calling AbstractBaseTest.tearDownMethod"));
		_previousTestName = currentTestName;
	}

	public static void generateSucDif(ITestResult result) {
		String testName = result.getTestClass().getName() + "." + result.getName();
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				Throwable exp = result.getThrowable();
				FileUtil.writeTextFile(getDteTWork() + "/" + testName + ".dif", exp == null ? "" : exp.getMessage());
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				FileUtil.writeTextFile(getDteTWork() + "/" + testName + ".suc", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void teardownClass() throws Exception {
		logger.debug("calling AbstractBaseTest.teardownClass");
		logger.endSuite();
	}

	private static String getPackagePath(Class clas) {
		String fullClassName = clas.getName();
		String path = fullClassName.substring(0, fullClassName.lastIndexOf('.'));
		return path.replace('.', '/');
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}

	public static boolean isMAC() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("mac") >= 0);
	}

	public static boolean isSolaris() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("sunos") >= 0);
	}

	private void copyAndUnzip(String srcType) {
		logDebug("automatically copy/unzip " + srcType + " files");
		try {
			File oriDir = new File(getTestDir(), srcType);
			if (oriDir.exists()) {
				File[] oriFiles = oriDir.listFiles();
				for (File file : oriFiles) {
					if (!file.getName().toLowerCase().endsWith(".zip") && !file.getName().startsWith(".")
							&& !file.isDirectory())
						FileUtil.copyFile(file, new File(getTargetDir(srcType), file.getName()));
				}

				File oriZip = new File(oriDir, srcType + ".zip");
				if (oriZip.exists()) {
					FileUtil.unzip(oriZip.getAbsolutePath(), getTargetDir(srcType));
				}

				for (int i = 1; i <= 10; i++) {
					File oriZipIndex = new File(oriDir, srcType + "_" + i + ".zip");
					if (oriZipIndex.exists()) {
						FileUtil.unzip(oriZipIndex.getAbsolutePath(), getTargetDir(srcType));
					} else
						break;
				}

				oriZip = new File(oriDir, srcType + "_" + getClass().getSimpleName() + ".zip");
				if (oriZip.exists()) {
					logDebug("extracting test suite specific zip file: " + oriZip.getAbsolutePath());
					FileUtil.unzip(oriZip.getAbsolutePath(), getTargetDir(srcType));
				}
				if (isWindows()) {
					File winZip = new File(oriDir, srcType + "_windows.zip");
					if (winZip.exists()) {
						logDebug("extracting Windows specific zip file: " + winZip.getAbsolutePath());
						FileUtil.unzip(winZip.getAbsolutePath(), getTargetDir(srcType));
					}

					for (int i = 1; i <= 10; i++) {
						File winZipIndex = new File(oriDir, srcType + "_" + i + "_windows.zip");
						if (winZipIndex.exists()) {
							FileUtil.unzip(winZipIndex.getAbsolutePath(), getTargetDir(srcType));
						} else
							break;
					}

					File winExtractZip = new File(oriDir, srcType + "_" + getClass().getSimpleName() + "_windows.zip");
					if (winExtractZip.exists()) {
						logDebug("extracting Windows specific zip file: " + winExtractZip.getAbsolutePath());
						FileUtil.unzip(winExtractZip.getAbsolutePath(), getTargetDir(srcType));
					}
				} else if (isMAC()) {
					File macZip = new File(oriDir, srcType + "_mac.zip");
					if (macZip.exists()) {
						logDebug("extracting MAC specific zip file: " + macZip.getAbsolutePath());
						FileUtil.unzip(macZip.getAbsolutePath(), getTargetDir(srcType));
					}

					for (int i = 1; i <= 10; i++) {
						File macZipIndex = new File(oriDir, srcType + "_" + i + "_mac.zip");
						if (macZipIndex.exists()) {
							FileUtil.unzip(macZipIndex.getAbsolutePath(), getTargetDir(srcType));
						} else
							break;
					}

					// extracting test suite specific zip file
					File macExtractZip = new File(oriDir, srcType + "_" + getClass().getSimpleName() + "_mac.zip");
					if (macExtractZip.exists()) {
						logDebug("extracting Windows specific zip file: " + macExtractZip.getAbsolutePath());
						FileUtil.unzip(macExtractZip.getAbsolutePath(), getTargetDir(srcType));
					}
				} else {
					File[] otherZips = FileUtil.listAllFiles(oriDir.getAbsolutePath(), srcType + "_");
					for (File otherZip : otherZips) {
						if (otherZip.getAbsolutePath().contains(".ade_path"))
							continue;
						String fileName = otherZip.getName().toLowerCase();
						if (fileName.endsWith(".zip") && (fileName.startsWith(srcType + "_linux")
								|| fileName.startsWith(srcType + "_solaris"))) {
							FileUtil.unzip(otherZip.getAbsolutePath(), getTargetDir(srcType));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logError(e.getMessage());
		}
	}

	public String getName() {
		return currentTestName;
	}

	public String getBaseDir() {
		if (initLoggerDone)
			return logger.getBaseDir().getAbsolutePath();
		return getTargetDir("base");
	}

	private String getTargetDir(String target) {
		return FileUtil.createDirIfNotExist(getOutputDir() + File.separator + target);
	}

	public String getResultDir() {
		if (initLoggerDone)
			return logger.getResultDir().getAbsolutePath();
		return getTargetDir("result");
	}

	public String getOutputDir() {
		if (initLoggerDone)
			return logger.getOutputDir().getAbsolutePath();
		String tWork = QA_WORK;
		if (tWork == null)
			tWork = System.getenv("QA_WORK");
		if (tWork == null)
			tWork = System.getProperty("qa.t.work");
		if (tWork == null || tWork.trim().length() == 0)
			throw new FrameworkException("Java system property qa.t.work or environment variable QA_WORK must be set");
		return FileUtil
				.createDirIfNotExist(tWork + File.separator + "SELENIUM" + File.separator + getClass().getSimpleName());
	}

	protected static String getTestData() {
		return System.getProperty(TESTDATA);
	}

	protected static String getTestDataRootDir() {
		return new File(System.getProperty(TESTDATA)).getParent();
	}

	protected static String getRootDir() {
		return System.getProperty(ROOT_DIR);
	}

	protected static String getDteTWork() {
		return getRootDir() + "/../../../../../..";
	}

	public String getTestDir() {
		return getRootDir() + "/" + getPackagePath(this.getClass());
	}

	private static void loadTestData(String testdataFile) {
		logger.debug("loadTestData(" + testdataFile + ")");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document dom = db.parse("file:///" + testdataFile);
			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName(PARAM);
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element paramEl = (Element) nl.item(i);
					if (paramEl.getParentNode() == docEle) {
						String attrName = paramEl.getAttribute(NAME);
						String nodeVal = paramEl.getTextContent();
						g_param.put(attrName, nodeVal);
					}
				}
			}
			_xmlTcList = new ArrayList<String>();
			NodeList tcNodes = docEle.getElementsByTagName("TestCase");
			if (tcNodes != null && tcNodes.getLength() > 0) {
				for (int i = 0; i < tcNodes.getLength(); i++) {
					Element tcEl = (Element) tcNodes.item(i);
					String tcId = tcEl.getAttribute("id");
					NodeList tcParams = tcEl.getElementsByTagName(PARAM);
					if (tcParams != null && tcParams.getLength() > 0) {
						Properties params = new Properties();
						for (int j = 0; j < tcParams.getLength(); j++) {
							Element paramEl = (Element) tcParams.item(j);
							String attrName = paramEl.getAttribute(NAME);
							String nodeVal = paramEl.getTextContent();
							if (attrName.equals("method") && !nodeVal.equals(tcId)) {
								logger.warn("method name [" + nodeVal + "] does not match " + "test case id [" + tcId
										+ "]");
								tcId = nodeVal;
							}
							// todo: check error
							params.put(attrName, nodeVal);
						}
						m_param.put(tcId, params);
					}
					_xmlTcList.add(tcId);
				}
			}

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current method name
	 *
	 * @return
	 */
	public static String myMethodName() {
		return (new Throwable().getStackTrace()[1].getMethodName());
	}

	public void logInfo(String msg) {
		logger.info(msg);
	}

	public void logError(String msg) {
		logger.error(msg);
	}

	public void logDebug(String msg) {
		logger.debug(msg);
	}

	public void logWarn(String msg) {
		logger.warn(msg);
	}

	public void sleep(int timeMS) {
		logger.sleep(timeMS, true);
	}

	public void logError(RuntimeException e) {
		logger.error(e);
	}

	public String getParam(String key) {
		return getParam(getName(), key);
	}

	public Map<String, String> getParamPairs(String key) throws FrameworkException {
		return StringUtil.toMap(getParam(key));
	}

	public String getParam(String tcId, String key) {
		logger.debugMethod(tcId, key);
		String ret = null;
		if (m_param == null)
			throw new FrameworkException("Error initializing test data");
		if (!m_param.containsKey(tcId))
			throw new FrameworkException("TestCase id " + tcId + " is not in test data xml file");
		if (!m_param.get(tcId).containsKey(key)) {
			if (g_param.containsKey(key))
				ret = g_param.get(key);
			else
				throw new FrameworkException("Parameter name " + key + " is not found for test case " + tcId);
		} else
			ret = m_param.get(tcId).getProperty(key);
		return logger.debugReturn(ret);
	}

	public Properties getParams(String tcId) {
		if (m_param == null)
			throw new FrameworkException("Error initializing test data");
		if (!m_param.containsKey(tcId))
			throw new FrameworkException("TestCase id " + tcId + " is not in test data xml file");
		Properties tcParams = new Properties();
		for (Map.Entry<String, String> entry : g_param.entrySet()) {
			tcParams.put(entry.getKey(), entry.getValue());
		}
		Properties tcParams0 = m_param.get(tcId);
		for (Object key : tcParams0.keySet()) {
			tcParams.put(key.toString(), tcParams0.get(key).toString());
		}
		return tcParams;
	}

	protected List<String> getXmlTcList() {
		return _xmlTcList;
	}

	public Map<String, String> getParamPairs(String methodName, String key) throws FrameworkException {
		return StringUtil.toMap(getParam(methodName, key));
	}

	public void saveResult(String name, String result) {
		logger.debugMethod(name, result);
		String tcId = getName();
		String fileName = (tcId == null ? name : tcId + "_" + name);
		try {
			FileUtil.writeTextFile(getResultDir() + "/" + fileName, result);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException(e.getMessage());
		}
	}

	public void saveResult(String name, File file) {
		logger.debugMethod(name, file.getAbsolutePath());
		String tcId = getName();
		String fileName = (tcId == null ? name : tcId + "_" + name);
		try {
			String resultPath = getResultDir() + "/" + fileName;
			File resultFile = new File(resultPath);
			if (!file.equals(resultFile)) {
				FileUtil.copyFile(file, resultFile);
				logger.debug("saved result file: " + resultPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FrameworkException(e.getMessage());
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	public void compareImage(String imageFileName, double threshold) {
		ImageCompareFinal imageUtil;
		String testID = getName();
		String resultDir = getResultDir();
		String baseDir = getBaseDir();
		String bigDir = resultDir + "/" + testID + "/bigcomponents";
		String smallDir = resultDir + "/" + testID + "/smallcomponents";
		File dir1 = new File(bigDir);
		if (!dir1.exists()) {
			dir1.mkdirs();
			File f = new File(bigDir + "/comp.log");
		}
		File dir2 = new File(smallDir);
		if (!dir2.exists()) {
			dir2.mkdirs();
		}
		String file1 = baseDir + "/" + imageFileName;
		String file2 = resultDir + "/" + imageFileName;
		imageUtil = new ImageCompareFinal(file1, file2, resultDir + "/" + testID, false, false, true);
		try {
			getLogger().sideBySide(ImageIO.read(new File(file1)), ImageIO.read(new File(file2)));
		} catch (Exception e) {
			logWarn(e.getMessage());
		}
		try {
			double percent = imageUtil.compareImages();
			if (percent < threshold) {
				logError("The image file is different from baseline");
			}
		} catch (Exception e) {
			logError("Exception in compare image file of screenshot. " + "Exception: " + e.getMessage());
		}
	}

}
