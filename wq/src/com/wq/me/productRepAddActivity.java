package com.wq.me;

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
import com.wq.Adapter.productRepAddListAdapter;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.product;
import com.wq.model.productList;
import com.wq.model.product;
import com.wq.model.productList;
import com.wq.model.photoModel;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class productRepAddActivity extends BaseActivity implements
		IXListViewListener {
	@ViewInject(id = R.id.ablum_list)
	XListView listview;
	productRepAddListAdapter adapter;
	ArrayList<product> list = new ArrayList<product>();
	productList p = new productList();

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
		adapter = new productRepAddListAdapter(this, list, imgClickHandler);

		localData();
		LoggerUtil.i("count" + list.size());
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		// listview.startRefresh(this);
		listview.setAdapter(adapter);
		setViewTouchListener(listview);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				product product = list.get(position - 1);
				Bundle b = new Bundle();
				b.putSerializable("product", product);
				changeViewForResult(productAddActivity.class, b,
						CommonUtil.PRODUCT_RESULT_FLAG);

			}
		});
	}

	// 批量添加或者编辑动态
	private void AddAndEditcommit(final product myproduct, final int position) {
		AjaxParams params = new AjaxParams();

		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.PRODUCT_ADD_NAME, myproduct.getTitle());

		params.put(httpUtil.PRODUCT_ADD_CONTENT, myproduct.getIntro()
				.toString());
		// if (TextUtils.isEmpty(product.getCateId()))
		// params.put(httpUtil.PRODUCT_ADD_TYPE_NAME_ID, "");
		// else
		params.put(httpUtil.PRODUCT_ADD_TYPE_NAME, myproduct.getCateName());
		params.put(httpUtil.PRODUCT_ADD_ATTR, myproduct.getProAttrStr());

		// 图片
		int count1 = myproduct.getPicList().size();

		int j = 0;
		for (int i = 0; i < count1; i++) {
			if (myproduct.getPicList().get(i).getFlag() != 1) {
				continue;
			}
			try {
				params.put("pic" + (j++), new File(myproduct.getPicList()
						.get(i).getImageUrl()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		params.put(httpUtil.PRODUCT_ADD_IMG_NUM, j + "");
		// 需求改变前的字段
		params.put(httpUtil.PRODUCT_ADD_TYPE, "0");
		String url = "";
		if (TextUtils.isEmpty(myproduct.getId())) {
			url = httpUtil.PRODUCT_ADD_URL;
		} else {
			params.put(httpUtil.PRODUCT_UPDATE_COMMODITY_ID, myproduct.getId());
			url = httpUtil.PRODUCT_UPDATE_URL;
		}
		httpUtil.post(url, params, new AjaxCallBack<String>() {
			private String errMsg = "";
			private String errcode = "";

			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {
				repCount++;
				try {

					JSONObject jsonResult = new JSONObject(result);
					errcode = jsonResult.getString(httpUtil.ERR_CODE);
					errMsg = jsonResult.getString(httpUtil.ERR_MGS);
					if (errcode.equals(httpUtil.errCode_success)) {
						if (!jsonResult.isNull("id")) {
							myproduct.setId(jsonResult.getString("id"));
						
						}
					} 
				} catch (JSONException e) {
				
					e.printStackTrace();
				} finally {
					try {

						db.update(myproduct, "id='" + myproduct.getId() + "'");

					} catch (Exception ex) {
						ex.printStackTrace();
					}
					list.set(position, myproduct);
					if (repCount == list.size()) {
						Intent intent = new Intent();
						intent.setAction(productMainListActivity.PART_BRO_ACTION_NAME);
						intent.putExtra("productList", list);
						intent.putExtra("listFlag", true);
						sendBroadcast(intent);
					}
					// Intent intent = new Intent();
					// intent.setAction(productMainActivity.product_BRO_ACTION_NAME);
					// intent.putExtra("product", myproduct);
					// sendBroadcast(intent);
				}
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
			
				
				list.set(position, myproduct);
				Intent intent = new Intent();
				intent.setAction(productMainListActivity.PART_BRO_ACTION_NAME);
				intent.putExtra("productList", list);
				intent.putExtra("listFlag", true);
				sendBroadcast(intent);
			}
		});

	}

	// 获取缓存数据
	private void localData() {
		// 图片
		try {
			// 检查表是否存在
			if (!db.checkmyTableExist(product.class)) {
				return;
			}
			ArrayList<product> tmplist = new ArrayList<product>();
			// 查询生意圈信息
			String sql = "select * from product where time<=(select min(time) from ( select time from product where userid='"
					+ User.id
					+ "' and sendFlag='1'   order by time desc limit 0,"
					+ (currentPage * pageSize + 1)
					+ ") as t )  and userid='"
					+ User.id
					+ "'  and sendFlag='1'    order by time desc limit 0,"
					+ pageSize + "";

			// if (db.findDbModelListBySQL(sql).size() > 0) {

			tmplist = (ArrayList<product>) db.findDbListBySQL(product.class,
					sql);
			if (tmplist.size() > 0)
				lastDate = tmplist.get(tmplist.size() - 1).getTime();

			// }

			// 查询图片
			StringBuilder sb = new StringBuilder();
			sb.append(" (");
			for (int i = 0; i < tmplist.size(); i++) {
				product item = tmplist.get(i);
				sb.append("'");
				sb.append(item.getId());
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
				for (product item : tmplist) {
					for (photoModel pic : picList) {
						// 先查询本地
						if ((item.getId().equals(pic.getEcid()))) {
							pic.setShareModel(item);
							item.getPicList().add(pic);
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
							// changeViewForResult(productAddActivity.class,
							// CommonUtil.product_RESULT_FLAG, 0);

							for (int i = 0; i < list.size(); i++) {
								product item = list.get(i);
							
								item.setTime(dateUtil.formatDate(new Date()));
								list.set(i, item);
								AddAndEditcommit(list.get(i), i);
							}
							unregisterBroadcaseReceiver();
							Intent intent = new Intent();
							// intent.setAction(productMainActivity.product_BRO_ACTION_NAME);
							intent.putExtra("productList", list);
							intent.putExtra("listFlag", true);
							intent.putExtra("flag", 3);
							// sendBroadcast(intent);
							setResult(CommonUtil.PRODUCT_RESULT_FLAG, intent);
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
			final product product = list.get(msg.arg1);
			for (photoModel pic : product.getPicList())
				pic.setShareModel(product);
			scanPhoto(product.getPicList(), 0, false, true, null);
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
			filter.addAction(productMainListActivity.PART_BRO_ACTION_NAME);
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

			if (action.equals(productMainListActivity.PART_BRO_ACTION_NAME)) {
				boolean sendFlag = intent.getBooleanExtra("sendFlag", false);
				product ec = (product) intent.getSerializableExtra("product");

				LoggerUtil.i("product=" + ec.getId());
				if (ec == null)
					return;
				if (sendFlag) {
					int count = list.size();
					boolean isExist = false;
					for (int i = 0; i < count; i++) {
						product item = list.get(i);
						if (item.getId().equals(ec.getId())) {
							list.remove(i);
							break;
						}
						if (isExist)
							break;
					}
					adapter.notifyDataSetChanged();
				} else {
					int count = list.size();
					boolean isExist = false;
					for (int i = 0; i < count; i++) {
						product item = list.get(i);
						if (item.getId().equals(ec.getId())) {
						
							break;
						}
						if (isExist)
							break;
					}
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
