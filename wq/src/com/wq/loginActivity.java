package com.wq;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import android.os.Bundle;

import android.text.TextUtils;

import android.view.KeyEvent;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.endure.wq.R;

import com.wq.me.forgetPwdActivity;
import com.wq.model.Company;
import com.wq.model.User;

import com.wq.utils.CommonUtil;

import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

public class loginActivity extends BaseActivity {
	@ViewInject(id = R.id.login_img_login)
	ImageView img_logo;
	@ViewInject(id = R.id.login_edt_user)
	EditText edt_user;
	@ViewInject(id = R.id.login_edt_userKey)
	EditText edt_userKey;

	@ViewInject(id = R.id.login_btn_confirm)
	Button btn_confirm;
	@ViewInject(id = R.id.login_btn_register)
	Button btn_register;
	@ViewInject(id = R.id.txt_forget_pwd)
	TextView txt_forget_pwd;

	@ViewInject(id = R.id.login_relate_main)
	LinearLayout relate_main;

	// 快捷登录

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		initListener();

	}

	private void initListener() {
		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (TextUtils.isEmpty(edt_user.getText())) {
					CommonUtil.showToast(loginActivity.this,
							R.string.login_msg_user);

					return;
				}
				if (TextUtils.isEmpty(edt_userKey.getText())) {
					CommonUtil.showToast(loginActivity.this,
							R.string.login_msg_userKey);

					return;
				}
				// TODO Auto-generated method
				httpData();
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeView(registerActivity.class);
			}
		});

		relate_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(loginActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		txt_forget_pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putString("wqh", edt_user.getText().toString());
				changeView(forgetPwdActivity.class, b);
			}
		});

	}

	private void initnavigation() {
		initNavigation(R.drawable.title_btn_right_click,
				getString(R.string.string_back),
				getString(R.string.login_btn_login), "", "", -1,
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

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.SAFT_NAME, edt_user.getText().toString().trim());
		params.put(
				httpUtil.SAFT_LOSIGN,
				CommonUtil.stringTo32Md5(edt_userKey.getText().toString()
						.trim(), CommonUtil.key1));
		params.put(httpUtil.SAFT_DECIVE_INFO,
				CommonUtil.deviceInfo(loginActivity.this));
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.SAFE_LOGIN_URL, params,
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
								User.userKey = jsonResult.getString("userKey");
								// 获取公司信息
								// JSONObject enterprise = jsonResult
								// .getJSONObject("enterprise");
								User.id = jsonResult.getString("id");
								User.name = jsonResult.getString("name");
								User.introduciont = jsonResult
										.getString("introduciont");
								User.culture = jsonResult.getString("culture");
								User.desire = jsonResult.getString("desire");
								User.commodity = jsonResult
										.getString("commodity");
								User.iconFile = jsonResult
										.getString("iconFile");

								User.propagandaFile = jsonResult
										.getString("propagandaFile");
								User.address = jsonResult.getString("address");
								User.email = jsonResult.getString("email");
								User.telePhone = jsonResult
										.getString("telePhone");
								User.moblie = jsonResult.getString("moblie");
								User.net = jsonResult.getString("net");
								User.enterpriseNet = jsonResult
										.getString("enterpriseNet");
								User.enterpriseType = jsonResult
										.getString("enterpriseType");
								User.wqh = jsonResult.getString("wqh");
								User.isCertification = jsonResult
										.getString("isCertification");
								User.agentId = jsonResult.getString("agentId");
								User.templateId = jsonResult
										.getString("templateId");
								User.contactName = jsonResult
										.getString("contactName");
								User.occupation = jsonResult
										.getString("occupation");
								User.weChat = jsonResult.getString("weChat");
								User.proEmail = jsonResult.getString("mail");
								User.signature = jsonResult
										.getString("signature");
								User.nameCardTempId = jsonResult
										.getString("cardId");

								sharedPreferenceUtil
										.saveCompany(loginActivity.this);
								Bundle b = new Bundle();
								b.putBoolean("loginFlag", true);
								changeView(mainActivity.class, b);
								finish();
							} else {
								CommonUtil
										.showToast(loginActivity.this, errMsg);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							CommonUtil.showToast(loginActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							hideProgress();
						}

					}

					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToast(loginActivity.this,
								getString(R.string.string_http_err_failure));
					}
				});
		// stub

	}

	public void onResume() {
		super.onResume();
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
