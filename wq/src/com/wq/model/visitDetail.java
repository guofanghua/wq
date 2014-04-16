package com.wq.model;

/**
 * 
 * 访问统列表详情
 * 
 * @author Administrator
 * 
 */
public class visitDetail extends myObject {
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	private String time = "";
	private String count = "0";

}
