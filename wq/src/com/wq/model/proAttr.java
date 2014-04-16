package com.wq.model;

import java.io.Serializable;

public class proAttr extends myObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1829415102056465959L;
	private String id = "";
	private String attrKey = "";
	private String attrValue = "";
	private String isPublicAttr="0";//0为不公开，1为公开

	

	public String getIsPublicAttr() {
		return isPublicAttr;
	}

	public void setIsPublicAttr(String isPublicAttr) {
		this.isPublicAttr = isPublicAttr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAttrKey() {
		return attrKey;
	}

	public void setAttrKey(String attrKey) {
		this.attrKey = attrKey;
	}

	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

}
