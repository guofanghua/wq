package com.example.dbtest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class dateUtil {

	/**
	 * 
	 * 获取当天时间的第前几天时间
	 * 
	 * @param beforeDays
	 * @return
	 * @throws ParseException
	 */
	public static String getBeforeNowDate(int beforeDays) throws ParseException {
		return getBeforeDate(new Date(), beforeDays);

	}

	/**
	 * 根据输入的日期字符串 和 提前天数 ， 获得 指定日期提前几天的日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param lazyDays
	 *            倒推的天数
	 * @return 指定日期倒推指定天数后的日期对象
	 * @throws ParseException
	 */
	public static String getBeforeDate(Date date, int beforeDays)
			throws ParseException {

		DateFormat dateFormat = new SimpleDateFormat("MM-dd");
		Date inputDate = dateFormat.parse(dateFormat.format(date));

		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);

		int inputDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		cal.set(Calendar.DAY_OF_YEAR, inputDayOfYear - beforeDays);
		return dateFormat.format(cal.getTime());
	}



	/**
	 * 比较两个时间相差小时数
	 * 
	 * @throws ParseException
	 * */
	public static long spanNowHour(String startDate, Date endDate)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long hour = 0;

		java.util.Date date = format.parse(startDate.replace("/", "-").replace(
				"\\", "-"));
		hour = (endDate.getTime() - date.getTime()) / (60 * 60 * 1000);

		return hour;
	}

	/**
	 * 比较两个时间相差分钟数
	 * 
	 * @throws ParseException
	 * */
	public static long spanNowMinute(String startDate, Date endDate)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long minute = 0;

		java.util.Date date = format.parse(startDate.replace("/", "-").replace(
				"\\", "-"));
		minute = (endDate.getTime() - date.getTime()) / (60 * 1000);

		return minute;
	}

	/** 将时间格式化为0000-00-00 00:00:00 */
	public static String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(date);
	}
	/** 将时间格式化为0000-00-00 */
	public static String formatDate1(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
	
	public static String SubDate(String dateStr) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			dateStr = dateStr.replace("/", "-").replace("\\", "-");
			StringBuilder sb = new StringBuilder();
			Date d = format.parse(dateStr);
			sb.append((d.getMonth() + 1) >= 10 ? (d.getMonth() + 1) : "0"
					+ (d.getMonth() + 1));
			sb.append("-");
			sb.append(d.getDate() >= 10 ? d.getDate() : "0" + d.getDate());
			sb.append(" ");
			sb.append(d.getHours() >= 10 ? d.getHours() : "0" + d.getHours());
			sb.append(":");
			sb.append(d.getMinutes() >= 10 ? d.getMinutes() : "0"
					+ d.getMinutes());
			return sb.toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return dateStr;
		}
	}

	// 将0000-00-00 00:00:00自定义 时间格式
	public static String formatDate(String date, SimpleDateFormat format) {
		String res = "";
		Date d = new Date();
		SimpleDateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			d = formattmp.parse(date);
			res = format.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;

	}
}
