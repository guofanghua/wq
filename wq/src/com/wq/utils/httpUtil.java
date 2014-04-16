package com.wq.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import net.endure.framework.FinalHttp;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.wq.WelecomeActivity;
import com.wq.model.Company;
import com.wq.model.User;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 网络请求工具类
 * 
 */
public class httpUtil {
	public static final boolean Isdebug = false;
	private static FinalHttp fh = null;
	public static String errCode_success = "000";
	public static String errCode_nodata = "103";
	public static String errCode_isCertification = "999";
	public static int ERR_MAX = 3;// 最大重新提交次数

	private static FinalHttp getInstance() {
		if (fh == null) {
			fh = new FinalHttp();
		}
		return fh;
	}

	// 正式环境
	// public static String SUPER_BASE_URL =
	// "http://www7789as.201ffg.tieya.net";
	// 正式环境
	public static String SUPER_BASE_URL = "http://www.v7zone.com";
	public static String BASE_SERVER_URL = SUPER_BASE_URL + "/m/";
	// 测试
	// private static String SUPER_BASE_URL = "http://192.168.0.101";
	 //public static String BASE_SERVER_URL = SUPER_BASE_URL +
	// "/microEnterprise/";
	// 认证对应项目
	public static String BASE_SERVER_URL1 = SUPER_BASE_URL + "/authenticate/";
	// 认证显示界面

	public static String RZXY_URL = SUPER_BASE_URL
			+ "/authenticate/index.php?action=certificate";

	public static final String APP_UPDATE_URL = "client/updateVersion.php";// 软件升级
	public static final String SAFE_LOGIN_URL = "client/safeLogin.php";// 安全登录
	public static final String SAFE_REGISTER_URL = "client/register.php";// 安全登录
	public static final String NAME_CARD_URL = "card/index.php";// 名片模版
	// 功能简介
	public static final String ABOUT_US_URL = BASE_SERVER_URL
			+ "client/web/about.html";
	// 欢迎姐 main
	public static final String WELCOME_US_URL = BASE_SERVER_URL
			+ "client/web/welcome.html";

	// 应用中心url
	public static final String USE_CENTER_WEBVIEW_URL = BASE_SERVER_URL
			+ "client/web/applyCenter.html?source=android";
	public static final String INQ_INFO_URL = "client/a_spatialDynamic.php";// 访问空间最新动态
	public static final String SYSTEM_INFO_URL = "client/getSysInfo.php";// 系统公告
	public static final String SYSTEM_TRAFFIC_URL = "client/getTraffic.php";// 访问统计量
	public static final String ENTER_PRISE_URL = "client/getEnterprise.php";// 获取企业基本信息
	public static final String ENTER_PRISE_UPDATE_URL = "client/updateEnterprise.php";// 更新企业基本信息
	public static final String PRODUCT_LIST_URL = "client/getCommodity.php";// 获取企业产品列表
	public static final String PRODUCT_DETAIL_URL = "client/getCommodityDetail.php";// 获取产品详情
	public static final String PRODUCT_ADD_URL = "client/addCommodity.php";// 添加产品
	public static final String PRODUCT_DEL_URL = "client/deleteCommodity.php";// 删除产品
	public static final String PRODUCT_UPDATE_URL = "client/updateCommodity.php";// 修改商品信息
	public static final String PRODUCT_CATEGORY_LIST_URL = "client/getCommType.php";// 获取产品类型列表
	public static final String PRODUCT_CATEGORY_ADD_URL = "client/addCommType.php";// 添加产品类型
	public static final String PRODUCT_CATEGORY_DEL_URL = "client/deleteCommType.php";// 删除产品类型
	public static final String ENTER_PROMOTIONS_URL = "client/getPromotions.php";// 获取动态公告
	public static final String NOTICE_ADD_URL = "client/addPromotions.php";// 添加动态公告
	public static final String PART_DETAIL_URL = "client/getPartnerDetails.php";// 获取伙伴详情
	public static final String PART_LIST_URL = "client/getPartnerList.php";// 获取伙伴列表
	public static final String PART_TIP_LIST_URL = "client/getAttentionMeList.php";// 关注我的伙伴列表详情
	public static final String PART_SEARCH_URL = "client/searchPartner.php";// 搜索伙伴
	public static final String PART_ATTENTION_URL = "client/attentionPartner.php";// 关注伙伴
	public static final String PART_CANCEL_ATTENTION_URL = "client/noAttentionPartner.php";// 取消关注

	public static final String PRODUCT_ATTR_ADD_URL = "client/addpublicAttribute.php";// 添加产品属性
	public static final String PRODUCT_ATTR_DEL_URL = "client/deletepublicAttribute.php";// 删除产品公共属性
	public static final String COLLECT_LIST_URL = "client/getCollection.php";// 获取我的收藏
	public static final String NOTICE_UPDATE_URL = "client/updatePromotions.php";// 更新动态公告
	public static final String NOTICE_DETAIL_URL = "client/getPromotionDetail.php";// 根据id获取动态公告详情
	public static final String COMPANY_PHOTO_ADD_URL = "client/addAulmu.php";// 添加企业墙图片
	public static final String COMPANY_PHOTO_DEL_URL = "client/deleteAulmu.php";// 删除企业墙图片
	public static final String COMPANY_PHOTO_GET_URL = "client/getAulmu.php";// 获取企业墙图片
	public static final String NOTICE_DEL_URL = "client/deletePromotions.php";// 删除动态公告
	public static final String ABLUM_DEL_URL = "client/deleteAulmu.php";// 删除企业相册
	public static final String ABLUM_ADD_URL = "client/addAulmu.php";// 添加
	public static final String ABLUM_UPDATE_URL = "client/updateAulmu.php";// 编辑企业相册
	public static final String ABLUM_GET_URL = "client/getAulmu.php";// 获取企业相册列表
	public static final String SET_KEY_URL = "client/changePaw.php";// 修改安全码
	public static final String VISIT_LIST_URL = "client/a_getTraffic.php";// 网站访问统计列表

	public static final String VISIT_DETAIL_URL = "client/a_getTrafficDetail.php";// 访问统计详情
	// 企业圈
	public static final String CIRCLE_PROGA_PIC_URL = "client/setPropaganda.php";// 修改宣传片图片
	public static final String OPERATE_TRADE_LIST_URL = "client/getTrade.php";// 获取企业圈列表信息
	public static final String CRICLE_MSG_ADD_URL = "client/sendbackComment.php";// 添加留言或者赞
	public static final String CIRCLE_PRO_ADD_URL = "client/sendMyTrade.php";// 发布生意圈
	public static final String CIRCLE_PRO_DEL_URL = "client/deleteMyTrade.php";// 删除企业圈信息
	public static final String CIRCLE_MSG_DEL_URL = "client/deletebackComment.php";// 删除评论
	public static final String CIRCLE_PRO_DETAILE_URL = "client/a_getAppointTrade.php";// 生意圈详情
	public static final String CIRCLE_PRO_TRADE_URL = "client/a_getMyTradeComment.php";// 获取最新评论
	// 碰一碰接口
	public static final String FIND_PYP_URL = "client/a_bump.php";// 碰一碰接口
	// 一起按
	public static final String PART_YQA_URL = "client/a_press.php";// 一起按
	// 意见反馈
	public static final String ME_SUGGEST_URL = "client/a_suggestion.php";// 意见反馈
	// public static final String OPERATE_TRADE_DEL_URL =
	// "client/deleteMyTrade.php";// 删除我发布的生意圈
	// 获取模板
	public static final String TEMPLATE_LIST_URL = "client/getTemplate.php";
	public static final String TEMPLATE_SET_URL = "client/setTemplate.php";
	// 设置密保
	public static final String PWD_PROTECT_URL = "client/a_setZiploc.php";// 设置密保
	public static final String PWD_PRO_SEND_URL = "client/a_sendToZiploc.php";// 发送密保邮箱
	public static final String PWD_RE_SET_URL = "client/a_changePassword.php";// 重置密码
	// 我的名片
	public static final String NAME_CARD_GET_URL = "client/a_getbusinessCard.php";// 获取名片列表
	public static final String NAME_CARD_SET_URL = "client/a_setMyCard.php";// 设置我的名片
	// 展会
	public static final String FIND_ZH_GET_URL = "client/a_getDynamicType.php";// 获取展会和招商
	// 批量关注
	public static final String PART_PL_GZ_URL = "client/attentionALLPartner.php";// 批量关注
	// 获取名片记录
	public static final String CUSTOM_RELATION_LIST_URL = "client/a_getCustomerRelationship.php";// 来往信息列表
	// 修改登录密码
	public static final String CHANGE_PWD_URL = "client/changePaw.php";// 修改登录密码
	/**
	 * 纪录联系人关系日志
	 */
	public static final String RECODE_RELATION_LOG = "client/a_sendCustomerRelationship.php";
	/** 添加工作报告 */
	public static final String WORK_REP_ADD_URL = "index.php?action=alc_workReport2";
	/** 批阅报告 */
	public static final String WORK_REP_PY_URL = "index.php?action=alc_workReport3";
	/** 报告详情 */
	public static final String WORK_REP_DETAIL_URL = "index.php?action=alc_workReport1";
	/** 报告列表 */
	public static final String WORK_REP_LIST_URL = "index.php?action=alc_workReport0";
	// 公共字段
	public static final String USER_ID = "id";
	public static final String USER_KEY = "userKey";
	public static final String ERR_CODE = "errCode";
	public static final String ERR_MGS = "errMgs";
	public static final String IMAGE_NUM = "imgNum";
	public static final String CUR_PAGE = "curPage";
	// 修改登录密码
	public static final String NEW_PWD = "newPaw";
	public static final String PWD_CHANGE_SIGN = "sign";
	// 访问统计
	public static final String FLAG = "type";// web访问统计
	public static final String DETAIL_FLAG = "styleType";// 名片访问
	// 关系日志
	public static final String RELATION_LOG_CONTENT = "content";
	public static final String RELATION_LOG_PID_STRING = "enterpriseIdArray";

	// 重置密码
	public static final String PWD_RE_SET_WQH = "wqh";
	public static final String PWD_RE_SET_JYM = "checkCode";// 校验码
	public static final String PWD_RE_SET_PWD = "newCode";//
	public static final String PWD_RE_SET_T = "t";// 校验
	// 密保邮箱
	public static final String PWD_PRO_EMIAL = "mail";// 设置邮箱
	public static final String PWD_PRO_PWD = "code";// 密码
	// 发送微企号至邮箱
	public static final String FORGET_PWD_WQH = "wqh";//
	// 留言
	public static final String SUGGEST_CONTENT = "content";
	// 换一换接口
	public static final String PYPWIFI = "wifi";
	public static final String PYPLON = "lon";
	public static final String PYPLAT = "lat";
	public static final String PYPAddr = "address";
	// public static final String PYPEId = "enterpriseId";

	// 安全登录接口
	public static final String SAFT_NAME = "wqh";
	public static final String SAFT_LOSIGN = "logSign";
	public static final String SAFT_DECIVE_INFO = "deviceInfo";
	// 注册
	public static final String REGISTER_WQH = "wqh";
	public static final String REGISTER_PWD = "password";
	public static final String REGISTER_T = "t";
	public static final String REGISTER_DECIVE_INFO = "deviceInfo";
	// 修改企业基本信息
	public static final String ENTER_UPDATE_INTRO = "introduciont";
	public static final String ENTER_UPDATE_CULTURE = "culture";
	public static final String ENTER_UPDATE_DESIRE = "desire";
	public static final String ENTER_UPDATE_COMMODITY = "commodity";
	public static final String ENTER_UPDATE_ADDR = "address";
	public static final String ENTER_UPDATE_EMAIL = "email";
	public static final String ENTER_UPDATE_TEL = "telePhone";
	public static final String ENTER_UPDATE_LX_NAME = "contactName";
	public static final String ENTER_UPDATE_LX_ZW = "occupation";
	public static final String ENTER_UPDATE_LX_MOBILE = "moblie";

	public static final String ENTER_UPDATE_LX_WECHAT = "weChat";
	public static final String ENTER_UPRATE_SIGN = "signature";
	public static final String ENTER_UPDATE_TYPE = "enterpriseType";

	public static final String ENTER_UPDATE_NET = "net";
	public static final String ENTER_UPDATE_NAME = "name";
	// 添加动态公告
	public static final String NOTICE_ADD_TITLE = "title";
	public static final String NOTICE_ADD_CONTENT = "content";
	public static final String NOTICE_ADD_CATENAME = "typeName";
	// 更新动态公告
	public static final String NOTICE_UPDATE_ID = "promotionId";
	public static final String NOTICE_UPDATE_TITLE = "title";
	public static final String NOTICE_UPDATE_CONTENT = "content";
	public static final String NOTICE_DEL_PIC = "deleImgIdArray";
	// 删除动态公告
	public static final String NOTICE_DEL_ID = "promotionId";
	public static final String NOTICE_ID = "adId";
	// 添加企业相册
	public static final String ABLUM_ADD_SCRIPTION = "fdscription";
	public static final String ABLUM_ADD_IMG_NUM = "imgNum";
	// 删除企业相册
	public static final String ABLUM_DEL_ID = "aulmuId";
	// 更新企业相册
	public static final String ABLUM_ID = "aubluId";
	public static final String ABLUM_UPUDATE_SCRIPTION = "fdscription";
	public static final String ABLUM_DEL_PIC = "deleImgIdArray";
	public static final String ABLUM_UPDATE_IMG_NUM = "imgNum";
	// 获取企业相册 列表
	public static final String ABLUM_ENTER_ID = "enterpriseId";
	// 添加产品
	public static final String PRODUCT_ADD_NAME = "name";
	public static final String PRODUCT_ADD_CONTENT = "content";
	public static final String PRODUCT_ADD_TYPE = "type";
	public static final String PRODUCT_ADD_PRICE_OLD = "priceOld";
	public static final String PRODUCT_ADD_PRICE_NOW = "priceNow";
	public static final String PRODUCT_ADD_INVERTORY_COUNT = "invertoryCount";// 点击次数
	public static final String PRODUCT_ADD_TYPE_NAME = "typeName";
	public static final String PRODUCT_ADD_IMG_NUM = "imgNum";
	public static final String PRODUCT_ADD_ATTR = "attribute";
	public static final String Product_ADD_DEL_IMG_ID = "deleImgIdArray";
	// 获取产品详情
	public static final String PRODUCT_PRO_ID = "goodsId";

	// 修改产品
	public static final String PRODUCT_UPDATE_NAME = "name";
	public static final String PRODUCT_UPDATE_CONTENT = "content";
	public static final String PRODUCT_UPDATE_TYPE = "type";
	public static final String PRODUCT_UPDATE_PRICE_OLD = "priceOld";
	public static final String PRODUCT_UPDATE_PRICE_NOW = "priceNow";
	public static final String PRODUCT_UPDATE_INVERTORY_COUNT = "invertoryCount";
	public static final String PRODUCT_UPDATE_COMMODITY_ID = "commodityId";
	public static final String PRODUCT_UPDATE_TYPE_NAME_ID = "commodityTypeId";
	// 添加公共属性
	public static final String PRODUCT_ATTR_KEY = "attributeKey";
	public static final String PRODUCT_ATTR_VALUE = "";
	public static final String PRODUCT_ATTR_IS_PUBLIC = "";
	// 删除公共属性
	public static final String PRODUCT_ATTR_ID = "attributeId";
	// 刪除产品
	public static final String PRODUCT_DEL_COMMODITY_ID = "commodityId";
	// 添加产品种类
	public static final String CATE_NAME = "name";
	// 删除产品种类
	public static final String TYPE_ID = "typeId";
	// 删除企业墙图片
	public static final String AULMU_ID = "aulmuId";
	// 修改安全码
	public static final String SET_KEY_NEW_PAW = "newPaw";
	public static final String SET_KEY_SIGN = "sign";
	// 获取伙伴详情，关注伙伴，取消伙伴,获取企业相册，产品列表，动态公告
	public static final String ENTER_PRISE_ID = "enterpriseId";
	public static final String ENTER_CHANNEL = "channel";
	// 批量关注
	public static final String ENTER_PRISE_ID_ARR = "enterpriseIdArray";

	// 搜索 伙伴
	public static final String PART_TYPE = "type";
	public static final String PART_SEARCH_STR = "searchStr";
	// 设置模板
	public static final String TEMPLATE_ID = "templateId";
	// 企业圈
	public static final String CIRCLE_CUR_PAGE = "curPage";
	public static final String CIRCLE_CUR_TIME = "time";
	// 留言
	public static final String MSG_CONTENT = "content";
	public static final String MSG_TYPE = "type";
	public static final String MSG_LEVEL = "level";
	public static final String MSG_SUPERID = "superId";
	// 发布企业圈信息
	public static final String CIRCLE_ADD_CONTENT = "content";
	public static final String CIRCLE_ADD_TYPE = "type";
	public static final String CIRCLE_ADD_IMG_NUM = "imgNum";
	public static final String CIRCLE_ADD_OTHER_ID = "otherId";
	// 删除企业圈信息
	public static final String CIRCLE_DEL_TRADE_ID = "tradeId";
	// 删除留言
	public static final String MSG_DEL_COMMENT_ID = "commentId";
	// 获取企业圈详情
	public static final String TRADE_ID = "tradeId";
	// 访问统计详情
	public static final String visit_Type = "type";
	// 设置名片模板
	public static final String NAME_CARD_ID = "cardId";

	// 展会和招商
	public static final String ZH_TYPE = "type";
	public static final String ZH_CUR_PAGE = "curPage";
	// 获取名片发布日志列表
	public static final String CARD_PAGE = "curPage";
	public static final String CARD_ENTERPRISE_ID = "enterpriseId";

	// 获取企业相册，产品列表，动态公告的企业id

	// 提交工作报告
	public static final String WORK_ADD_TIME = "time";
	public static final String WORK_ADD_TYPE = "type";
	public static final String WORK_ADD_SUMMARY1 = "workSummary1";
	public static final String WORK_ADD_SUMMARY2 = "workSummary2";
	public static final String WORK_ADD_MARK_ID = "markingId";
	public static final String WORK_ADD_MARK_CC = "markingCcId";
	// 获取工作报告列表
	public static final String WORK_LIST_TYPE = "type";
	public static final String WORK_LIST_PAGE = "page";
	// 获取工作报告详情
	public static final String WORK_DETAIL_ID = "workReportId";
	// 批阅
	public static final String WORK_PY_ID = "workReportId";
	public static final String WORK_PY_CONTENT = "content";

	/**
	 * get 异步网络请求
	 * 
	 * @param url
	 * @param callBack
	 */
	public static void get(String url, AjaxCallBack<? extends Object> callBack) {
		LoggerUtil.i("get:::" + BASE_SERVER_URL + url);
		getInstance().get(BASE_SERVER_URL + url, callBack);
	}

	public static void get(String url, AjaxParams params,
			AjaxCallBack<? extends Object> callBack) {
		try {
			LoggerUtil.i("get:::"
					+ BASE_SERVER_URL
					+ url
					+ "?"
					+ URLDecoder.decode(
							EntityUtils.toString(params.getEntity()), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getInstance().get(BASE_SERVER_URL + url, params, callBack);

	}

	/**
	 * post 异步网络请求类
	 */
	// private static AsyncHttpClient httpClient = new AsyncHttpClient();

	public static void post(String url, AjaxCallBack<? extends Object> callBack) {
		LoggerUtil.i(BASE_SERVER_URL + url);
		getInstance().post(BASE_SERVER_URL + url, callBack);

	}

	public static void post(String url, AjaxParams params,
			AjaxCallBack<? extends String> callBack) {
		try {

			LoggerUtil.i("get:::" + BASE_SERVER_URL + url + "?"
					+ URLDecoder.decode(params.getParamString(), "UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getInstance().post(BASE_SERVER_URL + url, params, callBack);

	}

	public static void post1(String url, AjaxCallBack<? extends Object> callBack) {
		LoggerUtil.i(BASE_SERVER_URL1 + url);
		getInstance().post(BASE_SERVER_URL1 + url, callBack);

	}

	public static void post1(String url, AjaxParams params,
			AjaxCallBack<? extends String> callBack) {
		try {

			LoggerUtil.i("get:::" + BASE_SERVER_URL1 + url + "?"
					+ URLDecoder.decode(params.getParamString(), "UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getInstance().post(BASE_SERVER_URL1 + url, params, callBack);

	}

	/**
	 * 检查手机是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	/**
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == cm)
			return false;

		NetworkInfo[] info = cm.getAllNetworkInfo();
		if (null != info) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].isConnected())
					return true;
			}
		}

		return false;
	}

	/**
	 * 是否Wifi
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wm.getConnectionInfo();
		if (null != wm
				&& (WifiInfo.getDetailedStateOf(info.getSupplicantState()) == DetailedState.OBTAINING_IPADDR || WifiInfo
						.getDetailedStateOf(info.getSupplicantState()) == DetailedState.CONNECTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否UTMS网络(3G)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isUmts(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkType() >= TelephonyManager.NETWORK_TYPE_UMTS;
	}

	/**
	 * 是否EDGE网络(GPRS)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isEdge(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE;
	}

	public static String mpURL(String userId) {
		return httpUtil.BASE_SERVER_URL + httpUtil.NAME_CARD_URL + "?v7h="
				+ userId;
	}

}
