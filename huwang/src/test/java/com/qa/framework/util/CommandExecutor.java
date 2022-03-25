package com.qa.framework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * 
 * Helps in excuting system commnands.
 *
 */
public class CommandExecutor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CommandExecutor commandExecutor=new CommandExecutor();
		String[] commands={"ps","ls"};
		commandExecutor.executeCommand(commands);

	}
	
	
	/**
	 * @param commandWithArgs
	 * @param dir
	 */
	public static void executeCommandInDir(List<String> commandWithArgs, File dir){
		logger.info("Executing command "+commandWithArgs.toString());
		ProcessBuilder pb=new ProcessBuilder(commandWithArgs).directory(dir);
		pb.redirectErrorStream(true);
		try {
			
			Process p=pb.start();
			copy(p.getInputStream(), System.out);
			int i=p.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param commandWithArgs
	 */
	public static void executeCommand(List<String> commandWithArgs){
		logger.info("Executing command "+commandWithArgs.toString());
		ProcessBuilder pb=new ProcessBuilder(commandWithArgs);
		pb.redirectErrorStream(true);
		try {
			
			Process p=pb.start();
			copy(p.getInputStream(), System.out);
			int i=p.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param fileContainingCommands
	 * @param options
	 */
	public static void executeCommandsFromFileAsRoot(String fileContainingCommands,List<String> options) {
		String rootToExecute ="/usr/local/packages/aime/install/run_as_root "+fileContainingCommands;
		executeCommandsFromFile(rootToExecute, options);
	}

	/**
	 * Executes given file containing commands string asynchronously.
	 * @param fileContainingCommands 
	 * @param options -- options passed to File 
	 */
	public static void executeCommandsFromFile(String fileContainingCommands,List<String> options) {
		new File(fileContainingCommands).setExecutable(true);
		List<String> commnd=new ArrayList<String>();
		commnd.add(fileContainingCommands);
		commnd.addAll(options);
		ProcessBuilder pb=new ProcessBuilder(commnd);
		pb.redirectErrorStream(true);
		
		try {
			
			Process p=pb.start();
			copy(p.getInputStream(), System.out);
			int i=p.waitFor();
			logger.info("Exit status="+i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes given Command string asynchronously and return output .
	 * 
	 * @param command
	 *            The command to execute
	 */
	public static void executeCommand(String command) {
		logger.info("Executing "+command);
		List<String> commands=new ArrayList<String>();
		commands.add(command);
		ProcessBuilder pb=new ProcessBuilder(commands);
		pb.redirectErrorStream(true);
		
		try {
			Process p=pb.start();
			copy(p.getInputStream(), System.out);
			int i=p.waitFor();
			logger.info("Exit status="+i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Executes the given List of Commands asynchronously. Stores the List of commands in file
	 * fileToStoreCommand . If executable file already exists ,
	 * removes existing contents.
	 * 
	 * 
	 * @param commandLines --command lines to execute
	 * 
	 */
	public static String executeCommand(String[] commandLines){ 
		Random random=new Random();
		int randomNumber=random.nextInt(10000);
		String fileToStoreCommand=System.getProperty("user.dir")+File.separator+commandFileName+randomNumber+".cmd";
		return executeCommand(commandLines, fileToStoreCommand);
	}
	/** @param commandLines
	 * @return
	 */
	public static String executeCommandArray(String[] commandLines){ 
		ProcessBuilder pb=new ProcessBuilder(commandLines);
		pb.redirectErrorStream(true);
		StringBuffer str=new StringBuffer();
		String s=null;
		try {
			
			Process p=pb.start();
			copy(p.getInputStream(), System.out);
			
			BufferedReader stdInput = new BufferedReader(new
	                 InputStreamReader(p.getInputStream()));
			//Thread.sleep(3000);
			 //int i=p.waitFor();
			System.out.println("Here is the standard output of the command:\n");
	            while ((s = stdInput.readLine()) != null) {
			               // System.out.println(s);
			                str.append(s+"\n");
 			                //str=str+s+"\n";
 			               
}
	             int i=p.waitFor();
	            
			   	logger.info("Exit status="+i);
			    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	return str.toString();
	}
	
	
	/**
	 * Executes the given List of Commands asynchronously. Stores the List of commands in file
	 * fileToStoreCommand . If executable file already exists ,
	 * removes existing contents.
	 * 
	 * 
	 * @param commandLines  --command lines to execute
	 * @param fileToStoreCommand  -- File in which to store the commandLines
	 */
	
public static String executeCommand(String[] commandLines,String fileToStoreCommand){
		
		logger.info("Executing following command lines");
		for(String line:commandLines){
			logger.info(line);
		}
		
		logger.info("Storing above lines in "+fileToStoreCommand);
		
		//Store command lines in file file_to_store_command

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					fileToStoreCommand));
			for (String command_line : commandLines) {
				bw.write(command_line);
				bw.write("\n");
			}
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Execute command
		
		new File(fileToStoreCommand).setExecutable(true);
		ProcessBuilder pb = new ProcessBuilder(fileToStoreCommand);
	
		pb.redirectErrorStream(true);
		StringBuffer str=new StringBuffer();
		String s=null;
		try {
			
			Process p=pb.start();
			//copy(p.getInputStream(), System.out);
			
			BufferedReader stdInput = new BufferedReader(new
	                 InputStreamReader(p.getInputStream()));
			//Thread.sleep(3000);
			 //int i=p.waitFor();
			System.out.println("Here is the standard output of the command:\n");
	            while ((s = stdInput.readLine()) != null) {
			                System.out.println(s);
			                str.append(s+"\n");
 			                //str=str+s+"\n";
 			               
}
	             int i=p.waitFor();
	            
			   	logger.info("Exit status="+i);
			    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	return str.toString();
	}
	
	
/**
 *  Executes the given List of Commands asynchronously. Stores the List of commands in file
	 * fileToStoreCommand . If executable file already exists ,
	 * removes existing contents. Executes commands after setting environmentVariables
	 * 
 * @param environmentVariables -- Environment variables set before commandLines execute
 * @param commandLines  -- Command lines to execute
 * @param fileToStoreCommand  -- File in which commands are stored.
 */
public static void executeCommand(Map<String,String> environmentVariables,String[] commandLines,String fileToStoreCommand){
		
		logger.info("Executing following command lines");
		for(String line:commandLines){
			logger.info(line);
		}
		
		logger.info("Storing above lines in "+fileToStoreCommand);
		
		//Store command lines in file file_to_store_command
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(fileToStoreCommand));
			for(String command_line:commandLines){
				bw.write(command_line);
				bw.write("\n");
			}
			bw.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Execute command
		
		new File(fileToStoreCommand).setExecutable(true);
		ProcessBuilder pb=new ProcessBuilder(fileToStoreCommand);
		Map<String, String> env = pb.environment();
		for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
			env.put(entry.getKey(), entry.getValue());
		}
		pb.redirectErrorStream(true);
		
		try {
			
			Process p=pb.start();
			copy(p.getInputStream(), System.out);
			int i=p.waitFor();
			logger.info("Exit status="+i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}


public static int executeCommandGetExitCode(String commandStringWithArgs) {
	int exitCode = -1;
	List<String> commandWithArgs = Arrays.asList(commandStringWithArgs.split(" "));
	logger.info("Executing command "+commandWithArgs.toString());
	ProcessBuilder pb=new ProcessBuilder(commandWithArgs);
	pb.redirectErrorStream(true);
	
	try {
		
		Process p=pb.start();
		copy(p.getInputStream(), System.out);
		exitCode=p.waitFor();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return exitCode;
}

public static String executeCommandGetOutput(String commandStringWithArgs) {
	int exitCode = -1;
	List<String> commandWithArgs = Arrays.asList(commandStringWithArgs.split(" "));
	logger.info("Executing command "+commandWithArgs.toString());
	ProcessBuilder pb=new ProcessBuilder(commandWithArgs);
	pb.redirectErrorStream(true);
	
	StringBuffer str=new StringBuffer();
	String s = null;
	try {
		
		Process p=pb.start();
		
		BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
		//Thread.sleep(3000);
		 //int i=p.waitFor();
		System.out.println("Here is the standard output of the command:\n");
           while ((s = stdInput.readLine()) != null) {
		                System.out.println(s);
		                str.append(s+"\n");
		                //str=str+s+"\n";
           }
        System.out.println("Output:" + str.toString());
		exitCode=p.waitFor();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return str.toString();
}

private static void copy(InputStream in, OutputStream out) throws IOException {
    while (true) {
        int c = in.read();
        if (c == -1) {
            break;
        }
        out.write((char) c);
    }
}

	/**
	 * Used in startDvDesktop method in DvDesktopHelper class
	 * 
	 * @param commandLines
	 * @return
	 */
	public static String executeCommandAsync(String[] commandLines) {

		logger.info("Executing following command lines");

			for (String command_line : commandLines) {
				logger.info(command_line);
			}

		// Execute command

		ProcessBuilder pb = new ProcessBuilder(commandLines);

		// pb.redirectErrorStream(true);
		StringBuffer str = new StringBuffer();
		String s = null;
		try {

			Process p = pb.start();
			// copy(p.getInputStream(), System.out);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			// Thread.sleep(3000);
			// int i=p.waitFor();
			System.out.println("Here is the standard output of the command:\n");
			/*
			 * while ((s = stdInput.readLine()) != null) {
			 * System.out.println(s); str.append(s+"\n"); //str=str+s+"\n";
			 * 
			 * }
			 */
			// int i=p.waitFor();

			// logger.info("Exit status="+i);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return str.toString();
	}

	/**
	 * Method to check whether process is running
	 * 
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public static boolean isProcessRunning(String serviceName) throws Exception {

		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {

			if (line.contains(serviceName)) {
				System.out.println(line);
				return true;
			}
		}

		return false;

	}

	/**
	 * Method to kill the service by name
	 * 
	 * @param serviceName
	 * @throws Exception
	 */
	public static void killProcess(String serviceName) throws Exception {

		Runtime.getRuntime().exec(KILL + serviceName);

	}

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /F /IM ";

static Logger logger=Logger.getLogger(CommandExecutor.class);
static String commandFileName="commandFile";

}