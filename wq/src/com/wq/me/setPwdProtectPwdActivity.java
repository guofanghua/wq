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

/**
 * 修改密保的时候，需走输入密码流程
 * */
public class setPwdProtectPwdActivity extends BaseActivity {

	@ViewInject(id = R.id.edit_pwd)
	EditText edit_pwd;
	@ViewInject(id = R.id.layout_pwd)
	LinearLayout layout_pwd;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	int topBg = -1;
	String title = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_pwd_protect_pwd_activity);
		// edit_email.setText(User.proEmail);
		initNavigation();
		setViewTouchListener(scroll_main);
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(setPwdProtectPwdActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

	}

	private void initNavigation() {
		title = getString(R.string.string_next_step);
		topBg = R.drawable.btn_style_green;
		initNavitation(getString(R.string.me_string_set_pwd_email_title),
				title, topBg, new EditClickListener() {

					@Override
					public void editClick() {
						if (TextUtils.isEmpty(edit_pwd.getText())) {
							showToast(R.string.me_string_set_pwd_pro_pwd_warn);
							return;
						}
						Bundle b = new Bundle();
						b.putString("pwd", edit_pwd.getText().toString());
						changeView(setPwdProtectActivity.class, b);
						finish();
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});
	}

}
