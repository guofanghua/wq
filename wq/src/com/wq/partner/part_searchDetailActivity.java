package com.wq.partner;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.partSearchListAdapter;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class part_searchDetailActivity extends BaseActivity implements
		IXListViewListener {
	@ViewInject(id = R.id.part_search_list_main)
	XListView listview;
	@ViewInject(id = R.id.part_search_txt_title)
	TextView txt_title;
	@ViewInject(id = R.id.edit_search)
	EditText edit_search;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	ArrayList<partComModel> list = new ArrayList<partComModel>();
	private String title = "";
	private int searchType = 0;
	partSearchListAdapter adapter;
	String channel = "0";// 渠道号

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.part_search_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		Intent intent = this.getIntent();

		if (!TextUtils.isEmpty(intent.getStringExtra("title"))) {
			title = intent.getStringExtra("title");
			channel = intent.getStringExtra("channel");// 渠道号
			searchType = intent.getIntExtra("searchType", -1);
			txt_title.setText(title);
			edit_search.setHint(String.format(
					getString(R.string.part_string_search_hint),
					title.substring(1)));
		}
		listview.setPullRefreshEnable(false);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		adapter = new partSearchListAdapter(part_searchDetailActivity.this,
				list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(part_searchDetailActivity.this,
						part_DetailActivity.class);
				intent.putExtra("id", list.get(position - 1).getEnterpriseId());
				intent.putExtra("channel", channel);
				startActivity(intent);
				animIn();
			}
		});

		initNavigation();
		setViewTouchListener(layout_main);

	}

	/**
	 * isRefresh 是否刷新
	 * */
	private void httpData(final boolean isRefresh) {
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
								if (isRefresh)
									list.clear();
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
									model.setIsAttention(object
											.getString("isAttention"));
									model.setVqh(object.getString("wqh"));
									model.setContactName(object
											.getString("contactName"));
									model.setOccupation(object
											.getString("occupation"));
									model.setFlag("2");
									list.add(model);
								}
								if (list.size() <= 0) {
									listview.setVisibility(View.GONE);
								} else {
									listview.setVisibility(View.VISIBLE);
									adapter.notifyDataSetChanged();
								}

							} else
								CommonUtil.showToast(getApplicationContext(),
										msg);

						} catch (JSONException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
							CommonUtil.showToast(
									part_searchDetailActivity.this,
									R.string.string_http_err_data);
						} finally {
							hideProgress();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						hideProgress();

						CommonUtil.showToastHttp(
								part_searchDetailActivity.this, errorNo);
					}
				});
	}

	private void initNavigation() {
		initNavitation(title, getString(R.string.part_string_search_btn_title),
				R.drawable.btn_style_green, new EditClickListener() {

					@Override
					public void editClick() {
						// if(!TextUtils.isEmpty(edit_search.getText().toString()))
						httpData(true);
						// else
						// CommonUtil.showToast(part_searchDetailActivity.this,
						// R.string.string_http_err_failure);
						// TODO Auto-generated method stub

					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});
	}

	private class handler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// 添加关注
			if (msg.what == 1) {

			}
			// 取消关注
			else if (msg.what == 2) {

			}
		}

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
}
