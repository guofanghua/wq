package com.wq.UI;

import com.endure.wq.R;
import com.wq.utils.LoggerUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
	private TextView tv;
	private CustomProgressDialog customProgressDialog = null;
	private Context context;
	private String msg;

	public CustomProgressDialog(Context context) {
		this(context, R.style.loadingDialogStyle);

		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public CustomProgressDialog(Context context, int msgId) {
		super(context, msgId);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.loading);

		tv = (TextView) this.findViewById(R.id.custom_dialog_txt_message);
		setMessage(msg);
		RelativeLayout relativeLayout = (RelativeLayout) this
				.findViewById(R.id.relativeLayout);
		relativeLayout.getBackground().setAlpha(210);

	}

	public void create(String message) {

		customProgressDialog = new CustomProgressDialog(context,
				R.style.loadingDialogStyle);
		customProgressDialog.setCanceledOnTouchOutside(false);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		if (tv != null)
			tv.setText(message);

	}

	public void create(int msgId) {

		customProgressDialog = new CustomProgressDialog(context,
				R.style.loadingDialogStyle);
		
		customProgressDialog.setCanceledOnTouchOutside(false);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		msg = this.context.getResources().getString(msgId);
		setMessage(msg);

	}

	// 在试图将要获取焦点的时候触发
	public void onWindowFocusChanged(boolean hasFocus) {
		if (customProgressDialog == null) {
			return;
		}

		// 设置控件动画
		// ImageView imageView = (ImageView)
		// customProgressDialog.findViewById(R.id.loadingImageView);
		//
		// AnimationDrawable animationDrawable = (AnimationDrawable)
		// imageView.getBackground();
		//
		// animationDrawable.start();

	}

	/**
	 * 设置提示内容
	 * 
	 * @param strMessage
	 * @return
	 */
	public CustomProgressDialog setMessage(String strMessage) {

		if (tv != null)
			tv.setText(strMessage);
		return customProgressDialog;

	}
}
