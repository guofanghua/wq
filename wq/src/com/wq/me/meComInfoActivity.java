package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
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
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/** 公司资料 */
public class meComInfoActivity extends BaseActivity {
	@ViewInject(id = R.id.me_info_layout_intro)
	LinearLayout layout_intro;// 企业简介
	@ViewInject(id = R.id.me_info_layout_jyfw)
	LinearLayout layout_jyfw;// 经营范围
	@ViewInject(id = R.id.me_info_layout_qyyj)
	LinearLayout layout_qyyj;// 企业远景
	@ViewInject(id = R.id.me_info_layout_con)
	LinearLayout layout_con;// 联系我们
	@ViewInject(id = R.id.me_info_txt_intro)
	TextView txt_intro;
	@ViewInject(id = R.id.me_info_txt_jyfw)
	TextView txt_jyfw;
	@ViewInject(id = R.id.me_info_txt_qyyj)
	TextView txt_qyyj;
	private Dialog editDialog = null;
	int errCount = 0;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_com_info_activity);
		initnavigation();
		bindUI();
	}

	private void bindUI() {
		txt_intro.setText(User.introduciont);
		txt_jyfw.setText(User.commodity);
		txt_qyyj.setText(User.desire);
	}

	public void click(View v) {
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		// 企业简介
		case R.id.me_info_layout_intro:
		case R.id.me_info_txt_intro:
			isEditFlag = true;
			editActivity.edit_backcontent = txt_intro;
			txt_intro.setTag("1");
			bundle.putString("title",
					getResources().getString(R.string.me_string_info_intro));
			changeView(editActivity.class, bundle, true);
			break;
		// 经营范围
		case R.id.me_info_layout_jyfw:
		case R.id.me_info_txt_jyfw:
			isEditFlag = true;
			editActivity.edit_backcontent = txt_jyfw;
			txt_jyfw.setTag("1");
			bundle.putString("title",
					getResources().getString(R.string.me_string_info_jjfw));
			changeView(editActivity.class, bundle, true);
			break;
		// 企业远景
		case R.id.me_info_layout_qyyj:
		case R.id.me_info_txt_qyyj:
			isEditFlag = true;
			editActivity.edit_backcontent = txt_qyyj;
			txt_qyyj.setTag("1");
			bundle.putString("title",
					getResources().getString(R.string.me_string_info_wqyw));
			changeView(editActivity.class, bundle, true);
			break;
		// 联系我们
		case R.id.me_info_layout_con:
			changeView(meConUsActivity.class);
			break;
		}

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.me_string_info_tile),
				"", -1, new EditClickListener() {
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						// if (checkForm())
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

	}

	// 提交修改
	private void commmit() {
		showProgress(R.string.dialog_comitting);

		User.introduciont = txt_intro.getText().toString();
		User.commodity = txt_jyfw.getText().toString();
		User.desire = txt_qyyj.getText().toString();
		meFragment.isUpdate = true;
		sharedPreferenceUtil.saveCompany(this);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_UPDATE_INTRO, txt_intro.getText().toString());
		params.put(httpUtil.ENTER_UPDATE_COMMODITY,
				CommonUtil.UpdateStr(txt_jyfw));
		params.put(httpUtil.ENTER_UPDATE_DESIRE, CommonUtil.UpdateStr(txt_qyyj));
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
								sharedPreferenceUtil
										.clearUPCompany(meComInfoActivity.this);
								hideProgress();
								showRightDialog();
							} else {
								hideProgress();
								showToast(msg);
								Company c = new Company();
								c.initData();
								sharedPreferenceUtil
										.saveUPCompany(meComInfoActivity.this);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch
							showToast(R.string.string_http_err_data);
							hideProgress();
							Company c = new Company();
							c.initData();
							sharedPreferenceUtil
									.saveUPCompany(meComInfoActivity.this);
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(getApplicationContext(),
								errorNo);
						Company c = new Company();
						c.initData();
						sharedPreferenceUtil
								.saveUPCompany(meComInfoActivity.this);

					}
				});

	}

	// 判断某个textview是否被修改过
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			finish();
			animOut();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	boolean isEditFlag = false;

	public void onRestart() {
		super.onRestart();
		// 显示edit编辑成功
		if (editActivity.isConfirm && isEditFlag) {
			// 弹出勾

			isEditFlag = false;
			editActivity.isConfirm = false;
			if (checkUpdate())
				commmit();
		}
	}

	// 检测是否已经修改

	public boolean checkUpdate() {

		// 微企介绍
		if (txt_intro.getTag().toString().equals("1")) {
			txt_intro.setTag("0");
			if (!txt_intro.getText().toString().equals(User.introduciont)) {

				return true;
			}

		} else if (txt_jyfw.getTag().toString().equals("1")) {
			txt_jyfw.setTag("0");
			if (!txt_jyfw.getText().toString().equals(User.commodity)) {

				return true;
			}

		}
		// 微企愿望
		else if (txt_qyyj.getTag().toString().equals("1")) {
			txt_qyyj.setTag("0");
			if (!txt_qyyj.getText().toString().equals(User.desire)) {
				return true;
			}
		}

		return false;

	}

}
