package com.wq.localAblumScan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片专题文件夹实体
 * 
 * @author Aiven
 * @date 2014-1-21
 * @email aiven163@sina.com
 */
public class PicAlbumMdl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mPathId;//图片id

	private int mAlubPic;//图片数量

	private String mPath;//图片路径

	private List<PicItemMdl> mPicList;//图片列表

	public int getAlubPic() {
		return mAlubPic;
	}

	public void setAlubPic(int mAlubPic) {
		this.mAlubPic = mAlubPic;
	}

	public List<PicItemMdl> getPicList() {
		return mPicList;
	}

	public void setPicList(List<PicItemMdl> mPicList) {
		this.mPicList = mPicList;
	}

	public void add(PicItemMdl mdl) {
		if (mPicList == null) {
			mPicList = new ArrayList<PicItemMdl>();
		}
		mPicList.add(mdl);
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String mPath) {
		this.mPath = mPath;
	}

	public String getPathId() {
		return mPathId;
	}

	public void setPathId(String mPathId) {
		this.mPathId = mPathId;
	}

	public int getLength() {
		if (mPicList == null)
			return 0;
		return mPicList.size();
	}

}
