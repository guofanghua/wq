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
import com.wq.loginActivity;
import com.wq.mainActivity;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.RegexpUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

public class setPwdProtectActivity extends BaseActivity {
	@ViewInject(id = R.id.edit_email)
	EditText edit_email;

	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	int topBg = -1;
	String title = "";
	String pwd = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_pwd_protect_activity);
		pwd = this.getIntent().getStringExtra("pwd");
		// edit_email.setText(User.proEmail);
		initNavigation();
		setViewTouchListener(scroll_main);
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(setPwdProtectActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

	}

	private void initNavigation() {

		title = getString(R.string.string_confirm);
		topBg = R.drawable.btn_style_green;

		initNavitation(getString(R.string.me_string_set_pwd_email_title),
				title, topBg, new EditClickListener() {

					@Override
					public void editClick() {
						if (TextUtils.isEmpty(edit_email.getText())) {
							showToast(R.string.me_string_set_pwd_email_empty);
							return;
						} else if (!RegexpUtil.isHardRegexpValidate(edit_email
								.getText().toString(), RegexpUtil.email_regexp)) {
							showToast(R.string.me_string_set_pwd_email_regex);
							return;
						}
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
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.PWD_PRO_EMIAL, edit_email.getText().toString());
		params.put(httpUtil.PWD_PRO_PWD,
				CommonUtil.stringTo32Md5(pwd, CommonUtil.key1));
		httpUtil.post(httpUtil.PWD_PROTECT_URL, params,
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

								User.proEmail = jsonResult.getString("mail");
								sharedPreferenceUtil
										.saveCompany(setPwdProtectActivity.this);
								finish();
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
						CommonUtil.showToastHttp(setPwdProtectActivity.this,
								errorNo);
					}
				});
		// TODO Auto-generated method stub

	}
}
