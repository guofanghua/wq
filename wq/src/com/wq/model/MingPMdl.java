package com.wq.model;

public class MingPMdl {
	private int id;
	private String bzStr;
	private String path;
	private String timStr;
	private String userId=User.id;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBzStr() {
		return bzStr;
	}

	public void setBzStr(String bzStr) {
		this.bzStr = bzStr;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTimStr() {
		return timStr;
	}

	public void setTimStr(String timStr) {
		this.timStr = timStr;
	}

}
