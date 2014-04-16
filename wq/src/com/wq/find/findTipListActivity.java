package com.wq.find;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.findEcTipListAdapter;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;

import com.wq.model.User;
import com.wq.model.leaveMessage;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class findTipListActivity extends BaseActivity implements
		IXListViewListener {
	@ViewInject(id = R.id.list_main)
	XListView listview;
	findEcTipListAdapter adapter;
	ArrayList<leaveMessage> list = new ArrayList<leaveMessage>();
	// 没数据显示界面
	@ViewInject(id = R.id.no_data_layout)
	LinearLayout noData_layout;
	@ViewInject(id = R.id.no_data_txt_title)
	TextView txt_no_data;
	@ViewInject(id = R.id.no_data_btn)
	Button btn_no_data;
	FinalDb db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_ec_circle_tip_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavigation();
		db = FinalDb.create(this);
		initUI();
	}

	private void initUI() {
		try {

			if (db.findAll(leaveMessage.class).size() > 0) {
				list = (ArrayList<leaveMessage>) db.findAll(leaveMessage.class,
						"time desc");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this);
		listview.startRefresh(this);
		adapter = new findEcTipListAdapter(this, list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putString("tradeId", list.get(position - 1).getSuperComId());
				changeView(findTipDetailAcitivy.class, b);
			}
		});
		setViewTouchListener(listview);

	}

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);

		httpUtil.post(httpUtil.CIRCLE_PRO_TRADE_URL, params,
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
										.getJSONArray("commentArray");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject o = arr.getJSONObject(i);
									leaveMessage msg = new leaveMessage();
									msg.setComId(o.getString("id"));
									msg.setContent(o.getString("content"));
									msg.setSuperComId(o.getString("superId"));
									msg.setLevel(o.getString("level"));
									msg.setType(o.getString("type"));
									msg.setTime(o.getString("time"));
									JSONObject ec = o
											.getJSONObject("enterprise");
									msg.setEnterPriseId(ec.getString("id"));
									msg.setLogoUrl(ec.getString("icon"));
									msg.setVqh(ec.getString("wqh"));
									try {
										db.save(msg);
									} catch (Exception e) {
										// TODO: handle exception
									}
									list.add(0, msg);

								}
								noData_layout.setVisibility(View.GONE);
								listview.setVisibility(View.VISIBLE);
								adapter.notifyDataSetChanged();
							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								noData(0);
							}

						} catch (JSONException e) {
							noData(1);
							CommonUtil.showToast(findTipListActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {

							onLoad(listview);
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						noData(1);
						onLoad(listview);
						// TODO Auto-generated method stub

						CommonUtil.showToastHttp(findTipListActivity.this,
								errorNo);
					}
				});
	}

	private void initNavigation() {
		initNavitation(getString(R.string.find_string_ec_tip_msg_list_title),
				"", -1, new EditClickListener() {

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
		httpData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	// 没数据
	/** flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载 */
	private void noData(int flag) {
		if (list.size() == 0) {
			noData_layout.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}
		btn_no_data.setVisibility(View.GONE);
		if (flag == 0) {
			txt_no_data.setText(String.format(
					getString(R.string.string_no_data_txt_add),
					getString(R.string.find_string_ec_tip_msg_list_title)));
		} else {
			txt_no_data.setText(R.string.string_no_data_txt_rep);
		}

	}

}
