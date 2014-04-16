package com.wq.model;

import java.io.Serializable;

import net.endure.framework.annotation.sqlite.Id;

/***
 * 评论 model
 * */
public class leaveMessage extends myObject {
	/**
	 * 
	 */
	@Id
	// 自定义主键名称
	private String comId = "";// 评论id
	private String superComId = "";// 被评论人的评论id
	private String enterPriseId = "";// 评论人的企业号
	private String vqh = "";// 评论人的微企号
	
	private String time = "";// 评论时间
	private String content = "";// 评论内容
	private String type = "";// 评论类型 0标示评论，1标示赞
	private String level = "";// 评论等级，最多3级，从1级开始计算
	private String superName = "";// 被回复人名字
	private boolean isSend = true;// 评论是否成功
	private boolean isExist = false;
	private String userId = User.id;
	private String ecid = "";// 评论所属的企业圈id
	
	// 用于查看最新评论
	private String logoUrl = "";// 评论人的头像
	private String name="";//评论人的联系人姓名

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEcid() {
		return ecid;
	}

	public void setEcid(String ecid) {
		this.ecid = ecid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isExist() {
		return isExist;
	}

	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getSuperName() {
		return superName;
	}

	public void setSuperName(String superName) {
		this.superName = superName;
	}

	public String getComId() {
		return comId;
	}

	public void setComId(String comId) {
		this.comId = comId;
	}

	public String getSuperComId() {
		return superComId;
	}

	public void setSuperComId(String superComId) {
		this.superComId = superComId;
	}

	public String getEnterPriseId() {
		return enterPriseId;
	}

	public void setEnterPriseId(String enterPriseId) {
		this.enterPriseId = enterPriseId;
	}

	public String getVqh() {
		return vqh;
	}

	public void setVqh(String vqh) {
		this.vqh = vqh;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

}
