package com.wq.model;

import net.endure.framework.annotation.sqlite.Id;

/**
 * 网络访问失败
 * */
public class httpErrModel extends myObject {
	@Id
	private int pid = 0;
	private String url = "";// 提交的接口地址
	private String paramStr = "";// 提交的参数
	private int errCount = 0;// 提交失败次数

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParamStr() {
		return paramStr;
	}

	public void setParamStr(String paramStr) {
		this.paramStr = paramStr;
	}

	public int getErrCount() {
		return errCount;
	}

	public void setErrCount(int errCount) {
		this.errCount = errCount;
	}

}
