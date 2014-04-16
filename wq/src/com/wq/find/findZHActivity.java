package com.wq.find;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;
import com.wq.Adapter.findZHListAdapter;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.me.noticeShowActivity;
import com.wq.model.User;
import com.wq.model.ZHModel;
import com.wq.model.partComModel;
import com.wq.model.photoModel;
import com.wq.partner.part_DetailActivity;
import com.wq.partner.part_searchDetailActivity;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;

/** 展会 */
public class findZHActivity extends BaseActivity implements IXListViewListener {
	@ViewInject(id = R.id.list_main)
	XListView listview;
	ArrayList<ZHModel> list = new ArrayList<ZHModel>();
	findZHListAdapter adapter;
	private static final int PageSize = 20;
	private int currPage = 0;
	int type = 0;// 访问类型

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_zh_activity);
		BaseApplication.getInstance().addActivity(this);

		initNavigation();
		initUI();

	}

	private void initUI() {
		type = this.getIntent().getIntExtra("type", 0);
		adapter = new findZHListAdapter(this, list, handler);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this);
		listview.startRefresh(this);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putSerializable("zhModel", list.get(position-1));
				b.putInt("type", type);
				changeView(noticeShowActivity.class, b);
			}
		});

	}

	private void initNavigation() {
		initNavitation(getString(R.string.find_zh_title), "", -1,
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

	/*
	 * * isRefresh 是否刷新
	 */
	private void httpData(final boolean isRefresh) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ZH_TYPE, type + "");
		params.put(httpUtil.ZH_CUR_PAGE, currPage + "");

		httpUtil.post(httpUtil.FIND_ZH_GET_URL, params,
				new AjaxCallBack<String>() {
					public void onLoading(long count, long current) {
					};

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String msg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								JSONArray arr = jsonResult
										.getJSONArray("dynamicTypeArray");
								if (isRefresh)
									list.clear();
								for (int i = 0; i < arr.length(); i++) {
									JSONObject o = arr.getJSONObject(i);
									ZHModel item = new ZHModel();
									item.setContent(o.getString("content"));
									item.setId(o.getString("id"));
									item.setTime(o.getString("time"));
									item.setTitle(o.getString("title"));
									item.setEnterpraseId(o
											.getString("enterpriseId"));
									item.setLogoUrl(o.getString("icon"));
									// 图片
									JSONArray picarr = o
											.getJSONArray("imgFileArray");
									for (int j = 0; j < picarr.length(); j++) {
										JSONObject picO = picarr
												.getJSONObject(j);
										photoModel pic = new photoModel();
										pic.setId(picO.getString("imgId"));
										pic.setImageUrl(picO
												.getString("imgUrl"));
										item.getPicList().add(pic);
									}
									list.add(item);
								}
								if (arr.length() < 15) {
									listview.setPullLoadEnable(false);
								} else
									listview.setPullRefreshEnable(true);
								currPage = list.size() / PageSize;
								adapter.notifyDataSetChanged();
							} else
								showToast(msg);
						} catch (JSONException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
							showToast(R.string.string_http_err_data);
						} finally {
							onLoad(listview);
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						onLoad(listview);
						CommonUtil.showToastHttp(findZHActivity.this, errorNo);
					}
				});
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		currPage = 0;
		httpData(true);
	}

	@Override
	public void onLoadMore() {
		httpData(false);
		// TODO Auto-generated method stub

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// 点击图片
			if (msg.what == 1) {
				Bundle b = new Bundle();
				b.putString("id", list.get(msg.arg1).getEnterpraseId());
				changeView(part_DetailActivity.class, b);
			}
		}
	};
}
