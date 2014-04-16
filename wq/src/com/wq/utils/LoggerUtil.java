package com.wq.utils;

import android.util.Log;

/**
 * 日志工具类
 * 
 * @author Administrator
 * 
 */
public class LoggerUtil {
	private String tag = "wq";
	private static boolean D = true;
	private static boolean I = true;
	private static LoggerUtil instance = new LoggerUtil();

	private LoggerUtil() {
	}

	public static LoggerUtil getLoggerUtil() {
		return instance;
	}

	/**
	 * 获取 函数名称
	 * */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (null == sts) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod())
				continue;
			if (st.getClassName().equals(Thread.class.getName()))
				continue;
			if (st.getClassName().equals(this.getClass().getName()))
				continue;
			return "[" /*
						 * + Thread.currentThread().getName() + "(" +
						 * Thread.currentThread().getId() + "):" +
						 */+ st.getFileName() + ":" + st.getLineNumber() + "]";
		}
		return null;

	}

	private static String createMessage(String msg) {
		String functionName = instance.getFunctionName();
		String message = (null == functionName ? msg
				: (functionName + "-" + msg));
		return message;
	}

	
	/**
	 * 打印出i信息
	 * 
	 * @param msg
	 */
	public static void i(String msg) {
		if (I) {
			String message = createMessage(msg);
			Log.i(instance.tag, message);
		}
	}

	public static void d(String msg) {
		if (D) {
			String message = createMessage(msg);
			Log.d(instance.tag, message);
		}
	}

}
