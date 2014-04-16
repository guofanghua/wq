package com.wq.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import org.w3c.dom.Text;

import android.text.TextUtils;
import android.util.Base64;



public class ClassCastUtil {
	/**
	 * 将对象转换为字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String classToString(Object item) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(item);
			return new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 将字符串转换为对象
	 * 
	 * @param str
	 * @return
	 */
	public static Object stringToClass(String str) {
		if (TextUtils.isEmpty(str))
			return null;
		Object object = null;
		byte[] base64 = Base64.decode(str.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				object = bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return object;
		}
	}

}
