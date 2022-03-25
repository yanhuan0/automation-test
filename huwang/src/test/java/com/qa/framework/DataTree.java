
package com.qa.framework;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.filter.ElementFilter;

class DataTree
  implements Cloneable
{
  private Map m_vars;
  private DataTree m_pData;
  private Map m_res;
  
  protected Object clone()
  {
    DataTree localDataTree = null;
    if (this.m_pData != null) {
      localDataTree = (DataTree)this.m_pData.clone();
    }
    LinkedHashMap localLinkedHashMap1 = new LinkedHashMap(this.m_vars);
    LinkedHashMap localLinkedHashMap2 = new LinkedHashMap(this.m_res);
    return new DataTree(localLinkedHashMap1, localDataTree, localLinkedHashMap2);
  }
  
  private DataTree(Map paramMap1, DataTree paramDataTree, Map paramMap2)
  {
    if (paramMap2 == null) {
      this.m_res = new LinkedHashMap();
    } else {
      this.m_res = paramMap2;
    }
    this.m_vars = paramMap1;
    this.m_pData = paramDataTree;
  }
  
  private Map getParentVars()
  {
    if (this.m_pData == null) {
      return new LinkedHashMap();
    }
    return this.m_pData.getOriginalVars();
  }
  
  private Map getOriginalVars()
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    Map localMap = getParentVars();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localLinkedHashMap.put(str, localMap.get(str));
    }
    localLinkedHashMap.putAll(this.m_vars);
    return localLinkedHashMap;
  }
  
  Map getVars()
  {
    return getOriginalVars();
  }
  
  Object getVar(Object paramObject)
  {
    Map localMap = getOriginalVars();
    String str = (String)localMap.get(paramObject);
    return str;
  }
  
  static DataTree getDataTree(String paramString, Document paramDocument)
  {
    Element localObject = null;
    Iterator localIterator = paramDocument.getDescendants(new ElementFilter());
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      String str = localElement.getName();
      if (((str.equalsIgnoreCase("TestCase")) || (str.equalsIgnoreCase("TestGroup"))) && (localElement.getAttributeValue("id").equals(paramString))) {
        localObject = localElement;
      }
    }
    return localObject == null ? null : getDataTreeByElement(localObject, null);
  }
  
  private static DataTree getDataTreeByElement(Element paramElement, Map paramMap)
  {
    Map localMap = getAllParams(paramElement, null);
    if (paramElement.getName().equalsIgnoreCase("TestSuite")) {
      return new DataTree(localMap, null, paramMap);
    }
    Element localElement = paramElement.getParentElement();
    DataTree localDataTree = getDataTreeByElement(localElement, paramMap);
    return new DataTree(localMap, localDataTree, paramMap);
  }
  
  private static Map getAllParams(Element paramElement, String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      paramString = "";
    } else {
      paramString = paramString + ".";
    }
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    List localList = paramElement.getContent();
    String str1 = "";
    String str2 = paramElement.getName();
    String str3 = paramString + (str2.equalsIgnoreCase("Param") ? paramElement.getAttributeValue("name") : "");
    for (int i = 0; i < localList.size(); i++)
    {
      Object localObject1 = localList.get(i);
      Object localObject2;
      if (((localObject1 instanceof Text)) && (str2.equalsIgnoreCase("Param")))
      {
        localObject2 = (Text)localObject1;
        str1 = str1 + ((Text)localObject2).getTextTrim();
      }
      else if ((localObject1 instanceof Element))
      {
        localObject2 = (Element)localObject1;
        if (((Element)localObject2).getName().equalsIgnoreCase("Param")) {
          localLinkedHashMap.putAll(getAllParams((Element)localObject2, str3));
        }
      }
    }
    if ((str2.equalsIgnoreCase("Param")) && (str1.length() >= 0))
    {
      String str4 = paramString + paramElement.getAttributeValue("name");
      localLinkedHashMap.put(str4, str1);
    }
    return localLinkedHashMap;
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {}
}
