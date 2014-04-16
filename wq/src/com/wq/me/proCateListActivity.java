package com.wq.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;
import com.wq.Adapter.proCateListAdapter;
import com.wq.UI.CustomAlertDialog;
import com.wq.UI.InScrolllistView;
import com.wq.model.User;
import com.wq.model.productCategory;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.httpUtil;

public class proCateListActivity extends BaseActivity {
	@ViewInject(id = R.id.cate_list)
	InScrolllistView listview;

	@ViewInject(id = R.id.layout_main)
	ScrollView layout_main;
	// 没数据显示界面
	@ViewInject(id = R.id.no_data_layout)
	LinearLayout noData_layout;
	@ViewInject(id = R.id.no_data_txt_title)
	TextView txt_no_data;
	@ViewInject(id = R.id.no_data_btn)
	Button btn_no_data;
	@ViewInject(id = R.id.layout_add_cate)
	LinearLayout me_layout_add_cate;

	private proCateListAdapter adapter;
	ArrayList<productCategory> list = new ArrayList<productCategory>();
	productCategory proCate = null;
	private String cateId = "-1";
	private String name = "";
	Bundle bundle = null;
	public static boolean IsEdit = false;
	public static boolean cateDelFlag = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pro_cate_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavigation();
		initData();
		initLinstener();

	}

	private void initData() {
		cateDelFlag = false;
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			cateId = bundle.getString("id");
			name = bundle.getString("name");
		}
		adapter = new proCateListAdapter(list, this,
				new proCateListAdapter.cateDelClick() {

					@Override
					public void click(final int position) {
						if (list.get(position).getCount() > 0) {
							DialogUtils
									.showDialog(
											proCateListActivity.this,
											getString(R.string.me_string_product_del_cate),
											String.format(
													getString(R.string.me_string_del_pro_message),
													list.get(position)
															.getCount() + ""),
											getString(R.string.string_dialog_qd),
											new DialogInterface.OnClickListener() {

												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													// TODO Auto-generated
													// method stub
													delCommit(position);
												}

											},
											getString(R.string.string_cacel),
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													dialog.dismiss();
												}
											}).show();

						} else
							delCommit(position);
						// TODO Auto-generated method
						// stub

					}
				});
		listview.setAdapter(adapter);
		setViewTouchListener(listview);
		httpData();

	}

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.PRODUCT_CATEGORY_LIST_URL, params,
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
								JSONArray array = jsonResult
										.getJSONArray("commType");
								for (int i = 0; i < array.length(); i++) {
									JSONObject object = array.getJSONObject(i);
									proCate = new productCategory();
									proCate.setId(object.getString("id"));
									proCate.setName(object.getString("name"));
									proCate.setCount(Integer.parseInt(object
											.getString("count")));
									list.add(proCate);
									if (proCate.getName().equals(name)) {
										proCateListAdapter.index = i;
									}
								}
								adapter.notifyDataSetChanged();
								noData_layout.setVisibility(View.GONE);
								layout_main.setVisibility(View.VISIBLE);
							} else if (errcode.equals(httpUtil.errCode_nodata)) {

								noData(0);

							} else {
								noData(1);
							}
							// CommonUtil.showToast(proCateListActivity.this,
							// errMsg);
						} catch (JSONException e) {
							noData(1);
							CommonUtil.showToast(proCateListActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							hideProgress();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						noData(1);
						// TODO Auto-generated method stub
						hideProgress();
						Log.i("fail", strMsg);
						CommonUtil.showToast(proCateListActivity.this,
								R.string.string_http_err_failure);
					}
				});
	}

	private void delCommit(final int position) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.TYPE_ID, list.get(position).getId());
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.PRODUCT_CATEGORY_DEL_URL, params,
				new AjaxCallBack<String>() {

					private String errcode = "";
					String errMsg = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								if (list.get(position).getCount() > 0) {
									cateDelFlag = true;
								}
								list.remove(position);
								adapter.notifyDataSetChanged();
								if (list.size() == 0) {
									noData(0);
								}
							}
							CommonUtil.showToast(proCateListActivity.this,
									errMsg);
						} catch (JSONException e) {
							// noData(1);
							CommonUtil.showToast(proCateListActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							hideProgress();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// noData(1);
						// TODO Auto-generated method stub
						hideProgress();
						Log.i("fail", strMsg);
						CommonUtil.showToastHttp(proCateListActivity.this,
								errorNo);
					}
				});
	}

	private void initLinstener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (IsEdit)
					return;
				Intent intent = new Intent();
				bundle = new Bundle();

				bundle.putString("id", list.get(position).getId());
				bundle.putString("name", list.get(position).getName());
				intent.putExtras(bundle);

				// TODO Auto-generated method stub
				proCateListActivity.this.setResult(102, intent);
				finish();
				// TODO Auto-generated method stub
			}
		});
		me_layout_add_cate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewForResult(proCateAddActivity.class,
						CommonUtil.PRO_CATE_ADD_RESULT_FLAG, 1);
				// TODO Auto-generated method stub

			}
		});

	}

	private void initNavigation() {
		initNavigation(IsEdit ? R.drawable.title_btn_right_click
				: R.drawable.title_btn_back_click,
				IsEdit ? getString(R.string.string_cacel)
						: getString(R.string.string_back),
				getString(R.string.me_string_product_add_cate), "",
				IsEdit ? getString(R.string.string_complete)
						: getString(R.string.string_edit),
				IsEdit ? R.drawable.btn_green_clickbg
						: R.drawable.title_btn_right_click, null,
				new EditClickListener() {
					@Override
					public void editClick() {
						IsEdit = !IsEdit;

						adapter.notifyDataSetChanged();
						initNavigation();
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						IsEdit = false;
						cateDelFlag = false;
						// TODO Auto-generated method stub
						if (cateDelFlag) {
							Intent intent = new Intent();
							bundle = new Bundle();
							bundle.putString("id", "-1");
							bundle.putString(
									"name",
									getString(R.string.me_string_product_no_cate));
							intent.putExtras(bundle);
							// TODO Auto-generated method stub
							proCateListActivity.this.setResult(
									CommonUtil.PRO_CATE_RESULT_FLAG, intent);

						}
						finish();
						animOut();
					}
				});

	}

	// 没数据
	// flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载
	private void noData(int flag) {
		if (list.size() <= 0) {
			noData_layout.setVisibility(View.VISIBLE);
			layout_main.setVisibility(View.GONE);
		}
		if (flag == 0) {
			txt_no_data.setText(String.format(
					getString(R.string.string_no_data_txt_add),
					getString(R.string.me_string_product_select_cate)));
			btn_no_data.setText(R.string.string_no_data_btn_add);
			btn_no_data.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					changeViewForResult(proCateAddActivity.class,
							CommonUtil.PRO_CATE_ADD_RESULT_FLAG);
					// TODO Auto-generated method stub
				}
			});
		} else {
			txt_no_data.setText(R.string.string_no_data_txt_rep);
			btn_no_data.setText(R.string.string_no_data_btn_rep);
			btn_no_data.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					httpData();
					// TODO Auto-generated method stub

				}
			});
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		{
			super.onActivityResult(requestCode, resultCode, data);
			if (data != null) {
				// 添加分类
				if (resultCode == CommonUtil.PRO_CATE_ADD_RESULT_FLAG) {
					String name = data.getStringExtra("name");
					String id = data.getStringExtra("id");
					if (list.size() == 0) {
						noData_layout.setVisibility(View.GONE);
						layout_main.setVisibility(View.VISIBLE);
					}
					proCate = new productCategory();
					proCate.setId(id);
					proCate.setName(name);
					proCate.setCount(0);
					list.add(proCate);
					adapter.notifyDataSetChanged();

				}

			}
		}
	}

	public void onDestory() {
		super.onDestory();
		IsEdit = false;
		cateDelFlag = false;
	}
}
