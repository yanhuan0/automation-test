package com.qa.framework.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.qa.framework.FileUtil;
import com.qa.framework.util.platform.PlatformHelper;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

public class SSHHelper {

	private String hostname;
	private String user;
	private String password;
	private String installUser;

	private SSHClient client;
	private SCPFileTransfer scpFileTransfer;
	private SFTPClient sftpClient;
	
	private boolean useSsh;
	
	public SSHHelper(String hostname, String user, String password,
			String installUser) throws IOException {
		
		if(System.getProperty("USE_SSH", "true").equalsIgnoreCase("false")) {
			useSsh = false;
		} else {
			useSsh = true;
		}
		
		this.hostname = hostname;
		this.user = user;
		this.password = password;
		this.installUser = installUser;
		if(useSsh()) connect();
	}

	public void connect() throws IOException {
		client = new SSHClient();

		client.addHostKeyVerifier(new HostKeyVerifier() {
			@Override
			public boolean verify(String hostname, int port, PublicKey key) {
				return true;
			}
		});

		System.out.println("Connecting to " + hostname);
		client.connect(hostname);
		System.out.println("Connected to " + hostname);

		System.out.println("Authenticating user/pwd: " + user + "/" + password);
		client.authPassword(user, password);
		System.out.println("Authenticated");
		
		scpFileTransfer = client.newSCPFileTransfer();
		sftpClient = client.newSFTPClient();
	}

	public void disconnect() throws IOException {
		if(useSsh()) client.disconnect();
	}

	public int executeCommandGetExitCode(String command) throws IOException {
		return executeCommandGetExitCode(command, this.installUser);
	}
	
	public int executeCommandGetExitCode(String command, String user)
			throws IOException {

		if(!useSsh()) return CommandExecutor.executeCommandGetExitCode(getExecutableCommand(command));
		
		command = "su " + user + " -c \"" + command + "\"";
		System.out.println("Executing following command in " + hostname + ": "
				+ command);

		String retString = "";

		final Session session = client.startSession();
		final Command cmd = session.exec(command);

		String inputStream = IOUtils.readFully(cmd.getInputStream()).toString();
		String errorStream = IOUtils.readFully(cmd.getErrorStream()).toString();
		System.out.println("Output: " + inputStream);
		System.out.println("\nError Output: " + errorStream);
		cmd.join(5, TimeUnit.SECONDS);
		int exitStatus = cmd.getExitStatus();
		System.out.println("\nExit status: " + exitStatus);
		session.close();
		retString = "Output: " + inputStream + "\nError Output: " + errorStream;

		return exitStatus;

	}
	
	public String executeCommand(String command) throws IOException {
		return executeCommand(command, this.installUser);
	}

	public String executeCommand(String command, String user)
			throws IOException {

		if(!useSsh()) return CommandExecutor.executeCommandGetOutput(getExecutableCommand(command));
		
		command = "su " + user + " -c \"" + command + "\"";
		System.out.println("Executing following command in " + hostname + ": "
				+ command);

		String retString = "";

		final Session session = client.startSession();
		final Command cmd = session.exec(command);

		String inputStream = IOUtils.readFully(cmd.getInputStream()).toString();
		String errorStream = IOUtils.readFully(cmd.getErrorStream()).toString();
		System.out.println("Output: " + inputStream);
		System.out.println("\nError Output: " + errorStream);
		cmd.join(5, TimeUnit.SECONDS);
		System.out.println("\nExit status: " + cmd.getExitStatus());
		session.close();
		retString = "Output: " + inputStream + "\nError Output: " + errorStream;

		return retString;

	}

	public String executeCommandAsRoot(String command) throws IOException {
		if(!useSsh()) return CommandExecutor.executeCommandGetOutput(getExecutableCommand(command));
		
		System.out.println("Executing following command in " + hostname + ": "
				+ command);

		String retString = "";

		final Session session = client.startSession();
		final Command cmd = session.exec(command);

		String inputStream = IOUtils.readFully(cmd.getInputStream()).toString();
		String errorStream = IOUtils.readFully(cmd.getErrorStream()).toString();
		System.out.println("Output: " + inputStream);
		System.out.println("\nError Output: " + errorStream);
		cmd.join(5, TimeUnit.SECONDS);
		System.out.println("\nExit status: " + cmd.getExitStatus());
		session.close();
		retString = "Output: " + inputStream + "\nError Output: " + errorStream;

		return retString;

	}

	public boolean isConnected() {
		if(!useSsh()) return true;
		return client.isConnected();
	}
	
	private SCPFileTransfer getSCPFileTransfer() {
		return this.scpFileTransfer;
	}
	
	private SFTPClient getSFTPClient() {
		return this.sftpClient;
	}
	
	
	public void downloadFile(String remoteFile, String localFile)
			throws Exception {
		System.out.println("Downloading file - " + remoteFile + " to "
				+ localFile);
		
		if(!useSsh()) {
			FileUtil.copy(new File(remoteFile), new File(localFile));
			return;
		}
		
		//sftpClient.get(remoteFile, localFile);
		scpFileTransfer.download(remoteFile, localFile);
	}
	
	public void uploadFile(String remoteFile, String localFile)
			throws Exception {
		System.out.println("Uploading file - " + localFile + " to "
				+ remoteFile);
		
		if(!useSsh()) {
			FileUtil.copy(new File(localFile), new File(remoteFile));
			return;
		}
		
		//sftpClient.put(localFile, remoteFile);
		scpFileTransfer.upload(localFile, remoteFile);
		changeOwnership(remoteFile);
		
		System.out
		.println("Uploaded to " + remoteFile + "   file: " + localFile);
		
	}
	
	public void createRemoteDir(String remoteDir) {
		try {
			if(!useSsh()) {
				new File(remoteDir).mkdirs();
				return;
			}
			sftpClient.mkdirs(remoteDir);
			changeOwnership(remoteDir);
		} catch (Exception e) {
			System.out.println(remoteDir + " creation failed");
		}
	}
	
	public void deleteRemoteDir(String remoteDir) {
		try {
			System.out.println("Deleting directory: " + remoteDir);
			
			if(!useSsh()) {
				FileUtil.deleteAllFiles(remoteDir);
				return;
			}
			
			List<RemoteResourceInfo> list = sftpClient.ls(remoteDir);
			for (RemoteResourceInfo remoteResourceInfo : list) {
				if(remoteResourceInfo.isRegularFile()) {
					sftpClient.rm(remoteResourceInfo.getPath());
				} else if(remoteResourceInfo.isDirectory()) {
					deleteRemoteDir(remoteResourceInfo.getPath());
				}
			}
			
			sftpClient.rmdir(remoteDir);
			
		} catch (Exception e) {
			System.out.println(remoteDir + " deletion failed");
		}
	}
	
	public void deleteRemoteFile(String remoteFile) {
		try {
			System.out.println("Deleting " + remoteFile);
			if(!useSsh()) {
				FileUtil.deleteFile(remoteFile);
				return;
			}
			sftpClient.rm(remoteFile);
		} catch (Exception e) {
			System.out.println("Deletion failed for: " + remoteFile);
		}
	}

	public ArrayList<String> listFilesRemoteDir(String remoteDir) {
		ArrayList<String> listFiles = new ArrayList<String>();
		try {
			System.out.println("Listing files/directories in " + remoteDir);
			
			if(!useSsh()) {
				File[] files = FileUtil.listFiles(remoteDir);
				
				for (File file : files) {
					listFiles.add(file.getCanonicalPath());
				}
				
				return listFiles;
			}
			
			List<RemoteResourceInfo> list = sftpClient.ls(remoteDir);
			for (RemoteResourceInfo remoteResourceInfo : list) {
				listFiles.add(remoteResourceInfo.getPath());
			}
			
		} catch(Exception e) {
			System.out.println("Exception caught while trying to list files in " + remoteDir);
		}
		
		return listFiles;
	}
	
	public ArrayList<String> listFilesRemoteDirRecursive(String remoteDir) {
		ArrayList<String> listFiles = new ArrayList<String>();
		try {
			System.out.println("Listing files/directories in " + remoteDir);
			
			if(!useSsh()) {
				File[] files = FileUtil.listFiles(remoteDir);
				
				for (File file : files) {
					listFiles.add(file.getCanonicalPath());
					
					if(file.isDirectory()) {
						ArrayList<String> listTemp = listFilesRemoteDirRecursive(file.getCanonicalPath());
						listFiles.addAll(listTemp);
					}
					
				}
				
				return listFiles;
			}
			
			List<RemoteResourceInfo> list = sftpClient.ls(remoteDir);
			for (RemoteResourceInfo remoteResourceInfo : list) {
				listFiles.add(remoteResourceInfo.getPath());
				
				if(remoteResourceInfo.isDirectory()) {
					ArrayList<String> listTemp = listFilesRemoteDirRecursive(remoteResourceInfo.getPath());
					listFiles.addAll(listTemp);
				}
				
			}
			
		} catch(Exception e) {
			System.out.println("Exception caught while trying to list files in " + remoteDir);
		}
		
		return listFiles;
	}
	
	public void downloadDir(String remoteDir, String localDir) throws Exception {
		System.out.println("Downloading dir - " + remoteDir + " to " + localDir);
		if(!useSsh()) {
			FileUtil.copyDirectory(new File(remoteDir), new File(localDir));
			return;
		}
		scpFileTransfer.download(remoteDir, localDir);
	}
	
	public void uploadDir(String remoteDir, String localDir) throws Exception {
		System.out.println("Uploading dir - " + remoteDir + " from " + localDir);
		
		if(!useSsh()) {
			FileUtil.copyDirectory(new File(localDir), new File(remoteDir));
			return;
		}
		
		scpFileTransfer.upload(localDir, remoteDir);
		changeOwnershipRecursive(remoteDir);
	}
	

	public void uploadFileInDir(String remoteDir, String file) throws Exception {
		String remoteFile = remoteDir + "/" + new File(file).getName();
		if(!useSsh()) {
			FileUtil.copyFile(new File(file), new File(remoteFile));
			return;
		}
		uploadFile(remoteFile, file);
	}

	public void downloadFileinDir(String localDir, String file)
			throws Exception {
		String localFile = localDir + "/" + new File(file).getName();
		if(!useSsh()) {
			FileUtil.copyFile(new File(file), new File(localFile));
			return;
		}
		downloadFile(file, localFile);
	}

	private void changeOwnership(String file) {
		try {
			executeCommandAsRoot("chown " + this.installUser + " "
					+ file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void changeOwnershipRecursive(String dir) {
		try {
			executeCommandAsRoot("chown -R " + this.installUser + " "
					+ dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/*	public static void closeSSHConnections(String hostname, String user,
			String rootuser, String rootpwd) throws IOException {
		SSHHelper sshHelper = new SSHHelper(hostname, rootuser, rootpwd, user);
		System.out.println("Closing ssh connections of " + user + " user");
		sshHelper.executeCommand("killall sshd -u " + user);
		sshHelper.disconnect();
	}*/
	
	private String writeCommandTempFile(String command) {
		String fileName = (isWindows()) ? "exec.cmd" : "exec.sh";
		String fileLoc = System.getProperty("user.dir") + "/" + fileName;
		
		File file = new File(fileLoc);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			String fileHeader = (isWindows()) ? "":"#!/bin/sh";
			bw.write(fileHeader + "\n\n" + command + "\n"); // Need to add condition for Windows - Bug 21548586 
			bw.close();
			file.setExecutable(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileLoc;
	}
	
	private String getExecutableCommand(String command) {
		String newCommand = writeCommandTempFile(command);
		System.out.println("Executing following command via " + newCommand + ": " + command);
		return newCommand;
	}
	
	private boolean useSsh() {
		return useSsh;
	}

	private boolean isWindows() {
		return PlatformHelper.isWindows();
	}
}
