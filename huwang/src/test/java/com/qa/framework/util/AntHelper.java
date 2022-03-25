package com.qa.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *
 * @author pcjha
 */
public class AntHelper {

	static Logger log = Logger.getLogger(AntHelper.class.getName());

	public List<String> getRequiredAntProperties(File file) {
		List<String> properties = new ArrayList<String>();
		BufferedReader bReader;
		Set<String> propertyRefs = new HashSet<String>();
		List<String> propertyDefined = new ArrayList<String>();

		try {
			bReader = new BufferedReader(new FileReader(file));
			String line;
			boolean insideComment = false;
			while ((line = bReader.readLine()) != null) {
				if (line.contains("<!--")) {
					insideComment = true;
					continue;
				}
				if (insideComment && line.endsWith("-->")) {
					insideComment = false;
					continue;
				}
				if (insideComment) {
					continue;
				}

				if (line.contains("${")) {
					Pattern pattern = Pattern.compile("(\\$)(\\{)([^\\}]*)(\\})");
					Matcher matcher = pattern.matcher(line);

					while (matcher.find()) {
						propertyRefs.add(matcher.group(3));
					}
				}

				if (line.contains("<property") && (!line.contains("${"))) {
					Pattern pattern = Pattern.compile("(<property)(.*)(name=\")([^\"]*)(\")");
					Matcher matcher = pattern.matcher(line);

					while (matcher.find()) {
						// System.out.println("fnd "+matcher.group(4));
						propertyDefined.add(matcher.group(4));

					}
				}

				if (line.contains("<basename")) {
					Pattern pattern = Pattern.compile("(<basename)(.*)(property=\")([^\"]*)(\")");
					Matcher matcher = pattern.matcher(line);

					while (matcher.find()) {
						propertyDefined.add(matcher.group(4));
					}
				}
			}
			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		propertyRefs.removeAll(propertyDefined);

		if (propertyRefs.size() > 0) {
			for (String property : propertyRefs) {
				properties.add(property);

			}
		}
		return properties;
	}

	public void callTarget(String buildFile, String targetName, String[] options) {
		File antFile = new File(buildFile);
		callTarget(antFile, targetName, options);
	}

	public void callTarget(File buildfile, String targetName, String[] options) {
		// chckRequiredPropertiesSet(buildfile);
		try {

			log.debug("***Calling target " + targetName + " in file " + buildfile.getAbsolutePath() + " with options ");
			for (String s : options) {
				log.debug(s);
			}
			Project p = new Project();
			p.setUserProperty("ant.file", buildfile.getAbsolutePath());
			p.setCoreLoader(Thread.currentThread().getContextClassLoader());

			DefaultLogger consoleLogger = new DefaultLogger();
			consoleLogger.setErrorPrintStream(System.err);
			consoleLogger.setOutputPrintStream(System.out);
			consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
			p.addBuildListener(consoleLogger);
			;
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();

			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildfile);
			p.executeTarget(targetName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void chckRequiredPropertiesSet(File file) {
		log.info("CHECKING required properties in ant file");
		;
		BufferedReader bReader;
		Set<String> propertyRefs = new HashSet<String>();
		List<String> propertyDefined = new ArrayList<String>();

		try {
			bReader = new BufferedReader(new FileReader(file));
			String line;
			boolean insideComment = false;
			while ((line = bReader.readLine()) != null) {
				if (line.contains("<!--")) {
					insideComment = true;
					continue;
				}
				if (insideComment && line.endsWith("-->")) {
					insideComment = false;
					continue;
				}
				if (insideComment) {
					continue;
				}

				if (line.contains("${")) {
					Pattern pattern = Pattern.compile("(\\$)(\\{)([^\\}]*)(\\})");
					Matcher matcher = pattern.matcher(line);

					while (matcher.find()) {
						propertyRefs.add(matcher.group(3));
					}
				}
				if (line.contains("<property")) {
					Pattern pattern = Pattern.compile("(<property)(.*)(name=\")([^\"]*)(\")");
					Matcher matcher = pattern.matcher(line);

					while (matcher.find()) {
						propertyDefined.add(matcher.group(4));

					}
				}

				if (line.contains("<basename")) {
					Pattern pattern = Pattern.compile("(<basename)(.*)(property=\")([^\"]*)(\")");
					Matcher matcher = pattern.matcher(line);

					while (matcher.find()) {
						propertyDefined.add(matcher.group(4));

					}
				}
			}
			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		propertyRefs.removeAll(propertyDefined);

		// properties accessibe from System
		propertyRefs.removeAll(System.getProperties().keySet());

		// check missing properties
		if (propertyRefs.size() > 0) {
			log.error("======= FOLLOWING PROPERTIES NOT DEFINED===============");
			for (String property : propertyRefs) {
				log.error(property);

			}
			log.error("========= in ant file" + file.getAbsolutePath() + "========");
			log.info("Please provide above properties. Exiting...");
			System.exit(1);
		}

	}

	public static void main(String[] args) {
		System.setProperty("param2", "cal");
		String file = "C:\\Users\\pcjha.ORADEV\\workspace\\Test\\ant_build.xml";
		AntHelper antHelper = new AntHelper();
		antHelper.callAntFile(new File(file));

	}

	public void callAntFile(File buildfile) {

		try {
			Project p = new Project();
			p.setUserProperty("ant.file", buildfile.getAbsolutePath());
			p.setCoreLoader(Thread.currentThread().getContextClassLoader());

			DefaultLogger consoleLogger = new DefaultLogger();
			consoleLogger.setErrorPrintStream(System.err);
			consoleLogger.setOutputPrintStream(System.out);
			consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
			p.addBuildListener(consoleLogger);

			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();

			helper.parse(p, buildfile);
			String defaultTarget = p.getDefaultTarget();
			log.info("Executing target " + defaultTarget + " in file " + buildfile.getAbsolutePath());
			p.executeTarget(defaultTarget);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void callTarget(File buildfile, String targetName, Map options, File outputFile) {
		chckRequiredPropertiesSet(buildfile);
		try {

			Project p = new Project();
			p.setUserProperty("ant.file", buildfile.getAbsolutePath());
			p.setCoreLoader(Thread.currentThread().getContextClassLoader());

			outputFile.delete();
			outputFile.createNewFile();
			log.info("Calling target " + targetName + " from file " + buildfile.getAbsolutePath());

			PrintStream antOutput = new PrintStream(outputFile);

			DefaultLogger consoleLogger = new DefaultLogger();
			consoleLogger.setErrorPrintStream(antOutput);
			consoleLogger.setOutputPrintStream(antOutput);
			consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
			p.addBuildListener(consoleLogger);

			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();

			Set s = options.keySet();
			Iterator i = s.iterator();
			log.info("Following properties are being passed to ant");
			while (i.hasNext()) {
				String key = (String) i.next();
				p.setUserProperty(key, (String) options.get(key));
				// p.set
				log.info(key + "=" + (String) options.get(key));
			}

			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildfile);
			p.executeTarget(targetName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
