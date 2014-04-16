package com.wq;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.wq.model.localModel;
import com.wq.service.deyService;
import com.wq.utils.LoggerUtil;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

public class BaseApplication extends Application {
	public static BaseApplication self;
	private PendingIntent restartIntent;
	private ArrayList<Activity> activityList = new ArrayList<Activity>();
	// 百度地图
	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;

	public MyLocationListenner myListener = new MyLocationListenner();
	private localModel local = null;
	public Handler localHander = null;// 访问的回调函数
	public NotifyLister mNotifyer = null;
	public Vibrator mVibrator01;
	public static String TAG = "LocTestDemo";

	public static BaseApplication getInstance() {
		return self;
	}

	@Override
	public void onCreate() {
		mLocationClient = new LocationClient(this);
		/**
		 * ——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		mLocationClient.setAK("697f50541f8d4779124896681cb6584d");
		mLocationClient.registerLocationListener(myListener);
		mGeofenceClient = new GeofenceClient(this);
		super.onCreate();
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/TestSyncListView/");
		if (!f.exists()) {
			f.mkdir();
		}
		self = this;

		// 以下用来捕获程序崩溃异常
		// Intent intent = new Intent();
		// 参数1：包名，参数2：程序入口的activity

		// intent.setClassName("com.wq", "com.wq.WelcomeActivity");
		// restartIntent = PendingIntent.getActivity(getApplicationContext(), 0,
		// intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		// Thread.setDefaultUncaughtExceptionHandler(restartHandler); //
		// 程序崩溃时触发线程
	}

	public void addActivity(Activity activity) {
		if (activityList.contains(activity)) {
			return;
		}
		activityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		if (activityList.contains(activity)) {
			activityList.remove(activity);
		}
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}

		System.exit(0);
	}

	public void onRestart() {
		for (Activity activity : activityList) {
			activity.finish();
		}

	}

	// 系统奔溃后重启
	public UncaughtExceptionHandler restartHandler = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			Intent intent = new Intent();
			intent.setAction("com.dey.service");
			stopService(intent);

			AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000,

			restartIntent);

			// 1秒钟后重启应用
			exit(); // 自定义方法，关闭当前打开的所有avtivity
		}
	};

	/**
	 * 显示请求字符串
	 * 
	 * @param str
	 */

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			local = new localModel();

			local.setLatitude(location.getLatitude() + "");
			local.setLongitude(location.getLongitude() + "");
			local.setAddr(location.getAddrStr());
			local.setRadius(location.getRadius() + "");
			// local.setProvince(location.getProvince());
			Message msg = localHander.obtainMessage();
			msg.what = 1;
			msg.obj = local;
			msg.sendToTarget();

		}

		// 离线
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			local = new localModel();

			local.setLatitude(poiLocation.getLatitude() + "");
			local.setLongitude(poiLocation.getLongitude() + "");
			local.setAddr(poiLocation.getAddrStr());
			local.setRadius(poiLocation.getRadius() + "");
			// local.setProvince(location.getProvince());
			Message msg = localHander.obtainMessage();
			msg.what = 1;
			msg.obj = local;
			msg.sendToTarget();
			// logMsg(sb.toString());
		}
	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
			mVibrator01.vibrate(1000);
		}
	}
}
