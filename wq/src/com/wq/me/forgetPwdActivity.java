package com.wq.me;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.editActivity;
import com.wq.loginActivity;
import com.wq.mainActivity;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.RegexpUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import com.wq.utils.stringUtils;

public class forgetPwdActivity extends BaseActivity {
	@ViewInject(id = R.id.edit_wqh)
	EditText edit_wqh;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	int topBg = -1;
	String title = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_pwd_activity);

		title = getString(R.string.string_next_step);
		topBg = R.drawable.btn_style_green;
		edit_wqh.setText(this.getIntent().getStringExtra("wqh"));
		edit_wqh.setSelection(edit_wqh.getText().length());
		initNavigation();
		setViewTouchListener(scroll_main);
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(forgetPwdActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

	}

	private void initNavigation() {
		initNavitation(getString(R.string.me_string_forget_pwd_title), title,
				topBg, new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						if (TextUtils.isEmpty(edit_wqh.getText())) {
							showToast(R.string.me_string_forget_pwd_empty);
							return;
						}

						if (stringUtils.hasZW(edit_wqh.getText().toString())) {
							showToast(R.string.register_edit_user_zw_warn);
							return;
						}
						// if (stringUtils.isSZ(edit_wqh.getText().toString()))
						// {
						// showToast(R.string.register_edit_user_sz_warn);
						// return;
						// }
						httpData();
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});
	}

	private void httpData() {
		showProgress(R.string.dialog_comitting);
		AjaxParams params = new AjaxParams();

		params.put(httpUtil.FORGET_PWD_WQH, edit_wqh.getText().toString());
		/*
		 * params.addQueryStringParameter(httpUtil.USER_ID, Company.id);
		 * params.addQueryStringParameter(httpUtil.USER_KEY, Company.userKey);
		 */
		httpUtil.post(httpUtil.PWD_PRO_SEND_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								Bundle b = new Bundle();
								b.putString("wqh", edit_wqh.getText()
										.toString());
								b.putString("email",
										jsonResult.getString("mail"));
								changeView(setPwdProtectActivity.class, b);

							}
							showToast(errMsg);

						} catch (JSONException e) {
							showToast(R.string.string_http_err_failure);
							e.printStackTrace();
						} finally {
							hideProgress();
						}

						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToastHttp(forgetPwdActivity.this,
								errorNo);
					}
				});
		// TODO Auto-generated method stub

	}
}
