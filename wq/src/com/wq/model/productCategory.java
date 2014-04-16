package com.wq.model;

import java.io.Serializable;
import java.util.ArrayList;



public class productCategory extends myObject implements Serializable {
	/**
	 * 
	 */
	private String id = "";
	private String name = "";

	private int count = 0;


	

	

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

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

	/**
	 * 
	 */
	private static final long serialVersionUID = -4486620452555819851L;

}
