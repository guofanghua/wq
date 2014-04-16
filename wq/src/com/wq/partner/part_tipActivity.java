package com.wq.partner;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.partListAdapter;
import com.wq.Adapter.partSortAdapter;
import com.wq.Adapter.partTipSortAdapter;
import com.wq.UI.CustomProgressDialog;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/**
 * 显示主动加我为伙伴的企业列表
 * 
 * */

public class part_tipActivity extends BaseActivity implements
		IXListViewListener {
	@ViewInject(id = R.id.part_tip_list_main)
	XListView listview;
	ArrayList<partComModel> listModel = new ArrayList<partComModel>();
	partTipSortAdapter adapter;

	private final int PAGESIZE = 20;
	private int currPage = 0;// 当前页
	FinalDb db;

	// 无数据
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.part_tip_activity);
		BaseApplication.getInstance().addActivity(this);
		sharedPreferenceUtil.saveTipPart(this, "0");
		db = FinalDb.create(this);
		initNavigation();
		initUI();

	}

	private void initUI() {

		if (db.checkmyTableExist(partComModel.class)) {
			try {
				listModel = (ArrayList<partComModel>) db.findAllByWhere(
						partComModel.class, "flag='1'   and userId='" + User.id
								+ "' ", "time desc");

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				onLoad(listview);
			}
		}
		if (listModel == null || listModel.size() == 0) {
			listModel = new ArrayList<partComModel>();
			listview.setPullRefreshEnable(true);
			listview.setXListViewListener(this);
			listview.startRefresh(this);
		} else
			listview.setPullRefreshEnable(false);
		listview.setPullLoadEnable(false);
		adapter = new partTipSortAdapter(this, listModel);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putString("id", listModel.get(position - 1).getEnterpriseId());
				b.putString("channel", "7");
				changeView(part_DetailActivity.class, b);
			}
		});
	}

	/**
	 * isRefresh 是否 刷新
	 * */
	int allCount = 0;

	private void httpData(final boolean isRefresh) {
		allCount = 0;
		if (isRefresh)
			currPage = 0;
		// showProgress(R.string.dialog_loading);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.CUR_PAGE, currPage + "");
		httpUtil.post(httpUtil.PART_TIP_LIST_URL, params,
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
							listModel.clear();
							if (errcode.equals(httpUtil.errCode_success)) {
								JSONArray arr = jsonResult.getJSONArray("data");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject o = arr.getJSONObject(i);
									partComModel item = new partComModel();
									item.setPid(o.getString("id"));
									item.setEnterpriseId(o
											.getString("enterpriseId"));
									item.setIcon(o.getString("icon"));
									item.setName(o.getString("name"));
									item.setTelePhone(o.getString("telePhone"));
									item.setMobile(o.getString("moblie"));
									item.setContactName(o
											.getString("contactName"));
									item.setOccupation(o
											.getString("occupation"));
									item.setTime(dateUtil
											.formatDate(new Date()));
									item.setFlag("1");
									CheckModel(item);
								}
								adapter.notifyDataSetChanged();
							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								CommonUtil.showToast(part_tipActivity.this,
										errMsg);
							} else {
								// noData(1);
								CommonUtil.showToast(part_tipActivity.this,
										errMsg);
							}

						} catch (JSONException e) {

							// noData(1);
							CommonUtil.showToast(part_tipActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							// progressDialog.cancel();
							onLoad(listview);
						}

						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						onLoad(listview);
						CommonUtil
								.showToastHttp(part_tipActivity.this, errorNo);
					}
				});

	}

	private void initNavigation() {
		initNavitation(getString(R.string.part_string_new_part), "", -1,
				new EditClickListener() {

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

		httpData(true);
	}

	@Override
	public void onLoadMore() {
		httpData(false);
		// TODO Auto-generated method stub

	}

	// 遍历数据库，看此条记录是否存在本地

	private void CheckModel(partComModel model) {
		boolean flag = false;
		int position = 0;
		for (int i = 0; i < listModel.size(); i++) {
			if (listModel.get(i).getEnterpriseId()
					.equals(model.getEnterpriseId())) {
				flag = true;
				position = i;
				break;
			}

		}
		if (flag) {
			listModel.set(position, model);
			try {
				db.update(model);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			listModel.add(model);
			try {
				db.save(model);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
	// 没数据
	// flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载

}
