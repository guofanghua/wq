package com.wq.model;

import java.io.Serializable;

public class Company extends myObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2255051662109199864L;
	public String userKey = "";// "CS11SU";
	public String id = "";// 企业ID
	public String name = "";// 企业名称
	public String introduciont = "";// 企业简介
	public String culture = "";// 企业文化
	public String desire = "";// 企业愿景
	public String commodity = "";// 产品介绍
	public String iconFile = "";// 企业Icon
	public String propagandaFile = "";// 宣传图片
	public String address = "";// 地址
	public String email = "";// 邮件
	public String telePhone = "";// 固定电话
	public String moblie = "";// 移动电话
	public String net = "";// 企业网站
	public String enterpriseNet = "";// 微企网
	public String enterpriseType = "";// 企业类型
	public String wqh = "";// 微企号
	public String isCertification = "";// 是否已经认证了
	public String cerStatus = "0";// 认证状态
	public String agentId = "";// 代理商id
	public String templateId = "";// 选择的模板id
	public String contactName = "";// 联系人名字
	public String occupation = "";// 联系人职务
	public String weChat = "";// 联系人微信
	public String proEmail = "";// 密保
	public String signature = "";// 个性签名
	public String nameCardTempId = "";// 名片模板

	public void initData() {
		this.cerStatus = User.cerStatus;
		this.nameCardTempId = User.nameCardTempId;
		this.signature = User.signature;
		this.id = User.id;
		this.userKey = User.userKey;
		this.name = User.name;
		this.isCertification = User.isCertification;
		this.agentId = User.agentId;
		this.enterpriseNet = User.enterpriseNet;
		this.introduciont = User.introduciont;
		this.culture = User.culture;
		this.desire = User.desire;
		this.commodity = User.commodity;
		this.iconFile = User.iconFile;
		this.propagandaFile = User.propagandaFile;
		this.address = User.address;
		this.email = User.email;
		this.telePhone = User.telePhone;
		this.moblie = User.moblie;
		this.net = User.net;
		this.enterpriseNet = User.enterpriseNet;
		this.enterpriseType = User.enterpriseType;
		this.wqh = User.wqh;
		this.templateId = User.templateId;
		this.contactName = User.contactName;
		this.occupation = User.occupation;
		this.weChat = User.weChat;
		this.proEmail = User.proEmail;

	}
}
