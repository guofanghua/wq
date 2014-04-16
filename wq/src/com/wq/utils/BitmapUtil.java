package com.wq.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.widget.Toast;

public class BitmapUtil {
	private static int screenWidth = 0;// 屏幕宽度
	private static int screenHeight = 0;// 屏幕高度
	public static int comMinSize = 200 * 1024;

	/**
	 * 读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = 8;// 设置为原来的5分之一大小
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 读取服务器图片到相册
	 * */
	public static boolean savePicCamer(Context mContext, Bitmap bmp) {
		return savePhotoToSDCard(mContext, bmp);
	}

	/**
	 * Save image to the SD card 本地相册：Environment.getExternalStorageDirectory()
	 * + "/DCIM/Camera"
	 */
	@SuppressWarnings("finally")
	public static boolean savePhotoToSDCard(Context context, Bitmap photoBitmap) {

		String filePath = Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera";
		String picName = System.currentTimeMillis() + ".png";
		if (SDCardUtils.ExistSDCard()) {
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File photoFile = new File(filePath, picName);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
						filePath = photoFile.toString();
						
					}

				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				photoFile.delete();
				return false;

			} catch (IOException e) {
				e.printStackTrace();
				photoFile.delete();
				return false;

			} finally {
				try {
					fileOutputStream.close();
					Toast.makeText(context, "保存成功:" + filePath + "/" + picName,
							1000).show();

				} catch (IOException e) {
					e.printStackTrace();
					return false;

				}
				LoggerUtil.i(filePath);
				// context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				// Uri.parse("file://" + filePath)));
				flashPhotos(filePath, context);

				return true;
			}
		} else
			return false;

	}

	/**
	 * 扫描、刷新相册
	 */
	public static void flashPhotos(String filePath, Context context) {
		Uri localUri = Uri.fromFile(new File(filePath));

		Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				localUri);

		context.sendBroadcast(localIntent);
	}

	/***
	 * 根据资源文件获取Bitmap
	 * 
	 * @param context
	 * @param drawableId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int drawableId,
			int screenWidth, int screenHight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inInputShareable = true;
		options.inPurgeable = true;
		InputStream stream = context.getResources().openRawResource(drawableId);
		Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
		return getBitmap(context, bitmap);
	}

	/***
	 * 等比例压缩图片
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap getBitmap(Context context, Bitmap bitmap) {
		screenWidth = DensityUtil.intScreenWidth(context);
		screenHeight = DensityUtil.intScreenHeight(context);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		// 判断

		Log.e("jj", "图片宽度" + w + ",screenWidth=" + screenWidth);
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHeight / h;

		// scale = scale < scale2 ? scale : scale2;

		// 保证图片不变形.
		matrix.postScale(scale, scale2);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	// 等比制作缩略提的方法
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			final float totalPixels = width * height;

			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/** 获取图片缩略图 */
	// 根据路径来创建图片
	public static Bitmap createBitmap(Context context, int id) {
		try {
			screenWidth = DensityUtil.intScreenWidth(context);
			screenHeight = DensityUtil.intScreenHeight(context);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeResource(context.getResources(), id, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			/*
			 * if (srcWidth < screenWidth || srcHeight < screenHeight) { ratio =
			 * 0.0; destWidth = srcWidth; destHeight = srcHeight; } else
			 */
			// 按照屏幕高度来等比缩放

			if (srcWidth > srcHeight) {
				if (srcWidth > (screenWidth * 2 / 3)) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
					ratio = (double) srcWidth * 3 / 2 * screenWidth;
					destWidth = screenWidth / 3;
					destHeight = (int) (srcHeight / ratio);
				}
			} else {
				if (srcHeight > (screenHeight * 2 / 3)) {
					ratio = (double) srcHeight * 3 / 2 * screenHeight;
					destHeight = (screenHeight * 2 / 3);
					destWidth = (int) (srcWidth / ratio);
				}
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;

			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			// return BitmapFactory.decodeStream(new FileInputStream(new
			// File(path)),null,newOpts);
			return BitmapFactory.decodeResource(context.getResources(), id,
					newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static Bitmap creatBitmap(Context context, String path) {
		try {
			screenWidth = DensityUtil.intScreenWidth(context);
			screenHeight = DensityUtil.intScreenHeight(context);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;

			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例

			/*
			 * if (srcWidth < screenWidth || srcHeight < screenHeight) { ratio =
			 * 0.0; destWidth = srcWidth; destHeight = srcHeight; } else
			 */
			// 按照屏幕高度来等比缩放

			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值

			newOpts.inSampleSize = 4;

			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			// return BitmapFactory.decodeStream(new FileInputStream(new
			// File(path)),null,newOpts);
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 根据已知的bitmap制作缩略图
	 * */
	public static Bitmap createComBitmap(Context context, Bitmap b) {
		try {
			screenWidth = DensityUtil.intScreenWidth(context);
			screenHeight = DensityUtil.intScreenHeight(context);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			byte[] bitByte = Bitmap2Bytes(b);
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeByteArray(bitByte, 0, bitByte.length, opts);// decodeFile(path,
																			// opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			/*
			 * if (srcWidth < screenWidth || srcHeight < screenHeight) { ratio =
			 * 0.0; destWidth = srcWidth; destHeight = srcHeight; } else
			 */
			// 按照屏幕高度来等比缩放

			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = 2;

			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			// return BitmapFactory.decodeStream(new FileInputStream(new
			// File(path)),null,newOpts);
			return BitmapFactory.decodeByteArray(bitByte, 0, bitByte.length,
					newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/***
	 * 保存图片至SD卡
	 * 
	 * @param bm
	 * @param url
	 * @param quantity
	 */
	private static int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
	private static int MB = 1024 * 1024;
	public final static String DIR = "/sdcard/hypers";

	public static void saveBmpToSd(Bitmap bm, String url, int quantity) {
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			return;
		}
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
			return;
		String filename = url;
		// 目录不存在就创建
		File dirPath = new File(DIR);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}

		File file = new File(DIR + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, quantity, outStream);
			outStream.flush();
			outStream.close();

		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/***
	 * 获取SD卡图片
	 * 
	 * @param url
	 * @param quantity
	 * @return
	 */
	public static Bitmap GetBitmap(String url, int quantity) {
		InputStream inputStream = null;
		String filename = "";
		Bitmap map = null;
		URL url_Image = null;
		String LOCALURL = "";
		if (url == null)
			return null;
		try {
			filename = url;
		} catch (Exception err) {
		}

		LOCALURL = URLEncoder.encode(filename);
		if (Exist(DIR + "/" + LOCALURL)) {
			map = BitmapFactory.decodeFile(DIR + "/" + LOCALURL);
		} else {
			try {
				url_Image = new URL(url);
				inputStream = url_Image.openStream();
				map = BitmapFactory.decodeStream(inputStream);
				// url = URLEncoder.encode(url, "UTF-8");
				if (map != null) {
					saveBmpToSd(map, LOCALURL, quantity);
				}
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return map;
	}

	/***
	 * 判断图片是存在
	 * 
	 * @param url
	 * @return
	 */
	public static boolean Exist(String url) {
		File file = new File(DIR + url);
		return file.exists();
	}

	/** * 计算sdcard上的剩余空间 * @return */
	private static int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;

		return (int) sdFreeMB;
	}

	/** 设置成圆角 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		try {
			if (bitmap == null) {
				return null;
			}
			pixels = bitmap.getWidth() / 10;
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final int color = 0xffffffff;
			final Paint paint = new Paint();
			Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			RectF rectF = new RectF(rect);
			float roundPx = pixels;
			paint.setAntiAlias(true);
			// canvas.drawARGB(255, 255, 255, 255);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return bitmap;
		}
	}

	// 图片旋转
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2,
					(float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle(); // Android开发网再次提示Bitmap操作完应该显示的释放
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
			}
		}
		return b;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * bitmap 与byte[]之间的转换
	 * */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
			int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;
		// scale = scale < scale2 ? scale : scale2;
		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

		return bitmap2;
	}

	// 调用系统功能裁剪图片方法
	public static void cropImageUri(Context context, Uri uri, int aspectX,
			int aspectY, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	// 加载返回回来的图片
	public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}