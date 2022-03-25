package com.qa.framework;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Date;
import javax.imageio.ImageIO;

public class TestLogger implements Logger {
	private File logfile;
	public int LOG_LEVEL = 3;
	private File resultDir;
	private File baseDir;
	private String currentTestId;
	private long lastLogTime = new Date().getTime();
	private Class testClass;
	private File perfLogfile;
	private long perfStartTime;
	private String perfDescription;

	public TestLogger() {

	}

	public TestLogger(File testOutputDir, Class testClass) {
		if (testOutputDir == null)
			throw new TestException("test dir cannot be null");
		if (!testOutputDir.exists())
			throw new TestException("test dir not exists: " + testOutputDir.getAbsolutePath());
		this.testClass = testClass;
		this.logfile = new File(testOutputDir, "testsuite.html");
		perfLogfile = new File(testOutputDir, "performance_log.html");
		this.resultDir = new File(testOutputDir, "result");
		this.baseDir = new File(testOutputDir, "base");
		logLine("<html>", getLogFile());
		logLine("<table>", getLogFile());
	}

	@Override
	public File getResultDir() {
		if (!resultDir.exists())
			resultDir.mkdirs();
		return resultDir;
	}

	@Override
	public File getBaseDir() {
		return baseDir;
	}

	@Override
	public File getOutputDir() {
		return this.logfile.getParentFile();
	}

	@Override
	public void setCurrentTestName(String testName) {
		currentTestId = testName;
	}

	private File getLogFile() {
		return this.logfile;
	}

	@Override
	public void logMethod(String methodName, int level, Object... args) {
		String msg = methodName + "(";
		if (args.length > 0) {
			for (int i = 0; i < args.length - 1; i++)
				msg += StringUtil.argToString(args[i]) + ",";
			Object lastArg = args[args.length - 1];
			msg += StringUtil.argToString(lastArg);
		}
		msg += ")";
		log(msg, level);
	}

	@Override
	public void infoMethod(Object... args) {
		boolean findNext = false;
		for (StackTraceElement stElem : Thread.currentThread().getStackTrace()) {
			String methodName = stElem.getMethodName();
			if (findNext) {
				logMethod(methodName, 2, args);
				break;
			}
			if (methodName.equals("infoMethod"))
				findNext = true;
		}
	}

	@Override
	public void debugMethod(Object... args) {
		boolean findNext = false;
		for (StackTraceElement stElem : Thread.currentThread().getStackTrace()) {
			String methodName = stElem.getMethodName();
			if (findNext) {
				logMethod(methodName, 3, args);
				break;
			}
			if (methodName.equals("debugMethod"))
				findNext = true;
		}
	}

	@Override
	public String debugReturn(String ret) {
		debug("" + ret);
		return ret;
	}

	@Override
	public boolean debugReturn(boolean ret) {
		debug("" + ret);
		return ret;
	}

	@Override
	public int debugReturn(int ret) {
		debug("" + ret);
		return ret;
	}

	@Override
	public void debug(String msg) {
		log(msg, 3);
	}

	@Override
	public void info(String msg) {
		log(msg, 2);
	}

	@Override
	public void warn(String msg) {
		log(msg, 1);
	}

	@Override
	public void error(String msg) {
		log(msg, 0);
		throw new FrameworkException(msg);
	}

	@Override
	public void error(Exception e) {
		e.printStackTrace();
		log(e.getMessage(), 0);
		throw new TestException(e);
	}

	private void log(String msg, int loglevel) {
//		getCurrentTestName();
		if (loglevel > LOG_LEVEL)
			return;
		System.out.println(msg);
		String line = makeHtmlLine(msg, loglevel);
		logLine(line, getLogFile());
	}

	private String makeHtmlLine(String msg, int loglevel) {
//		String[] levels=new String[]{"ERROR", "WARN", "INFO", "DEBUG", "TRACE"};
		String tsCell = "<td>" + getColoredTimestamp() + "</td>";
		String msgCell = "<td>" + getColoredCell(filterHtml(msg), loglevel) + "</td>";
		return "<tr>" + tsCell + msgCell + "</tr>";
	}

	private String makeHtmlTextCompare(String result, String benchmark) {
		String tsCell = "<td>" + getColoredTimestamp() + "</td>";
		String resultCell = "<textarea style=\"width: 40%; height: 100px\">" + filterForTextArea(result)
				+ "</textarea>";
		String benchmarkCell = "<textarea style=\"width: 40%; height: 100px\">" + filterForTextArea(benchmark)
				+ "</textarea>";
		return "<tr>" + tsCell + "<td>" + resultCell + benchmarkCell + "</td></tr>";
	}

	private String makeHtmlImageCompare(BufferedImage img1, BufferedImage img2) {
		String tsCell = "<td>" + getColoredTimestamp() + "</td>";
		File outputfile1 = new File(getOutputDir(), new Date().getTime() + "1.png");
		File outputfile2 = new File(getOutputDir(), new Date().getTime() + "2.png");
		try {
			ImageIO.write(img1, "png", outputfile1);
			ImageIO.write(img2, "png", outputfile2);
		} catch (IOException e) {
			e.printStackTrace();
			error(e);
		}
		String imgCell1 = "<a target=\"_blank\" href=\"" + outputfile1.getName() + "\"><img src=\""
				+ outputfile1.getName() + "\" style=\"width: 40%;\"/></a>";
		String imgCell2 = "<a target=\"_blank\" href=\"" + outputfile2.getName() + "\"><img src=\""
				+ outputfile2.getName() + "\" style=\"width: 40%;\"/></a>";
		return "<tr>" + tsCell + "<td>" + imgCell1 + imgCell2 + "</td></tr>";
	}

	private long getLogInterval() {
		long currentTime = new Date().getTime();
		long interval = currentTime - lastLogTime;
		lastLogTime = currentTime;
		return interval;
	}

	private String getColoredTimestamp() {
		long logInterval = getLogInterval();
		String color = "000000";
		if (logInterval < 500)
			color = "d0d0d0";
		else if (logInterval < 2000)
			color = "a0a0a0";
		else if (logInterval < 8000)
			color = "707070";
		else if (logInterval < 30000)
			color = "404040";
		return "<font color=\"" + color + "\">[" + StringUtil.getTimeStamp() + "]</font>";
	}

	private String getColoredCell(String text, int loglevel) {
		String[] colors = new String[] { "red", "orange", "blue", "grey", "e0e0e0" };
		return "<font color=\"" + colors[loglevel] + "\">" + text + "</font>";
	}

	private boolean logLine(String line, File file) {
		if (file == null)
			return false;
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(file, true));
			writer.println(line);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void logScreenShot(File imgFile) {
		String timestamp = getColoredTimestamp();
		String line = "<tr><td>" + timestamp + "</td><td><a target=\"_blank\" href=\"" + imgFile.getName()
				+ "\"><img src=\"" + imgFile.getName() + "\" width=\"300\"/></a></td></tr>";
		logLine(line, getLogFile());
	}

	@Override
	public boolean saveResult(String name, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean saveResult(String name, String result, String benchmark) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sleep(int milliSec, boolean withLog) {
		if (withLog)
			debug("sleep(" + milliSec + ")");
		try {
			Thread.sleep(milliSec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static String filterHtml(String inStr) {
		return filterHtml(inStr, true);
	}

	private static String filterHtml(String inStr, boolean filterBreak) {
		if (inStr == null)
			return inStr;
		inStr = filterHtmlSpecials(inStr);
		if (!filterBreak)
			return inStr;
		BufferedReader br = new BufferedReader(new StringReader(inStr));
		StringBuffer outStrBuf = new StringBuffer(inStr.length());
		String line;
		int numOfLines = 0;
		try {
			while (null != (line = br.readLine())) {
				if (++numOfLines > 1)
					outStrBuf.append("<br>");
				outStrBuf.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outStrBuf.toString();
	}

	private static String filterForTextArea(String input) {
		StringBuffer filtered = new StringBuffer(input.length());
		char c;
		char cc = 0;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (i < input.length() - 1)
				cc = input.charAt(i + 1);
			if (c == '<') {
				filtered.append("&lt;");
			} else if (c == '>') {
				filtered.append("&gt;");
				// }
				// else if (c == '"') {
				// filtered.append("&quot;");
			} else if (c == '&') {
				filtered.append("&amp;");
				// } else if (c == ' ' && cc==' ') {
				// filtered.append("&nbsp;");
			} else {
				filtered.append(c);
			}
		}
		return (filtered.toString());
	}

	private static String filterHtmlSpecials(String input) {
		StringBuffer filtered = new StringBuffer(input.length());
		char c;
		char cc = 0;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (i < input.length() - 1)
				cc = input.charAt(i + 1);
			if (c == '<') {
				filtered.append("&lt;");
			} else if (c == '>') {
				filtered.append("&gt;");
			} else if (c == '"') {
				filtered.append("&quot;");
			} else if (c == '&') {
				filtered.append("&amp;");
			} else if (c == ' ' && cc == ' ') {
				filtered.append("&nbsp;");
			} else {
				filtered.append(c);
			}
		}
		return (filtered.toString());
	}

	@Override
	public void beginSuite() {
		logLine("</table>", getLogFile());
		logLine("<hr/>", getLogFile());
		logLine("<hr/>", perfLogfile);
		logLine(testClass.getSimpleName(), perfLogfile);
		logLine("<br/>", perfLogfile);
		logLine(new Date().toString(), perfLogfile);
		logLine("<table>", perfLogfile);
		logLine("<tr><th>TestCase</th><th>Description</th><th>Time</th></tr>", perfLogfile);
	}

	@Override
	public void endSuite() {
		logLine("</html>", getLogFile());
		logLine("</table>", perfLogfile);
	}

	@Override
	public void beginTest() {
		logLine("<table>", getLogFile());
	}

	@Override
	public void endTest() {
		logLine("<tr><td colspan=2><b>End Test: " + testClass.getSimpleName() + "." + currentTestId + "</b></td></tr>",
				getLogFile());
		logLine("</table>", getLogFile());
		logLine("<hr/>", getLogFile());
	}

	@Override
	public void sideBySide(String text1, String text2) {
		String line = makeHtmlTextCompare(text1, text2);
		logLine(line, getLogFile());
	}

	@Override
	public void sideBySide(BufferedImage img1, BufferedImage img2) {
		String line = makeHtmlImageCompare(img1, img2);
		logLine(line, getLogFile());
	}

	public void startPerfTimer(String description) {
		perfStartTime = new Date().getTime();
		perfDescription = description;
	}

	public void stopPerfTimer() {
		int duration = (int) ((new Date().getTime() - perfStartTime) / 1000);
		logLine("<tr><td>" + currentTestId + "</td>" + "<td>" + perfDescription + "</td>" + "<td>" + duration
				+ "</td></tr>", perfLogfile);
	}

}
