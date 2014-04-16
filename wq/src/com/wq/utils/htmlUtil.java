package com.wq.utils;

public class htmlUtil {
	/**
	 * 设置指定字符串的颜色
	 * 
	 * @param strSource
	 *            :源字符串
	 * @param color
	 *            : #ccc or red 设置颜色字符串
	 * @return
	 */
	public static String setStrColor(String strSource, String color) {
		StringBuilder sb = new StringBuilder();
		sb.append("<font color=\"");
		sb.append(color);
		sb.append("\">");
		sb.append(strSource);
		sb.append("</font>");
		return sb.toString();

	}
	/**
	 * @param strSource
	 *            :源字符串
	 * @param subStr
	 *            ：子字符串数组
	 * @param subColor
	 *            ：子字串需要改变的颜色字串，（需与需改变的子串对应）
	 * @return
	 */
	public static String setSubStrColor(String strSource, String[] subStr,
			String[] subColor) {
		if (subStr.length != subColor.length || subStr == null
				|| subColor == null)
			return strSource;
		else
			for (int i = 0; i < subStr.length; i++) {
				strSource.replace(subStr[i],
						setStrColor(subStr[i], subColor[i]));
			}
		return strSource;

	}

	/**
	 * 设置指定字符串是否粗细
	 * 
	 * @param strSource
	 *            :源字符串
	 * @param color
	 *            : #ccc or red 设置颜色字符串
	 * @return
	 */
	public static String setStrBold(String strSource) {
		StringBuilder sb = new StringBuilder();
		sb.append("<b>");
		sb.append(strSource);
		sb.append("</b>");
		return sb.toString();

	}
	public static String setStrFont(String strSource) {
		StringBuilder sb = new StringBuilder();
		sb.append("<font size='20'>");
		sb.append(strSource);
		sb.append("</font>");
		return sb.toString();

	}

	/**
	 * 改变子串的粗细
	 * @param strSource
	 *            :源字符串
	 * @param subStr
	 *            ：子字符串数组
	 *
	 * @return
	 */
	public static String setSubStrColor(String strSource, String[] subStr
			) {
		if ( subStr == null||strSource==null)
			return strSource;
		else
			for (int i = 0; i < subStr.length; i++) {
				strSource.replace(subStr[i],
						setStrBold(subStr[i]));
			}
		return strSource;

	}

	
}
