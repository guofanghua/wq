package com.wq.utils;

import com.endure.wq.R;
import com.wq.UI.CustomAlertDialog;
import com.wq.UI.CustomAlertDialog1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DialogUtils {
	public static Dialog showDialog(Context context, String message) {
		String positiveText = "确定";
		return showDialog(context, null, message, positiveText, null, null,
				null);
	}

	public static Dialog showTitleDialog(Context context, String title,
			String message) {
		String positiveText = "确定";
		return showDialog(context, title, message, positiveText, null, null,
				null);
	}

	public static Dialog showDialog(Context context, String message,
			DialogInterface.OnClickListener positiveClickListener) {
		String positiveText = "确定";
		return showDialog(context, null, message, positiveText,
				positiveClickListener, null, null);
	}

	public static Dialog showDialog(Context context, String message,
			DialogInterface.OnClickListener positiveClickListener,
			DialogInterface.OnClickListener negativeClickListener) {
		String positiveText = "确定";
		String negativeText = "取消";
		return showDialog(context, null, message, positiveText,
				positiveClickListener, negativeText, negativeClickListener);
	}

	public static Dialog showDialog(Context context, String message,
			String positiveText,
			DialogInterface.OnClickListener positiveClickListener) {
		return showDialog(context, null, message, positiveText,
				positiveClickListener, null, null);
	}

	public static Dialog showDialog(Context context, String message,
			String positiveText,
			DialogInterface.OnClickListener positiveClickListener,
			String negativeText) {
		return showDialog(context, null, message, positiveText,
				positiveClickListener, negativeText, null);
	}

	public static Dialog showDialog(Context context, String message,
			String positiveText,
			DialogInterface.OnClickListener positiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener negativeClcikListener) {
		return showDialog(context, null, message, positiveText,
				positiveClickListener, negativeText, negativeClcikListener);
	}

	public static Dialog showDialog(Context context, String title,
			String message, String positiveText,
			DialogInterface.OnClickListener positiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener negativeClcikListener) {

		CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
				context);
		builder.setTitle(title).setMessage(message)
				.setPositiveButton(positiveText, positiveClickListener)
				.setNegativeButton(negativeText, negativeClcikListener);
		Dialog dialog = builder.create();
		return dialog;
	}

	// 注册后进来的
	public static Dialog showDialog1(Context context, String title,
			String message,String btnText,
			DialogInterface.OnClickListener positiveClickListener) {
		CustomAlertDialog1.Builder builder = new CustomAlertDialog1.Builder(
				context);
		builder.setTitle(title)
				.setMessage(message)
				.setPositiveButton(btnText,
						positiveClickListener);
		Dialog dialog = builder.create();
		return dialog;
	}

	// 编辑后确认提示框
	public static Dialog showConfirmDialog(Context context) {
		return showConfirmDialog(context, "");
	}

	// 编辑后确认提示框
	public static Dialog showConfirmDialog(Context context, String message) {
		LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(context);
		Dialog dialog = new Dialog(context, R.style.myDialogWhiteBg);
		View layout = inflater.inflate(R.layout.dialog_edit_confrim_view, null);
		TextView txt = (TextView) layout.findViewById(R.id.txt_msg);
		if (TextUtils.isEmpty(message)) {
			txt.setVisibility(View.GONE);
		} else {
			txt.setVisibility(View.VISIBLE);
			txt.setText(message);
		}
		((LinearLayout) layout.findViewById(R.id.dialog_bg)).getBackground()
				.setAlpha(150);
		dialog.setContentView(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		return dialog;
	}
	// public static Dialog showDialogEwm(Context context, String title,
	// String message, String positiveText,
	// DialogInterface.OnClickListener positiveClickListener,
	// String otherText,
	// DialogInterface.OnClickListener otherClickListener,
	// String negativeText,
	// DialogInterface.OnClickListener negativeClcikListener) {
	//
	// CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
	// context);
	// builder.setTitle(title).setMessage(message)
	// .setPositiveButton(positiveText, positiveClickListener)
	// .setNegativeButton(negativeText, negativeClcikListener)
	// .setOtherButtonClickListener(otherText, otherClickListener);
	// Dialog dialog = builder.create1();
	// return dialog;
	// }
}
