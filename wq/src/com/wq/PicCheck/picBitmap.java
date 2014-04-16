package com.wq.PicCheck;

import java.io.Serializable;

import android.graphics.Bitmap;

public class picBitmap implements Serializable {
	private String flagstr="1";//0表示服务器，1表示本地
	private String filePath="";//图片地址
	private Bitmap bitmap=null;
	public String getFlagstr() {
		return flagstr;
	}
	public void setFlagstr(String flagstr) {
		this.flagstr = flagstr;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

}
