package com.wq.model;

/**
 * 
 * 名片模版
 * */
public class nameCardMb extends myObject {
	private String userId = User.id;
	private String cardId = "";// 名片模版

	private String frontImgUrl = "";// 正面图
	private String reverseImgUrl = "";// 反面图

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getFrontImgUrl() {
		return frontImgUrl;
	}

	public void setFrontImgUrl(String frontImgUrl) {
		this.frontImgUrl = frontImgUrl;
	}

	public String getReverseImgUrl() {
		return reverseImgUrl;
	}

	public void setReverseImgUrl(String reverseImgUrl) {
		this.reverseImgUrl = reverseImgUrl;
	}

}
