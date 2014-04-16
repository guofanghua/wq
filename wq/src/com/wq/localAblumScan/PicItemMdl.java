package com.wq.localAblumScan;

import java.io.Serializable;

/**
 * 每项图片实体
 * 
 * @author Aiven
 * @date 2014-1-21
 * @email aiven163@sina.com
 */
public class PicItemMdl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mDisplayName;
	private int mThumbId;
	private boolean isSelected;
	private String mPath;

	public int getThumbId() {
		return mThumbId;
	}

	public void setThumbId(int mThumbId) {
		this.mThumbId = mThumbId;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String mPath) {
		this.mPath = mPath;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public void setDisplayName(String mDisplayName) {
		this.mDisplayName = mDisplayName;
	}

}
