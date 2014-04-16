package com.wq.model;

import java.util.ArrayList;

/**
 * 产品种类列表
 * */
public class productList extends myObject {
	private String id="";//种类id
	private String name="";//产品种类名称
	
	private ArrayList<product> proList=new ArrayList<product>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<product> getProList() {
		return proList;
	}
	public void setProList(ArrayList<product> proList) {
		this.proList = proList;
	}
	

}
