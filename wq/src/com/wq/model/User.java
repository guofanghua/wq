package com.wq.model;

import java.io.Serializable;

import com.wq.mainActivity;
import com.wq.fragment.findFragment;
import com.wq.fragment.inqFragment;
import com.wq.fragment.meFragment;
import com.wq.fragment.partFragment;
import com.wq.utils.LoggerUtil;

public class User extends myObject implements Serializable {
	private static final long serialVersionUID = 2255051662109199864L;
	public static String userKey = "";// "CS11SU";
	public static String id = "";// 企业ID
	public static String name = "";// 企业名称
	public static String introduciont = "";// 企业简介
	public static String culture = "";// 企业文化
	public static String desire = "";// 企业愿景
	public static String commodity = "";// 产品介绍
	public static String iconFile = "";// 企业Icon
	public static String propagandaFile = "";// 宣传图片
	public static String address = "";// 地址
	public static String email = "";// 邮件
	public static String telePhone = "";// 固定电话
	public static String moblie = "";// 移动电话
	public static String net = "";// 企业网站
	public static String enterpriseNet = "";// 微企网
	// public static String erweimaUrl = "";// 二维码
	public static String enterpriseType = "";// 企业类型
	public static String wqh = "";// 微企号
	public static String isCertification = "";// 是否已经认证了
	public static String cerStatus = "0";// 0 在野用户 1 收编用户 2 管理员 3 子管理员
	public static String agentId = "";// 代理商id
	public static String templateId = "1";// 选择的模板id
	public static String nameCardTempId = "1";// 名片模板
	public static String contactName = "";// 联系人名字
	public static String occupation = "";// 联系人职务
	public static String weChat = "";// 联系人微信
	public static String proEmail = "";// 密保
	public static String signature = "";// 个性签名

	/**
	 * 确定各个tip是否显示的标志 index=0标示 tab1 txt_count是否显示， index==1 标示tab1系统动态是否显示
	 */
	public static boolean[] bArr = { false, false, false, false, false, false,
			false, false };
	// tip 显示的次数
	public static String[] tipCountArr = { "0", "0", "0", "0" };

	public static void init(Company company) {
		User.cerStatus = company.cerStatus;
		User.nameCardTempId = company.nameCardTempId;
		User.signature = company.signature;
		User.id = company.id;
		User.userKey = company.userKey;
		User.name = company.name;
		User.isCertification = company.isCertification;
		User.agentId = company.agentId;
		User.enterpriseNet = company.enterpriseNet;
		User.introduciont = company.introduciont;
		User.culture = company.culture;
		User.desire = company.desire;
		User.commodity = company.commodity;
		User.iconFile = company.iconFile;
		User.propagandaFile = company.propagandaFile;
		User.address = company.address;
		User.email = company.email;
		User.telePhone = company.telePhone;
		User.moblie = company.moblie;
		User.net = company.net;
		User.enterpriseNet = company.enterpriseNet;
		User.enterpriseType = company.enterpriseType;
		User.wqh = company.wqh;
		User.templateId = company.templateId;
		User.contactName = company.contactName;
		User.occupation = company.occupation;
		User.weChat = company.weChat;
		User.proEmail = company.proEmail;

	}

	public static void ClearUser() {
		mainActivity.currIndex = 0;
		mainActivity.preIndex = 0;
		findFragment.broLoadFlag = true;
		inqFragment.broLoadFlag = true;
		inqFragment.isUpdate = false;
		meFragment.broLoadFlag = true;
		meFragment.isUpdate = false;
		partFragment.broLoadFlag = true;
		partFragment.isUpdate = false;
		User.id = "";
		User.userKey = "";
		User.name = "";
		User.isCertification = "";
		User.agentId = "";
		User.enterpriseNet = "";
		User.introduciont = "";
		User.culture = "";
		User.desire = "";
		User.commodity = "";
		User.iconFile = "";
		User.propagandaFile = "";
		User.address = "";
		User.email = "";
		User.telePhone = "";
		User.moblie = "";
		User.net = "";
		User.enterpriseNet = "";
		User.enterpriseType = "";
		User.wqh = "";
		User.templateId = "";
		User.contactName = "";
		User.occupation = "";
		User.weChat = "";
		User.proEmail = "";
		User.nameCardTempId = "";
		User.signature = "";

	}
}
