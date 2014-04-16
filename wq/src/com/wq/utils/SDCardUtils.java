package com.wq.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class SDCardUtils {

	String SDPATH = Environment.getExternalStorageDirectory() + "/";

	/**
	 * 判断文件是已经存在;
	 * 
	 * @param filepath
	 *            文件全路径名称
	 * @return
	 */
	public static boolean checkFileExists(String filepath) {

		File file = new File(filepath);
		return file.exists();
	}

	/**
	 * 在SD卡上创建目录；
	 * 
	 * @param dirpath
	 * @return
	 */
	public static File createDIR(String dirpath) {

		File dir = new File(dirpath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;

	}

	/**
	 * 在SD卡上创建文件；
	 * 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public static File createFile(String filepath) throws IOException {

		File file = new File(filepath);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;

	}

	public static String readFromSDCard(String dirpath, String filename) {

		try {

			String FilePath = dirpath + "//" + filename + ".txt";
			if (checkFileExists(FilePath)) {
				BufferedReader br = new BufferedReader(new FileReader(FilePath));

				String str = "";

				String r = br.readLine();
				while (r != null) {
					str += r;
					r = br.readLine();
				}
				return str;
			} else {
				return "File:" + FilePath + " is not exist";

			}

		} catch (IOException e) {

			e.printStackTrace();

		}
		return "is Error";

	}

	/**
	 * 
	 * @param dirpath
	 * @param filename
	 * @param input
	 */
	public static void writeStringToSDCard(String dirpath, String filename,
			String input) {

		try {

			// 创建目录；
			createDIR(dirpath);
			// 在创建 的目录上创建文件；
			File file = createFile(dirpath + "//" + filename + ".txt");
			PrintWriter pw = new PrintWriter(new FileWriter(file), true);
			;
			pw.println(input);

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public static void getFileDir(String filePath) {
		List<String> items = null;// 存放名称
		List<String> paths = null;// 存放路径
		String rootPath = "/";
		try {

			items = new ArrayList<String>();
			paths = new ArrayList<String>();
			File f = new File(filePath);
			File[] files = f.listFiles();// 列出所有文件
			// 如果不是根目录,则列出返回根目录和上一目录选项
			if (!filePath.equals(rootPath)) {
				items.add("返回根目录");
				paths.add(rootPath);
				items.add("返回上一层目录");
				paths.add(f.getParent());
			}
			// 将所有文件存入list中
			if (files != null) {
				int count = files.length;// 文件个数
				for (int i = 0; i < count; i++) {
					File file = files[i];
					items.add(file.getName());
					paths.add(file.getPath());
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("sdfsd");
	}

	/**
	 * 
	 * @param dirpath
	 * @param filename
	 * @param input
	 */
	public static void writeStreamToSDCard(String dirpath, String filename,
			InputStream input) {

		File file = null;

		OutputStream output = null;

		try {

			// 创建目录；

			createDIR(dirpath);

			// 在创建 的目录上创建文件；

			file = createFile(dirpath + filename);

			output = new FileOutputStream(file);

			byte[] bt = new byte[4 * 1024];

			while (input.read(bt) != -1) {

				output.write(bt);

			}

			// 刷新缓存，

			output.flush();

		} catch (IOException e) {

			e.printStackTrace();

		}

		finally {

			try {

				output.close();

			} catch (Exception e) {

				e.printStackTrace();

			}

		}
	}
	/**
	 * 判断sd卡是否存在
	 * 
	 * */
	public static  boolean ExistSDCard() {  
		  if (android.os.Environment.getExternalStorageState().equals(  
		    android.os.Environment.MEDIA_MOUNTED)) {  
		   return true;  
		  } else  
		   return false;  
		 }  
	
}


/*
 * 需要的权限 <!-- 在SDCard中创建与删除文件权限 --> <uses-permission
 * android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/> <!--
 * 往SDCard写入数据权限 --> <uses-permission
 * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 */