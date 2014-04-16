package com.wq.me;

import java.util.Date;

import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.model.User;
import com.wq.model.meCollectModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.RegexpUtil;
import com.wq.utils.dateUtil;

public class MeCollectWZActivity extends BaseActivity {

	// public static TextView txt_backContent=null;
	@ViewInject(id = R.id.edit_content)
	EditText edit_content;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.txt_count)
	TextView txt_count;
	meCollectModel model;
	String[] titleArr;
	int type = 0;
	FinalDb db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_collect_wz_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initData();
		initNavigation();
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(MeCollectWZActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

	}

	private void initData() {
		titleArr = getResources().getStringArray(R.array.me_collect_arr);
		type = this.getIntent().getIntExtra("type", 0);
		if (type >= 2)
			type -= 2;

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			finish();
			overridePendingTransition(0, R.anim.slide_down_out);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onDestory() {
		super.onDestory();

	}

	// 动态改变事件
	private class watch implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			// TODO Auto-generated method stub

		}

	}

	public void lengthFilter(final Context context, final EditText editText,
			final int max_length, final String err_msg) {

		InputFilter[] filters = new InputFilter[1];

		filters[0] = new InputFilter.LengthFilter(max_length) {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,

			Spanned dest, int dstart, int dend) {

				int destLen = dest.toString().length(); // 获取字符个数(一个中文算2个字符)

				int sourceLen = source.toString().length();

				if (destLen + sourceLen > max_length) {

					// Toast.makeText(context, err_msg,
					// Toast.LENGTH_SHORT).show();

					return source.toString().substring(0, max_length - destLen);

				}

				return source;

			}

		};
		edit_content.setFilters(filters);
	}

	private void initNavigation() {
		initNavitation(titleArr[type].split("\\|")[1],
				getString(R.string.string_complete),
				R.drawable.btn_green_clickbg, new EditClickListener() {
					@Override
					public void editClick() {
						if (TextUtils.isEmpty(edit_content.getText())) {
							return;
						}
						model = new meCollectModel();
						model.seteId(User.id);
						model.setIcon(User.iconFile);
						model.setName(edit_content.getText().toString());
						model.setTime(dateUtil.formatDate(new Date()));
						model.setTitle(User.wqh);
						model.setType(type + 2);
						// 只有公共属性才会存入数据库
						try {
							db.save(model);
							Intent intent = new Intent();
							intent.putExtra("meCollectModel", model);
							setResult(200, intent);
							finish();
							animDown();
						} catch (Exception e) {
							showToast(R.string.me_collect_err_warn);

						}
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animDown();
					}
				});
	}
}
