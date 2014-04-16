package com.wq.model;

import android.graphics.Color;

public class listItemModelBase extends myObject {
	/** 图片背景 －1 表示不显示图片 */
	private int ImageId = -1;
	/** item 背景 */
	private int Background = -1;
	/** item 类型 */
	private int type = 0;// 0表示分割 1 跳转 2 表示点击操作
	/** item 高度 */
	private int itemHeight = 50;// dp为单位
	/** item 显示的标题 */
	private String tittle = "";// 现实标题
	/** 显示再右边的 */
	private String tittle1 = "";// 现实标题
	/** spline 离左边距离 */
	private int splitMarginLeft = 0;// dp
	/** 文字 离左边距离 */
	private int tittleMarginLeft = 0;// 单位dp
	/** 是否显示箭头 */
	private boolean arrawIsShow = true;// true 表示显示

	private int titleTextColor = Color.BLACK;
	private int title1TextColor = Color.GRAY;

	public int getTitleTextColor() {
		return titleTextColor;
	}

	public void setTitleTextColor(int titleTextColor) {
		this.titleTextColor = titleTextColor;
	}

	public int getTitle1TextColor() {
		return title1TextColor;
	}

	public void setTitle1TextColor(int title1TextColor) {
		this.title1TextColor = title1TextColor;
	}

	private int index = -1;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getImageId() {
		return ImageId;
	}

	public void setImageId(int imageId) {
		ImageId = imageId;
	}

	public int getBackground() {
		return Background;
	}

	public void setBackground(int background) {
		Background = background;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}

	public String getTittle() {
		return tittle;
	}

	public void setTittle(String tittle) {
		this.tittle = tittle;
	}

	public String getTittle1() {
		return tittle1;
	}

	public void setTittle1(String tittle1) {
		this.tittle1 = tittle1;
	}

	public int getSplitMarginLeft() {
		return splitMarginLeft;
	}

	public void setSplitMarginLeft(int splitMarginLeft) {
		this.splitMarginLeft = splitMarginLeft;
	}

	public int getTittleMarginLeft() {
		return tittleMarginLeft;
	}

	public void setTittleMarginLeft(int tittleMarginLeft) {
		this.tittleMarginLeft = tittleMarginLeft;
	}

	public boolean isArrawIsShow() {
		return arrawIsShow;
	}

	public void setArrawIsShow(boolean arrawIsShow) {
		this.arrawIsShow = arrawIsShow;
	}

}
