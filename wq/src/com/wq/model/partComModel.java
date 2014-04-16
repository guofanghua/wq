package com.wq.model;

import java.io.Serializable;

import net.endure.framework.annotation.sqlite.Table;
import net.endure.framework.annotation.sqlite.Transient;

/**
 * 伙伴
 * 
 * @author Administrator
 * 
 */
@Table(name = "partComModel")
public class partComModel extends myObject implements Serializable {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 1L;
	private String userId = User.id;
	private int id = 0;
	private String Pid = "";//
	private String enterpriseId = "";// 企业id
	private String vqw = "";// 微企网地址
	private String vqh = "";// 微企号
	private String icon = "";// 企业logo

	private String commodity = "";// 经营类型
	private String isAttention = "0";// 0表示未关注，1表示已关注是否已关注
	private String name = "";// 企业名字
	private String channel = "";// 渠道号 0是按照微企号1是按照企业名称2是按照经营类型3按照扫一扫4是碰碰
	private String contactName = "";// 联系人
	private String occupation = "";// 职务
	private String telePhone = "";
	private String mobile = "";
	private String flag = "-1";// 0标示名片录 1关注我的伙伴，2标示搜索3标示一起摇 4标示一起按
								// 5.伙伴详情999表示用户信息需要修改
	private String pycontactName = "";// 用于存储名字的全拼音
	private String address = "";// 地址
	private String enterpriseNet = "";// 微企网

	private String xcCount = "";// 图片数量
	private String noticeTitle = "";// 动态标题

	private String weChat = "";// 联系人微信
	private String lx_mobile = "";// 联系人电话
	private int type = 0;// 0表示预留 1 表示伙伴有所更新

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEnterpriseNet() {
		return enterpriseNet;
	}

	public void setEnterpriseNet(String enterpriseNet) {
		this.enterpriseNet = enterpriseNet;
	}

	public String getXcCount() {
		return xcCount;
	}

	public void setXcCount(String xcCount) {
		this.xcCount = xcCount;
	}

	public String getNoticeTitle() {
		return noticeTitle;
	}

	public void setNoticeTitle(String noticeTitle) {
		this.noticeTitle = noticeTitle;
	}

	public String getWeChat() {
		return weChat;
	}

	public void setWeChat(String weChat) {
		this.weChat = weChat;
	}

	public String getLx_mobile() {
		return lx_mobile;
	}

	public void setLx_mobile(String lx_mobile) {
		this.lx_mobile = lx_mobile;
	}

	// 用于勾选时标示用
	@Transient
	private boolean isCheck = false;// 是否勾选

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPycontactName() {
		return pycontactName;
	}

	public void setPycontactName(String pycontactName) {
		this.pycontactName = pycontactName;
	}

	private String time = "";

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTelePhone() {
		return telePhone;
	}

	public void setTelePhone(String telePhone) {
		this.telePhone = telePhone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public partComModel() {

	}

	public partComModel(String contactName) {
		this.contactName = contactName;

	}

	public partComModel(String contactName, String pYcontactName) {
		this.contactName = contactName;
		this.pycontactName = pYcontactName;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getName() {
		return name;
	}

	public String getIsAttention() {
		return isAttention;
	}

	public void setIsAttention(String isAttention) {
		this.isAttention = isAttention;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPid() {
		return Pid;
	}

	public void setPid(String pid) {
		Pid = pid;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getVqw() {
		return vqw;
	}

	public void setVqw(String vqw) {
		this.vqw = vqw;
	}

	public String getVqh() {
		return vqh;
	}

	public void setVqh(String vqh) {
		this.vqh = vqh;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String imgPath) {
		this.icon = imgPath;
	}

	public void initData() {
		this.channel = "";
		this.commodity = User.commodity;
		this.contactName = User.contactName;

		this.enterpriseId = User.id;

		this.flag = "0";
		this.icon = User.iconFile;

		this.Pid = "";

		this.isAttention = "0";

		this.mobile = User.moblie;

		this.name = User.name;

		this.occupation = User.occupation;

		this.pycontactName = "";
		this.telePhone = User.telePhone;
		this.vqh = User.wqh;
		this.vqw = User.enterpriseNet;

	}
}
