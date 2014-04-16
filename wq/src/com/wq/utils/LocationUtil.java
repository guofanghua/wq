package com.wq.utils;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 定位帮助类
 * */
public class LocationUtil {
	/**
	 * 判断wifi是否连接
	 * */
	public static boolean checkWifiConnection(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isAvailable() || mobile.isAvailable())
			return true;
		else
			return false;
	}

	/**
	 * 获取wifi的ssid
	 * */
	public static String getWifiSSID(Context context) {
		if (checkWifiConnection(context)) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();// 获取连接中的wifi信息
			return wifiInfo.getSSID();
		} else
			return "";
	}
	/***
	 * 获取wifi的id
	 * */
	public static String getWifiBSSID(Context context) {
		if (checkWifiConnection(context)) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();// 获取连接中的wifi信息
			return wifiInfo.getBSSID();
		} else
			return "";
	}
	// 判断gps是否打开
	public static final boolean isOPen(final Context context) {

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		if (gps) {
			return true;

		}
		return false;

	}

}
