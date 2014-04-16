package com.wq.model;

import com.baidu.location.LocationClientOption;

/**
 * 定位model
 * */
public class localModel extends myObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String addr = "";// 详细地址
	private String latitude = "";// 维度
	private String longitude = "";// 经度
	private String radius = "";// 定位半径
	private String province = "";// 省份
	private String city = "";// 城市
	private String district = "";// 县

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}




	public static int priority = LocationClientOption.NetWorkFirst;// 默认网路优先
	public static boolean isShowAddr = true;// 是否显示详细地址
	public static String localClass = "bd09ll";// 请求类型
	public static int interval = 3000;// 傻喵哦间隔

}
