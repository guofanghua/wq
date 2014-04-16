package com.wq;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import com.endure.wq.R;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import com.wq.utils.stringUtils;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class registerActivity extends BaseActivity {
	@ViewInject(id = R.id.register_btn_confirm)
	Button btn_register;
	@ViewInject(id = R.id.register_edt_user)
	EditText edit_username;
	@ViewInject(id = R.id.register_edt_userKey)
	EditText edit_pwd;
	@ViewInject(id = R.id.register_edt_rep_userKey)
	EditText edit_rep_pwd;
	@ViewInject(id = R.id.login_relate_main)
	LinearLayout relate_main;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		initListener();
	}

	private void initListener() {
		relate_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(registerActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkForm())
					httpData();
			}
		});

	}

	private void initnavigation() {
		initNavigation(R.drawable.title_btn_right_click,
				getString(R.string.string_back),
				getString(R.string.register_string_zc), "", "", -1,
				new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub

					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animDown();
					}
				});

	}

	private boolean checkForm() {
		if (TextUtils.isEmpty(edit_username.getText())) {
			showToast(R.string.register_edit_user_hint);
			return false;
		}
		if (stringUtils.hasZW(edit_username.getText().toString())) {
			showToast(R.string.register_edit_user_zw_warn);
			return false;
		}
		// if (stringUtils.isSZ(edit_username.getText().toString())) {
		// showToast(R.string.register_edit_user_sz_warn);
		// return false;
		// }
		if (stringUtils.hasZW(edit_pwd.getText().toString())) {
			showToast(R.string.register_edit_user_pwd_gf_warn);
			return false;

		}
		if (stringUtils.strLength(edit_username.getText().toString()) < 6
				|| stringUtils.strLength(edit_username.getText().toString()) > 20) {
			showToast(R.string.register_edit_user_num_count_warn);
			return false;
		}
		if (TextUtils.isEmpty(edit_pwd.getText())) {
			showToast(R.string.register_edit_userkey_hint);
			return false;
		}
		if (!edit_rep_pwd.getText().toString()
				.equals(edit_pwd.getText().toString())) {
			showToast(R.string.register_rep_pwd_warn);
			return false;

		}

		return true;
	}

	private void httpData() {

		AjaxParams params = new AjaxParams();
		params.put(httpUtil.REGISTER_WQH, edit_username.getText().toString());
		params.put(httpUtil.REGISTER_PWD, CommonUtil.stringTo32Md5(edit_pwd
				.getText().toString(), CommonUtil.key1));
		params.put(httpUtil.REGISTER_T, CommonUtil.stringTo32Md5(
				edit_username.getText().toString()
						+ CommonUtil.stringTo32Md5(edit_pwd.getText()
								.toString(), CommonUtil.key1), CommonUtil.key2));
		params.put(httpUtil.REGISTER_DECIVE_INFO,
				CommonUtil.deviceInfo(registerActivity.this));
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.SAFE_REGISTER_URL, params,
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

								// 获取公司信息
								User.userKey = jsonResult.getString("userKey");
								// 获取公司信息
								// JSONObject enterprise = jsonResult
								// .getJSONObject("enterprise");
								User.id = jsonResult.getString("id");
								User.isCertification = "0";
								User.enterpriseNet = jsonResult
										.getString("enterpriseNet");
								User.name = jsonResult.getString("name");
								User.wqh = edit_username.getText().toString();
								sharedPreferenceUtil
										.saveCompany(registerActivity.this);
								changeView(register_searchComActivity.class);
								finish();
							}
							CommonUtil.showToast(registerActivity.this, errMsg);

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
						CommonUtil.showToast(registerActivity.this,
								getString(R.string.string_http_err_failure));
					}
				});
	}

	public void onResume() {
		super.onResume();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			animDown();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
