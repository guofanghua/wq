package com.wq.model;

import java.util.ArrayList;

public class ecParentCircleModel extends myObject {
	private String time = "";
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	ArrayList<ecCircleModel> eclist = new ArrayList<ecCircleModel>();

	public ArrayList<ecCircleModel> getEclist() {
		return eclist;
	}

	public void setEclist(ArrayList<ecCircleModel> eclist) {
		this.eclist = eclist;
	}

}
