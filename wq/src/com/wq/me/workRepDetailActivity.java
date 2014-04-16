package com.wq.me;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.Adapter.retationLogAdapter;
import com.wq.BaseActivity.EditClickListener;
import com.wq.model.User;
import com.wq.model.myObject;
import com.wq.model.partComModel;
import com.wq.model.workCSModel;
import com.wq.model.workRepModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class workRepDetailActivity extends BaseActivity {

	String[] titleArr = null;
	String[] typeArr;
	String[] statusArr;
	int type = 0;

	// 时间
	@ViewInject(id = R.id.txt_time)
	TextView txt_time;
	@ViewInject(id = R.id.img_tj_logo)
	ImageView img_tj_logo;
	@ViewInject(id = R.id.txt_tj_name)
	TextView txt_tj_name;
	@ViewInject(id = R.id.txt_tj_ocp)
	TextView txt_tj_ocp;
	@ViewInject(id = R.id.txt_type)
	TextView txt_type;
	@ViewInject(id = R.id.txt_status)
	TextView txt_staus;
	//
	@ViewInject(id = R.id.txt_now_title_work)
	TextView txt_now_title;
	@ViewInject(id = R.id.txt_now_work)
	TextView txt_now;
	@ViewInject(id = R.id.txt_next_title_work)
	TextView txt_next_title;
	@ViewInject(id = R.id.txt_next_work)
	TextView txt_next;
	@ViewInject(id = R.id.txt_pyr_title)
	TextView txt_pyrTextView;
	@ViewInject(id = R.id.img_pyr)
	ImageView img_pyr;
	@ViewInject(id = R.id.grid_container)
	CustomListView listView;
	@ViewInject(id = R.id.txt_name)
	TextView txt_name;
	@ViewInject(id = R.id.txt_ocp)
	TextView txt_ocp;
	@ViewInject(id = R.id.rela_py_suggest)
	RelativeLayout rela_reguesst;
	@ViewInject(id = R.id.txt_py_suggest)
	TextView txt_suggest;
	partComModel shrModel = new partComModel();
	ArrayList<partComModel> listModel = new ArrayList<partComModel>();
	FinalBitmap finalBitmap;
	retationLogAdapter adapter;
	workRepModel model = new workRepModel();
	private String workId = "";
	BitmapDisplayConfig config = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_rep_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		this.config = new BitmapDisplayConfig();
		this.config.setBitmapHeight(DensityUtil.dip2px(this, 90));
		this.config.setBitmapWidth(DensityUtil.dip2px(this, 90));
		initData();
		// initnavigation();

	}

	private void initData() {
		typeArr = getResources().getStringArray(R.array.work_rep_arr);
		statusArr = getResources().getStringArray(R.array.work_rep_status);
		if (listModel == null || listModel.size() <= 0) {
			listView.setVisibility(View.GONE);
		} else {
			listView.setVisibility(View.VISIBLE);
		}
		adapter = new retationLogAdapter(this, listModel);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});

		workId = this.getIntent().getStringExtra("workId");
		if (!TextUtils.isEmpty(workId)) {
			GetHttpData();
		}

	}

	private void bindUi() {
		// 时间
		finalBitmap.display(img_tj_logo, model.getIcon(), config, true);
		txt_tj_name.setText(model.getName());
		txt_tj_ocp.setText(model.getOccupation());
		if (model.getStatus().equals("0")) {
			txt_staus.setText(statusArr[0]);
			txt_staus.setTextColor(Color.RED);
		} else if (model.getStatus().equals("1")) {
			txt_staus.setText(statusArr[1]);
			txt_staus.setTextColor(Color.GRAY);
		}
		
		type = Integer.parseInt(model.getType());
		if (type == 0) {

			titleArr = getResources().getStringArray(
					R.array.me_collect_day_title);
		} else {
			titleArr = getResources().getStringArray(
					R.array.me_collect_week_title);
		}
		if (type <= typeArr.length)
			txt_type.setText(typeArr[type]);
		txt_time.setText("[ " + model.getTime() + " ]");
		txt_now_title.setText(titleArr[0]);
		txt_next_title.setText(titleArr[1]);
		txt_now.setText(model.getWorkSummary1());
		txt_next.setText(model.getWorkSummary2());
		// 抄送人无法查看批阅建议
		if (model.getEnterpriseID().equals(User.id)
				|| (model.getCsList().size() > 0 && model.getCsList().get(0)
						.getId().equals(User.id))) {
			rela_reguesst.setVisibility(View.VISIBLE);
			txt_suggest.setText(model.getMarkingCountnent());
		} else
			rela_reguesst.setVisibility(View.GONE);
		if (model.getCsList().size() > 0) {
			txt_name.setText(model.getCsList().get(0).getName());
			txt_ocp.setText(model.getCsList().get(0).getOccupation());
			finalBitmap.display(img_pyr, model.getCsList().get(0).getIcon(),
					config, true);
		}
		for (int i = 1; i < model.getCsList().size(); i++) {
			partComModel itemModel = new partComModel();
			itemModel.setIcon(model.getCsList().get(i).getIcon());
			itemModel.setName(model.getCsList().get(i).getName());
			itemModel.setEnterpriseId(model.getCsList().get(i).getId());
			itemModel.setOccupation(model.getCsList().get(i).getOccupation());
			listModel.add(itemModel);
		}
		if (listModel == null || listModel.size() <= 0) {
			listView.setVisibility(View.GONE);
		} else {
			listView.setVisibility(View.VISIBLE);
		}
		adapter = new retationLogAdapter(this, listModel);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
		initnavigation();
	}

	private void initnavigation() {
		int bgId = -1;
		// .btn_green_clickbg,
		if (model.getCsList().size() > 0
				&& model.getCsList().get(0).getId().equals(User.id)
				&& model.getStatus().equals("0")) {

			bgId = 0;
		}
		initNavitation(getString(R.string.me_string_bgxq),
				getString(R.string.me_string_work_sp), bgId,
				new EditClickListener() {
					@Override
					public void editClick() {
						Bundle bundle = new Bundle();
						// changeViewForResult(recodeRelationLog.class, 102, 1);
						editActivity.edit_backcontent = txt_suggest;
						txt_suggest.setTag("1");
						bundle.putString(
								"title",
								getResources().getString(
										R.string.me_string_work_sp));
						// bundle.putString("limitWord", "12");
						changeView(editActivity.class, bundle, true);
					}

					@Override
					public void backClick() {
						finish();
						animOut();
					}
				});

	}

	/** 获取 提交詳情 */
	private void GetHttpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.WORK_DETAIL_ID, workId);
		showProgress(R.string.dialog_comitting);
		httpUtil.post1(httpUtil.WORK_REP_DETAIL_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonResult = new JSONObject(result);
							// errcode =
							// jsonResult.getString(httpUtil.ERR_CODE);
							// errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							// if (httpUtil.errCode_success.equals(errcode)) {
							hideProgress();
							model.setId(jsonResult.getString("id"));
							model.setTime(jsonResult.getString("time"));
							model.setWorkSummary1(jsonResult
									.getString("workSummary1"));
							model.setWorkSummary2(jsonResult
									.getString("workSummary2"));
							model.setType(jsonResult.getString("type"));
							model.setStatus(jsonResult.getString("status"));
							model.setMarkingCountnent(jsonResult
									.getString("markingContent"));
							model.setEnterpriseID(jsonResult
									.getString("enterpriseID"));
							model.setIcon(jsonResult.getString("icon"));
							model.setName(jsonResult.getString("name"));
							model.setOccupation(jsonResult
									.getString("occupation"));
							JSONArray array = jsonResult
									.getJSONArray("markingCcArray");

							for (int i = 0; i < array.length(); i++) {
								workCSModel item = new workCSModel();
								JSONObject o = array.getJSONObject(i);
								item.setIcon(o.getString("icon"));
								item.setId(o.getString("id"));
								item.setName(o.getString("name"));
								item.setOccupation(o.getString("occupation"));
								model.getCsList().add(item);
							}
							if (!TextUtils.isEmpty(model.getId()))
								bindUi();

						} catch (Exception e) {
							hideProgress();
							e.printStackTrace();

						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToastHttp(workRepDetailActivity.this,
								errorNo);
					}
				});

	}

	/*
	 * isRefresh提交数据
	 */
	private void postData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.WORK_PY_ID, model.getId());
		params.put(httpUtil.WORK_PY_CONTENT, txt_suggest.getText().toString());
		showProgress(R.string.dialog_comitting);
		httpUtil.post1(httpUtil.WORK_REP_PY_URL, params,
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
								Intent intent = new Intent();
								intent.putExtra("id", model.getId());
								setResult(200, intent);
								finish();
								animOut();
							} else {
								hideProgress();
								showToast(errMsg);
							}

						} catch (Exception e) {
							hideProgress();
							e.printStackTrace();
							showToast(R.string.me_string_commit_err_warn);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToastHttp(workRepDetailActivity.this,
								errorNo);
					}
				});

	}

	protected void onRestart() {
		super.onRestart();
		if (txt_suggest.getTag().toString().equals("1")
				&& model.getStatus().equals("0")
				&& !TextUtils.isEmpty(txt_suggest.getText().toString().trim())) {
			postData();
		}
	}
}
