package com.cloudvision.tanzhenv2.order.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * json初步解析
 * @Description TODO
 */
public class JSONUtil {
	public static String optResult(String json){
		String result = null;
		try {
			JSONObject jsonObj=new JSONObject(json);
			result=jsonObj.optString("result", null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static int optError(String json){
		int error = 0;
		try {
			JSONObject jsonObj=new JSONObject(json);
			error=jsonObj.optInt("error", 0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public static String optObject(String json, String key){
		String result = null;
		try {
			JSONObject jsonObj=new JSONObject(json);
			result=jsonObj.optString(key, null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
