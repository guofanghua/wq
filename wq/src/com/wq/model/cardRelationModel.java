package com.wq.model;

import java.util.ArrayList;

public class cardRelationModel extends myObject {
	String id = "";// 来往id

	String hdId = "";// 伙伴关系id
	String content = "";// 来往描述
	String place = "";// 地点
	String startTime = "";// 开始时间
	String endTime = "";// 结束时间
	ArrayList<partComModel> partlist = new ArrayList<partComModel>();

	public String getId() {
		return id;
	}

	public ArrayList<partComModel> getPartlist() {
		return partlist;
	}

	public int Childsize() {
		if (partlist == null)
			return 0;
		return partlist.size();
	}

	public void setPartlist(ArrayList<partComModel> partlist) {
		this.partlist = partlist;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHdId() {
		return hdId;
	}

	public void setHdId(String hdId) {
		this.hdId = hdId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
