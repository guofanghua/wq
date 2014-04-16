package com.wq.me;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.WelecomeActivity;
import com.wq.loginActivity;
import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.RegexpUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/**
 * 修改密码
 * */
public class setChangePwdActivity extends BaseActivity {

	@ViewInject(id = R.id.edit_old_pwd)
	EditText edit_old_pwd;
	@ViewInject(id = R.id.edit_pwd)
	EditText edit_pwd;
	@ViewInject(id = R.id.edit_re_pwd)
	EditText edit_re_pwd;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_pwd_activity);
		initNavigation();
		setViewTouchListener(scroll_main);
		layout_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(setChangePwdActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

	}

	private void initNavigation() {
		initNavitation(getString(R.string.me_string_set_change_pwd_title),
				getString(R.string.string_confirm), R.drawable.btn_style_green,
				new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						if (checkFrom())
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

	private boolean checkFrom() {
		if (TextUtils.isEmpty(edit_old_pwd.getText())) {
			showToast(R.string.me_string_set_pwd_old_warn);
			return false;
		}
		if (TextUtils.isEmpty(edit_pwd.getText())) {
			showToast(R.string.me_string_set_pwd_pwd_warn);
			return false;
		}
		if (!edit_pwd.getText().toString()
				.equals(edit_re_pwd.getText().toString())) {
			showToast(R.string.me_string_set_pwd_pwd_warn1);
			return false;
		}

		return true;
	}

	// 提交，修改密码
	private void httpData() {
		String oldpwd = CommonUtil.stringTo32Md5(edit_old_pwd.getText()
				.toString(), CommonUtil.key1);
		String newPwd = CommonUtil.stringTo32Md5(edit_pwd.getText().toString(),
				CommonUtil.key1);
		LoggerUtil.i("1111" + httpUtil.CHANGE_PWD_URL);
		showProgress(R.string.dialog_comitting);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		// params.put(httpUtil.PWD_RE_SET_WQH, wqh);
		// params.put(httpUtil.PWD_RE_SET_JYM, edit_jym.getText().toString());
		params.put(httpUtil.NEW_PWD, newPwd);
		params.put(httpUtil.PWD_CHANGE_SIGN,
				CommonUtil.stringTo32Md5(oldpwd + newPwd, CommonUtil.key2));

		httpUtil.post(httpUtil.CHANGE_PWD_URL, params,
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
								CommonUtil.Quit(setChangePwdActivity.this);
								showToast(R.string.me_string_change_right_warn);
								changeView(WelecomeActivity.class);

							} else {
								showToast(R.string.me_string_change_err_warn);
							}
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
						CommonUtil.showToastHttp(setChangePwdActivity.this,
								errorNo);
					}
				});
		// TODO Auto-generated method stub

	}

}
