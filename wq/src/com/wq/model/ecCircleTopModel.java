package com.wq.model;

import net.endure.framework.annotation.sqlite.Transient;

/**
 * 生意圈顶部
 * */

public class ecCircleTopModel extends myObject {
	private String bigImageUrl = "";// 顶图img
	private String icon = "";
	private String userId = User.id;
	private String enterpriseId = "";// 企业圈id
	private String wqh = "";// 微企号
	@Transient
	private boolean isSendFaulire = false;// 是否有企业圈发送失败的
	@Transient
	private int sendFaulireCount = 0;// 发布失败的信息

	public int getSendFaulireCount() {
		return sendFaulireCount;
	}

	public void setSendFaulireCount(int sendFaulireCount) {
		this.sendFaulireCount = sendFaulireCount;
	}

	public boolean isSendFaulire() {
		return isSendFaulire;
	}

	public void setSendFaulire(boolean isSendFaulire) {
		this.isSendFaulire = isSendFaulire;
	}

	public String getWqh() {
		return wqh;
	}

	public void setWqh(String wqh) {
		this.wqh = wqh;
	}

	public String getBigImageUrl() {
		return bigImageUrl;
	}

	public void setBigImageUrl(String bigImageUrl) {
		this.bigImageUrl = bigImageUrl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	int id = 0;

}
