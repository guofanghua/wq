package com.example.dbtest;

import java.util.ArrayList;

import net.endure.framework.annotation.sqlite.Table;
@Table(name="oneModel")
public class oneModel {
public ArrayList<manyModel> getManyList() {
		return manyList;
	}
	public void setManyList(ArrayList<manyModel> manyList) {
		this.manyList = manyList;
	}
private String id="";
private String name="";
private ArrayList<manyModel> manyList=new ArrayList<manyModel>();
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
}
