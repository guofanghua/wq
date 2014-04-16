package com.wq.Interface;

import android.widget.ImageView;

import com.wq.model.ecCircleModel;
import com.wq.model.leaveMessage;

/**
 * 企业圈list中点击事件接口
 * */
public interface IecCircleInterface {
	public void topImageClick(ImageView img);// 顶部宣传图片点击事件

	public void checkUserClick(ecCircleModel model);// 查看用户的生意圈点击事件,企业圈id

	/**
	 * 查看企业详情信息
	 * 
	 * */
	public void checkUeserDetailClick(ecCircleModel model);

	/**
	 * position 正对留言的position 需要留言的msg
	 * */
	public void commitClick(int position, leaveMessage msg);

	/**
	 * 取消赞
	 * */
	public void delMsgClick(int position, leaveMessage msg);

	/**
	 * position 正对留言的位置 msg 需要留言的msg
	 * */
	public void btnLeaveClick(int position, leaveMessage msg);// 点击评论按钮事件（显示出输入框）

	/**
	 * 删除 企业圈信息
	 * */
	public void btnDelClick(int position, String tradeId);

	public void contentClick(ecCircleModel model);// 点击内容浏览详情

	public void scanPicClick(ecCircleModel model);// 点击图片，浏览图片

	public void commitRep(int position,ecCircleModel model);// 重新提交数据
}
