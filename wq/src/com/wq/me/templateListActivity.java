package com.wq.me;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.loginActivity;
import com.wq.Adapter.templateListAdapter;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.templateModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

public class templateListActivity extends BaseActivity {
	@ViewInject(id = R.id.grid_main)
	GridView grid_main;
	ArrayList<templateModel> list = new ArrayList<templateModel>();
	// 没数据显示界面
	@ViewInject(id = R.id.no_data_layout)
	LinearLayout noData_layout;
	@ViewInject(id = R.id.no_data_txt_title)
	TextView txt_no_data;
	@ViewInject(id = R.id.no_data_btn)
	Button btn_no_data;
	templateListAdapter adapter;

	// templateListAdapter
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		adapter = new templateListAdapter(this, list);
		grid_main.setAdapter(adapter);
		httpData();
		grid_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/*
				 * 
				 * if (list.get(position).equals("1")) {
				 * showToast(R.string.me_template_warn); return; } Bundle b =
				 * new Bundle(); b.putSerializable("tempModel",
				 * list.get(position)); b.putInt("position", position);
				 * changeViewForResult(templateDetailActivity.class, b,
				 * templateDetailActivity.RESULT_FLAG);
				 */

			}
		});
	}

	private void initnavigation() {
		initNavitation(getString(R.string.me_string_wqmb), "", -1,
				new EditClickListener() {

					@Override
					public void editClick() {
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

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.TEMPLATE_LIST_URL, params,
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
								JSONArray templateArr = jsonResult
										.getJSONArray("templateArray");
								for (int i = 0; i < templateArr.length(); i++) {
									JSONObject o = templateArr.getJSONObject(i);
									templateModel item = new templateModel();
									item.setTemplateId(o
											.getString("templateId"));
									item.setName(o.getString("name"));
									item.setPreviewImg(o
											.getString("previewImg"));
									item.setResourceInfo(o
											.getString("resourceInfo"));
									item.setStylist(o.getString("stylist"));
									item.setMakeTime(o.getString("makeTime"));
									item.setSpace(o.getString("space"));
									item.setVersion(o.getString("version"));
									item.setIsCertification(o
											.getString("isCertification"));
									item.setIsValid(o.getString("isValid"));
									item.setFrequency(o.getString("frequency"));
									JSONArray picArr = o
											.getJSONArray("resourceImgArray");
									for (int j = 0; j < picArr.length(); j++) {
										JSONObject picObject = picArr
												.getJSONObject(j);
										photoModel p = new photoModel();
										// p.setId(picObject.getString("imgId"));
										p.setImageUrl(picObject
												.getString("imgUrl"));
										item.getPicList().add(p);
									}
									list.add(item);

								}

								if (list.size() > 0) {
									grid_main.setVisibility(View.VISIBLE);
									noData_layout.setVisibility(View.GONE);
									adapter.notifyDataSetChanged();
								}

								else {
									noData(0);
								}
							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								noData(0);
							} else {
								noData(1);
								CommonUtil.showToast(templateListActivity.this,
										errMsg);
							}
						} catch (JSONException e) {

							noData(1);
							CommonUtil.showToast(templateListActivity.this,
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
						// TODO Auto-generated method stub
						hideProgress();
						// Log.i("fail", strMsg);
						noData(1);
						CommonUtil.showToastHttp(templateListActivity.this,
								errorNo);
					}
				});
	}

	// 没数据
	/** flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载 */
	private void noData(int flag) {
		if (list.size() <= 0) {
			noData_layout.setVisibility(View.VISIBLE);
			grid_main.setVisibility(View.GONE);
			btn_no_data.setVisibility(View.GONE);
		}
		if (flag == 0) {
			txt_no_data.setText(String.format(
					getString(R.string.string_no_data_txt_add),
					getString(R.string.me_string_wqmb)));
		} else {
			btn_no_data.setVisibility(View.VISIBLE);
			txt_no_data.setText(R.string.string_no_data_txt_rep);
			btn_no_data.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					httpData();
				}
			});

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == templateDetailActivity.RESULT_FLAG) {
			if (data == null)
				return;
			int position = data.getIntExtra("position", -1);
			if (position >= 0) {
				// 更新修改的模板

				templateModel item = (templateModel) data
						.getSerializableExtra("tempModel");
				User.templateId = item.getTemplateId();

				sharedPreferenceUtil.saveCompany(templateListActivity.this);
				LoggerUtil.i(position + ",," + item.getTemplateId() + ","
						+ User.templateId);
				list.set(position, item);
				adapter.notifyDataSetChanged();
			}

		}

	}
}
