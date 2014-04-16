package com.wq.model;

import java.io.Serializable;
import java.util.ArrayList;

import net.endure.framework.annotation.sqlite.Id;
import net.endure.framework.annotation.sqlite.Table;
import net.endure.framework.annotation.sqlite.Transient;

@Table(name = "notice")
public class notice extends myObject {
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 1L;

	private String cateName = "";// 种类名
	private String id = "";
	private String time = "";
	private String title = "";// 标题
	private String content = "";// 内容

	private String userId = User.id;// 用户数据缓存
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	

	@Transient
	private ArrayList<photoModel> pic = new ArrayList<photoModel>();// 图片

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<photoModel> getPic() {
		return pic;
	}

	public void setPic(ArrayList<photoModel> pic) {
		this.pic = pic;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
