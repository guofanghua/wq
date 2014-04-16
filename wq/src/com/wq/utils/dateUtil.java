package com.wq.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.endure.wq.R;

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
	 * 返回两个时间的相差
	 * 
	 * @throws ParseException
	 * */
	@SuppressWarnings("finally")
	public static String spanNow(Context context, String startDate, Date endDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String returnStr = "";
		try {
			long sTime = format.parse(
					startDate.replace("/", "-").replace("\\", "-")).getTime();
			// 当前时间，加上时区差
			long eTime = endDate.getTime();
			long spanTime = eTime - sTime;
			LoggerUtil.i(spanTime + ",," + startDate);
			// 1分钟以内
			if (spanTime <= 1000 * 60) {
				returnStr = context.getString(R.string.find_date_now);
			}
			// 1小时以内
			else if (spanTime > 1000 && spanTime < 60 * 60 * 1000) {
				returnStr = String.format(
						context.getString(R.string.find_date_pre_minute),
						String.valueOf(spanNowMinute(startDate, endDate)));
			}
			// 一天以内
			else if (spanTime >= 60 * 60 * 1000
					&& spanTime < 60 * 60 * 24 * 1000) {
				LoggerUtil.i(spanTime + ",," + startDate);
				returnStr = String.format(
						context.getString(R.string.find_date_pre_hour),
						String.valueOf(spanNowHour(startDate, endDate)));
			}
			// 一天已外
			else if (spanTime >= 60 * 60 * 24 * 1000) {
				long day = spanNowDay(startDate, endDate);
				// 昨天
				if (day <= 1) {
					returnStr = context.getString(R.string.find_date_yesterday);
				}
				// 前天
				else if (day == 2) {
					returnStr = context
							.getString(R.string.find_date_q_yesterday);
				}
				// 多少天前
				else {
					returnStr = String.format(
							context.getString(R.string.find_date_pre_day),
							String.valueOf(day));
				}
			}
		} catch (Exception e) {
			LoggerUtil.i(startDate);
			returnStr = startDate;
			// TODO: handle exception
		} finally {
			return returnStr;
		}
	}

	/**
	 * 比较两个时间相差天数
	 * 
	 * @throws ParseException
	 * */
	public static long spanNowDay(String startDate, Date endDate) {
		long day = 0;
		try {
			LoggerUtil.i(startDate.split("\\:").length + "");
			if (startDate.split("\\:").length <= 1)
				startDate = startDate + " 00:00:00";
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date = format.parse(startDate.replace("/", "-")
					.replace("\\", "-"));
			day = (endDate.getTime() - date.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day;

	}

	/**
	 * 比较两个时间相差小时数
	 * 
	 * @throws ParseException
	 * */
	public static long spanNowHour(String startDate, Date endDate)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long minute = 0;

		java.util.Date date = format.parse(startDate.replace("/", "-").replace(
				"\\", "-"));
		minute = (endDate.getTime() - date.getTime()) / (60 * 1000);

		return minute;
	}

	/** 将时间格式化为0000-00-00 00:00:00 */
	public static String formatDate(Date date) {
		// 24小时制
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	/** 将时间格式化为0000-00-00 */
	public static String formatDate1(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	/** 将时间格式化为0000-00-00 */
	@SuppressWarnings("deprecation")
	public static String formatDateAdd(Date date, int day) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// Calendar c = Calendar.getInstance();
		// c.add(Calendar.DAY_OF_MONTH, day);
		date.setDate(date.getDate() + day);
		return format.format(date);
	}

	public static String SubDate(String dateStr) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			d = formattmp.parse(date);
			res = format.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	// 将0000-00-00 00:00:00自定义 时间格式
	public static String formatDate(String date, String formatStr) {
		String res = "";
		Date d = new Date();
		SimpleDateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		try {
			d = formattmp.parse(date);
			res = format.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	// 获取星期急
	public static String getWeekDay(Calendar c) {
		if (c == null) {
			return "";
		}

		if (Calendar.MONDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "星期一";
		}
		if (Calendar.TUESDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "星期二";
		}
		if (Calendar.WEDNESDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "星期三";
		}
		if (Calendar.THURSDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "星期四";
		}
		if (Calendar.FRIDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "星期五";
		}
		if (Calendar.SATURDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "星期六";
		}
		if (Calendar.SUNDAY == c.get(Calendar.DAY_OF_WEEK)) {
			return "星期日";
		}

		return "星期一";
	}
}
