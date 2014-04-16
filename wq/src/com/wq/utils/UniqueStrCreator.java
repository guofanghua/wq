package com.wq.utils;

import java.util.Calendar;

/**
 * 本地设备唯一字符串创建工具类
 * 
 * @author Aiven
 * @date 2013-8-1
 * @email aiven163@sina.com
 */
public class UniqueStrCreator {

	private static final char PREFIX = 'U';

	/**
	 * 创建唯一标识字符串
	 * 
	 * @param prefix_str
	 *            前缀字符串
	 * @return
	 */
	public static String createUniqueString(String prefix_str) {
		if (prefix_str != null && prefix_str.trim().length() > 0) {
			StringBuffer Str = new StringBuffer(prefix_str);
			Str.append(mixString());
			return Str.toString();
		} else {
			return createUniqueString();
		}

	}

	/**
	 * 创建唯一标识字符串
	 * 
	 * @param prefix
	 *            字符串前缀字符
	 * @return
	 */
	public static String createUniqueString(char prefix) {
		if (prefix == ' ')
			return createUniqueString();
		StringBuffer Str = new StringBuffer(String.valueOf(prefix));
		Str.append(mixString());
		return Str.toString().toUpperCase();
	}

	/**
	 * 创建唯一标识字符串,默认前缀为U
	 * 
	 * @return
	 */
	public static String createUniqueString() {
		StringBuffer Str = new StringBuffer(String.valueOf(PREFIX));
		Str.append(mixString());
		return Str.toString().toUpperCase();
	}

	synchronized private static String mixString() {
		StringBuffer buffer = new StringBuffer(createTimeStr());
		int len = 31 - buffer.length();
		int len2 = buffer.length();
		for (int i = 0; i < len; i++) {
			buffer.insert(getRandomPos(len2), getRandomChar());
			len2 = buffer.length();
		}
		return buffer.toString();
	}

	private static int getRandomPos(int len) {
		if (len == 0)
			len = 1;
		int pos = 0;
		pos = (int) (Math.random() * 1000);
		return pos % len;
	}

	synchronized public static String createTimeStr() {
		StringBuffer buffer = new StringBuffer("");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		int h = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int ms = calendar.get(Calendar.MILLISECOND);
		buffer.append(year);
		if (month < 10) {
			buffer.append(0);
		}
		buffer.append(month);
		if (day < 10) {
			buffer.append(0);
		}
		buffer.append(day);
		if (h < 10)
			buffer.append(0);
		buffer.append(h);
		if (min < 10)
			buffer.append(0);
		buffer.append(min);
		if (second < 10)
			buffer.append(0);
		buffer.append(second);
		if (ms < 10) {
			buffer.append("00");
		} else if (ms >= 10 && ms < 100) {
			buffer.append(0);
		}
		buffer.append(ms);
		return buffer.toString();
	}

	public static char getRandomChar() {
		char c = 0;
		int b = (int) (Math.random() * 1000) % 26;
		c = (char) (b + 65);
		return c;
	}

}
