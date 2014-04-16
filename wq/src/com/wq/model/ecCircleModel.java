package com.wq.model;

import java.io.Serializable;
import java.util.ArrayList;

import net.endure.framework.annotation.sqlite.Id;
import net.endure.framework.annotation.sqlite.Table;

/**
 * 企业圈model
 * */
@Table(name = "ecCircleModel")
public class ecCircleModel extends myObject {
	/**
	 * 
	 */
	// private static final long serialVersionUID = 1L;

	private String id = "";// 生意圈编号
	private String type = "";// 生意圈的类型
								// type0是普通的信息，type1是分享产品，type2是分享动态，type3是分享企业链接
	private String enterpriseId = "";// 企业的id
	private String isCertification = "";// 是否已经认证了
	private String vqh = "";// 微企号
	private String title = "";// 标题
	private String cotent = "";// 生意圈的内容
	private String name = "";// 企业名称
	private String logoUrl = "";// 企业logo
	private String bigBgUrl = "";// 背景大图
	private String otherId = "";// 其他类型id
	private String isListFlag = "0";// 0表示时生意圈列表 1表示时生意圈详情
	private String isSend = "0";// 0表示发送成功 1表示发送失败
	@Id
	private int pid = 0;// 数据库中自增长id

	public String getIsSend() {
		return isSend;
	}

	public void setIsSend(String isSend) {
		this.isSend = isSend;
	}

	public String getIsListFlag() {
		return isListFlag;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setIsListFlag(String isListFlag) {
		this.isListFlag = isListFlag;
	}

	private String userId = User.id;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOtherId() {
		return otherId;
	}

	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

	private String time = "";// 发布时间
	private ArrayList<photoModel> imgList = new ArrayList<photoModel>();// 图片

	private ArrayList<leaveMessage> msgList = new ArrayList<leaveMessage>();// 留言内容
	private ArrayList<leaveMessage> zanList = new ArrayList<leaveMessage>();// 赞的内容

	public String getBigBgUrl() {
		return bigBgUrl;
	}

	public void setBigBgUrl(String bigBgUrl) {
		this.bigBgUrl = bigBgUrl;
	}

	public ArrayList<leaveMessage> getZanList() {
		return zanList;
	}

	public void setZanList(ArrayList<leaveMessage> zanList) {
		this.zanList = zanList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getIsCertification() {
		return isCertification;
	}

	public void setIsCertification(String isCertification) {
		this.isCertification = isCertification;
	}

	public String getVqh() {
		return vqh;
	}

	public void setVqh(String vqh) {
		this.vqh = vqh;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCotent() {
		return cotent;
	}

	public void setCotent(String cotent) {
		this.cotent = cotent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<photoModel> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<photoModel> imgList) {
		this.imgList = imgList;
	}

	public ArrayList<leaveMessage> getMsgList() {
		return msgList;
	}

	public void setMsgList(ArrayList<leaveMessage> msgList) {
		this.msgList = msgList;
	}

}
