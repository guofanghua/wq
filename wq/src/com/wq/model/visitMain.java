package com.wq.model;

/**
 * 
 * 访问统计显示界面
 * 
 * @author Administrator
 * 
 */
public class visitMain extends myObject {
	private String allCount = "0";
	private String dayCount = "0";
	private String weekCount = "0";
	private String monthCount = "0";
	private String quarterCount = "0";
	private String yearCount = "0";

	public String getAllCount() {
		return allCount;
	}

	public void setAllCount(String allCount) {
		this.allCount = allCount;
	}

	public String getDayCount() {
		return dayCount;
	}

	public void setDayCount(String dayCount) {
		this.dayCount = dayCount;
	}

	public String getWeekCount() {
		return weekCount;
	}

	public void setWeekCount(String weekCount) {
		this.weekCount = weekCount;
	}

	public String getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(String monthCount) {
		this.monthCount = monthCount;
	}

	public String getQuarterCount() {
		return quarterCount;
	}

	public void setQuarterCount(String quarterCount) {
		this.quarterCount = quarterCount;
	}

	public String getYearCount() {
		return yearCount;
	}

	public void setYearCount(String yearCount) {
		this.yearCount = yearCount;
	}

}
