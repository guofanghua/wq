package com.wq.me;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.R.attr;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.UI.WiperSwitch;
import com.wq.UI.WiperSwitch.OnChangedListener;
import com.wq.model.User;
import com.wq.model.proAttr;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

/**
 * 添加产品属性
 * */
public class proAttrAddActivity extends BaseActivity {
	@ViewInject(id = R.id.attr_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.edit_att_key)
	EditText edit_key;
	@ViewInject(id = R.id.edit_att_value)
	EditText edit_value;


	proAttr attrModel = new proAttr();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pro_attr_add_activity);
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
				// TODO Auto-generated method stub
				InputMethodManager inputManager = (InputMethodManager) edit_key
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(edit_key.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		
		setViewTouchListener(scroll_main);
		// TODO Auto-generated method stub

	}

	private void initNavigation() {
		initNavitation(getString(R.string.me_string_product_add_attr),
				getString(R.string.string_complete),
				R.drawable.title_btn_right_click, new EditClickListener() {

					@Override
					public void editClick() {
						if (!checkFrom())
							return;
						// 只有公共属性才会存入数据库

						attrModel.setId("");
						attrModel.setAttrKey(edit_key.getText().toString());
						attrModel.setAttrValue(edit_value.getText().toString());
						

						
						Intent intent = new Intent();
						intent.putExtra("attr", attrModel);
						setResult(CommonUtil.PRO_ATTR_ADD_RESULT_FLAG, intent);
						finish();
						animDown();
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animDown();
					}
				});
	}

	private boolean checkFrom() {
		if (TextUtils.isEmpty(edit_key.getText())) {
			return false;
		}
		if (TextUtils.isEmpty(edit_value.getText()))
			return false;
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();
			animDown();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}
}
