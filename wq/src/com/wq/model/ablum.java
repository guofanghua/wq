package com.wq.model;

import java.util.ArrayList;

import net.endure.framework.annotation.sqlite.Id;
import net.endure.framework.annotation.sqlite.Table;

@Table(name = "ablum")
public class ablum extends myObject {
	String time = "";// 时间
	String content = "";// 内容
	ArrayList<photoModel> list = new ArrayList<photoModel>();
	String id = "";
	
	String userId = User.id;
	

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<photoModel> getList() {
		return list;
	}

	public void setList(ArrayList<photoModel> list) {
		this.list = list;
	}

}
