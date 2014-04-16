package com.wq.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 设置模板
 */
public class templateModel extends myObject implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String templateId = "";// 模板id
	private String name = "";//模板名字
	private String previewImg = "";//预览图片
	private String resourceInfo = "";//资源信息简介
	private String stylist = "";//设计师
	private String makeTime = "";//制作时间
	private String space = "";//空间大小
	private String version = "";//版本
	private String frequency = "";//使用数量
	private String isCertification="";//是否需要企业认证
	private String isValid="0";//0是可用，1是不可用
	

	public String getIsValid() {
		return isValid;
	}
	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}
	private ArrayList<photoModel> picList = new ArrayList<photoModel>();//图片
	
	public String getIsCertification() {
		return isCertification;
	}
	public void setIsCertification(String isCertification) {
		this.isCertification = isCertification;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPreviewImg() {
		return previewImg;
	}
	public void setPreviewImg(String previewImg) {
		this.previewImg = previewImg;
	}
	public String getResourceInfo() {
		return resourceInfo;
	}
	public void setResourceInfo(String resourceInfo) {
		this.resourceInfo = resourceInfo;
	}
	public String getStylist() {
		return stylist;
	}
	public void setStylist(String stylist) {
		this.stylist = stylist;
	}
	public String getMakeTime() {
		return makeTime;
	}
	public void setMakeTime(String makeTime) {
		this.makeTime = makeTime;
	}
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public ArrayList<photoModel> getPicList() {
		return picList;
	}
	public void setPicList(ArrayList<photoModel> picList) {
		this.picList = picList;
	}

}
