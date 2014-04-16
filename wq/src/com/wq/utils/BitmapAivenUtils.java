package com.wq.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapAivenUtils {

	/**
	 * 图片圆角
	 * 
	 * @param ctx
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
//	public static Bitmap toRoundCorner(Activity ctx, Bitmap bitmap, int pixels) {
//		if (bitmap == null) {
//			return null;
//		}
//		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//				bitmap.getHeight(), Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//		final int color = 0xffffffff;
//		final Paint paint = new Paint();
//		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		RectF rectF = new RectF(rect);
//		float roundPx = 0;
//		if (ctx != null) {
//			roundPx = pixels * DevicesUtils.getScreenDensity(ctx);
//		}
//		paint.setAntiAlias(true);
//		// canvas.drawARGB(255, 255, 255, 255);
//		paint.setColor(color);
//		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rect, paint);
//		return output;
//	}

	/**
	 * 传入的bmp的宽和高需要相等
	 * 
	 * @param ctx
	 * @param bmp
	 * @return
	 * @throws Exception
	 */
	public static Bitmap toRoundBitmap(Context ctx, Bitmap bmp)
			throws Exception {
		if (bmp == null)
			return null;
		if (bmp.getWidth() != bmp.getHeight())
			throw new Exception(" bitmap's width must equal it's height ");
		Bitmap output = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xffffffff;
		final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
		RectF rectF = new RectF(rect);
		float roundPx = bmp.getWidth() / 2.0f;
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bmp, rect, rect, paint);
		return output;
	}

	public static boolean saveBimtapToImg(Bitmap bmp, String path)
			throws Exception {
		boolean b = true;
		if (path == null || path.trim().length() < 1)
			throw new Exception("The path must cannot be empty");
		if (!path.trim().endsWith(".png")) {
			return false;
		}
		try {
			checkFile(path);
			FileOutputStream out = new FileOutputStream(path);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	private static void checkFile(String path) {
		File file = new File(path);
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(15 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	/**
	 * 以最省内存读取资源图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 
	 * @param context
	 * @param bmpPath
	 *            图片本地路径
	 * @return
	 */
	public static Bitmap readBitmap(Context context, String bmpPath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		try {
			InputStream inputStream = new FileInputStream(new File(bmpPath));
			return BitmapFactory.decodeStream(inputStream, null, opt);
		} catch (Exception e) {
			return null;
		}
	}

}
