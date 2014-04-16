package com.wq.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.endure.framework.FinalDb;

import com.endure.wq.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wq.BaseApplication;
import com.wq.UI.TypeListPopDialog;
import com.wq.me.settingActivity;
import com.wq.model.DecInfo;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.leaveMessage;
import com.wq.model.mmAlertModel;
import com.wq.model.notice;
import com.wq.model.partComModel;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.service.deyService;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;

public class CommonUtil {
	/**
	 * startActivityForResult Flag
	 * */

	public static final int PRO_CATE_RESULT_FLAG = 100;// proCateListActivity
	public static final int PRO_ATTR_ADD_RESULT_FLAG = 101;// proAttrAddActivity
	public static final int PRO_ADD_RESULT_FLAG = 102;// productAddActivity 产品添加
	public static final int PRO_ATTR_EDIT_RESULT_FLAG = 103;// proAttrEditActivity
	public static final int PRO_EDIT_RESULT_FLAG = 104;// productAddActivity
														// 产品编辑
	public static final int PRO_DEL_RESULT_FLAG = 105;// productAddActivity 产品删除
	public static final int PRO_CATE_ADD_RESULT_FLAG = 106;// proCateAddActivity
															// 添加分类
	public static final int NOTICE_RESULT_FLAG = 107;// noticeAddActivity 添加公告
	public static final int PHOTO_RESULT_FLAG = 108;// 添加相册
	public static final int PRODUCT_RESULT_FLAG = 108;// 添加相册
	public static final int CIRCLE_ADD_RESULT_FLAG = 109;// 添加企业圈信息

	private final static int QR_WIDTH = 200, QR_HEIGHT = 200;
	private static final int TIME_OUT_CODE = 1;// 访问超
	private static final int UNHOST_CODE = 2;// 网络连接错误
	private static final int OTHER_CODE = 3;// 其他错误
	public static final String key1 = "!@weiqiwang@";
	public static final String key2 = "!@applogkey@!";

	public static ArrayList<mmAlertModel> getSplitmmAlertModel(String[] arr) {
		ArrayList<mmAlertModel> list = new ArrayList<mmAlertModel>();

		for (int i = 0; i < arr.length; i++) {
			String[] arr1 = arr[i].split("\\|");
			if (arr1.length >= 2) {
				mmAlertModel item = new mmAlertModel();
				item.setIndex(arr1[0]);
				item.setText(arr1[1]);
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * 将字符串MD5加密（16位）
	 * 
	 * @param plainText
	 * @return
	 */
	public static String stringTo16Md5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString().substring(8, 24);// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将字符串MD5加密（32位）
	 * 
	 * @param plainText
	 * @return
	 */
	public static String stringTo32Md5(String plainText, String key) {
		try {

			plainText += key;
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将字符串MD5加密（32位）
	 * 
	 * @param plainText
	 * @return
	 */
	// public static String stringTo32Md5(String plainText) {
	// try {
	//
	// MessageDigest md = MessageDigest.getInstance("MD5");
	// md.update(plainText.getBytes());
	// byte b[] = md.digest();
	// int i;
	// StringBuffer buf = new StringBuffer("");
	// for (int offset = 0; offset < b.length; offset++) {
	// i = b[offset];
	// if (i < 0)
	// i += 256;
	// if (i < 16)
	// buf.append("0");
	// buf.append(Integer.toHexString(i));
	// }
	// return buf.toString();// 16位的加密
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	/**
	 * 获取array资源里面的数组，并转换成list
	 * 
	 * @param context
	 * @param arrayResId
	 * @return
	 */
	public static ArrayList<String> splitArray(Context context, int arrayResId) {
		ArrayList<String> strArr = new ArrayList<String>();
		String[] tmp = context.getResources().getStringArray(arrayResId);
		for (String s : tmp) {
			strArr.add(s);
		}
		return strArr;
	}

	/**
	 * 获取设备信息
	 * 
	 * @return
	 */
	public static String deviceInfo(Activity activity) {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuffer sb = new StringBuffer();
		sb.append(tm.getDeviceId());// imei
		sb.append("|");
		sb.append(tm.getSimSerialNumber());// sim
		sb.append("|");
		sb.append(tm.getLine1Number());
		sb.append("|");
		sb.append(android.os.Build.MODEL);// 手机型号
		sb.append("|");

		WifiManager wifiMgr = (WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info)
			sb.append(info.getMacAddress());// mac地址
		return sb.toString();

	}

	/**
	 * 输出打印信息
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToast(Context context, String msg) {
		if (setting.toastSwitch)
			Toast.makeText(context, msg, 1000).show();
	}

	/**
	 * 输出打印消息
	 * 
	 * @param context
	 * @param msgId
	 */
	public static void showToast(Context context, int msgId) {
		if (setting.toastSwitch)
			Toast.makeText(context, context.getResources().getString(msgId),
					1000).show();
	}

	/***
	 * 输出网络访问失败信息
	 * 
	 * */
	public static void showToastHttp(Context context, int errcode) {
		if (errcode == TIME_OUT_CODE) {
			showToast(context, R.string.sys_network_time_out);
		} else if (errcode == UNHOST_CODE) {
			showToast(context, R.string.sys_network_no_available);
		} else
			showToast(context, R.string.string_http_err_failure);
	}

	/**
	 * 金额格式化
	 * 
	 * @param s
	 *            金额
	 * @param len
	 *            小数位数
	 * @return 格式后的金额
	 */
	public static String insertComma(String s, int len) {
		if (s == null || s.length() < 1) {
			return "";
		}

		NumberFormat formater = null;
		double num = Double.parseDouble(s);
		if (len == 0) {
			formater = new DecimalFormat("###,###");
		} else {
			StringBuffer buff = new StringBuffer();
			buff.append("###,###.");
			for (int i = 0; i < len; i++) {
				buff.append("#");
			}
			formater = new DecimalFormat(buff.toString());
		}
		return formater.format(num);
	}

	/**
	 * Edittext设置为可编辑状态
	 * 
	 * @param edit
	 * @param IsEidt
	 */
	public static void EditStatus(EditText edit, boolean isEdit) {
		if (isEdit) {
			edit.setEnabled(true);
			CharSequence text = edit.getText();
			// Debug.asserts(text instanceof Spannable);
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}
			// edit.setBackgroundResource(R.drawable.edittext_bg);
			// edit.
		} else {
			edit.setEnabled(false);
			edit.setTextColor(Color.BLACK);
			edit.setBackgroundDrawable(null);
		}
	}

	/**
	 * 设置图片是否可编辑的背景
	 * 
	 * @param layout
	 * @param child
	 * @param isEdit
	 */
	/*
	 * public static void InitImgEditStatus(LinearLayout layout, View child,
	 * boolean isEdit) { // 编辑模式的params LinearLayout.LayoutParams params = new
	 * LinearLayout.LayoutParams( LayoutParams.FILL_PARENT,
	 * LayoutParams.FILL_PARENT); if (isEdit) { params.setMargins(5, 5, 5, 5);
	 * child.setLayoutParams(params);
	 * layout.setBackgroundResource(R.drawable.edit_status_bg); } else {
	 * params.setMargins(0, 0, 0, 0); child.setLayoutParams(params);
	 * layout.setBackgroundColor(Color.WHITE); } }
	 */

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		String str = new String(c);
		return stringFilter(str);
	}

	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static String UpdateStr(TextView txt_str) {
		// if (txt_str.getTag().equals("1")) {
		return txt_str.getText().toString();
		// } else {
		// return "";
		// }
	}

	/** 生成二维码 */
	public static Bitmap createTwodimensionImage(String content) {

		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();

			if (content == null || "".equals(content) || content.length() < 1) {
				return null;
			}

			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(content, BarcodeFormat.QR_CODE,
					QR_WIDTH, QR_HEIGHT);

			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}

				}
			}

			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);

			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 生意圈
	public static ArrayList<leaveMessage> sortMsgList(
			ArrayList<leaveMessage> list) {
		ArrayList<leaveMessage> tmpList = new ArrayList<leaveMessage>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLevel().equals("1")) {
				tmpList.add(list.get(i));
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).getLevel().equals("2")
							&& list.get(j).getSuperComId()
									.equals(list.get(i).getComId())) {
						tmpList.add(list.get(j));
						for (int k = 0; k < list.size(); k++) {
							if (list.get(k).getLevel().equals("3")
									&& list.get(k).getSuperComId()
											.equals(list.get(j).getComId())) {
								tmpList.add(list.get(k));
							}

						}
					}

				}
			}
		}
		return tmpList;
	}

	/**
	 * 将对象编编码成字符串
	 * 
	 * @param item
	 * @return
	 */
	public static String encodeObject(Object item) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		String mobilesString = "";
		// 然后将得到的字符数据装载到ObjectOutputStream。
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它。
			objectOutputStream.writeObject(item);
			// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
			mobilesString = new String(Base64.encode(
					byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 关闭objectOutputStream
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mobilesString;
	}

	/**
	 * 将字符串编码为对象
	 * 
	 * @param src
	 * @return
	 */
	public static Object decodeObject(String src)

	{
		byte[] mobileBytes = Base64.decode(src.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				mobileBytes);
		ObjectInputStream objectInputStream = null;
		Object item = new Object();
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			item = (Object) objectInputStream.readObject();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return item;
	}

	public static boolean Quit(Context context) {
		try {
			// 写操作
			User.ClearUser();
			// 删除本地数据
			clearDbData(context);
			// clearCacheSize();
			sharedPreferenceUtil.clearCompany(context);
			// 关闭服务
			Intent intent_deyService = new Intent(context, deyService.class);
			context.stopService(intent_deyService);

			BaseApplication.getInstance().onRestart();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void clearDbData(Context context) {
		FinalDb db = FinalDb.create(context);

		db.deleteByWhere(product.class, "");
		db.deleteByWhere(notice.class, "");
		db.deleteByWhere(ablum.class, "");
		db.deleteByWhere(partComModel.class, "");
		db.deleteByWhere(product.class, "");
		db.deleteByWhere(DecInfo.class, "");
		db.deleteByWhere(photoModel.class, "");
	}

	// 检测用户信息的提示框
	public static void webshowDialog(final Context context, int flag,
			int title, int formatId, int contentId, final Class<?> jumpClass) {
		webshowDialog(context, flag, title, formatId, contentId, jumpClass,
				false);
	}

	public static void webshowDialog(final Context context, int flag,
			int title, int formatId, int contentId, final Class<?> jumpClass,
			final boolean isFinish) {
		DialogUtils.showDialog1(
				context,
				context.getString(title),
				String.format(context.getString(formatId),
						context.getString(contentId)),
				context.getString(R.string.string_wszl),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(context, jumpClass);
						// 更新
						context.startActivity(intent);
						if (isFinish)
							((Activity) context).finish();
					}
				}).show();
	}

	// 根据认证的标题返回是否已经
	public static String getcerStatus(String title) {

		if (title.equals("微企空间认证平台服务协议")) {
			return "0";
		}
		if (title.equals("微企诚信认证档案信息")) {
			return "1";
		}
		if (title.equals("微企诚信认证审核中")) {
			return "2";
		}
		if (title.equals("微企诚信认证审核失败")) {
			return "3";
		}
		return User.cerStatus;

	}

	// 获取当前版本
	public static int getAppVersion(Context context) {
		int version = 0;
		PackageManager pkgManager = context.getPackageManager();
		try {
			PackageInfo info = pkgManager.getPackageInfo(
					context.getPackageName(), 0);
			version = info.versionCode;
		} catch (NameNotFoundException e) {

		}
		return version;
	}

	// popupDialog
	public static void loadTypeListDialog(String[] comm, int[] imgArr,
			TypeListPopDialog typeListDialog, Handler handler) {
		if (comm == null)
			return;
		// 访问空间量...

		typeListDialog.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		typeListDialog.loadContentView(comm, imgArr, handler);
		// 为弹出框设置自定义的布局文件
		typeListDialog.setCardContentView();
		typeListDialog.setFocusable(true);
		typeListDialog.setBackgroundDrawable(new BitmapDrawable());// 一定要设置背景才起点击外面关闭才起作用
		typeListDialog.setOutsideTouchable(true);

	}
}
