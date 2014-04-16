package com.wq.model;

import java.io.Serializable;

/**
 * 
 * @author luoyao E-mail:iluoyao@qq.com
 * @version 2013-4-28 下午5:25:29
 * @Description TODO
 */
public class VersionItem extends myObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String verName = "";
	public int versionCode = 0;
	public String updateUrl = "";
	public String updateContent = "";

	public String mandupVer = "";// 禁止版本
	public int UpdateFlag = 0;// -1: 当前版本已被禁止，0:当前版本最新 ，1需要更新
	public String isClearLocalData = "false";// 是否需要更新
}