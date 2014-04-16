package com.example.dbtest;

import net.endure.framework.annotation.sqlite.Table;

@Table(name="testModel")
public class testModel {
	private String name="";
	private int id=0;
	private String age="";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}

}
