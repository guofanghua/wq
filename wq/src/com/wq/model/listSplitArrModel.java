package com.wq.model;

public class listSplitArrModel {
	private int splitHeight = 0;
	int splitMarginLeft = 15;
	int TextMarginleft = splitMarginLeft + 5;
	int index = 0;
	String title = "";
	String title1 = "";
	int imageid = -1;
	boolean isArrowShow = true;

	public boolean isArrowShow() {
		return isArrowShow;
	}

	public void setArrowShow(boolean isArrowShow) {
		this.isArrowShow = isArrowShow;
	}

	public int getImageid() {
		return imageid;
	}

	public void setImageid(int imageid) {
		this.imageid = imageid;
	}

	public int getSplitHeight() {
		return splitHeight;
	}

	public void setSplitHeight(int splitHeight) {
		this.splitHeight = splitHeight;
	}

	public int getSplitMarginLeft() {
		return splitMarginLeft;
	}

	public void setSplitMarginLeft(int splitMarginLeft) {
		this.splitMarginLeft = splitMarginLeft;
	}

	public int getTextMarginleft() {
		return TextMarginleft;
	}

	public void setTextMarginleft(int textMarginleft) {
		TextMarginleft = textMarginleft;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle1() {
		return title1;
	}

	public void setTitle1(String title1) {
		this.title1 = title1;
	}

}
