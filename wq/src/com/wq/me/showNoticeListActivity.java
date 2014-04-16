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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;
import com.wq.Adapter.showNoticeListAdapter;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;

public class showNoticeListActivity extends BaseActivity implements
		IXListViewListener {
	String enterpriseId = "";
	String enterpriseWqh = "";
	@ViewInject(id = R.id.show_notice_list)
	XListView listview;
	showNoticeListAdapter adapter;
	private ArrayList<notice> list = new ArrayList<notice>();
	// 没数据显示界面
	@ViewInject(id = R.id.no_data_layout)
	LinearLayout noData_layout;
	@ViewInject(id = R.id.no_data_txt_title)
	TextView txt_no_data;
	@ViewInject(id = R.id.no_data_btn)
	Button btn_no_data;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_notice_list_activity);
		BaseApplication.getInstance().addActivity(this);

		initData();
	}

	private void initData() {

		Intent intent = this.getIntent();
		enterpriseId = intent.getStringExtra("enterpriseId");
		enterpriseWqh = intent.getStringExtra("enterpriseWqh");
		initnavigation();
		adapter = new showNoticeListAdapter(list, this, null);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		listview.setAdapter(adapter);
		setViewTouchListener(listview);
		httpData();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Bundle b = new Bundle();
				b.putSerializable("noticeModel", list.get(position - 1));
				changeView(showNoticeDetailActivity.class, b);
				// TODO Auto-generated method stub

			}
		});

	}

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		if (TextUtils.isEmpty(enterpriseId)) {
			params.put(httpUtil.ENTER_PRISE_ID, User.id);
		} else {
			params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		}
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.ENTER_PROMOTIONS_URL, params,
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
								JSONArray arr = jsonResult
										.getJSONArray("promotions");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject object = arr.getJSONObject(i);
									notice item = new notice();
									item.setTime(object.getString("time"));
									item.setId(object.getString("id"));
									item.setTitle(object.getString("title"));
									item.setContent(object.getString("content"));
									JSONArray picArr = object
											.getJSONArray("imgFileArray");
									for (int j = 0; j < picArr.length(); j++) {
										JSONObject picObj = picArr
												.getJSONObject(j);
										photoModel pic = new photoModel();
										pic.setFlag(0);
										pic.setId(picObj.getString("imgId"));
										pic.setImageUrl(picObj
												.getString("imgUrl"));

										pic.setShareModel(item);
										pic.setShareType(photoModel.NOTICE_SHARE_FLAG);
										item.getPic().add(pic);
									}
									list.add(item);

								}
								if (list.size() > 0) {
									listview.setVisibility(View.VISIBLE);
									noData_layout.setVisibility(View.GONE);
									adapter.notifyDataSetChanged();
								} else {
									noData(0);
								}
							} else if (errcode.equals(httpUtil.errCode_nodata)) {

								noData(0);

							} else {
								noData(1);
								CommonUtil.showToast(
										showNoticeListActivity.this, errMsg);
							}
						} catch (JSONException e) {
							noData(1);
							CommonUtil.showToast(showNoticeListActivity.this,
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
						CommonUtil.showToastHttp(showNoticeListActivity.this,
								errorNo);
					}
				});
	}

	// 没数据
	/** flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载 */
	private void noData(int flag) {
		if (list.size() <= 0) {
			noData_layout.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}
		if (flag == 0) {
			txt_no_data.setText(String.format(
					getString(R.string.string_no_data_txt_add),
					getString(R.string.me_string_wqdt)));
			btn_no_data.setVisibility(View.GONE);
		} else {
			txt_no_data.setText(R.string.string_no_data_txt_rep);
			btn_no_data.setText(R.string.string_no_data_btn_rep);
			btn_no_data.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					initData();
					// TODO Auto-generated method stub

				}
			});
		}

	}

	private void initnavigation() {
		initNavitation(getString(R.string.me_string_wqdt), enterpriseWqh, "",
				-1, new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub

					}

					@Override
					public void backClick() {
						finish();
						animOut();
						// TODO Auto-generated method stub
					}
				});
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
