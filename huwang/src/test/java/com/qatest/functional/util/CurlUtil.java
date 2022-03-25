package com.qatest.functional.util;

import java.io.IOException;

public class CurlUtil {

	public static void executeCurlCommand(String curlCommand) {

		String[] cmd = { "/bin/sh", "-c", curlCommand };
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
}
