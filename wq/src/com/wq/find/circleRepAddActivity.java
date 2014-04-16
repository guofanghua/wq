package com.wq.find;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ListView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;

import com.wq.mainActivity;
import com.wq.BaseActivity.EditClickListener;

import com.wq.Adapter.circleRepAddListAdapter;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;

import com.wq.model.User;
import com.wq.model.ecCircleModel;

import com.wq.model.ecCircleModel;

import com.wq.model.ecCircleModel;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class circleRepAddActivity extends BaseActivity implements
		IXListViewListener {
	@ViewInject(id = R.id.ablum_list)
	XListView listview;
	circleRepAddListAdapter adapter;
	ArrayList<ecCircleModel> list = new ArrayList<ecCircleModel>();

	String enterpriseId = "";
	FinalDb db;
	private static final int pageSize = 100;// 还未进行分组。此处就设置为100
	private int currentPage = 0;
	private String lastDate = "";
	int repCount = 0;// 计算重新发送了多少个重新

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.err_add_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initnavigation();
		initData();
		registerBoradcastReceiver();

	}

	public void initData() {
		adapter = new circleRepAddListAdapter(this, list, imgClickHandler);
		localData();
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		// listview.startRefresh(this);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				ecCircleModel product = list.get(position - 1);
				Bundle b = new Bundle();
				b.putSerializable("circleModel", product);
				changeViewForResult(findTipDetailAcitivy.class, b,
						CommonUtil.NOTICE_RESULT_FLAG);

			}
		});
	}

	// 发布失败的重新提交
	// 添加或者编辑
	private void httpAddData(final ecCircleModel model) {
		// 添加

		AjaxParams params = new AjaxParams();
		for (int i = 0; i < model.getImgList().size(); i++) {
			try {
				params.put("pic" + i, new File(model.getImgList().get(i)
						.getImageUrl()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.CIRCLE_ADD_CONTENT, model.getCotent());
		params.put(httpUtil.CIRCLE_ADD_IMG_NUM, model.getImgList().size() + "");
		params.put(httpUtil.CIRCLE_ADD_TYPE, model.getType());
		httpUtil.post(httpUtil.CIRCLE_PRO_ADD_URL, params,
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
								model.setId(jsonResult.getString("id"));
								model.setIsSend("0");
							} else {
								model.setIsSend("1");
							}
						} catch (JSONException e) {
							model.setIsSend("1");

							e.printStackTrace();
						} finally {
							if (model.getIsSend().equals("0")) {
								try {
									db.update(model);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								Intent intent = new Intent();
								intent.putExtra("circleModel", model);
								intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
								sendOrderedBroadcast(intent, null);
							}
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						model.setIsSend("1");
						Intent intent = new Intent();
						intent.putExtra("circleModel", model);
						intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
						sendOrderedBroadcast(intent, null);
					}
				});
	}

	// 获取缓存数据
	private void localData() {
		// 图片
		try {
			// 检查表是否存在
			if (!db.checkmyTableExist(ecCircleModel.class)) {
				return;
			}
			ArrayList<ecCircleModel> tmplist = new ArrayList<ecCircleModel>();
			// 查询生意圈信息
			String sql = "select * from ecCircleModel where time<=(select min(time) from ( select time from ecCircleModel where userid='"
					+ User.id
					+ "' and isSend='1'   order by time desc limit 0,"
					+ (currentPage * pageSize + 1)
					+ ") as t )  and userid='"
					+ User.id
					+ "'  and isSend='1'    order by time desc limit 0,"
					+ pageSize + "";
			// if (db.findDbModelListBySQL(sql).size() > 0) {

			tmplist = (ArrayList<ecCircleModel>) db.findDbListBySQL(
					ecCircleModel.class, sql);
			if (tmplist.size() > 0)
				lastDate = tmplist.get(tmplist.size() - 1).getTime();

			// }

			// 查询图片
			StringBuilder sb = new StringBuilder();
			sb.append(" (");
			for (int i = 0; i < tmplist.size(); i++) {
				ecCircleModel item = tmplist.get(i);
				sb.append("'");
				sb.append(item.getId());
				sb.append("'");
				sb.append(",");
				sb.append("'");
				sb.append("circleModel" + item.getPid());
				sb.append("'");
				if (i < tmplist.size() - 1)
					sb.append(",");
			}
			sb.append(" )  ");
			// 当自身添加图片时，ec寸的是自增长id
			String sqlWhere = "select * from photoModel where " + "userId='"
					+ User.id + "'  and ecid in " + sb.toString();
			if (db.findDbModelListBySQL(sqlWhere).size() > 0) {
				ArrayList<photoModel> picList = (ArrayList<photoModel>) db
						.findDbListBySQL(photoModel.class, sqlWhere);
				for (ecCircleModel item : tmplist) {
					for (photoModel pic : picList) {
						// 先查询本地
						if ((item.getId().equals(pic.getEcid()) || pic
								.getEcid()
								.equals("circleModel" + item.getPid()))) {
							pic.setShareModel(item);
							item.getImgList().add(pic);
						}
					}

				}

			}
			list.addAll(tmplist);
			if (tmplist.size() < pageSize) {
				listview.setPullLoadEnable(false);
			} else
				listview.setPullLoadEnable(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void initnavigation() {
		if (TextUtils.isEmpty(enterpriseId)) {
			initNavitation(
					getResources().getString(R.string.me_string_rep_http_title),
					getResources().getString(R.string.me_string_http_err_pl),
					new EditClickListener() {
						@Override
						public void editClick() {
							// changeViewForResult(ecCircleModelAddActivity.class,
							// CommonUtil.NOTICE_RESULT_FLAG, 0);

							for (int i = 0; i < list.size(); i++) {
								ecCircleModel item = list.get(i);
								item.setIsSend("0");
								item.setTime(dateUtil.formatDate(new Date()));
								list.set(i, item);
								httpAddData(list.get(i));
							}
							unregisterBroadcaseReceiver();
							Intent intent = new Intent();
							intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
							intent.putExtra("ecCircleModelList", list);
							intent.putExtra("listFlag", true);
							intent.putExtra("flag", 3);
							sendBroadcast(intent);
							// setResult(CommonUtil.NOTICE_RESULT_FLAG, intent);
							finish();
							// TODO Auto-generated method stub
						}

						@Override
						public void backClick() {
							// TODO Auto-generated method stub
							unregisterBroadcaseReceiver();
							finish();
							animOut();
						}
					});
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

	private Handler imgClickHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final ecCircleModel product = list.get(msg.arg1);
			for (photoModel pic : product.getImgList())
				pic.setShareModel(product);
			scanPhoto(product.getImgList(), 0, false, false, null);

		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// exitBy2Click();
			unregisterBroadcaseReceiver();
			finish();
			animOut();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	public DataReceiver dataReceiver = null;

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
			registerReceiver(dataReceiver, filter);
		}

	}

	// 注销广播
	public void unregisterBroadcaseReceiver() {
		if (dataReceiver != null) {
			unregisterReceiver(dataReceiver);
			dataReceiver = null;
		}
	}

	private class DataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(findEpCircleListActivity.PART_BRO_ACTION_NAME)) {
				boolean sendFlag = intent.getBooleanExtra("sendFlag", false);
				boolean delFlag = intent.getBooleanExtra("delFlag", false);
				ecCircleModel ec = (ecCircleModel) intent
						.getSerializableExtra("circleModel");

				if (ec == null)
					return;
				if (sendFlag || delFlag) {
					
					for (int i = 0; i < list.size(); i++) {
						ecCircleModel item = list.get(i);
						if (item.getId().equals(ec.getId())
								|| item.getPid() == ec.getPid()) {
							list.remove(i);
							break;
						}
					}
					adapter.notifyDataSetChanged();
				} else {
					for (int i = 0; i < list.size(); i++) {
						ecCircleModel item = list.get(i);
						if (item.getId().equals(ec.getId())
								|| item.getPid() == ec.getPid()) {
							if (ec.getIsSend().equals("1"))
								list.add(0, ec);
							break;
						}
					}
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
