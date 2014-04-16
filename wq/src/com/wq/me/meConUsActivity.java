package com.wq.me;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.editActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.fragment.meFragment;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.RegexpUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/** 联系我们 */
public class meConUsActivity extends BaseActivity {

	@ViewInject(id = R.id.me_edit_addr)
	TextView txt_addr;
	@ViewInject(id = R.id.me_edit_mobile)
	TextView txt_mobile;
	@ViewInject(id = R.id.me_edit_url)
	TextView txt_url;
	@ViewInject(id = R.id.me_edit_email)
	TextView txt_email;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_con_us_activity);
		initnavigation();
		bindUI();
	}

	private void bindUI() {
		txt_addr.setText(User.address);
		txt_email.setText(User.email);
		txt_mobile.setText(User.moblie);
		txt_url.setText(User.enterpriseNet);
	}

	public void click(View v) {
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		// 地址
		case R.id.me_rela_addr:
		case R.id.me_edit_addr:
			isEditFlag = true;
			editActivity.edit_backcontent = txt_addr;
			bundle.putString("title",
					getResources().getString(R.string.me_string_com_info_addr));
			changeView(editActivity.class, bundle, true);
			break;
		// 电话
		case R.id.me_rela_mobile:
		case R.id.me_edit_mobile:
			isEditFlag = true;
			editActivity.edit_backcontent = txt_mobile;
			bundle.putString("Regexmsg", RegexpUtil.phone_regexp);
			bundle.putString("RegexWarn", getString(R.string.regex_mobile));
			bundle.putString("title",
					getResources()
							.getString(R.string.me_string_com_info_mobile));
			changeView(editActivity.class, bundle, true);
			break;
		// url
		case R.id.me_rela_url:
		case R.id.me_edit_url:
			isEditFlag = true;
			editActivity.edit_backcontent = txt_url;
			bundle.putString("Regexmsg", RegexpUtil.url_regexp);
			bundle.putString("RegexWarn", getString(R.string.regex_url));
			bundle.putString("title",
					getResources().getString(R.string.me_string_com_info_url));
			changeView(editActivity.class, bundle, true);
			break;
		// 邮箱
		case R.id.me_rela_email:
		case R.id.me_edit_email:
			isEditFlag = true;
			editActivity.edit_backcontent = txt_email;
			bundle.putString("Regexmsg", RegexpUtil.email_regexp);
			bundle.putString("RegexWarn", getString(R.string.regex_email));
			bundle.putString("title",
					getResources().getString(R.string.me_string_com_info_email));
			changeView(editActivity.class, bundle, true);
			break;
		}

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.me_string_info_con),
				"", -1, new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						// if (checkForm())

					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						commmit();
						finish();
						animOut();
					}
				});

	}

	// 提交修改
	private void commmit() {
		User.address = txt_addr.getText().toString();
		User.telePhone = txt_mobile.getText().toString();
		User.enterpriseNet = txt_url.getText().toString();
		User.email = txt_email.getText().toString();
		sharedPreferenceUtil.saveCompany(this);
		meFragment.isUpdate = true;
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_UPDATE_ADDR, CommonUtil.UpdateStr(txt_addr));
		params.put(httpUtil.ENTER_UPDATE_TEL, CommonUtil.UpdateStr(txt_mobile));
		params.put(httpUtil.ENTER_UPDATE_NET, CommonUtil.UpdateStr(txt_url));
		params.put(httpUtil.ENTER_UPDATE_EMAIL, CommonUtil.UpdateStr(txt_email));
		httpUtil.post(httpUtil.ENTER_PRISE_UPDATE_URL, params,
				new AjaxCallBack<String>() {
					public void onLoading(long count, long current) {
					};

					public void onStart() {
					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String msg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {

							} else {
								Company c = new Company();
								c.initData();
								sharedPreferenceUtil
										.saveUPCompany(meConUsActivity.this);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch
							Company c = new Company();
							c.initData();
							sharedPreferenceUtil
									.saveUPCompany(meConUsActivity.this);
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						Company c = new Company();
						c.initData();
						sharedPreferenceUtil
								.saveUPCompany(meConUsActivity.this);

					}
				});

	}

	// 判断某个textview是否被修改过
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			commmit();
			finish();
			animOut();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	boolean isEditFlag = false;
	Dialog editDialog = null;

	public void onRestart() {
		super.onRestart();

		// 显示edit编辑成功
		if (editActivity.isConfirm && isEditFlag) {
			// 弹出勾
			editDialog = DialogUtils.showConfirmDialog(this);
			editDialog.show();
			isEditFlag = false;
			editActivity.isConfirm = false;
			myhandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = myhandler.obtainMessage();
					msg.sendToTarget();
				}
			}, 800);
		}
	}

	// 定时隐藏
	Handler myhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (editDialog != null)
				editDialog.cancel();
		}

	};
}
