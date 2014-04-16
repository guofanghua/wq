package com.wq.model;

import net.endure.framework.annotation.sqlite.Id;

/**
 * 最新动态
 * */
public class DecInfo extends myObject {
	/**
	 * 
	 */
	@Id
	int id = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	String userId = User.id;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	String sdmicId = "";// 动态id编号
	String partnerId = "";// 伙伴id
	String name = "";// 企业名称
	String info = "";// 介绍
	String url = "";// 点击详情跳转URL地址
	String icon = "";// 图标
	String time = "";// 时间
	String newDynamicCount = "";// 动态个数
	String souceType = "";// 类型来源
	String vqh = "";// 微企号

	public String getVqh() {
		return vqh;
	}

	public void setVqh(String vqh) {
		this.vqh = vqh;
	}

	boolean clickFlag = false;// 还未点击

	public boolean isClickFlag() {
		return clickFlag;
	}

	public void setClickFlag(boolean clickFlag) {
		this.clickFlag = clickFlag;
	}

	public String getSdmicId() {
		return sdmicId;
	}

	public void setSdmicId(String sdmicId) {
		this.sdmicId = sdmicId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNewDynamicCount() {
		return newDynamicCount;
	}

	public void setNewDynamicCount(String newDynamicCount) {
		this.newDynamicCount = newDynamicCount;
	}

	public String getSouceType() {
		return souceType;
	}

	public void setSouceType(String souceType) {
		this.souceType = souceType;
	}

}
