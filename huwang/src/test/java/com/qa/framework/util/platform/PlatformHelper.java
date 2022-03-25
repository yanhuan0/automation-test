package com.qa.framework.util.platform;

import org.apache.commons.lang3.SystemUtils;

public class PlatformHelper {

	public static boolean isWindows() {
		return SystemUtils.IS_OS_WINDOWS;
	}
	
	public static boolean isUnix() {
		return SystemUtils.IS_OS_UNIX;
	}
	
	public static boolean isMac() {
		return SystemUtils.IS_OS_MAC;
	}
	
	public static String getPythonExecutable() {
		return (isWindows()) ? "C:/ADE/packages/python24/python" : "python";
	}
}
