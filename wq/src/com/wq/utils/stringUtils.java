package com.wq.utils;

import java.util.ArrayList;

import android.R.integer;
import android.text.TextUtils;

import com.wq.model.listSplitArrModel;
import com.wq.model.proAttr;

public class stringUtils {
	/**
	 * 
	 * @description 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
	 * 
	 * @param content
	 * 
	 * @return
	 */

	public static int getCharacterNum(final String content) {

		if (null == content || "".equals(content)) {

			return 0;

		} else {

			return (content.length() + getChineseNum(content));

		}

	}

	/**
	 * 
	 * @description 返回字符串里中文字或者全角字符的个数
	 * 
	 * @param s
	 * 
	 * @return
	 */

	public static int getChineseNum(String s) {

		int num = 0;

		char[] myChar = s.toCharArray();

		for (int i = 0; i < myChar.length; i++) {

			if ((char) (byte) myChar[i] != myChar[i]) {

				num++;

			}

		}

		return num;

	}

	// 计算字符长度
	public static int strLength(String value) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
			String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				valueLength += 2;
			} else {
				/* 其他字符长度为1 */
				valueLength += 1;
			}
		}

		return valueLength;
	}

	/**
	 * 拆分属性
	 * */
	public static ArrayList<proAttr> splitAttr(String attrStr) {
		LoggerUtil.i(attrStr);
		ArrayList<proAttr> pro = new ArrayList<proAttr>();
		if (!TextUtils.isEmpty(attrStr)) {
			String[] arr = attrStr.split("&\\|");

			for (String s : arr) {
				String[] arr1 = s.split("&:");
				if (arr1.length >= 2) {
					proAttr attr = new proAttr();
					attr.setAttrKey(arr1[0]);
					attr.setAttrValue(arr1[1]);
					pro.add(attr);
				}
			}
		}
		return pro;
	}

	// 字符串中是否包含中文
	public static boolean hasZW(String value) {
		boolean isZW = false;
		String chinese = "[\u0391-\uFFE5]";
		for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
			String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				isZW = true;
				break;
			}
		}
		return isZW;
	}

	// 字符串是否全为数字
	public static boolean isSZ(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	/*** 将2｜二维码数据格式切割 */
	public static void splitarrString(String[] arr,
			ArrayList<listSplitArrModel> splitArrModels) {

		for (int i = 0; i < arr.length; i++) {
			listSplitArrModel item = new listSplitArrModel();
			if (arr[i].indexOf("|") >= 0) {
				String[] tmpStrings = arr[i].split("\\|");
				int length = tmpStrings.length;
				if (length >= 1) {
					item.setSplitHeight(Integer.parseInt(tmpStrings[0]));

				}
				if (length >= 2) {
					item.setSplitMarginLeft(Integer.parseInt(tmpStrings[1]));

				}
				if (length >= 3) {
					item.setTextMarginleft(Integer.parseInt(tmpStrings[2]));
				}
				if (length >= 4) {
					item.setIndex(Integer.parseInt(tmpStrings[3]));
				}
				if (length >= 5) {
					item.setTitle(tmpStrings[4]);
				}
				if (length >= 6)
					item.setTitle1(tmpStrings[5]);
				if (length >= 5) {
					splitArrModels.add(item);
				}
			}
		}
	}
}
