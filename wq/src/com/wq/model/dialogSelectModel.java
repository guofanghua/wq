package com.wq.model;

import java.io.Serializable;

public class dialogSelectModel extends myObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private int imgResourceId = 0;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getImgResourceId() {
		return imgResourceId;
	}
	public void setImgResourceId(int imgResourceId) {
		this.imgResourceId = imgResourceId;
	}
}
