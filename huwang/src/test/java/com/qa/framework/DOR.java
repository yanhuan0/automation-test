package com.qa.framework;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

public class DOR {
	private Map m_guiObjs = new Hashtable();
	private Map m_webClassMapping = new Hashtable();

	public int size() {
		return m_guiObjs.size();
	}

	public void add(Document doc) throws FrameworkException {
		add(doc, false);
	}

	public void add(Document doc, boolean allowDupKeys) throws FrameworkException {
		Element root = doc.getRootElement();
		List groups = root.getChildren();
		Iterator it = groups.iterator();
		int numDupKeys = 0;
		while (it.hasNext()) {
			Element group = (Element) it.next();
			if (group.getAttributes().size() > 0) {// this is not a group, but an object
				String webClassName = null;
				String key = group.getName();
				if (key.startsWith("btn"))
					webClassName = "WebButton";
				else if (key.startsWith("chk"))
					webClassName = "WebCheckBox";
				else if (key.startsWith("img") || key.startsWith("image"))
					webClassName = "Image";
				else if (key.startsWith("lst") || key.startsWith("list"))
					webClassName = "WebList";
				else if (key.startsWith("edit"))
					webClassName = "WebEdit";
				else if (key.startsWith("opt"))
					webClassName = "WebRadioButton";
				else if (key.startsWith("tbl"))
					webClassName = "WebTable";
				else if (key.startsWith("lnk") || key.startsWith("link"))
					webClassName = "Link";
				else
					webClassName = "WebElement";
				try {
					addGUIObject(group, webClassName, allowDupKeys);
				} catch (FrameworkException e) {
					System.out.println(e.getMessage());
					numDupKeys++;
				}
			} else { // this is a group
				List objects = group.getChildren();
				Iterator it2 = objects.iterator();
				while (it2.hasNext()) {
					Element obj = (Element) it2.next();
					try {
						addGUIObject(obj, group.getName(), allowDupKeys);
					} catch (FrameworkException e) {
						System.out.println(e.getMessage());
						numDupKeys++;
					}
				}
			}
		}
		if (numDupKeys > 0)
			throw new FrameworkException("found " + numDupKeys + " duplicated keys");
	}

	private String mapToString(Map properties) {
		Iterator it = properties.keySet().iterator();
		String locator = "";
		while (it.hasNext()) {
			String name = (String) it.next();
			String value = (String) properties.get(name);
			locator += name + "=" + value + " ";
		}
		String result = locator.trim();
		return result;
	}

	private void addGUIObject(Element obj, String webClassName, boolean allowDupKeys) throws FrameworkException {
		String key = obj.getName();
		Map properties = new LinkedHashMap();
		List attrs = obj.getAttributes();
		Iterator it3 = attrs.iterator();
		while (it3.hasNext()) {
			Attribute attr = (Attribute) it3.next();
			if (attr.getName().equals("browser"))
				key = key + "." + attr.getValue();
			else
				properties.put(attr.getName(), attr.getValue());
		}
		if (m_guiObjs.containsKey(key) && !allowDupKeys) {
			String dorValue1 = mapToString(getProperties(key));
			String dorValue2 = mapToString(properties);
			throw new FrameworkException(
					"Found duplicate DOR key: " + key + "\nvalue1=" + dorValue1 + "\nvalue2=" + dorValue2);
		}
		m_guiObjs.put(key, properties);
		m_webClassMapping.put(key, webClassName);
	}

	public Map getProperties(String key) {
		return (Map) m_guiObjs.get(key);
	}

	public String getProperty(String key, String name) {
		if (!m_guiObjs.containsKey(key))
			return null;
		return (String) getProperties(key).get(name);
	}

	public String getWebClass(String keyStr) {
		String key = keyStr;
		int index = keyStr.indexOf("(");
		if (index > 0)
			key = keyStr.substring(0, index);
		return (String) m_webClassMapping.get(key);
	}

	/**
	 * get Selenium locator string for a specifed object from DOR xml
	 * 
	 * @param keyStr unique key tag in DOR xml file to identify object
	 * @return Selenium locator string
	 */
	public String getLocator(String keyStr) {
		keyStr = keyStr.trim();
		String key = keyStr;
		int index = keyStr.indexOf("(");
		String paramStr = null;
		if (index > 0) {
			key = keyStr.substring(0, index);
			paramStr = keyStr.substring(index + 1, keyStr.length() - 1);
		}
		String browser = System.getProperty("qa.browser");
		Map properties = getProperties(key);
		if (browser != null && browser.startsWith("*ie")) {
			Map ieProps = getProperties(key + ".ie");
			if (ieProps != null) {
				properties = ieProps;
				System.out.println("Use IE specific locator: " + key);
			}
		}
		if (properties == null)
			return null;
		Iterator it = properties.keySet().iterator();
		String locator = "";
		while (it.hasNext()) {
			String name = (String) it.next();
			String value = (String) properties.get(name);
			locator += name + "=" + value + " ";
		}
		String result = locator.trim();
		if (index > 0) {
			String[] params = paramStr.split(",");
			for (int i = 0; i < params.length; i++)
				result = result.replaceAll("%" + (i + 1), params[i]);
		}
		try {
			// DataDrivenTestCase.drawRect(result);
		} catch (Exception e) {
		}
		System.out.println("locator[" + keyStr + "]: " + result);

		return result;
	}

	public String replaceNestedDorKey(String locator) {
		String regexp = "\\$\\{.*?\\}";
		Pattern p = Pattern.compile(regexp);
		String newLocator = locator;
		while (true) {
			Matcher m = p.matcher(newLocator);
			if (m.find()) {
				String group = m.group();
				String nestedKey = group.substring(2, group.length() - 1);
				String nestedLocator = getLocator(nestedKey);
				nestedLocator = nestedLocator.replace("xpath=", "");
				nestedLocator = nestedLocator.replace("css=", "");
				newLocator = newLocator.replace(group, nestedLocator);
			} else {
				break;
			}
		}
		return newLocator;
	}

	static void main(String[] args) {
	}
}
