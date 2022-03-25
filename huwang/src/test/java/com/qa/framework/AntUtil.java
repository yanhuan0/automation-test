package com.qa.framework;

import java.io.File;
import java.util.Map;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntUtil {

    public static void callAnt(File buildXmlFile, String target, Map<String,String>properties){
    	
        Project project=new Project();
        project.setUserProperty("ant.file", buildXmlFile.getAbsolutePath());
        project.init();
        ProjectHelper.configureProject(project, buildXmlFile);
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        //helper.parse(project, buildXmlFile);
        project.addReference("ant.projectHelper", helper);
        if(properties!=null){
            for(String key : properties.keySet())
                project.setProperty(key, properties.get(key));
        }
        project.addBuildListener(getDefaultLogger());
        project.executeTarget(target);
    }

    private static DefaultLogger getDefaultLogger(){
        DefaultLogger consoleLogger=new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
//        consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        return consoleLogger;
    }
}
