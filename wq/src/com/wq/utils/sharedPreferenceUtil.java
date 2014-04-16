package com.wq.utils;

import com.wq.fragment.inqFragment;
import com.wq.fragment.meFragment;
import com.wq.me.me_name_card_activity;
import com.wq.model.Company;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedPreferenceUtil {
	public final static String PREFS_FILE = "push.dat";
	private final static String PRO_ATTR = "proAttr";
	private final static String COMPANY = "Company";
	private final static String ISFIRATFLAG = "IsFirst";
	private final static String partTipFlag = "tipFlag";
	private final static String UpdateUserFlag = "userUpdateFlag";

	private static SharedPreferences sharedPrefrernces = null;

	public static SharedPreferences getInstance(Context context) {
		if (sharedPrefrernces == null) {
			sharedPrefrernces = context.getSharedPreferences(PREFS_FILE,
					Context.MODE_PRIVATE);
		}
		return sharedPrefrernces;
	}

	/**
	 * 首页是否是第一次进入状态 0标示第一次访问，1标示已经访问了
	 * */
	public static void saveIsFirst(Context context, String flag) {
		writeSharePrefreferences(context, ISFIRATFLAG, flag);
	}

	/**
	 * 读取是否是第一次进入
	 * */
	public static String readIsFirst(Context context) {
		return readSharedPreferences(context, ISFIRATFLAG, "");
	}

	/**
	 * 保存写入产品属性
	 * 
	 * @param context
	 * @param value
	 */
	public static void saveProAttr(Context context, String value) {
		writeSharePrefreferences(context, PRO_ATTR, value);
	}

	/**
	 * 写入最新伙伴数据
	 * */
	public static void saveTipPart(Context context, String value) {
		writeSharePrefreferences(context, partTipFlag, value);
	}

	/**
	 * 读取最新伙伴
	 * */
	public static String readTipPart(Context context) {
		return readSharedPreferences(context, partTipFlag, "0");
	}

	/**
	 * 读取产品属性
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public static String readProAttr(Context context) {
		return readSharedPreferences(context, PRO_ATTR, "");
	}

	/**
	 * 用户信息提交失败时保存用户信息
	 * 
	 * @param context
	 * @param value
	 */
	public static void saveUPCompany(Context context) {
		Company company = new Company();
		company.initData();
		writeSharePrefreferences(context, UpdateUserFlag,
				CommonUtil.encodeObject(company));
	}

	/**
	 * 清除信息提交失败保存的用户信息
	 * 
	 * @param context
	 * @param value
	 */
	public static void clearUPCompany(Context context) {
		writeSharePrefreferences(context, UpdateUserFlag, "");
	}

	/**
	 * 获取公司
	 * 
	 * @param context
	 * @return
	 */
	public static String readUPCompany(Context context) {
		return readSharedPreferences(context, UpdateUserFlag, "");
	}

	/**
	 * 保存公司
	 * 
	 * @param context
	 * @param value
	 */
	public static void saveCompany(Context context) {
		meFragment.isUpdate = true;
		inqFragment.isUpdate = true;
		me_name_card_activity.isUpdate = true;
		Company company = new Company();
		company.initData();
		writeSharePrefreferences(context, COMPANY,
				CommonUtil.encodeObject(company));
	}

	/**
	 * 清除
	 * 
	 * @param context
	 * @param value
	 */
	public static void clearCompany(Context context) {
		writeSharePrefreferences(context, COMPANY, "");
	}

	/**
	 * 获取公司
	 * 
	 * @param context
	 * @return
	 */
	public static String readCompany(Context context) {
		return readSharedPreferences(context, COMPANY, "");
	}

	/**
	 * 读取共享
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	/**
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	private static String readSharedPreferences(Context context, String key,
			String defValue) {
		return getInstance(context).getString(key, defValue);
	}

	/**
	 * 写入共享
	 * */
	private static void writeSharePrefreferences(Context context, String key,
			String value) {
		SharedPreferences.Editor editor = getInstance(context).edit();
		editor.putString(key, value);
		editor.commit();
	}
}
