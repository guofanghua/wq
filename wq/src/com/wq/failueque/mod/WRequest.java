package com.wq.failueque.mod;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wq.utils.httpUtil;

public class WRequest {

	private int reqId;

	private String reqUrl;

	private Map<String, String> param;
	
	/**
	 * 是否加入失败队列
	 */
	private boolean isAddInFailQueue;

	/**
	 * 优先级
	 */
	private int privilege;

	private int maxRequestTimes = 3;

	private int requestCount = 0;

	public WRequest(int reqId) {
		this.reqId = reqId;
		param = new HashMap<String, String>();
	}

	public int getReqId() {
		return reqId;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public Map<String, String> getParam() {
		return param;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}

	public int getPrivilege() {
		return privilege;
	}

	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}

	public int getMaxRequestTimes() {
		return maxRequestTimes;
	}

	public void setMaxRequestTimes(int maxRequestTimes) {
		this.maxRequestTimes = maxRequestTimes;
	}

	public void requestOver() {
		requestCount++;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}

	public void addParam(String key, String value) {
		param.put(key, value);
	}

	public void addParam(String key, int value) {
		addParam(key, String.valueOf(value));
	}

	public void addParam(String key, long value) {
		addParam(key, String.valueOf(value));
	}

	public void addParam(String key, float value) {
		addParam(key, String.valueOf(value));
	}

	public void addParam(String key, double value) {
		addParam(key, String.valueOf(value));
	}

	public void addParam(String key, boolean value) {
		addParam(key, String.valueOf(value));
	}

	public void addParam(String key, char value) {
		addParam(key, String.valueOf(value));
	}
	
	public boolean isAddInFailQueue() {
		return isAddInFailQueue;
	}

	public void setAddInFailQueue(boolean isAddInFailQueue) {
		this.isAddInFailQueue = isAddInFailQueue;
	}

	public String createJson() {
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("reqId", reqId);
			outObj.put("url", httpUtil.BASE_SERVER_URL + getReqUrl());
			JSONArray paraArray = new JSONArray();
			if (param != null && param.size() > 0) {
				for (String key : param.keySet()) {
					JSONArray itemArray = new JSONArray();
					itemArray.put(0, key);
					itemArray.put(1, param.get(key));
					paraArray.put(itemArray);
				}
			}
			outObj.putOpt("param", paraArray);
			outObj.put("privilege", privilege);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	public void initFromJson(JSONObject obj) {
		if (obj != null) {
			try {
				requestCount = 0;
				String url = obj.getString("url");
				if (url != null
						&& url.length() > httpUtil.BASE_SERVER_URL.length()) {
					reqUrl = url.substring(httpUtil.BASE_SERVER_URL.length());
				}
				System.out.println("AAA:"+url);
				System.out.println("AAA:"+httpUtil.BASE_SERVER_URL);
				System.out.println(reqUrl);
				privilege = obj.optInt("privilege");
				reqId = obj.optInt("reqId");
				JSONArray paramArray = obj.getJSONArray("param");
				if (paramArray != null && paramArray.length() > 0) {
					for (int i = 0; i < paramArray.length(); i++) {
						JSONArray itemArray = paramArray.getJSONArray(i);
						if (itemArray.length() == 2) {
							if (param == null)
								param = new HashMap<String, String>();
							param.put(itemArray.getString(0),
									itemArray.getString(1));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
