package com.wq.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.mainActivity;
import com.wq.Adapter.partSortAdapter;
import com.wq.Adapter.workWDBGListAdapter;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.me.MingPListActivity;
import com.wq.me.WorkRepListActivity;
import com.wq.me.workRepDetailActivity;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.model.workRepModel;
import com.wq.partner.part_DetailActivity;
import com.wq.partner.part_searchActivity;
import com.wq.partner.part_tipActivity;
import com.wq.sortListView.StringHelper;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;

import com.wq.utils.httpUtil;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 我的报告
 * */
public class workWdbgFragment extends Fragment implements IXListViewListener {
	private XListView listview;
	ArrayList<workRepModel> listModel = new ArrayList<workRepModel>();
	workWDBGListAdapter adapter;

	// private ArrayList<partComModel> list = new ArrayList<partComModel>();
	public boolean isUpdate = false;// 用于标示，发送广播来时，是都需要加载数据

	public static final String BRO_ACTION_NAME = "com.wq.fragment.wdbg";

	public DataReceiver dataReceiver = null;
	private int curPage = 1;

	// 获取动态数据更新ui的handler

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isUpdate = false;
		View v = inflater.inflate(R.layout.work_wdbg_fragement, container,
				false);

		initUI(v);
		registerBoradcastReceiver();
		return v;
	}

	private void initUI(View v) {
		adapter = new workWDBGListAdapter(getActivity(), listModel);
		listview = (XListView) v.findViewById(R.id.list_main);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this);
		listview.startRefresh(getActivity());
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						workRepDetailActivity.class);
				intent.putExtra("workId", listModel.get(position - 1).getId());
				getActivity().startActivityForResult(intent, 100);
				animIn();

			}
		});

	}

	/**
	 * isRefresh 是否 刷新
	 * */
	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.WORK_LIST_TYPE, "0");
		params.put(httpUtil.WORK_LIST_PAGE, curPage + "");
		httpUtil.post1(httpUtil.WORK_REP_LIST_URL, params,
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
								isUpdate = false;
								JSONArray array = jsonResult
										.getJSONArray("data");
								listModel.clear();
								for (int i = 0; i < array.length(); i++) {
									workRepModel itemModel = new workRepModel();
									JSONObject object = array.getJSONObject(i);
									itemModel.setId(object.getString("id"));
									itemModel.setTime(object.getString("time"));
									itemModel.setType(object.getString("type"));
									itemModel.setStatus(object
											.getString("status"));
									itemModel.setIcon(object.getString("icon"));
									itemModel.setName(object.getString("name"));
									itemModel.setEnterpriseID(object
											.getString("enterpriseId"));
									listModel.add(itemModel);
								}
								adapter.notifyDataSetChanged();

							}

						} catch (JSONException e) {
							isUpdate = true;
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
						// noData(1);
						isUpdate = true;
						onLoad(listview);
						// TODO Auto-generated method stub
						// progressDialog.cancel();

					}
				});

	}

	public void animIn() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			getActivity().overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.transition_in);
		}
	}

	public void onResume() {
		super.onResume();
		registerBoradcastReceiver();
	}

	public void onStop() {
		super.onStop();
		unregisterBroadcaseReceiver();
	}

	public void onDestory() {
		super.onDestroy();
		unregisterBroadcaseReceiver();

	}

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(BRO_ACTION_NAME);
			
			getActivity().registerReceiver(dataReceiver, filter);
		}

	}

	// 注销广播
	public void unregisterBroadcaseReceiver() {
		if (dataReceiver != null) {
			getActivity().unregisterReceiver(dataReceiver);
			dataReceiver = null;
		}
	}

	private class DataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(BRO_ACTION_NAME)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					int type = bundle.getInt("type", -1);
					// 0 表示访问 1 表示添加 2 表示审核
					// if (isUpdate && type == 0) {
					// listview.startRefresh(getActivity());
					// } else
					if (type == 1) {

					} else if (type == 2) {

					}

				}
			} 
			// else if (action.equals(WorkRepListActivity.comm_bro_name)) {
			// String id = intent.getStringExtra("id");
			// for (workRepModel item : listModel) {
			// if (item.getId().equals(id)) {
			// item.setStatus("1");
			// break;
			// }
			// }
			// adapter.notifyDataSetChanged();
			// }
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		httpData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	/**
	 * 使用了XListView控件更新页面方法
	 * 
	 * @param xListView
	 */
	public void onLoad(XListView xListView) {
		if (xListView == null)
			return;

		xListView.stopRefresh();
		xListView.stopLoadMore();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String result = sdf.format(new Date());
		xListView.setRefreshTime(result);
	}

}
