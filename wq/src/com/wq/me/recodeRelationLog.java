package com.wq.me;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.retationLogAdapter;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

/** 写客户关系日志 */
public class recodeRelationLog extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.et_content)
	EditText mEtTxT;
	@ViewInject(id = R.id.add_relation_btn)
	ImageView addContactBtn;
	@ViewInject(id = R.id.grid_container)
	CustomListView grid_container;
	@ViewInject(id = R.id.txt_time)
	TextView txt_time;
	@ViewInject(id = R.id.txt_title)
	TextView txt_title;
	@ViewInject(id = R.id.layout_pic)
	LinearLayout layout_pic;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	retationLogAdapter adapter;
	public FinalBitmap finalBitmap;
	// boolean JumpFlag = false;
	private ArrayList<partComModel> resultList = new ArrayList<partComModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_recode_relationship_log);
		BaseApplication.getInstance().addActivity(this);
		initUI();
	}

	private void initUI() {
		initnavigation();
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			partComModel item = (partComModel) b
					.getSerializable("contact");
			resultList.add(item);
		}
		finalBitmap = FinalBitmap.create(this);
		addContactBtn.setOnClickListener(this);
		layout_main.setOnClickListener(this);

		// JumpFlag = this.getIntent().getBooleanExtra("jumpFlag", false);
		if (resultList != null && resultList.size() > 0) {
			txt_title.setVisibility(View.GONE);
			grid_container.setVisibility(View.VISIBLE);

		} else {
			// txt_title.setText(Color.BLACK);
			// txt_title.setVisibility(View.VISIBLE);
			grid_container.setVisibility(View.GONE);
		}
		adapter = new retationLogAdapter(this, resultList);
		grid_container.setAdapter(adapter);
		grid_container.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
		layout_pic.setOnClickListener(this);
		CancleData();
	}

	private void CancleData() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));// 东8区时间
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		txt_time.setText(String.format(getString(R.string.record_format_str),
				month, day, dateUtil.getWeekDay(c)));
	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.log_edit), "提交",
				new EditClickListener() {
					@Override
					public void editClick() {
						String content = mEtTxT.getText().toString();

						String pidString = BuildPIDstring();
						if (pidString.trim().length() < 2) {
							txt_title.setTextColor(Color.RED);
							return;
						}
						if (content.trim().length() < 1) {
							showToast("亲，你还未输入任何内容");
							return;
						}
						postData(pidString, content);
					}

					@Override
					public void backClick() {
						finish();
						// if (JumpFlag) {
						animDown();
						// } else
						// animOut();
					}
				});
	}

	private String BuildPIDstring() {
		StringBuffer bf = new StringBuffer("");
		if (resultList != null && resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				bf.append(resultList.get(i).getEnterpriseId());
				if (i < resultList.size() - 1) {
					bf.append("|");
				}
			}
		}
		return bf.toString();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.add_relation_btn:
		case R.id.layout_pic:
			Bundle b = new Bundle();
			b.putSerializable("result", resultList);
			changeViewForResult(card_relation_select_people_activity.class, b,
					101, 1);
			break;
		case R.id.layout_main:
			InputMethodManager inputManager = (InputMethodManager) mEtTxT
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(mEtTxT.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 101 && resultCode == 102 && data != null) {
			Bundle bd = data.getExtras();
			if (bd != null) {
				try {

					resultList = (ArrayList<partComModel>) bd
							.getSerializable("result");
					if (resultList != null && resultList.size() > 0) {
						txt_title.setVisibility(View.GONE);
						grid_container.setVisibility(View.VISIBLE);
						adapter = new retationLogAdapter(this, resultList);
						grid_container.setAdapter(adapter);
						grid_container
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										// TODO Auto-generated method stub

									}
								});
					} else {
						txt_title.setText(Color.BLACK);
						txt_title.setVisibility(View.VISIBLE);
						grid_container.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (resultList.size() == 0) {
			txt_title.setVisibility(View.VISIBLE);
			grid_container.setVisibility(View.GONE);
		}
	}

	/**
	 * isRefresh提交数据
	 * */
	private void postData(String pidString, String content) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.RELATION_LOG_CONTENT, content);
		params.put(httpUtil.RELATION_LOG_PID_STRING, pidString);
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.RECODE_RELATION_LOG, params,
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
							if (httpUtil.errCode_success.equals(errcode)) {
								hideProgress();
								showToast("日志提交成功");
								setResult(102);
								finish();
								// if (JumpFlag) {
								animDown();
								// } else
								// animOut();
							} else {
								hideProgress();
								showToast("日志提交失败");
							}

						} catch (Exception e) {
							hideProgress();
							e.printStackTrace();
							showToast("日志提交失败");
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToastHttp(recodeRelationLog.this,
								errorNo);
					}
				});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			// if (JumpFlag) {
			animDown();
			// } else
			// animOut();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
