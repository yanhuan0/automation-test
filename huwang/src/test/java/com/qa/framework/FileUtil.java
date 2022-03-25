package com.qa.framework;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class.getName());

	/**
	 * copy a file to destination
	 */
	public static void copyFile(File in, File out) throws IOException {

		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1)
				fos.write(buf, 0, i);
		} finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}

	/*
	 * this will helpful to copy entire directory/files to Directory/files
	 * 
	 * @AUTHOR
	 * 
	 */
	public static void copy(File sourceLocation, File targetLocation) throws IOException {
		if (sourceLocation.isDirectory()) {
			// System.out.println("sourceLocation======="+sourceLocation);
			logger.debug("Copying folder " + sourceLocation + " to " + targetLocation);
			copyDirectory(sourceLocation, targetLocation);
		} else {
			copyFile(sourceLocation, targetLocation);
		}
	}

	/*
	 * this will helpful to copy entire directory/files to Directory/files
	 * 
	 * @AUTHOR
	 * 
	 */
	public static void copyDirectory(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdir();
		}

		for (String f : source.list()) {
			copy(new File(source, f), new File(target, f));

		}
	}

	/**
	 * 
	 * @param folderPath
	 * @return
	 * @author vmanikon it watchs the folder , once any file is get modify then it
	 *         will try to execute the code.
	 * 
	 */
	public static boolean watchFolderChanges(String folderPath) {
		Path myDir = Paths.get(folderPath);
		boolean counter = false;
		try {
			WatchService watcher = myDir.getFileSystem().newWatchService();
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);// StandardWatchEventKinds.ENTRY_CREATE
																			// ,StandardWatchEventKinds.ENTRY_DELETE,
			WatchKey watchKey = watcher.take();
			List<WatchEvent<?>> events = watchKey.pollEvents();
			for (WatchEvent event : events) {
				/*
				 * if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
				 * logger.info(event.context().toString()+ " is Created ");
				 * System.out.println(event.context().toString()+ " is Created ");
				 * 
				 * } if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
				 * logger.info(event.context().toString()+ " is Deleted");
				 * System.out.println(event.context().toString()+ " is Deleted");
				 * old_rpd=event.context().toString(); if (old_rpd.contains("_")){
				 * watchKey.reset();
				 * 
				 * }else{watchKey.reset();continue;} }
				 */
				if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
					logger.info(event.context().toString() + "is Modified: ");
					System.out.println(event.context().toString() + " is Modified: ");
					counter = true;
				}
			}
			watchKey.cancel();
		} catch (Exception e) {
			logger.info("Error: " + e.toString());
		}
		return counter;
	}

	/**
	 * It deletes Directory by deleting recursively all the files.
	 * 
	 * @param path
	 * @return
	 */
	public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * recursively copy all files
	 * 
	 * @param fromDir
	 * @param toDir     will be created if not exist
	 * @param extension
	 * @throws IOException
	 */
	public static void copyFiles(File fromDir, File toDir, String extension) {
		try {
			if (!fromDir.exists() || !fromDir.isDirectory() || (toDir.exists() && !toDir.isDirectory()))
				throw new IOException("invalid source or destination directory");
			if (!toDir.exists())
				toDir.mkdir();
			File[] files = fromDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().startsWith("."))
					continue;
				if (files[i].isDirectory())
					copyFiles(files[i], new File(toDir, files[i].getName()), extension);
				if (extension == null || files[i].getName().toLowerCase().endsWith("." + extension.toLowerCase())) {
					copyFile(files[i], new File(toDir, files[i].getName()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * recursively copy all files
	 * 
	 * @param fromDir
	 * @param toDir   will be created if not exist
	 * @param regexp
	 * @throws IOException
	 */
	public static void moveFiles(File fromDir, File toDir, String regex) {
		try {
			if (!fromDir.exists() || !fromDir.isDirectory() || (toDir.exists() && !toDir.isDirectory()))
				throw new IOException("invalid source or destination directory");
			if (!toDir.exists())
				toDir.mkdir();
			File[] files = fromDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().startsWith("."))
					continue;
				if (files[i].isDirectory())
					moveFiles(files[i], new File(toDir, files[i].getName()), regex);
				if (regex == null || files[i].getName().matches(regex) && files[i].isFile()) {
					copyFile(files[i], new File(toDir, files[i].getName()));
					deleteFile(files[i].getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * recursively list all files matching the pattern
	 * 
	 * @param dir
	 * @param regexp pattern for file name matching
	 * @return the number of matching files
	 * @throws IOException
	 */
	public static int countFiles(File dir, String regexp) throws IOException {
		if (!dir.exists() || !dir.isDirectory())
			throw new IOException("invalid source directory");
		File[] files = dir.listFiles();
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory())
				count += countFiles(file, regexp);
			else if (file.getName().matches(regexp))
				count++;
		}
		return count;
	}

	public static StringReader filterNonAnsiFile(FileReader fileReader) throws IOException {
		int c;
		StringBuffer buf = new StringBuffer();
		while ((c = fileReader.read()) >= 0) {
			if (c < 128 && c > 0)
				buf.append((char) c);
		}
		return new StringReader(buf.toString());
	}

	public static void compareFile(String baseFile, String resultFile) throws IOException {
		BufferedReader baseBuffReader = new BufferedReader(filterNonAnsiFile(new FileReader(baseFile)));
		BufferedReader resultBuffReader = new BufferedReader(filterNonAnsiFile(new FileReader(resultFile)));
		// Read line
		while (baseBuffReader.ready() && resultBuffReader.ready()) {
			String baseLine = baseBuffReader.readLine();
			String resultLine = resultBuffReader.readLine();
			if (baseLine == null || resultLine == null)
				break;
			System.out.println("[base]" + baseLine);
			System.out.println("[resu]" + resultLine);
			System.out.println("[compare]" + baseLine.compareTo(resultLine));
		}

	}

	public static String readTextFile(String filePath) throws IOException {
		return readTextFile(filePath, null);
	}

	public static String readTextFile(String filePath, String charsetName) throws IOException {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		FileInputStream fis = new FileInputStream(filePath);
		byte[] buf = new byte[(int) file.length()];
		fis.read(buf);
		fis.close();
		if (charsetName != null)
			return new String(buf, charsetName);
		else
			return new String(buf);
	}

	public static void writeTextFile(String filePath, String content) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		fos.write(content.getBytes());
		fos.close();
	}

	/**
	 *
	 * @param filePath
	 * @param section  use null if no section exists in property file
	 * @param key      if key does not exist, then add as new key
	 * @param newValue if newValue is null, then this key-value pair will be removed
	 * @throws IOException
	 */
	public static void updatePropertyFile(String filePath, String section, String key, String newValue)
			throws IOException {
		String configStr = readTextFile(filePath);
		String newConfigStr = "";
		BufferedReader reader = new BufferedReader(new StringReader(configStr));
		String line = null;
		boolean foundKey = false;
		String currentSection = null;
		while ((line = reader.readLine()) != null) {
			String newLine = line.trim();
			if (newLine.startsWith("[") && newLine.endsWith("]")) {
				if (currentSection != null && section != null && currentSection.equals(section) && !foundKey
						&& newValue != null) {// insert a new key-value at the end of the previous section
					foundKey = true;
					newConfigStr += key + " = " + newValue + "\n";
				}
				currentSection = newLine.substring(1, newLine.length() - 1);
				newConfigStr += line + "\n";
			} else if (newLine.startsWith(key) && newLine.length() > key.length()
					&& newLine.substring(key.length()).trim().startsWith("=")
					&& (section == null || section.equalsIgnoreCase(currentSection))) {
				foundKey = true;
				if (newValue != null)
					newConfigStr += key + " = " + newValue + "\n";
			} else
				newConfigStr += line + "\n";
		}
		if (!foundKey && newValue != null) {
			if (section != null)
				newConfigStr += "[" + section + "]\n";
			newConfigStr += key + " = " + newValue + "\n";
		}
		writeTextFile(filePath, newConfigStr);
	}

	/**
	 * create dummy dif files based on the keys in mapping file, and save the dummy
	 * dif to toDir
	 * 
	 * @param mappingFile
	 * @param toDir
	 */
	public static void createDummyDifFiles(String mappingFile, String toDir) throws IOException {
		if (!new File(mappingFile).exists() || !new File(toDir).exists())
			throw new FileNotFoundException("either mappingFile or toDir could not be found");
		Properties props = new Properties();
		props.load(new FileInputStream(mappingFile));
		for (Enumeration keys = props.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			writeTextFile(toDir + "/" + key + ".dif", "");
		}
	}

	public static String replace(String inputFile, String oldStrFile, String newStrFile) throws IOException {
		String inputStr = readTextFile(inputFile);
		String oldStr = readTextFile(oldStrFile);
		String newStr = readTextFile(newStrFile);
		inputStr = inputStr.replace(oldStr, newStr);
		writeTextFile(inputFile, inputStr);
		return inputStr;
	}

	/**
	 * 
	 * @param inputFile
	 * @param textToSerch
	 * @param textToReplace
	 * @return String
	 * @throws IOException
	 * @author vmanikon
	 */
	public static String replaceStringInFile(String inputFile, String textToSerch, String textToReplace)
			throws IOException {
		String inputStr = readTextFile(inputFile);
		// String oldStr=readTextFile(oldStrFile);
		// String newStr=readTextFile(newStrFile);
		logger.info("replacing the " + textToSerch + "with" + textToReplace);
		inputStr = inputStr.replace(textToSerch, textToReplace);

		writeTextFile(inputFile, inputStr);
		return inputStr;
	}

	public static String insert(String inputFile, String lookfor, String insertAfter, String insertFile)
			throws IOException {
		String inputStr = readTextFile(inputFile);
		int index1 = inputStr.indexOf(lookfor);
		int index2 = inputStr.indexOf(insertAfter, index1 + lookfor.length());
		String subStr1 = inputStr.substring(0, index2 + insertAfter.length());
		String subStr2 = inputStr.substring(index2 + insertAfter.length());
		String insertStr = readTextFile(insertFile);
		String newStr = subStr1 + insertStr + subStr2;
		writeTextFile(inputFile, newStr);
		return newStr;
	}

	public static String getWebPage(String requestUrl) throws IOException {
		String ret = "";
		URL url = new URL(requestUrl);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			ret += inputLine + "\n";
		}
		in.close();
		return ret;
	}

	public static String importFromDTEBlock(String propertyLine, String qa_work) throws IOException {
		String[] propertyStrs = propertyLine.split(" \\-D");
		String newPropertyLine = "";
		for (int i = 0; i < propertyStrs.length; i++) {
			String propertyStr = propertyStrs[i];
			int index1 = propertyStr.indexOf("=");
			String toReplace = propertyStr.substring(index1 + 1);
			int index2 = toReplace.indexOf(".");
			String dteBlockName = toReplace.substring(0, index2);
			String dteBlockExportParam = toReplace.substring(index2 + 1);
			String dteExportFile = qa_work + "/" + dteBlockName + "/export.txt";
			Properties exports = new Properties();
			exports.load(new FileInputStream(dteExportFile));
			String dteExportParamValue = exports.getProperty(dteBlockExportParam);
			String prifix = "";
			if (i > 0)
				prifix = "-D";
			newPropertyLine += prifix + propertyStr.replace(toReplace, dteExportParamValue) + " ";
		}
		return newPropertyLine;
	}

	public static void deleteAllFiles(String dir) {
		File dirFile = new File(dir);
		if (dirFile.exists() && dirFile.isDirectory()) {
			for (File file : dirFile.listFiles())
				file.delete();
		}
	}

	public static String[] listAllFileNames(String rootName) {
		File[] files = listAllFiles(rootName);
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; i++)
			names[i] = files[i].getPath();
		return names;
	}

	public static File[] listFiles(String rootName) {
		Vector allFiles = new Vector();
		File rootDir = new File(rootName);
		if (!rootDir.exists())
			return null;
		if (!rootDir.isDirectory())
			return new File[] { rootDir };
		File[] files = rootDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			allFiles.add(files[i]);
			/*
			 * if (!files[i].isDirectory()) allFiles.add(files[i]); else { File[] subFiles =
			 * listAllFiles(files[i].getPath()); for (int j = 0; j < subFiles.length; j++)
			 * allFiles.add(subFiles[j]); }
			 */
		}
		return (File[]) allFiles.toArray(new File[0]);
	}

	public static File[] listAllFiles(String rootName) {
		Vector allFiles = new Vector();
		File rootDir = new File(rootName);
		if (!rootDir.exists())
			return null;
		if (!rootDir.isDirectory())
			return new File[] { rootDir };
		File[] files = rootDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory())
				allFiles.add(files[i]);
			else {
				File[] subFiles = listAllFiles(files[i].getPath());
				for (int j = 0; j < subFiles.length; j++)
					allFiles.add(subFiles[j]);
			}
		}
		return (File[]) allFiles.toArray(new File[0]);
	}

	public static File[] listAllFiles(String rootName, String nameStartsWith) {
		File[] allFiles = listAllFiles(rootName);
		if (nameStartsWith == null)
			return allFiles;
		Vector files = new Vector();
		for (int i = 0; i < allFiles.length; i++) {
			if (allFiles[i].getName().startsWith(nameStartsWith))
				files.add(allFiles[i]);
		}
		return (File[]) files.toArray(new File[] {});
	}

	public static void unzip(String zipName, String destDir) throws FileNotFoundException, IOException {
		System.out.println("unzip(" + zipName + ", " + destDir + ")");
		int BUFFER = 2048;
		try {
			// System.out.println("Example of ZIP file decompression.");
			// Specify file to decompress
			String inFileName = zipName;
			// Specify destination where file will be unzipped
			String destinationDirectory = destDir;
			File sourceZipFile = new File(inFileName);
			File unzipDestinationDirectory = new File(destinationDirectory);
			// Open Zip file for reading
			ZipFile zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
			// Create an enumeration of the entries in the zip file
			Enumeration zipFileEntries = zipFile.entries();
			// Process each entry
			while (zipFileEntries.hasMoreElements()) {
				// grab a zip file entry
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
				String currentEntry = entry.getName();
				// System.out.println("Extracting: " + entry);
				File destFile = new File(unzipDestinationDirectory, currentEntry);
				// grab file's parent directory structure
				File destinationParent = destFile.getParentFile();
				// create the parent directory structure if needed
				destinationParent.mkdirs();
				// extract file if not a directory
				if (!entry.isDirectory()) {
					BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					byte data[] = new byte[BUFFER];
					// write the current file to disk
					FileOutputStream fos = new FileOutputStream(destFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
					// read and write until last byte is encountered
					while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, currentByte);
					}
					dest.flush();
					dest.close();
					is.close();
				}
			}
			zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void zip(String zipName, String rootName) throws FileNotFoundException, IOException {
		System.out.println("zip(" + zipName + ", " + rootName + ")");
		File root = new File(rootName);
		if (!root.exists())
			throw new FileNotFoundException("File or directory not found: " + rootName);
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipName));
		// Set the compression ratio
		out.setLevel(Deflater.DEFAULT_COMPRESSION);
		String[] filesToZip = listAllFileNames(rootName);
		byte[] buffer = new byte[64 * 1024];
		// iterate through the array of files, adding each to the zip file
		for (int i = 0; i < filesToZip.length; i++) {
			// Associate a file input stream for the current file
			FileInputStream in = new FileInputStream(filesToZip[i]);
			// Add ZIP entry to output stream.
			String relativeName = filesToZip[i].substring(rootName.length() + 1);
			out.putNextEntry(new ZipEntry(relativeName));
			// Transfer bytes from the current file to the ZIP file
			// out.write(buffer, 0, in.read(buffer));
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			// Close the current entry
			out.closeEntry();
			// Close the current file input stream
			in.close();
		}
		// Close the ZipOutPutStream
		out.close();
	}

	/**
	 * @param filepath
	 * @param start    the first index(included)
	 * @param end      the last index(excluded)
	 * @return
	 */
	public static String readStringFromFile(File file, long start, long end) throws IOException {
		if (end < 0)
			end = file.length();
		if (!file.exists() || file.isDirectory())
			return null;
		if (start >= end)
			return "";
		byte[] buf = new byte[(int) (end - start)];
		FileInputStream fis = new FileInputStream(file);
		fis.skip(start);
		fis.read(buf);
		fis.close();
		return new String(buf);
	}

	/**
	 * Recursively walk a directory tree and return a List of all Files found; the
	 * List is sorted using File.compareTo().
	 * 
	 * @param aStartingDir is a valid directory, which can be read.
	 */
	public static List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = getFileListingNoSort(aStartingDir);
		Collections.sort(result);
		return result;
	}

	// PRIVATE //

	private static List<File> getFileListingNoSort(File aStartingDir) throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);

		for (File file : filesDirs) {
			result.add(file); // always add, even if directory
			if (!file.isFile() && !file.getAbsolutePath().contains(".ade_path")) {
				// must be a directory
				// recursive call!
				List<File> deeperList = getFileListingNoSort(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

	private static String getValidFileName(String filename) {
		filename = filename.replace('\\', '_');
		filename = filename.replace('/', '_');
		filename = filename.replace(':', '_');
		filename = filename.replace('*', '_');
		filename = filename.replace('?', '_');
		filename = filename.replace('"', '_');
		filename = filename.replace('<', '_');
		filename = filename.replace('>', '_');
		filename = filename.replace('|', '_');
		return filename;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private static void validateDirectory(File aDirectory) throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}

	public static String createDirIfNotExist(String dirStr) {
		File dir = new File(dirStr);
		if (!dir.exists())
			if (!dir.mkdirs())
				throw new FrameworkException("Failed to create dir: " + dirStr);
		return dirStr;
	}

	public static String readFileFromPackage(Class claz, String packageName, String fileName) {
		String file = "/" + packageName.replace('.', '/') + "/" + fileName;
		InputStream is = claz.getResourceAsStream(file);
		if (is == null) {
			return null;
		}
		return readInputStream(is);
	}

	public static String readInputStream(InputStream is) {
		BufferedInputStream bis = new BufferedInputStream(is);
		StringBuffer buf = new StringBuffer();
		try {
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = bis.read(contents)) != -1) {
				buf.append(new String(contents, 0, bytesRead));
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
			return null;
		} catch (IOException ioe) {
			System.out.println("Exception while reading the file " + ioe);
			return null;
		} finally {
			try {
				if (bis != null)
					bis.close();
			} catch (IOException ioe) {
				System.out.println("Error while closing the stream :" + ioe);
				return null;
			}
		}
		return buf.toString();
	}

	public static void zip(File zipFile, File root) throws IOException {
		if (!root.exists() || root.listFiles().length == 0)
			return;
		String rootName = root.getAbsolutePath();
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		// Set the compression ratio
		out.setLevel(Deflater.DEFAULT_COMPRESSION);
		File[] filesToZip = root.listFiles();
		byte[] buffer = new byte[64 * 1024];
		// iterate through the array of files, adding each to the zip file
		for (int i = 0; i < filesToZip.length; i++) {
			// Associate a file input stream for the current file
			FileInputStream in = new FileInputStream(filesToZip[i]);
			// Add ZIP entry to output stream.
			String relativeName = filesToZip[i].getAbsolutePath().substring(rootName.length() + 1);
			out.putNextEntry(new ZipEntry(relativeName));
			// Transfer bytes from the current file to the ZIP file
			// out.write(buffer, 0, in.read(buffer));
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			// Close the current entry
			out.closeEntry();
			// Close the current file input stream
			in.close();
		}
		// Close the ZipOutPutStream
		out.close();
	}

	public static void appendLine(File file, String line) {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(file, true));
			writer.println(line);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void appendFiles(FileInputStream fis, FileOutputStream fos) {
		try {

			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = fis.read(contents)) != -1) {

				fos.write(contents, 0, bytesRead);
			}

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static void rename(String src, String dest) {
		File file = new File(src);

		// File (or directory) with new name
		File file2 = new File(dest);
		// if(file2.exists()) throw new java.io.IOException("file exists");

		// Rename file (or directory)
		boolean success = file.renameTo(file2);
		if (!success) {
			System.out.println("renameing of result file is failed");
		}

	}

	public static long getSize(String fileName) {
		return new File(fileName).getTotalSpace();
	}

	public static boolean deleteFile(String path) {
		return new File(path).delete();
	}

	/**
	 * @param xmlFile
	 * @param rootElement
	 * @param tagName
	 * @return
	 * @author vmanikon this method will return the value of the specified tag.
	 */
	public static String readXmlByTag(String xmlFile, String rootElement, String tagName) {
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(xmlFile));

			// normalize text representation
			doc.getDocumentElement().normalize();
			System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());

			NodeList listOfBranchElements = doc.getElementsByTagName(rootElement);
			int totalBranches = listOfBranchElements.getLength();
			System.out.println("Total no of branches : " + totalBranches);

			for (int s = 0; s < listOfBranchElements.getLength(); s++) {

				Node firstBranchNode = listOfBranchElements.item(s);
				if (firstBranchNode.getNodeType() == Node.ELEMENT_NODE) {

					Element firstBranchElement = (Element) firstBranchNode;

					// -------
					NodeList firstTagList = firstBranchElement.getElementsByTagName(tagName);
					Element firstElement = (Element) firstTagList.item(0);

					NodeList textList = firstElement.getChildNodes();
					System.out.println("tag value: " + ((Node) textList.item(0)).getNodeValue().trim());
					return ((Node) textList.item(0)).getNodeValue().trim();

				} // end of if clause

			} // end of for loop with s var

		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}
		// System.exit (0);
		return null;
	}

	public static void mergeFiles(File[] files, File mergedFile) {

		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(mergedFile, true);
			out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (File f : files) {
			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));

				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) throws Exception {
	}
}
