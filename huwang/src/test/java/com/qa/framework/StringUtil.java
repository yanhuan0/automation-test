package com.qa.framework;
import java.io.*;

import java.net.URLEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StringUtil{
    private static final String DELIMITATE_REGEXP = ",";
    public static final int MAX_SHORT_MSG_LEN=255;
    
	public static String getTimeStamp(){
		SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd-HH:mm:ss.S");
		return formatter.format(new Date());
	}

	public static String argToString(Object arg){
		if(arg==null)
			return "null";
		if(arg instanceof String)
			return "\""+truncateMsg(arg.toString())+"\"";
		if(arg instanceof File)
			return "["+((File)arg).getAbsolutePath()+"]";
		if(arg instanceof String[])
			return "["+arg.toString()+"]";
		if(arg instanceof int[])
			return "["+arg.toString()+"]";
		if(arg instanceof List)
			return "["+arg.toString()+"]";
		return arg.toString();
	}

	public static String truncateMsg(String msg){
		if(msg==null)
			return msg;
		if(msg.length()<=MAX_SHORT_MSG_LEN)
			return msg;
		return msg.substring(0, MAX_SHORT_MSG_LEN)+"...("+msg.length()+" chars in total)";
	}

	public static Map<String, String> toMap(String s) {
        LinkedHashMap<String, String> linkedhashmap = new LinkedHashMap<String, String>();
        if (s == null || s.length() == 0)
            return linkedhashmap;
        String as[] = splitAndTrim(s);
        for (int i = 0; i < as.length; i++) {
            String s1 = as[i];
            int j = s1.indexOf("=");
            if (j >= 0) {
                String s2 = s1.substring(0, j).trim();
                String s3 = s1.substring(j + 1).trim();
                if (s3.startsWith("\"") && s3.endsWith("\""))
                    s3 = s3.substring(1, s3.length() - 1);
                linkedhashmap.put(s2, s3);
            } else {
                linkedhashmap.put(s1, "");
            }
        }

        return linkedhashmap;
    }

    private static String[] splitAndTrim(String s) {
        if (s == null)
            return null;
        String s1 = ("" + Math.random()).substring(2);
        String s2 = ("" + Math.random()).substring(2);
        String s3 = s.replaceAll("\\\\\\\\", s1);
        s3 = s3.replaceAll("\\\\,", s2);
        s = s3.replaceAll(s1, "\\\\");
        String as[] = s.split(DELIMITATE_REGEXP);
        for (int i = 0; i < as.length; i++) {
            as[i] = as[i].trim();
            if (as[i].indexOf(s2) >= 0)
                as[i] = as[i].replaceAll(s2, ",");
        }

        return as;
    }

    /**
     * Escape a string for inclusion in a URL as a query argument
     * eg the value parts of:
     *
     * ... &key1=value1&key2=value2
     * eg the name part of:
     * ..../ibots/name
     */
    public static String urlEncode(String value)
    {
            
         try 
         {
            final String encodedValue = URLEncoder.encode(value, "UTF-8");
            return encodedValue;
         }
         catch (UnsupportedEncodingException ex)
         {
            throw new RuntimeException("UTF-8 not supported", ex);
         }
    }
    
    
   /**
    * When creating javascript literals must escape \ and ' characters
    */
   public static String escapeJavascriptStringLiteral(String sRaw)
   {
      String ret = sRaw;
      ret = ret.replace("\\", "\\\\");
      ret = ret.replace("'", "\\'");
      return ret;
   }
   
   /**
    * Escaping xpath expression literals is horrid since there is no escape character. 
    * Strings containing ' can be delimited by ", and strings containing " can be 
    * delimited by '.  Strings containing both have to be broken up into separate pieces 
    * and concatanated back together.  
    * eg input:     Paul said "Hello"
    *    output:    'Paul said "Hello"'
    * eg input:     abc's house
    *    output:    concat('abc', "'", 's house')
    */
   public static String escapeXPath(String sRaw)
   {

      String ret;

      // Use negative control count, so we do not discard trailing space
      String[] parts = sRaw.split("'", -1);


      if (parts.length == 1)
      {
         // String contains no single quotes so only need to wrap in single quotes
         ret = "'" + sRaw + "'";;
      }
      else
      {
         ret = "concat(";
         ret += "'" + parts[0] + "'";

         for (int i=1; i < parts.length; i++)
         {
            // Quote the single quote in a double quote delimited string
            ret += ",\"'\",";
            ret += "'" + parts[i] + "'";
         }

         ret += ")";
      }

      return ret;

   }
	public static String listToString(List<Object> list){
		StringBuffer buf=new StringBuffer();
		for(Object obj : list)
			buf.append(obj.toString()+"\n");
		return buf.toString();
	}

   public static void main(String args[]) throws Exception{
  }
}
