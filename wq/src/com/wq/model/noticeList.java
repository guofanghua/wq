package com.wq.model;

import java.util.ArrayList;

public class noticeList extends myObject {
	private String cateName = "";
	ArrayList<notice> list = new ArrayList<notice>();

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public ArrayList<notice> getList() {
		return list;
	}

	public void setList(ArrayList<notice> list) {
		this.list = list;
	}

}
