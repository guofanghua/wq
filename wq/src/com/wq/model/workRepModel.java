package com.wq.model;

import java.util.ArrayList;

public class workRepModel extends myObject {
	private String id = "";
	private String time = "";
	private String workSummary1 = "";
	private String workSummary2 = "";
	private String type = "0";
	private ArrayList<workCSModel> CsList = new ArrayList<workCSModel>();
	private String status = "";
	private String markingCountnent = "";
	private String enterpriseID = "";
	private String icon = "";
	private String name = "";
	private String occupation = "";

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

	public String getWorkSummary1() {
		return workSummary1;
	}

	public void setWorkSummary1(String workSummary1) {
		this.workSummary1 = workSummary1;
	}

	public String getWorkSummary2() {
		return workSummary2;
	}

	public void setWorkSummary2(String workSummary2) {
		this.workSummary2 = workSummary2;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<workCSModel> getCsList() {
		return CsList;
	}

	public void setCsList(ArrayList<workCSModel> csList) {
		CsList = csList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMarkingCountnent() {
		return markingCountnent;
	}

	public void setMarkingCountnent(String markingCountnent) {
		this.markingCountnent = markingCountnent;
	}

	public String getEnterpriseID() {
		return enterpriseID;
	}

	public void setEnterpriseID(String enterpriseID) {
		this.enterpriseID = enterpriseID;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

}
