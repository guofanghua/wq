package com.wq.model;

import java.io.Serializable;

import net.endure.framework.annotation.sqlite.Id;
import net.endure.framework.annotation.sqlite.Table;
import net.endure.framework.annotation.sqlite.Transient;
import android.os.Parcel;
import android.os.Parcelable;

@Table(name = "photoModel")
public class photoModel implements Serializable {
	/**
	 * 
	 */

	@Transient
	public static final int ABLUM_SHARE_FLAG = 4;// 企业相册
	@Transient
	public static final int LINK_SHARE_FLAG = 3;// 分享链接
	@Transient
	public static final int NOTICE_SHARE_FLAG = 2;// 动态
	@Transient
	public static final int PRODUCT_SHARE_FLAG = 1;// 产品
	@Transient
	public static final int NOMAL_SHARE_FLAG = 0;// 正常发布
	@Transient
	private static final long serialVersionUID = 1L;



	private myObject shareModel = new myObject();
	@Id
	private int pid = 0;

	private String extStr;

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	private String id = "";
	// private String smallImageUrl = "";

	private String ImageUrl = "";
	private String ecid = "";
	private String userId = User.id;
	private int flag = 0;// 0表示从服务器上加载 的图片 1表示本地图片
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Object getShareModel() {
		return shareModel;
	}

	public void setShareModel(myObject shareModel) {
		this.shareModel = shareModel;
	}

	private int shareType = 0;// 分享类型

	public int getShareType() {
		return shareType;
	}

	public void setShareType(int shareType) {
		this.shareType = shareType;
	}

	public String getEcid() {
		return ecid;
	}

	public void setEcid(String ecid) {
		this.ecid = ecid;
	}

	public String getImageUrl() {
		return ImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExtStr() {
		return extStr;
	}

	public void setExtStr(String extStr) {
		this.extStr = extStr;
	}

	// public String getSmallImageUrl() {
	// return smallImageUrl;
	// }
	//
	// public void setSmallImageUrl(String smallImageUrl) {
	// this.smallImageUrl = smallImageUrl;
	// }

	// @Override
	// public int describeContents() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public void writeToParcel(Parcel dest, int flags) {
	// // TODO Auto-generated method stub
	// dest.writeString(id);
	// dest.writeString(url);
	// dest.writeInt(flag);
	//
	// }
	//
	// public static final Parcelable.Creator<photoModel> CREATOR = new
	// Creator<photoModel>() {
	//
	// @Override
	// public photoModel[] newArray(int size) {
	// // TODO Auto-generated method stub
	// return new photoModel[size];
	// }
	//
	// @Override
	// public photoModel createFromParcel(Parcel source) {
	// photoModel item = new photoModel();
	// item.id = source.readString();
	// item.url = source.readString();
	// item.flag = source.readInt();
	// // TODO Auto-generated method stub
	// return item;
	// }
	// };

}
