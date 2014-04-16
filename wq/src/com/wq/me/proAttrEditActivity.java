package com.wq.me;

import java.util.ArrayList;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;
import com.wq.UI.WiperSwitch;
import com.wq.UI.WiperSwitch.OnChangedListener;
import com.wq.model.User;
import com.wq.model.proAttr;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;

/**
 * 编辑属性
 * */
public class proAttrEditActivity extends BaseActivity {
	@ViewInject(id = R.id.edit_att_value)
	EditText edit_value;
	@ViewInject(id = R.id.attr_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.btn_del)
	Button btn_del;
	@ViewInject(id = R.id.txt_explan)
	TextView txt_explan;

	private proAttr p = new proAttr();
	int position = -1;
	String title = "";
	ArrayList<proAttr> list = new ArrayList<proAttr>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pro_attr_edit_activity);
		BaseApplication.getInstance().addActivity(this);
		initData();
		initNavigation();
		initListener();
	}

	private void initData() {
		Intent intent = this.getIntent();
		position = intent.getIntExtra("position", -1);
		list = (ArrayList<proAttr>) this.getIntent().getSerializableExtra(
				"list");
		// 添加
		if (position >= 0) {
			if (list != null && list.size() > 0) {
				p = list.get(position);
				title = p.getAttrKey();
				edit_value.setText(p.getAttrValue());
				edit_value.setSelection(edit_value.getText().length());
			}
			txt_explan.setVisibility(View.GONE);
		} else {
			title = getString(R.string.me_string_pro_attr_title);
			txt_explan.setVisibility(View.VISIBLE);

		}

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
		// 删除此属性
		btn_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				p.setAttrValue(edit_value.getText().toString());
				Intent intent = new Intent(proAttrEditActivity.this,
						productAddActivity.class);
				intent.putExtra("flag", 0);
				intent.putExtra("attr", p);
				intent.putExtra("position", position);
				setResult(CommonUtil.PRO_ATTR_EDIT_RESULT_FLAG, intent);
				finish();

			}
		});

		setViewTouchListener(scroll_main);
	}

	private void initNavigation() {
		initNavitation(title, getString(R.string.string_complete),
				R.drawable.title_btn_right_click, new EditClickListener() {
					@Override
					public void editClick() {
						// 新添加
						if (position < 0) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i)
										.getAttrKey()
										.equals(edit_value.getText().toString())) {
									showToast(R.string.me_attr_exist_warn);
									break;
								}
							}
							p.setAttrKey(edit_value.getText().toString());
						}
						// 填写属性
						else {
							p.setAttrValue(edit_value.getText().toString());
						}

						Intent intent = new Intent(proAttrEditActivity.this,
								productAddActivity.class);
						intent.putExtra("position", position);
						intent.putExtra("attrModel", p);
						setResult(101, intent);
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

	// 判断某个textview是否被修改过
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			animDown();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
}
