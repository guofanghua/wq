package com.wq;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.endure.wq.R;
import com.wq.Adapter.partSearchListAdapter;
import com.wq.Adapter.registerPartSearchListAdapter;
import com.wq.BaseActivity.EditClickListener;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.partner.part_DetailActivity;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class register_searchComActivity extends BaseActivity {
	@ViewInject(id = R.id.list_main)
	ListView listview;
	@ViewInject(id = R.id.part_search_txt_title)
	TextView txt_title;
	@ViewInject(id = R.id.edit_search)
	EditText edit_search;
	@ViewInject(id = R.id.layout_search)
	LinearLayout layout_search;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.top_btn_edit)
	Button btn_edit;
	@ViewInject(id = R.id.txt_tip)
	TextView txt_tip;
	@ViewInject(id = R.id.btn_search)
	Button btn_search;
	@ViewInject(id = R.id.top_txt_title)
	TextView txt_top_title;
	ArrayList<partComModel> list = new ArrayList<partComModel>();
	private int searchType = 1;
	registerPartSearchListAdapter adapter;
	String channel = "1";// 渠道号
	int checkCount = 0;// 勾选的人数

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_search_com_activity);
		BaseApplication.getInstance().addActivity(this);
		adapter = new registerPartSearchListAdapter(
				register_searchComActivity.this, list, handler);
		listview.setAdapter(adapter);
		initlistener();
	}

	/**
	 * isRefresh 是否刷新
	 * */
	private void httpData() {
		txt_top_title.setText(edit_search.getText().toString());
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.PART_TYPE, searchType + "");
		params.put(httpUtil.PART_SEARCH_STR, edit_search.getText().toString());
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.PART_SEARCH_URL, params,
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
								JSONArray arr = jsonResult
										.getJSONArray("searchArray");

								list.clear();
								if (arr.length() > 0) {
									layout_search.setVisibility(View.GONE);

								}
								// 跳转到主页
								else {
									Bundle b = new Bundle();
									b.putBoolean("registerFlag", true);
									changeView(mainActivity.class, b);
									finish();
								}
								for (int i = 0; i < arr.length(); i++) {
									JSONObject object = arr.getJSONObject(i);
									partComModel model = new partComModel();
									// model.setId(object.getString("id"));
									model.setEnterpriseId(object
											.getString("enterpriseId"));
									model.setIcon(object.getString("icon"));
									model.setName(object.getString("name"));
									model.setCommodity(object
											.getString("commodity"));
									model.setIsAttention("1");
									model.setVqh(object.getString("wqh"));
									model.setContactName(object
											.getString("contactName"));
									model.setOccupation(object
											.getString("occupation"));

									list.add(model);
								}
								checkCount = list.size();
								if (list.size() <= 0) {
									txt_tip.setVisibility(View.GONE);
									listview.setVisibility(View.GONE);
								} else {
									txt_tip.setVisibility(View.VISIBLE);
									txt_tip.setText(list.size() + "");
									listview.setVisibility(View.VISIBLE);
									adapter.notifyDataSetChanged();
								}

							} else
								CommonUtil.showToast(getApplicationContext(),
										msg);

						} catch (JSONException e) {

							e.printStackTrace();

						} finally {
							hideProgress();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									edit_search.getWindowToken(), 0); // 强制隐藏键盘

						}

					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						hideProgress();

						// CommonUtil.showToastHttp(
						// register_searchComActivity.this, errorNo);
					}
				});
	}

	// 添加关注
	private void addHttpData(String ids) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_PRISE_ID_ARR, ids);
		params.put(httpUtil.ENTER_CHANNEL, "1");
		/*
		 * params.addQueryStringParameter(httpUtil.USER_ID, Company.id);
		 * params.addQueryStringParameter(httpUtil.USER_KEY, Company.userKey);
		 */

		httpUtil.post(httpUtil.PART_PL_GZ_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {
						try {
							result = result.trim();
							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {

							}

						} catch (JSONException e) {
							e.printStackTrace();
						} finally {

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub

						// CommonUtil.showToastHttp(
						// /register_searchComActivity.this, errorNo);
					}
				});

	}

	private void initlistener() {
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(
								register_searchComActivity.this
										.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		// 键盘的搜索按健监听
		edit_search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (TextUtils.isEmpty(edit_search.getText())) {
						showToast(R.string.register_com_edit_warn);
						return true;
					}

					// // 先隐藏键盘
					// ((InputMethodManager) edit_search.getContext()
					// .getSystemService(Context.INPUT_METHOD_SERVICE))
					// .hideSoftInputFromWindow(
					// register_searchComActivity.this
					// .getCurrentFocus().getWindowToken(),
					// InputMethodManager.HIDE_NOT_ALWAYS);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0); // 强制隐藏键盘

					httpData();
					return true;
				}
				return false;
			}
		});
		btn_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// httpData();
				Bundle b = new Bundle();
				b.putBoolean("registerFlag", true);
				if (checkCount == 0) {
					b.putBoolean("nodataFlag", true);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.sendToTarget();
				}
				changeView(mainActivity.class, b);

				// TODO Auto-generated method stub

			}
		});
		// 搜索
		btn_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(edit_search.getText())) {
					showToast(R.string.register_com_edit_warn);
					return;
				}

				// 先隐藏键盘
				// ((InputMethodManager) edit_search.getContext()
				// .getSystemService(Context.INPUT_METHOD_SERVICE))
				// .hideSoftInputFromWindow(
				// register_searchComActivity.this
				// .getCurrentFocus().getWindowToken(),
				// InputMethodManager.HIDE_NOT_ALWAYS);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0); // 强制隐藏键盘
				httpData();
			}
		});
		// list点击
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View parent,
					int position, long id) {
				partComModel item = list.get(position);
				CheckBox check = (CheckBox) parent.findViewById(R.id.check);

				// 去除勾选
				if (item.getIsAttention().equals("1")) {
					check.setChecked(false);
					item.setIsAttention("0");
					checkCount--;
				} else {
					check.setChecked(true);
					item.setIsAttention("1");
					checkCount++;
				}
				txt_tip.setText(checkCount + "");

				// if (checkCount == 0) {
				// txt_tip.setVisibility(View.GONE);
				// btn_edit.setText(R.string.string_tg);
				// btn_edit.setBackgroundDrawable(null);
				// } else {
				// txt_tip.setVisibility(View.VISIBLE);
				// txt_tip.setText(checkCount + "");
				// btn_edit.setText(R.string.string_complete);
				// btn_edit.setBackgroundResource(R.drawable.btn_green_clickbg);
				// }
				list.set(position, item);

			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// 批量关注
			if (msg.what == 1) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < list.size(); i++) {
					partComModel item = list.get(i);
					if (item.getIsAttention().equals("1")) {
						sb.append(item.getEnterpriseId());
						sb.append("|");
					}
				}
				if (!TextUtils.isEmpty(sb.toString())) {
					// 判断并存储

					addHttpData(sb.toString().substring(0,
							sb.toString().length() - 1));
				}
			}
			// 勾选或者去除勾选
			else if (msg.what == 2) {
				// 去除勾选
				if (list.get(msg.arg1).getIsAttention().equals("1")) {
					list.get(msg.arg1).setIsAttention("0");
					checkCount--;
				} else {
					list.get(msg.arg1).setIsAttention("1");
					checkCount++;
				}
				if (checkCount == 0) {
					txt_tip.setVisibility(View.GONE);

				} else {
					txt_tip.setVisibility(View.VISIBLE);
					txt_tip.setText(checkCount + "");
				}

			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
