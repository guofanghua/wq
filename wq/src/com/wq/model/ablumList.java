package com.wq.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ablumList extends myObject {
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ArrayList<ablum> getList() {
		return list;
	}
	public void setList(ArrayList<ablum> list) {
		this.list = list;
	}
	String time = "";
	ArrayList<ablum> list = new ArrayList<ablum>();
}
