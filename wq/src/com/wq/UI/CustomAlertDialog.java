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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CustomAlertDialog extends Dialog {

	public CustomAlertDialog(Context context) {
		super(context);

		// TODO Auto-generated constructor stub
	}

	public CustomAlertDialog(Context context, int theme) {
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
		public CustomAlertDialog create() {
			LayoutInflater inflater = (LayoutInflater) LayoutInflater
					.from(context);
			// .getSystemService(context.LAYOUT_INFLATER_SERVICE);
			final CustomAlertDialog dialog = new CustomAlertDialog(context,
					R.style.myDialog);
			View layout = inflater.inflate(R.layout.alert_dialog, null);
			TextView txt_title = (TextView) layout
					.findViewById(R.id.alert_dialog_txt_title);
			TextView txt_message = (TextView) layout
					.findViewById(R.id.alert_dialog_txt_content);
			ScrollView scroll = (ScrollView) layout.findViewById(R.id.scroll);
			dialog.setContentView(layout);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);

			// 确认按钮
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.alert_dialog_btn_confirm))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout
							.findViewById(R.id.alert_dialog_btn_confirm))
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
									// TODO Auto-generated method stub

								}
							});
				}
			} else {
				layout.findViewById(R.id.alert_dialog_btn_confirm)
						.setVisibility(View.GONE);
			}
			// 取消按钮
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.alert_dialog_btn_cancel))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.alert_dialog_btn_cancel))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.alert_dialog_btn_cancel)
						.setVisibility(View.GONE);
			}
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
			return dialog;
		}

		// /**
		// *
		// // 弹出框有三个按钮
		// * */
		// public CustomAlertDialog create1() {
		// LayoutInflater inflater = (LayoutInflater) LayoutInflater
		// .from(context);
		// // .getSystemService(context.LAYOUT_INFLATER_SERVICE);
		// final CustomAlertDialog dialog = new CustomAlertDialog(context,
		// R.style.myDialog);
		// View layout = inflater.inflate(R.layout.alert_dialog_ewm, null);
		// dialog.setContentView(layout);
		// dialog.setCanceledOnTouchOutside(false);
		//
		// // 访问
		// if (positiveButtonText != null) {
		// ((Button) layout.findViewById(R.id.alert_btn_one))
		// .setText(positiveButtonText);
		// if (positiveButtonClickListener != null) {
		// ((Button) layout.findViewById(R.id.alert_btn_one))
		// .setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// positiveButtonClickListener.onClick(dialog,
		// DialogInterface.BUTTON_POSITIVE);
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// }
		// } else {
		// layout.findViewById(R.id.alert_btn_one)
		// .setVisibility(View.GONE);
		// }
		// // 关注
		// if (otherButtonText != null) {
		//
		// ((Button) layout.findViewById(R.id.alert_btn_two))
		// .setText(otherButtonText);
		// if (otherButtonClickListener != null) {
		// ((Button) layout.findViewById(R.id.alert_btn_two))
		// .setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// otherButtonClickListener.onClick(dialog,
		// DialogInterface.BUTTON_NEUTRAL);
		// }
		// });
		// }
		// } else {
		// layout.findViewById(R.id.alert_btn_two)
		// .setVisibility(View.GONE);
		// }
		// // 取消
		// if (negativeButtonText != null) {
		// ((Button) layout.findViewById(R.id.alert_btn_three))
		// .setText(negativeButtonText);
		// if (negativeButtonClickListener != null) {
		// ((Button) layout.findViewById(R.id.alert_btn_three))
		// .setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// negativeButtonClickListener.onClick(dialog,
		// DialogInterface.BUTTON_NEGATIVE);
		// }
		// });
		// }
		// } else {
		// layout.findViewById(R.id.alert_btn_three).setVisibility(
		// View.GONE);
		// }
		//
		// // if (title == null || title.equals("")) {
		// // ((TextView) layout.findViewById(R.id.alert_dialog_txt_title))
		// // .setVisibility(View.GONE);
		// // } else {
		// // ((TextView) layout.findViewById(R.id.alert_dialog_txt_title))
		// // .setText(title);
		// // }
		// // if (message != null) {
		// // ((TextView) layout.findViewById(R.id.alert_dialog_txt_content))
		// // .setText(message);
		// // } else if (message == null || message.equals("")) {
		// // ((TextView) layout.findViewById(R.id.alert_dialog_txt_content))
		// // .setVisibility(View.GONE);
		// // }
		// // if (contentView != null) {
		// // ((LinearLayout) layout.findViewById(R.id.alert_layout))
		// // .removeAllViews();
		// // ((LinearLayout) layout.findViewById(R.id.alert_layout))
		// // .addView(contentView, new LayoutParams(
		// // LayoutParams.WRAP_CONTENT,
		// // LayoutParams.WRAP_CONTENT));
		// // }
		// return dialog;
		// }
	}

}
