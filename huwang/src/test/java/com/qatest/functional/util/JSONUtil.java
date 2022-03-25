package com.qatest.functional.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JSONUtil {

	/**
	 * Delete key from JSONObject
	 * 
	 * @param str
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject delKeys(JSONObject jo, String[] keys) {
		JSONObject targetJO = jo;
		if (jo != null && keys.length > 0) {
			for (String key : keys) {
				try {
					targetJO = (JSONObject) jo.remove(key);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return targetJO;
	}

	/**
	 * Input or replace key for JSON string
	 * 
	 * @param str
	 *            : JSON String
	 * @param key
	 * @param value
	 * @return
	 * @throws JSONException
	 */
	public static String putParameter(String str, String key, String value) {
		if (str != null) {
			JSONObject jo;
			try {
				jo = new JSONObject(str);
				jo.put(key, value);
				str = jo.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("JSON String is null, please check input parameter");
		}

		return str;
	}

	/**
	 * Input or replace multiple keys for JSON Object
	 * 
	 * @param str
	 *            : JSON String
	 * @param map
	 *            : Map<key, value>
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject putParameter(JSONObject jo, Map<String, String> map) {
		if (jo != null) {
			if (map != null) {
				for (String key : map.keySet()) {
					try {
						jo.put(key, map.get(key));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("JSONObject is null, please check input parameter");
		}

		return jo;
	}

	/**
	 * Check a parameter value for JSON Object
	 * 
	 * @param jo
	 * @param key
	 * @param value
	 * @return
	 * @throws JSONException
	 */
	public static boolean checkParameterValue(JSONObject jo, String key, String value) {
		boolean flag = false;
		if (jo == null) {
			System.out.println("JSONObject is null");
			return flag;
		}

		if (key == null)
			return flag;

		int index = key.indexOf(":");
		if (index < 0) {
			flag = checkSimpleParameterValue(jo, key, value);
			return flag;
		} else {
			String parentKey = key.substring(0, index);
			String childKey = key.substring(index + 1, key.length());

			if (jo.has(parentKey)) {
				JSONArray joArray = jo.optJSONArray(parentKey);

				if (joArray != null) {
					for (int i = 0; i < joArray.length(); i++) {
						try {
							flag = checkParameterValue(joArray.getJSONObject(i), childKey, value);
							if (flag)
								return flag;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					JSONObject childJo = jo.optJSONObject(parentKey);
					flag = checkParameterValue(childJo, childKey, value);
				}
			} else {
				System.out.println(parentKey + "doesn't exists");
			}

		}

		return flag;
	}

	/**
	 * Check multiple parameter values for JSON object
	 * 
	 * @param str
	 * @param map
	 * @return
	 * @throws JSONException
	 */
	public static boolean checkParametersValue(JSONObject jo, Map<String, String> map) {
		if (jo == null) {
			System.out.println("JSONObject is null");
			return false;
		}

		if (map != null) {
			for (String key : map.keySet()) {
				if (!checkSimpleParameterValue(jo, key, map.get(key)))
					return false;
			}
			return true;
		} else {
			System.out.println("Map is empty");
			return false;
		}
	}

	private static boolean checkSimpleParameterValue(JSONObject jo, String key, String value) {
		if (jo == null) {
			System.out.println("JSONObject is null");
			return false;
		}
		if (jo.has(key)) {
			String actualValue = jo.optString(key);

			if (actualValue.equals(value)) {
				return true;
			} else {
				return false;
			}
		} else {
			System.out.println(key + "doesn't exists");
			return false;
		}
	}

	/**
	 * Check a parameter value for JSON Object
	 * 
	 * @param jo
	 * @param key
	 * @param value
	 * @return
	 * @throws JSONException
	 */
	public static String getKeysValue(JSONObject jo, String key) {
		String value = "";
		if (jo == null) {
			System.out.println("JSONObject is null");
			return value;
		}

		if (key == null)
			return value;
		int index = key.indexOf(":");
		if (index < 0) {
			value = getKeyValue(jo, key);
			return value;
		} else {
			String parentKey = key.substring(0, index);
			String childKey = key.substring(index + 1, key.length());
			
			if (jo.has(parentKey)) {
				JSONArray joArray = jo.optJSONArray(parentKey);

				if (joArray != null) {
					for (int i = 0; i < joArray.length(); i++) {
						try {
							value = getKeysValue(joArray.getJSONObject(i), childKey);
							return value;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					JSONObject childJo = jo.optJSONObject(parentKey);
					value = getKeysValue(childJo, childKey);
				}
			} else {
				System.out.println(parentKey + " doesn't exists");
			}

		}

		return value;
	}

	public static JSONObject replaceKeyValue(JSONObject jo, String sourceTxt, String targetTxt) {
		String jsonString = "";
		JSONObject targetJO = jo;
		try {
			if (jo != null) {
				System.out.println("Replace '" + sourceTxt + "' as '" + targetTxt + "'.");
				jsonString = jo.toString().replaceAll(sourceTxt, targetTxt);
				targetJO = new JSONObject(jsonString);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetJO;
	}

	private static String getKeyValue(JSONObject jo, String key) {
		if (jo == null) {
			System.out.println("JSONObject is null");
			return "";
		}
		if (jo.has(key)) {
			String actualValue = jo.optString(key);
			return actualValue;
		} else {
			return "";
		}
	}

	public static JSONObject readJSONFile(File filePath) {

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;

		try {
			Object obj = parser.parse(new FileReader(filePath));
			try {
				jsonObject = new JSONObject(obj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}



}
