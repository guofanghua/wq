package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

import com.baidu.location.al;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.ablumActivity;
import com.wq.mainActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.Adapter.ablumListAdapter;
import com.wq.Adapter.ablumListAdapter;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.ablumList;
import com.wq.model.ablum;
import com.wq.model.ablumList;
import com.wq.model.photoModel;
import com.wq.model.product;
import com.wq.model.productList;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class ablumMainActivity extends BaseActivity implements
		IXExplandListViewListener {
	@ViewInject(id = R.id.ablum_list)
	XExpandableListView listview;
	@ViewInject(id = R.id.http_err_layout)
	LinearLayout layout_http_err;// 有上传失败的
	@ViewInject(id = R.id.http_err_txt_count)
	TextView txt_http_err_count;
	ablumListAdapter adapter;
	ArrayList<ablumList> list = new ArrayList<ablumList>();
	ablumList p = new ablumList();

	String enterpriseId = "";
	FinalDb db;
	private static final int pageSize = 100;// 还未进行分组。此处就设置为100
	private int currentPage = 0;
	int mgroupPosition = 0;
	int mchildPosition = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ablum_list_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initnavigation();
		initData();
		registerBoradcastReceiver();

	}

	public void initData() {
		adapter = new ablumListAdapter(this, list);
		localData();
		listview.setXListViewListener(this);
		listview.setPullLoadEnable(false);
		if (list.size() == 0) {
			listview.setPullRefreshEnable(true);
			listview.startRefresh(this);
		} else {
			httpData();
			listview.setPullRefreshEnable(false);
		}
		listview.setAdapter(adapter);

		for (int i = 0; i < adapter.getGroupCount(); i++) {
			listview.expandGroup(i);
		}
		listview.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// TODO Auto-generated method stub
				listview.expandGroup(groupPosition);
			}
		});

		listview.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				mgroupPosition = groupPosition;
				mchildPosition = childPosition;
				final ablum ablumItem = list.get(groupPosition).getList()
						.get(childPosition);
				scanPhoto(ablumItem.getList(), 0, true, false, true, true,
						new Handler() {
							@Override
							public void handleMessage(Message msg) {
								// TODO Auto-generated method stub
								super.handleMessage(msg);
								delCommit(mgroupPosition, mchildPosition);
							}
						});
				// TODO Auto-generated method stub
				return false;

			}
		});

	}

	private void httpData() {

		Intent intent = this.getIntent();
		String enterpriseId = intent.getStringExtra("enterpriseId");
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		if (TextUtils.isEmpty(enterpriseId)) {
			params.put(httpUtil.ENTER_PRISE_ID, User.id);
		} else {
			params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		}

		httpUtil.post(httpUtil.COMPANY_PHOTO_GET_URL, params,
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
								DeleLocalData();
								list.clear();
								JSONArray arr = jsonResult
										.getJSONArray("aulmuArray");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject object = arr.getJSONObject(i);
									ablum item = new ablum();
									item.setTime(object.getString("time"));
									item.setId(object.getString("id"));

									item.setContent(object
											.getString("fdscription"));
									JSONArray picArr = object
											.getJSONArray("imgArray");
									for (int j = 0; j < picArr.length(); j++) {
										JSONObject picObj = picArr
												.getJSONObject(j);
										photoModel pic = new photoModel();
										pic.setFlag(0);
										pic.setId(picObj.getString("imgId"));
										pic.setImageUrl(picObj.getString("url"));
										pic.setShareModel(item);
										pic.setShareType(photoModel.ABLUM_SHARE_FLAG);
										item.getList().add(pic);
										pic.setEcid(item.getId());
										// 保存图片
										try {
											db.save(pic);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									try {
										db.save(item);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									explandData(item);
								}
								listview.setPullRefreshEnable(false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {

							onLoad(listview);

							adapter.notifyDataSetChanged();
							for (int i = 0; i < adapter.getGroupCount(); i++) {
								listview.expandGroup(i);
							}

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						onLoad(listview);

					}
				});
	}

	// 获取缓存数据
	private void localData() {

		// 图片
		try {
			// 检查表是否存在
			if (!db.checkmyTableExist(ablum.class)) {
				return;
			}
			ArrayList<ablum> tmplist = new ArrayList<ablum>();
			// 查询相册
			String sql = "select * from ablum where time<=(select min(time) from ( select time from ablum where userid='"
					+ User.id
					+ "'  order by time desc limit 0,"
					+ (currentPage * pageSize + 1)
					+ ") as t )  and userid='"
					+ User.id + "'  order by time asc limit 0," + pageSize + "";

			// if (db.findDbModelListBySQL(sql).size() > 0) {

			tmplist = (ArrayList<ablum>) db.findDbListBySQL(ablum.class, sql);

			// }

			// 查询图片
			StringBuilder sb = new StringBuilder();
			sb.append(" (");
			for (int i = 0; i < tmplist.size(); i++) {
				ablum item = tmplist.get(i);
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
				for (ablum item : tmplist) {
					for (photoModel pic : picList) {
						// 先查询本地
						if ((item.getId().equals(pic.getEcid()))) {
							pic.setShareModel(item);
							item.getList().add(pic);
						}
					}

				}

			}

			for (int i = 0; i < tmplist.size(); i++) {
				explandData(tmplist.get(i));
			}
			if (tmplist.size() < pageSize) {
				listview.setPullLoadEnable(false);
			} else
				listview.setPullLoadEnable(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// 数据分组
	private void explandData(ablum model) {

		if (list.size() == 0) {
			p = new ablumList();
			p.setTime(model.getTime());
			p.getList().add(model);
			list.add(p);
		} else {
			int count = list.size();
			boolean flag = false;
			int i = 0;
			for (i = 0; i < count; i++) {
				SimpleDateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");
				if (dateUtil
						.formatDate(list.get(i).getTime(), formattmp)
						.equals(dateUtil.formatDate(model.getTime(), formattmp))) {
					flag = true;
					break;
				}
			}
			if (flag) {
				p = list.get(i);
				p.getList().add(0, model);
			} else {
				p = new ablumList();
				p.setTime(model.getTime());
				p.getList().add(0, model);
				list.add(0, p);
			}
		}
	}

	private void initnavigation() {

		initNavitation(
				getResources().getString(R.string.me_string_wqxc),
				getResources().getString(R.string.string_new_add),
				new EditClickListener() {
					@Override
					public void editClick() {
						changeViewForResult(ablumAddActivity.class,
								CommonUtil.NOTICE_RESULT_FLAG, 1);
						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						unregisterBroadcaseReceiver();
						finish();
						animDown();
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			// 动态
			if (resultCode == CommonUtil.NOTICE_RESULT_FLAG) {
				int flag = data.getIntExtra("flag", -1);
				// 删除
				if (flag == 2) {
					showRightDialog();
					int groupPosition = data.getIntExtra("group", -1);
					int childPosition = data.getIntExtra("child", -1);
					if (groupPosition >= 0 && childPosition >= 0) {
						list.get(groupPosition).getList().remove(childPosition);
						if (list.get(groupPosition).getList().size() == 0) {
							list.remove(groupPosition);
						}

						adapter.notifyDataSetChanged();

					}
					if (list.size() >= 0) {
						listview.setPullRefreshEnable(false);
						onLoad(listview);

					}

				}
				// 批量上传
				else if (flag == 3) {
					showRightDialog();

					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onRefresh() {

		// TODO Auto-generated method stub
		if (list.size() == 0) {

			httpData();
		}

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}

	public static String ABLUM_BRO_ACTION_NAME = "com.ablumMainActivity.bro";
	public DataReceiver dataReceiver = null;

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ABLUM_BRO_ACTION_NAME);
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

			int errCount = 0;
			if (action.equals(ABLUM_BRO_ACTION_NAME)) {

				boolean isCurPage = intent.getBooleanExtra("curPage", false);
				// 批量上传的标识
				boolean repCommitHttp = intent.getBooleanExtra("listFlag",
						false);

				boolean sendFlag = intent.getBooleanExtra("sendFlag", false);// 添加和修改产品的标识

				// 添加，更新后，访问网络后的结果
				if (!repCommitHttp && !sendFlag) {

					ablum ec = (ablum) intent.getSerializableExtra("ablum");

					if (ec == null)
						return;
					int count = list.size();
					boolean isExist = false;
					for (int i = 0; i < count; i++) {
						for (int j = 0; j < list.get(i).getList().size(); j++) {
							ablum item = list.get(i).getList().get(j);
							if ((!TextUtils.isEmpty(item.getId()) && item
									.getId().equals(ec.getId()))) {

								list.get(i).getList().remove(j);
								if (list.get(i).getList().size() == 0) {
									list.remove(i);
								}
								explandData(ec);
								isExist = true;
								break;
							}
						}
						if (isExist)
							break;
					}

					// 判断是否有未传成功的企业信息

					adapter.notifyDataSetChanged();
					for (int i = 0; i < adapter.getGroupCount(); i++) {
						listview.expandGroup(i);
					}

				}
				// 首次添加和更新
				else if (sendFlag) {
					if (!isCurPage)
						showRightDialog();
					int flag = intent.getIntExtra("flag", -1);
					// 把添加和更新方广播里面，是为了处理在批量上传列表里，数据修改的时候
					// 添加公告
					if (flag == 0) {
						ablum p = (ablum) intent.getSerializableExtra("ablum");
						if (p != null) {
							explandData(p);
							adapter.notifyDataSetChanged();
							for (int i = 0; i < adapter.getGroupCount(); i++) {
								listview.expandGroup(i);
							}
						}
					}
					// 更新公告
					else if (flag == 1) {
						boolean isExit = false;
						ablum n = (ablum) intent.getSerializableExtra("ablum");
						if (n != null) {
							for (int i = 0; i < list.size(); i++) {
								for (int j = 0; j < list.get(i).getList()
										.size(); j++) {
									ablum item = list.get(i).getList().get(j);
									if ((!TextUtils.isEmpty(item.getId()) && item
											.getId().equals(n.getId()))) {

										list.get(i).getList().remove(j);
										if (list.get(i).getList().size() == 0) {
											list.remove(i);
										}
										explandData(n);
										isExit = true;
										break;
									}
									if (isExit)
										break;
								}
							}
							for (int i = 0; i < adapter.getGroupCount(); i++) {
								listview.expandGroup(i);
							}
							adapter.notifyDataSetChanged();

						}
					}
				}
			}
			if (list.size() >= 0) {
				listview.setPullRefreshEnable(false);
				onLoad(listview);

			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// exitBy2Click();
			unregisterBroadcaseReceiver();
			finish();
			animDown();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	// 删除动态公告
	private void delCommit(final int groupPosition, final int childPosition) {
		final ablum itemAblum = list.get(groupPosition).getList()
				.get(childPosition);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ABLUM_DEL_ID, itemAblum.getId());
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.ABLUM_DEL_URL, params,
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
								hideProgress();
								try {
									db.deleteByWhere(ablum.class, "id='"
											+ itemAblum.getId() + "'");
									deleLocalImage(itemAblum.getList());

									if (groupPosition >= 0
											&& childPosition >= 0) {
										list.get(groupPosition).getList()
												.remove(childPosition);
										if (list.get(groupPosition).getList()
												.size() == 0) {
											list.remove(groupPosition);
										}
									}
									showRightDialog();
									adapter.notifyDataSetChanged();
								} catch (Exception ex) {
									ex.printStackTrace();
								}

							} else {
								hideProgress();
								showToast(errMsg);
							}
						} catch (JSONException e) {
							hideProgress();

							e.printStackTrace();
						} finally {

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						hideProgress();

					}
				});

	}

	// 删除数据库本地图片数据
	private void deleLocalImage(ArrayList<photoModel> delList) {
		if (delList.size() <= 0 || !db.checkmyTableExist(photoModel.class))
			return;
		StringBuilder sbBuilder = new StringBuilder();
		sbBuilder.append(" ( ");
		for (int i = 0; i < delList.size(); i++) {
			sbBuilder.append("'");
			sbBuilder.append(delList.get(i).getId());
			sbBuilder.append("'");
			if (i < delList.size() - 1)
				sbBuilder.append(" , ");
		}
		sbBuilder.append(" ) ");
		try {
			db.deleteByWhere(photoModel.class, " id in " + sbBuilder.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 删除本地相关相册数据
	private void DeleLocalData() {
		if (list.size() <= 0)
			return;

		StringBuilder sbPicBuilder = new StringBuilder();
		StringBuilder sbPicBuilder1 = new StringBuilder();
		sbPicBuilder.append(" ( ");
		for (int i = 0; i < list.size(); i++) {
			ablumList itemlist = list.get(i);
			for (int j = 0; j < itemlist.getList().size(); j++) {
				ablum itemProduct = list.get(i).getList().get(j);
				// 删除产品列表
				sbPicBuilder.append("'");
				sbPicBuilder.append(itemProduct.getId());
				sbPicBuilder.append("'");
				sbPicBuilder.append(" ,");
			}
			// 删除产品相关列表
		}

		if (sbPicBuilder.toString().length() > 1) {

			sbPicBuilder1.append(sbPicBuilder.toString().substring(0,
					sbPicBuilder.toString().length() - 1));
		}
		sbPicBuilder1.append(" ) ");
		try {
			db.deleteByWhere(ablum.class, "userid = '" + User.id + "'");
			db.deleteByWhere(photoModel.class,
					" ecid in " + sbPicBuilder1.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}
