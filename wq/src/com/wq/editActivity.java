package com.wq;

import net.endure.framework.annotation.view.ViewInject;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.utils.CommonUtil;
import com.wq.utils.RegexpUtil;

public class editActivity extends BaseActivity {
	public static TextView edit_backcontent = null;
	public static boolean isConfirm = false;
	// public static TextView txt_backContent=null;
	@ViewInject(id = R.id.edit_content)
	EditText edit_content;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.txt_count)
	TextView txt_count;
	@ViewInject(id = R.id.txt_explan)
	TextView txt_explan;

	private String regexMsg = "";
	private String regexWarn = "";
	private String title = "";
	private int limitWord = 0;// 限制字数
	private boolean isUrl = false;// 判断是否是url 前面可能会自动添加http://
	public boolean IsNumber = false;// 只允许输入数字
	public boolean IsPhoto = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_activity);
		BaseApplication.getInstance().addActivity(this);
		isConfirm = false;
		Bundle bundle = this.getIntent().getExtras();

		if (bundle != null) {
			// 正则表达式
			if (!TextUtils.isEmpty(bundle.getString("Regexmsg")))
				regexMsg = bundle.getString("Regexmsg");
			regexWarn = bundle.getString("RegexWarn");
			// 字数限制
			limitWord = bundle.getInt("limitWord", 0);

			// 输入样式
			if (!TextUtils.isEmpty(bundle.getString("title"))) {
				title = bundle.getString("title");
			} else
				title = getResources().getString(R.string.string_edit);

			isUrl = bundle.getBoolean("isUrl", false);
			IsNumber = bundle.getBoolean("isNumber", false);
			IsPhoto = bundle.getBoolean("IsPhoto", false);
			if (!TextUtils.isEmpty(bundle.getString("explan"))) {
				txt_explan.setText(bundle.getString("explan"));
			}
		}

		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(editActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		if (IsNumber) {
			edit_content.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (IsPhoto) {
			edit_content.setInputType(InputType.TYPE_CLASS_PHONE);
		}

		if (isUrl) {
			if (String.valueOf(edit_backcontent.getText()).indexOf("http://") < 0) {
				edit_backcontent
						.setText("http://" + edit_backcontent.getText());
			}
		}
		if (edit_backcontent != null
				&& !TextUtils.isEmpty(edit_backcontent.getText().toString()
						.replace(getString(R.string.string_hint_no_str), ""))) {

			edit_content.setText(edit_backcontent.getText().toString()
					.replace(getString(R.string.string_hint_no_str), ""));
			edit_content.setSelection(edit_backcontent.getText().length());
		}
		if (limitWord > 0) {
			lengthFilter(this, edit_content, limitWord, String.format(
					getString(R.string.string_limt_warm), limitWord));
			edit_content.addTextChangedListener(new watch());
			txt_count.setVisibility(View.VISIBLE);
			if (edit_backcontent != null)
				txt_count.setText((limitWord - edit_backcontent.getText()
						.length()) + "");
		} else
			txt_count.setVisibility(View.GONE);
		initNavigation1(0, "", title,
				getResources().getString(R.string.string_save),
				R.drawable.btn_green_clickbg, new EditClickListener() {
					@Override
					public void editClick() {
						if (checkRegex()) {
							if (edit_backcontent != null
									&& !TextUtils.isEmpty(edit_content
											.getText().toString().trim())) {
								edit_backcontent.setText(edit_content.getText()
										.toString().trim());
								edit_backcontent.setTag("1");
							}

							InputMethodManager inputManager = (InputMethodManager) edit_content
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							inputManager.hideSoftInputFromWindow(
									edit_content.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
							edit_backcontent = null;
							isConfirm = true;
							finish();
							overridePendingTransition(0, R.anim.slide_down_out);
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						edit_backcontent = null;
						isConfirm = false;
						InputMethodManager inputManager = (InputMethodManager) edit_content
								.getContext().getSystemService(
										Context.INPUT_METHOD_SERVICE);
						inputManager.hideSoftInputFromWindow(
								edit_content.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
						finish();
						overridePendingTransition(0, R.anim.slide_down_out);

						// TODO Auto-generated method stub

					}
				});

	}

	private boolean checkRegex() {
		if (TextUtils.isEmpty(edit_content.getText())) {
			CommonUtil.showToast(getApplicationContext(), "字数不能为0");
			return false;
		}
		if (limitWord > 0 && edit_content.getText().length() > limitWord) {
			CommonUtil.showToast(getApplicationContext(), "不能超过" + limitWord
					+ "个字");
			return false;
		}
		// 正则表达式验证
		if (!RegexpUtil.isHardRegexpValidate(
				String.valueOf(edit_content.getText()), regexMsg)) {
			CommonUtil.showToast(getApplicationContext(), regexWarn);
			return false;
		} else
			return true;
	}

	public void onResume() {
		super.onResume();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			finish();
			overridePendingTransition(0, R.anim.slide_down_out);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onDestory() {
		super.onDestory();

		edit_backcontent = null;
	}

	// 动态改变事件
	private class watch implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (limitWord > 0) {

				txt_count.setText((limitWord - edit_content.getText().length())
						+ "");
			}
			// TODO Auto-generated method stub

		}

	}

	public void lengthFilter(final Context context, final EditText editText,
			final int max_length, final String err_msg) {

		InputFilter[] filters = new InputFilter[1];

		filters[0] = new InputFilter.LengthFilter(max_length) {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,

			Spanned dest, int dstart, int dend) {

				int destLen = dest.toString().length(); // 获取字符个数(一个中文算2个字符)

				int sourceLen = source.toString().length();

				if (destLen + sourceLen > max_length) {

					// Toast.makeText(context, err_msg,
					// Toast.LENGTH_SHORT).show();

					return source.toString().substring(0, max_length - destLen);

				}

				return source;

			}

		};
		edit_content.setFilters(filters);
	}
}
