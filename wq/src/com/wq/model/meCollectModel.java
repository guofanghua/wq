package com.wq.model;

import net.endure.framework.annotation.sqlite.Id;
import net.endure.framework.annotation.sqlite.Table;

/** 我的收藏 */
@Table(name = "meCollectModel")
public class meCollectModel extends myObject {
	@Id
	private int pid;// 自增长id
	private String eId = "";// 收藏用户的企业id
	private String url = "";// 收藏的链接地址
	private String time = "";// 收藏的时间
	private String userId = User.id;
	private String name = "";// 收藏人名称
	private String icon = "";// 头像地址
	private String title="";
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private int type = 0;// 0表示收藏的网站 1表示名片

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String geteId() {
		return eId;
	}

	public void seteId(String eId) {
		this.eId = eId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
