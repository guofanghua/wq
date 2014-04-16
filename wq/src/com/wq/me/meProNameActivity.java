package com.wq.me;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;
import com.wq.find.findTipListActivity;
import com.wq.fragment.meFragment;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/** 绑定公司名称 */
public class meProNameActivity extends BaseActivity {
	@ViewInject(id = R.id.edit_name)
	EditText edit_name;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_pro_name_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.me_pro_name_title),
				getResources().getString(R.string.string_confirm), 0,
				new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						// if (checkForm())
						if (TextUtils.isEmpty(edit_name.getText())) {
							showToast(R.string.me_pro_name_edit_warn);
							return;
						}
						// 提交
						commmit();
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

		sharedPreferenceUtil.saveCompany(this);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_UPDATE_NAME, edit_name.getText().toString());
		showProgress(R.string.dialog_comitting);
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
								meFragment.isUpdate = true;
								companyDetailActivity.isUpdate = true;
								User.name = edit_name.getText().toString();
								sharedPreferenceUtil
										.saveCompany(meProNameActivity.this);
								setResult(RESULT_OK);
								hideProgress();
								Intent intent = new Intent();
								intent.putExtra("name", edit_name.getText()
										.toString());
								setResult(111, intent);
								finish();
							} else {
								showToast(msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch
							e.printStackTrace();
							showToast(R.string.string_http_err_data);
						} finally {
							hideProgress();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToastHttp(meProNameActivity.this,
								errorNo);
						// TODO Auto-generated method stub
					}
				});

	}
}
