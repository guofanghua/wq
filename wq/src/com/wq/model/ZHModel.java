package com.wq.model;

import java.util.ArrayList;

/** 展会 */
public class ZHModel extends myObject {
	String id = "";
	String time = "";

	String title = "";
	String content = "";
	ArrayList<photoModel> picList = new ArrayList<photoModel>();
	String enterpraseId = "";// 企业id
	String logoUrl = "";// 企业logo

	public String getEnterpraseId() {
		return enterpraseId;
	}

	public void setEnterpraseId(String enterpraseId) {
		this.enterpraseId = enterpraseId;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<photoModel> getPicList() {
		return picList;
	}

	public void setPicList(ArrayList<photoModel> picList) {
		this.picList = picList;
	}
}
