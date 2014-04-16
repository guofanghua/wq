package com.wq.model;

import java.io.Serializable;
import java.util.ArrayList;

import net.endure.framework.annotation.sqlite.Id;
import net.endure.framework.annotation.sqlite.Table;
import net.endure.framework.annotation.sqlite.Transient;

/** 产品 */
@Table(name = "product")
public class product extends myObject {
	/**
	 * 
	 */

	private ArrayList<photoModel> picList = new ArrayList<photoModel>();// 产品图片
	
	private String id = "";// 产品id
	private String title = "";// 产品名称
	private String price = "";// 产品价格
	private String intro = "";// 产品简介
	private String cateId = "";// 产品种类id
	private String cateName = "";// 产品种类名
	private String proAttrStr = "";// 属性
	private String time = "";// 产品添加时间
	//private String sendFlag = "0";// 0 操作成功 1表示操作失需重新发送
	private String userId = User.id;

	

//	public String getSendFlag() {
	//	return sendFlag;
	//}

	//public void setSendFlag(String sendFlag) {
		//this.sendFlag = sendFlag;
	//}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private ArrayList<proAttr> attrList = new ArrayList<proAttr>();// 产品属性

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getProAttrStr() {
		return proAttrStr;
	}

	public void setProAttrStr(String proAttrStr) {
		this.proAttrStr = proAttrStr;
	}

	public ArrayList<photoModel> getPicList() {
		return picList;
	}

	public void setPicList(ArrayList<photoModel> picList) {
		this.picList = picList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getCateId() {
		return cateId;
	}

	public void setCateId(String cateId) {
		this.cateId = cateId;
	}

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public ArrayList<proAttr> getAttrList() {
		return attrList;
	}

	public void setAttrList(ArrayList<proAttr> attrList) {
		this.attrList = attrList;
	}

}
