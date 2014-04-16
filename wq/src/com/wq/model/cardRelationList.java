package com.wq.model;

import java.util.ArrayList;

public class cardRelationList extends myObject {
	String time = "";
	ArrayList<cardRelationModel> list = new ArrayList<cardRelationModel>();

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<cardRelationModel> getList() {
		return list;
	}

	public void setList(ArrayList<cardRelationModel> list) {
		this.list = list;
	}
}
