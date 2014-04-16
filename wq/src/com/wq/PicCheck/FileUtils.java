package com.wq.PicCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.wq.utils.LoggerUtil;
import com.wq.utils.SDCardUtils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileUtils {

	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/.wqCrop/";

	public static String saveBitmap(Bitmap bm, String picName) {
		SDCardUtils.createDIR(SDPATH);
		String pathString = SDPATH + picName + ".JPEG";
		try {

			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			// LoggerUtil.i(SDPATH + picName + ".JPEG");
			File f = new File(pathString);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
			out.flush();
			out.close();
			if (bm != null && !bm.isRecycled())
				bm.recycle();

		} catch (Exception e) {
			pathString = "";
			e.printStackTrace();
		} finally {
			return pathString;
		}

	}

	public static String saveBitmapPath(Bitmap bm, String filePath) {
		try {
			SDCardUtils.createDIR(SDPATH);
			File f = new File(filePath);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
			out.flush();
			out.close();
			if (bm != null && !bm.isRecycled()) {
				bm.recycle();
				System.gc();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return filePath;
		}

	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}

	public static void delFile(String fileName) {
		File file = new File(SDPATH + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

}
