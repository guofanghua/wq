package com.wq.me;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.Adapter.retationLogAdapter;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class WorkRepAddActivity extends BaseActivity {

	String[] typeArr = null;
	String[] titleArr = null;
	int type = 0;

	// 控件
	// 时间
	@ViewInject(id = R.id.txt_time)
	TextView txt_time;
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
	@ViewInject(id = R.id.txt_pyr_name)
	TextView txt_pyr_name;
	partComModel shrModel = new partComModel();
	ArrayList<partComModel> listModel = new ArrayList<partComModel>();
	FinalBitmap finalBitmap;
	retationLogAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_rep_add_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		initData();
		initnavigation();
		initUI();
	}

	private void initData() {
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

		typeArr = getResources().getStringArray(R.array.work_rep_add_title_arr);
		type = this.getIntent().getIntExtra("type", 0);
		if (type == 0) {
			titleArr = getResources().getStringArray(
					R.array.me_collect_day_title);
		} else {
			titleArr = getResources().getStringArray(
					R.array.me_collect_week_title);
		}

	}

	private void initUI() {
		// 时间
		if (type == 0) {
			txt_time.setText(dateUtil.formatDate1(new Date()));
		} else {
			txt_time.setText(dateUtil.formatDate1(new Date()) + "至"
					+ dateUtil.formatDateAdd(new Date(), 7));
		}
		txt_now_title.setText(titleArr[0]);
		txt_next_title.setText(titleArr[1]);

	}

	private void initnavigation() {
		initNavitation(typeArr[type], getString(R.string.string_tj),
				R.drawable.btn_green_clickbg, new EditClickListener() {
					@Override
					public void editClick() {
						// changeViewForResult(recodeRelationLog.class, 102, 1);
						if (checkFrom()) {
							postData();
						}
					}

					@Override
					public void backClick() {
						finish();
						animOut();
					}
				});

	}

	public void xmlOnclick(View v) {
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		// 时间
		case R.id.rela_time:

			break;
		// 当前工作
		case R.id.rela_now_work:
			txt_now_title.setTextColor(Color.BLACK);
			editActivity.edit_backcontent = txt_now;
			bundle.putString("title", titleArr[0]);
			// bundle.putString("limitWord", "12");
			changeView(editActivity.class, bundle, true);
			break;
		// 下一步计划
		case R.id.rela_next_work:
			editActivity.edit_backcontent = txt_next;
			bundle.putString("title", titleArr[1]);
			// bundle.putString("limitWord", "12");
			changeView(editActivity.class, bundle, true);
			break;
		// 批阅人
		case R.id.rela_pyr:
			txt_pyrTextView.setTextColor(Color.BLACK);
			bundle.putInt("Num", 1);
			changeViewForResult(card_relation_select_people_activity.class,
					bundle, 100, 1);
			break;
		// 抄送人
		case R.id.rela_csr:

			bundle.putSerializable("result", listModel);
			changeViewForResult(card_relation_select_people_activity.class,
					bundle, 101, 1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {

			if (requestCode == 100 && resultCode == 102) {

				shrModel = ((ArrayList<partComModel>) data
						.getSerializableExtra("result")).get(0);
				if (shrModel != null) {
					txt_pyr_name.setText(shrModel.getContactName());
					finalBitmap
							.display(img_pyr, shrModel.getIcon(), null, true);
				}

			} else if (requestCode == 101 && resultCode == 102) {
				listModel = ((ArrayList<partComModel>) data
						.getSerializableExtra("result"));
			
				if (listModel == null || listModel.size() <= 0) {
					listView.setVisibility(View.GONE);
				} else {
					listView.setVisibility(View.VISIBLE);

					adapter = new retationLogAdapter(WorkRepAddActivity.this,
							listModel);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub

						}
					});
				}
			}
		}
	}

	private boolean checkFrom() {
		// 今日工作总结
		if (TextUtils.isEmpty(txt_now.getText())) {
			txt_now_title.setTextColor(Color.RED);
			return false;
		} else if (TextUtils.isEmpty(shrModel.getEnterpriseId())) {
			txt_pyrTextView.setTextColor(Color.RED);
			return false;
		} else
			return true;
	}

	/*
	 * isRefresh提交数据
	 */
	private void postData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.WORK_ADD_TIME, txt_time.getText().toString());
		params.put(httpUtil.WORK_ADD_TYPE, type + "");
		params.put(httpUtil.WORK_ADD_SUMMARY1, txt_now.getText().toString());
		params.put(httpUtil.WORK_ADD_SUMMARY2, txt_next.getText().toString());
		params.put(httpUtil.WORK_ADD_MARK_ID, shrModel.getEnterpriseId());
		params.put(httpUtil.WORK_ADD_MARK_CC, GetCSRId());
		showProgress(R.string.dialog_comitting);
		httpUtil.post1(httpUtil.WORK_REP_ADD_URL, params,
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

								showRightDialog(R.string.me_string_commit_success_warn);
								// } else
								// animOut();
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
						CommonUtil.showToastHttp(WorkRepAddActivity.this,
								errorNo);
					}
				});

	}

	private String GetCSRId() {
		StringBuilder sbBuilder = new StringBuilder();
		for (int i = 0; i < listModel.size(); i++) {
			sbBuilder.append(listModel.get(i).getEnterpriseId());
			if (i < listModel.size() - 1)
				sbBuilder.append("|");
		}
		return sbBuilder.toString();
	}
}
