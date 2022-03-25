package com.qa.framework;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

public class TestData
  implements Cloneable
{
  private String m_option;
  private String m_status;
  private String m_type;
  private String m_name;
  private String m_testId;
  private Map m_vars;
  
  public TestData(String s, Document document)
  {
    this.m_vars = DataTree.getDataTree(s, document).getVars();
    if (this.m_vars == null) {
      throw new RuntimeException("ID not found in the xml file");
    }
    this.m_testId = s;
    Iterator iterator = document.getDescendants(new ElementFilter("TestCase"));
    while (iterator.hasNext())
    {
      Element element = (Element)iterator.next();
      String s1 = element.getAttributeValue("id");
      if (s1.equals(s))
      {
        String s2 = element.getAttributeValue("status");
        if ((s2 == null) || (s2.trim().length() == 0)) {
          this.m_status = "Normal";
        } else {
          this.m_status = s2;
        }
        String s3 = element.getAttributeValue("type");
        if ((s3 == null) || (s3.trim().length() == 0)) {
          this.m_type = "LRG";
        } else {
          this.m_type = s3;
        }
        this.m_name = element.getAttributeValue("name");
        if ((this.m_name != null) && (this.m_name.trim().length() == 0)) {
          this.m_name = null;
        }
        this.m_option = element.getAttributeValue("option");
        if ((this.m_option != null) && (this.m_option.trim().length() == 0)) {
          this.m_option = null;
        }
      }
    }
  }
  
  public String getTestId()
  {
    return this.m_testId;
  }
  
  public String getTestStatus()
  {
    return this.m_status;
  }
  
  public String getTestType()
  {
    return this.m_type;
  }
  
  public String getTestName()
  {
    return this.m_name;
  }
  
  public String getTestOption(String s)
  {
    if (this.m_option == null) {
      return null;
    }
    HashMap hashmap = toHashMap(this.m_option);
    

    return (String)hashmap.get(s);
  }
  
  public String setNameFilter(String s)
  {
    String s1 = s;
    if (s != null)
    {
      Iterator iterator = this.m_vars.keySet().iterator();
      while (iterator.hasNext())
      {
        String s2 = (String)iterator.next();
        if (s2.length() > s.length())
        {
          String s3 = null;
          if (s2.startsWith(s + "."))
          {
            s3 = s2.substring(s.length() + 1);
          }
          else
          {
            int i = s2.indexOf("." + s + ".");
            if (i > 0) {
              s3 = s2.substring(0, i) + s2.substring(i + s.length());
            }
          }
          if (s3 != null)
          {
            this.m_vars.put(s3, this.m_vars.get(s2));
            this.m_vars.remove(s2);
          }
        }
      }
    }
    return s1;
  }
  
  public String getParam(String s)
  {
    if (s == null) {
      throw new NullPointerException("null was used as the parameter name");
    }
    return (String)this.m_vars.get(s);
  }
  
  public HashMap getParamPairs(String s)
  {
    String s1 = getParam(s);
    if (s1 == null) {
      return null;
    }
    return toHashMap(s1);
  }
  
  public String[] getParamKeys(String s)
  {
    String s1 = getParam(s);
    if (s1 == null) {
      return null;
    }
    String[] as = splitAndTrim(s1);
    for (int i = 0; i < as.length; i++)
    {
      int j = as[i].indexOf("=");
      if (j > 0) {
        as[i] = as[i].substring(0, j).trim();
      }
    }
    return as;
  }
  
  public static Document getXMLDoc(String s)
    throws IOException
  {
      System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

    File file = new File(s);
    if (file.isDirectory())
    {
      File[] afile = file.listFiles();
      int i = 0;
      while (i < afile.length)
      {
        if (afile[i].getName().endsWith(".xml"))
        {
          file = afile[i];
          break;
        }
        i++;
      }
    }
    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
    PrintStream printstream = new PrintStream(bytearrayoutputstream);
    BufferedReader bufferedreader = new BufferedReader(new FileReader(file));
    String s1 = null;
    boolean flag = false;
    while ((s1 = bufferedreader.readLine()) != null)
    {
      if (s1.indexOf("<![CDATA[") >= 0) {
        flag = true;
      }
      if ((s1.indexOf("]]>") >= 0) && (flag)) {
        flag = false;
      }
      if (!flag) {
        s1 = loadFile(s1, file.getParentFile().getParent());
      }
      printstream.println(s1);
    }
    ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
    Document document = null;
    try
    {
      document = new SAXBuilder().build(bytearrayinputstream);
    }
    catch (JDOMException jdomexception)
    {
      throw new IOException(jdomexception.getMessage());
    }
    String s2 = checkError(document);
    if (s2 != null) {
      throw new IOException(s2);
    }
    return document;
  }
  
  private static String loadFile(String s, String s1)
    throws IOException
  {
    String s2 = "IncludeFile";
    int i = s.indexOf("<" + s2 + " ");
    if (i < 0) {
      return s;
    }
    int j = s.indexOf("/>", i) + 2;
    if (j < 0) {
      return s;
    }
    String s3 = s.substring(i, j);
    s3 = s3.substring(s2.length() + 1, s3.length() - 2).trim();
    if (s3.split("=").length < 2) {
      return s;
    }
    String s4 = s3.substring(s3.indexOf("\"") + 1, s3.length() - 1);
    if ((s1 == null) || (s1.trim().length() == 0)) {
      s1 = ".";
    }
    File file = new File(getAbsoluePath(s4, s1));
    
    return readFile(file);
  }
  
  private static String readFile(File file)
    throws IOException
  {
    String s = null;
    FileInputStream fileinputstream = new FileInputStream(file);
    byte[] abyte0 = new byte[(int)file.length()];
    fileinputstream.read(abyte0);
    fileinputstream.close();
    s = new String(abyte0);
    s = s.trim();
    if (s.indexOf('<') > 0) {
      return s;
    }
    String s1 = s.substring(0, s.indexOf('>'));
    if ((s1 == null) || (s1.startsWith("<TestCase")) || (s1.startsWith("<TestGroup")) || (s1.startsWith("<Param"))) {
      return s;
    }
    if (s1 != null)
    {
      int i = s.indexOf('>') + 1;
      int j = s.lastIndexOf("</");
      




      return s.substring(i, j);
    }
    return s;
  }
  
  private static String getAbsoluePath(String s, String s1)
  {
    if ((s.startsWith("/")) || (s.startsWith("\\")) || (s.substring(1).startsWith(":\\"))) {
      return s;
    }
    return s1 + File.separator + s;
  }
  
  public static String[] getTestIDs(Document document)
  {
    return getTestIDs(document, null);
  }
  
  public static String[] getTestIDs(Document document, String s)
  {
    Vector vector = getAllTestIDs(document, s);
    Vector vector1 = new Vector();
    Element element = document.getRootElement();
    String[] as = getContaineeInfo(element, true);
    if (as != null)
    {
      for (int i = 0; i < as.length; i++)
      {
        String s1 = as[i].trim();
        if (vector.contains(s1)) {
          vector1.add(s1);
        }
      }
      return (String[])vector1.toArray(new String[0]);
    }
    String[] as1 = getContaineeInfo(element, false);
    if (as1 != null) {
      for (int j = 0; j < as1.length; j++) {
        vector.remove(as1[j]);
      }
    }
    return (String[])vector.toArray(new String[0]);
  }
  
  public static Map getGlobals(Document document)
  {
    LinkedHashMap linkedhashmap = new LinkedHashMap();
    List list = document.getRootElement().getChildren("Param");
    for (int i = 0; i < list.size(); i++)
    {
      Element element = (Element)list.get(i);
      String s = element.getAttributeValue("name");
      String s1 = element.getText();
      linkedhashmap.put(s, s1);
    }
    return linkedhashmap;
  }
  
  private String export()
  {
    return "#option=" + (this.m_option != null ? this.m_option : "") + "\n" + "#status=" + this.m_status + "\n" + "#type=" + this.m_type + "\n" + "#name=" + this.m_name + "\n" + mapToString(this.m_vars, false);
  }
  
  public static HashMap toHashMap(String s)
  {
    LinkedHashMap linkedhashmap = new LinkedHashMap();
    if ((s == null) || (s.length() == 0)) {
      return linkedhashmap;
    }
    String[] as = splitAndTrim(s);
    for (int i = 0; i < as.length; i++)
    {
      String s1 = as[i];
      int j = s1.indexOf("=");
      if (j >= 0)
      {
        String s2 = s1.substring(0, j).trim();
        String s3 = s1.substring(j + 1).trim();
        if ((s3.startsWith("\"")) && (s3.endsWith("\""))) {
          s3 = s3.substring(1, s3.length() - 1);
        }
        linkedhashmap.put(s2, s3);
      }
      else
      {
        linkedhashmap.put(s1, "");
      }
    }
    return linkedhashmap;
  }
  
  private static Vector getAllTestIDs(Document document)
  {
    return getAllTestIDs(document, null);
  }
  
  private static Vector getAllTestIDs(Document document, String s)
  {
    Vector vector = null;
    if (s != null)
    {
      String[] as = s.toLowerCase().split("/");
      vector = new Vector();
      for (int i = 0; i < as.length; i++)
      {
        vector.add(as[i]);
        if (as[i].equalsIgnoreCase("LRG")) {
          vector.add("srg");
        }
      }
    }
    Vector vector1 = new Vector();
    Iterator iterator = document.getDescendants(new ElementFilter("TestCase"));
    while (iterator.hasNext())
    {
      Element element = (Element)iterator.next();
      String s1 = element.getAttributeValue("type");
      if ((s1 == null) || (s1.trim().length() == 0)) {
        s1 = "LRG";
      }
      s1 = s1.toLowerCase();
      if ((vector == null) || (vector.contains(s1))) {
        vector1.add(element.getAttributeValue("id"));
      }
    }
    return vector1;
  }
  
  private static String[] getContaineeInfo(Element element, boolean flag)
  {
    String s = flag ? "includes" : "excludes";
    String s1 = element.getAttributeValue(s);
    if ((s1 == null) || (s1.length() == 0)) {
      return null;
    }
    return s1.split(",");
  }
  
  private static String[] splitAndTrim(String s)
  {
    if (s == null) {
      return null;
    }
    String s1 = ("" + Math.random()).substring(2);
    String s2 = ("" + Math.random()).substring(2);
    String s3 = s.replaceAll("\\\\\\\\", s1);
    s3 = s3.replaceAll("\\\\,", s2);
    s = s3.replaceAll(s1, "\\\\");
    String[] as = s.split(DELIMITATE_REGEXP);
    for (int i = 0; i < as.length; i++)
    {
      as[i] = as[i].trim();
      if (as[i].indexOf(s2) >= 0) {
        as[i] = as[i].replaceAll(s2, ",");
      }
    }
    return as;
  }
  
  private static String mapToString(Map map, boolean flag)
  {
    StringWriter stringwriter = null;
    try
    {
      stringwriter = new StringWriter();
      PrintWriter printwriter = new PrintWriter(stringwriter);
      String[] as = (String[])map.keySet().toArray(new String[0]);
      if (flag) {
        Arrays.sort(as);
      }
      Object obj = null;
      for (int i = 0; i < as.length; i++)
      {
        String s = (String)map.get(as[i]);
        writeKeyValuePair(as[i], s, printwriter);
      }
      printwriter.close();
      stringwriter.close();
    }
    catch (IOException ioexception)
    {
      ioexception.printStackTrace();
    }
    return stringwriter.toString();
  }
  
  private static void writeKeyValuePair(String s, String s1, Writer writer)
    throws IOException
  {
    if ((s == null) || (s1 == null)) {
      return;
    }
    writer.write(s);
    if ((s1.length() > 8000) || (s1.indexOf('\n') >= 0))
    {
      writer.write("\n" + s1.length() + "\n");
      writer.write(s1 + "\n");
    }
    else
    {
      writer.write("=" + s1 + "\n");
    }
  }
  
  private static String checkError(Document document)
  {
    for (Iterator iterator = document.getDescendants(new ElementFilter("TestCase")); iterator.hasNext();)
    {
      Element element = (Element)iterator.next();
      String s = element.getAttributeValue("id");
      if ((s == null) || (s.trim().length() == 0)) {
        return "Missing or invalid 'id' attribute in <TestCase>";
      }
    }
    for (Iterator iterator1 = document.getDescendants(new ElementFilter("Param")); iterator1.hasNext();)
    {
      Element element1 = (Element)iterator1.next();
      String s1 = element1.getAttributeValue("name");
      if ((s1 == null) || (s1.trim().length() == 0)) {
        return "Missing or invalid 'name' attribute in <Param>";
      }
    }
    return null;
  }
  
  private static boolean validateArgs(String[] as, int i, int j)
  {
    if ((as.length > j) || (as.length < i))
    {
      System.out.println("#Command line arguments:\n#to get test case IDs to be executed:\ndata_file_name -getIDs [tcType]\n\n#to get all global test parameters:\ndata_file_name -getGlobals\n\n#to get test parameter(s) for all/single test case(s):\ndata_file_name -getData [testID [paramName]]\n\n#to get test case IDs, global parameters and test case parameters in one call:\ndata_file_name -getAll ids_file globals_file data_file [tcType]\n\n#other utility commands:\nscreenshot file_name\ntrim file_name\nreplace file_name regexp replacmentFile\ngetQTPSetupVar file_name name");
      




      return false;
    }
    return true;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    if (!validateArgs(args, 2, 6)) {
      return;
    }
    if (!args[1].trim().startsWith("-")) {
      return;
    }
    String s = args[0];
    Document document = null;
    try
    {
      document = getXMLDoc(s);
    }
    catch (Exception exception)
    {
      System.out.println("Error occurs when parsing data file: " + exception.getMessage()); return;
    }
    String s1 = args[1];
    if ((s1 != null) && (s1.equalsIgnoreCase("-getIDs")))
    {
      String[] args1 = null;
      if (args.length > 2) {
        args1 = getTestIDs(document, args[2]);
      } else {
        args1 = getTestIDs(document);
      }
      for (int i = 0; i < args1.length; i++) {
        System.out.print(args1[i] + "\n");
      }
    }
    else if ((s1 != null) && (s1.equalsIgnoreCase("-getGlobals")))
    {
      Map map = getGlobals(document);
      System.out.println(mapToString(map, false));
    }
    else if ((s1 != null) && (s1.equalsIgnoreCase("-getData")))
    {
      if (args.length > 3)
      {
        String s2 = args[2];
        String s4 = args[3];
        TestData testdata1 = new TestData(s2, document);
        System.out.print(testdata1.getParam(s4));
      }
      else if (args.length > 2)
      {
        String s3 = args[2];
        TestData testdata = new TestData(s3, document);
        System.out.print(testdata.export());
      }
      else
      {
        String[] args2 = (String[])getAllTestIDs(document).toArray(new String[0]);
        for (int j = 0; j < args2.length; j++)
        {
          TestData testdata2 = new TestData(args2[j], document);
          String s5 = testdata2.export();
          StringBuffer stringbuffer = new StringBuffer(args2[j]);
          stringbuffer.append('\n');
          stringbuffer.append(s5.length());
          stringbuffer.append('\n');
          stringbuffer.append(s5);
          stringbuffer.append('\n');
          System.out.print(stringbuffer);
        }
      }
    }
    else if ((s1 != null) && (s1.equalsIgnoreCase("-getAll")))
    {
      if (!validateArgs(args, 5, 6)) {
        return;
      }
      File file = new File(args[2]);
      File file1 = new File(args[3]);
      File file2 = new File(args[4]);
      file.delete();
      file1.delete();
      file2.delete();
      String[] args3 = null;
      if (args.length > 5) {
        args3 = getTestIDs(document, args[5]);
      } else {
        args3 = getTestIDs(document);
      }
      PrintStream printstream = new PrintStream(new FileOutputStream(file));
      for (int k = 0; k < args3.length; k++) {
        printstream.print(args3[k] + "\n");
      }
      Map map1 = getGlobals(document);
      printstream = new PrintStream(new FileOutputStream(file1));
      printstream.println(mapToString(map1, false));
      printstream = new PrintStream(new FileOutputStream(file2));
      for (int l = 0; l < args3.length; l++)
      {
        TestData testdata3 = new TestData(args3[l], document);
        String s6 = testdata3.export();
        StringBuffer stringbuffer1 = new StringBuffer(args3[l]);
        stringbuffer1.append('\n');
        stringbuffer1.append(s6.length());
        stringbuffer1.append('\n');
        stringbuffer1.append(s6);
        stringbuffer1.append('\n');
        printstream.print(stringbuffer1);
      }
    }
    else
    {
      System.out.println("Invalid action: " + s1);
    }
  }
  
  public static String DELIMITATE_REGEXP = ",";
}
