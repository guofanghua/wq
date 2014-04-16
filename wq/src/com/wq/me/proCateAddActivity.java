package com.wq.me;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;
import com.wq.model.User;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;

/**
 * 添加类别
 * */
public class proCateAddActivity extends BaseActivity {
	@ViewInject(id = R.id.edit_cate_value)
	EditText edit_value;
	@ViewInject(id = R.id.attr_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.btn_del)
	Button btn_del;

	int position = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pro_cate_add_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavigation();
		initListener();
	}

	private void initListener() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) edit_value
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(
						edit_value.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// TODO Auto-generated method stub

			}
		});
		setViewTouchListener(scroll_main);

	}

	private void initNavigation() {
		initNavitation(getString(R.string.me_string_product_add_cate),
				getString(R.string.string_save),
				R.drawable.title_btn_right_click, new EditClickListener() {
					@Override
					public void editClick() {
						if (TextUtils.isEmpty(edit_value.getText())) {
							CommonUtil.showToast(proCateAddActivity.this,
									R.string.me_string_pro_attr_value_hint);
							return;
						}
						commit();
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animDown();
					}
				});
	}

	private void commit() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.CATE_NAME, edit_value.getText().toString());
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.PRODUCT_CATEGORY_ADD_URL, params,
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
								Intent intent = new Intent(
										proCateAddActivity.this,
										productAddActivity.class);
								intent.putExtra("name", edit_value.getText()
										.toString());
								intent.putExtra("id",
										jsonResult.getString("id"));

								setResult(CommonUtil.PRO_CATE_ADD_RESULT_FLAG,
										intent);
								finish();
							}
							CommonUtil.showToast(proCateAddActivity.this,
									errMsg);

						} catch (JSONException e) {
							CommonUtil.showToast(proCateAddActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							hideProgress();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						hideProgress();
						Log.i("fail", strMsg);
						CommonUtil.showToastHttp(proCateAddActivity.this,
								errorNo);
					}
				});

	}

}
