package com.wq.UI;

import java.util.zip.Inflater;

import org.w3c.dom.Text;

import com.endure.wq.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** 注册完后，未搜索到企业回到main界面后弹出的框 */
public class CustomAlertDialog1 extends Dialog {

	public CustomAlertDialog1(Context context) {
		super(context);

		// TODO Auto-generated constructor stub
	}

	public CustomAlertDialog1(Context context, int theme) {
		super(context, R.style.loadingDialogStyle);

		// TODO Auto-generated constructor stub
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private String otherButtonText;
		private View contentView;
		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener, otherButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;

			return this;
		}

		public Builder setOtherButtonClickListener(String otherButtonText,
				DialogInterface.OnClickListener otherButtonClickListener) {
			this.otherButtonText = otherButtonText;
			this.otherButtonClickListener = otherButtonClickListener;
			return this;
		}

		/**
		 * Set the negative button text and it"s listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		// 弹出框中有两个按钮
		public CustomAlertDialog1 create() {
			LayoutInflater inflater = (LayoutInflater) LayoutInflater
					.from(context);
			// .getSystemService(context.LAYOUT_INFLATER_SERVICE);
			final CustomAlertDialog1 dialog = new CustomAlertDialog1(context,
					R.style.myDialog);
			View layout = inflater.inflate(R.layout.alert_dialog1, null);
			TextView txt_title = (TextView) layout
					.findViewById(R.id.alert_dialog_txt_title);
			TextView txt_message = (TextView) layout
					.findViewById(R.id.alert_dialog_txt_content);
			ScrollView scroll = (ScrollView) layout.findViewById(R.id.scroll);
			dialog.setContentView(layout);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);

			if (TextUtils.isEmpty(title)) {
				txt_title.setVisibility(View.GONE);
			} else {
				txt_title.setText(title);
			}
			if (TextUtils.isEmpty(message)) {
				txt_message.setVisibility(View.GONE);
				scroll.setVisibility(View.GONE);
			} else {
				txt_message.setText(message);
			}
			if (contentView != null) {
				((LinearLayout) layout.findViewById(R.id.alert_layout))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.alert_layout))
						.addView(contentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			if (TextUtils.isEmpty(title)) {

			}
			Button btnButton = ((Button) layout.findViewById(R.id.btn_ok));
			btnButton.setText(positiveButtonText);
			btnButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					positiveButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_POSITIVE);
					// TODO Auto-generated method stub
				}
			});

			((ImageView) layout.findViewById(R.id.img_close))
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							// TODO Auto-generated method stub

						}
					});
			return dialog;
		}

	}

}
