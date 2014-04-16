package com.wq.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;

public class DensityUtil {
	private static final String Tag = DensityUtil.class.getSimpleName();
	// 当前屏幕的desityDpi
	private static float dmDensityDpi = 0.0f;
	private static DisplayMetrics dm = null;

	/**
	 * 根据构造函数获得当前手机的屏幕系数
	 * 
	 * @param context
	 */
	public DensityUtil(Context context) {
		getDisplayInstance(context);
		// 设置DensityDpi
		setDmDensityDpi(dm.densityDpi);
		// 密度因子

	}

	/**
	 * 获取当前的density因子
	 * 
	 * @return
	 */
	public float getDmDensityDpi() {
		return dmDensityDpi;

	}

	public static DisplayMetrics getDisplayInstance(Context context) {
		if (dm == null) {
			dm = new DisplayMetrics();
			dm = context.getApplicationContext().getResources()
					.getDisplayMetrics();
		}
		return dm;

	}

	/** 获取当前屏幕宽度 */

	public static int intScreenWidth(Context context) {
		getDisplayInstance(context);
		return dm.widthPixels;

	}

	/** 获取当前屏幕高度 */

	public static int intScreenHeight(Context context) {
		getDisplayInstance(context);
		return dm.heightPixels;

	}

	/**
	 * 当前屏幕的density因子
	 * 
	 * @param dmDensityDpi
	 */
	public void setDmDensityDpi(float dmDensityDpi) {
		DensityUtil.dmDensityDpi = dmDensityDpi;
	}

	/**
	 * 
	 * 将dip转换成px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);

	}

	/**
	 * 将px转换成dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取状态栏高度
	 * */
	public static int statusHeight(Context context) {
		Rect frame = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);

		int statusBarHeight = frame.top;

		int contenttop = ((Activity) context).getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// statusBarHeight是上面所求的状态栏的高度
		return contenttop - statusBarHeight;

	}

}
