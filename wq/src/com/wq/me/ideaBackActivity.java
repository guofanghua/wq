package com.wq.me;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.loginActivity;
import com.wq.mainActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/**
 * 意见反馈
 * */

public class ideaBackActivity extends BaseActivity {
	@ViewInject(id = R.id.edit_content)
	EditText edit_content;

	@ViewInject(id = R.id.txt_count)
	TextView txt_count;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	private int limitWord = 200;// 限制字数

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idea_back_activity);
		BaseApplication.getInstance().addActivity(this);
		lengthFilter(this, edit_content, limitWord,
				String.format(getString(R.string.string_limt_warm), limitWord));
		edit_content.addTextChangedListener(new watch());
		txt_count.setVisibility(View.VISIBLE);

		txt_count.setText(limitWord + "");
		initNavitation(getString(R.string.me_string_set_feed_back),
				getString(R.string.string_tj), new EditClickListener() {

					@Override
					public void editClick() {
						if (TextUtils.isEmpty(edit_content.getText())) {
							showToast(R.string.me_suggest_warn);
						} else
							httpData();
						// TODO Auto-generated method stub

					}

					@Override
					public void backClick() {
						finish();
						animOut();
						// TODO Auto-generated method stub

					}

				});
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(ideaBackActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
	}

	// 提交
	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.SUGGEST_CONTENT, edit_content.getText().toString());
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.ME_SUGGEST_URL, params,
				new AjaxCallBack<String>() {
					public void onStart() {

					}

					public void onSuccess(String result) {
						try {
							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String errMsg = jsonResult
									.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								finish();
							}
							CommonUtil.showToast(ideaBackActivity.this, errMsg);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							hideProgress();
						}

					}

					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToast(ideaBackActivity.this,
								getString(R.string.string_http_err_failure));
					}
				});
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
				Log.i("den", destLen + ",,," + sourceLen);
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
