package com.qatest.functional.util;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.testng.ITestResult;

public class GenerateSucDif {
	private static String currentTestName;

	public static String getCurrentTestName() {
		return currentTestName;
	}

	public static void setCurrentTestName(String currentTestName) {
		GenerateSucDif.currentTestName = currentTestName;
	}

	public static final String ROOT_DIR = "root.dir";

	public static void generateSucDif(ITestResult result) {
		String testName = result.getTestClass().getName() + "." + result.getName();
		generateSucDif(result, testName);
	}

	public static void generateSucDif(ITestResult result, String testName) {
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				Throwable exp = result.getThrowable();
				BufferedWriter bw = new BufferedWriter(new FileWriter(getDteTWork() + "/" + testName + ".dif"));
				bw.write(exp == null ? "" : exp.getMessage());
				bw.close();
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(getDteTWork() + "/" + testName + ".suc"));
				bw.write("");
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void generateSucDifTest(ITestResult result) {
		String testName = GenerateSucDif.currentTestName;
		System.out.println("Inside suc dif printing testname" + testName);
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				Throwable exp = result.getThrowable();
				BufferedWriter bw = new BufferedWriter(new FileWriter(getDteTWork() + "/" + testName + ".dif"));
				bw.write(exp == null ? "" : exp.getMessage());
				bw.close();
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(getDteTWork() + "/" + testName + ".suc"));
				bw.write("");
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static String getRootDir() {
		return System.getProperty(ROOT_DIR);
	}

	protected static String getDteTWork() {
		System.out.println("TWORK from SUC DIF " + System.getProperty("qa.t.work"));
		return System.getProperty("qa.t.work");
	}

}
