package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.R.integer;
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
import com.wq.Adapter.noticeListAdapter;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.ablumList;
import com.wq.model.notice;
import com.wq.model.notice;
import com.wq.model.notice;
import com.wq.model.noticeList;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class noticeMainActivity extends BaseActivity implements
		IXExplandListViewListener {
	@ViewInject(id = R.id.notice_list)
	XExpandableListView listview;
	noticeListAdapter adapter;
	@ViewInject(id = R.id.http_err_layout)
	LinearLayout layout_http_err;// 有上传失败的
	@ViewInject(id = R.id.http_err_txt_count)
	TextView txt_http_err_count;
	ArrayList<noticeList> list = new ArrayList<noticeList>();
	noticeList p = new noticeList();
	private int listPosition = -1;
	String enterpriseId = "";
	FinalDb db;
	private static final int pageSize = 100;// 还未进行分组。此处就设置为100
	private int currentPage = 0;
	private int mgroupPosition = 0;
	private int mchildPosition = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_list_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initnavigation();
		initData();
		registerBoradcastReceiver();

	}

	public void initData() {
		adapter = new noticeListAdapter(this, list);
		localData();
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
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
				final notice noticeItem = list.get(mgroupPosition).getList()
						.get(mchildPosition);

				scanPhoto(noticeItem.getPic(), 0, true, false, true, true,
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
		// 获取动态公告信息
		// httpData();
		layout_http_err.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// changeViewForResult(noticeRepAddActivity.class, 0);
				// TODO Auto-generated method stub

			}
		});
	}

	private void httpData() {
		Intent intent = this.getIntent();
		enterpriseId = intent.getStringExtra("enterpriseId");
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		if (TextUtils.isEmpty(enterpriseId)) {
			params.put(httpUtil.ENTER_PRISE_ID, User.id);
		} else {
			params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		}

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
								DeleLocalData();
								list.clear();
								JSONArray arr = jsonResult
										.getJSONArray("promotions");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject object = arr.getJSONObject(i);
									notice item = new notice();
									item.setTime(object.getString("time"));
									item.setId(object.getString("id"));
									item.setCateName(object
											.getString("typeName"));
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
			if (!db.checkmyTableExist(notice.class)) {
				return;
			}
			ArrayList<notice> tmplist = new ArrayList<notice>();
			// 查询生意圈信息
			String sql = "select * from notice where time<=(select min(time) from ( select time from notice where userid='"
					+ User.id
					+ "'  order by time desc limit 0,"
					+ (currentPage * pageSize + 1)
					+ ") as t )  and userid='"
					+ User.id + "'  order by time asc limit 0," + pageSize + "";

			// if (db.findDbModelListBySQL(sql).size() > 0) {

			tmplist = (ArrayList<notice>) db.findDbListBySQL(notice.class, sql);

			// }

			// 查询图片
			StringBuilder sb = new StringBuilder();
			sb.append(" (");
			for (int i = 0; i < tmplist.size(); i++) {
				notice item = tmplist.get(i);
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
				for (notice item : tmplist) {
					for (photoModel pic : picList) {
						// 先查询本地
						if ((item.getId().equals(pic.getEcid()))) {
							pic.setShareModel(item);
							item.getPic().add(pic);
						}
					}

				}

			}
			LoggerUtil.i(tmplist.size() + "");
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
		// 判断是否有未传成功的企业信息

	}

	// 数据分组
	private void explandData(notice model) {
		LoggerUtil.i("dddd" + list.size());
		if (list.size() == 0) {
			p = new noticeList();
			p.setCateName(model.getCateName());
			p.getList().add(model);
			list.add(p);
		} else {
			int count = list.size();
			boolean flag = false;
			int i = 0;
			for (i = 0; i < count; i++) {
				if (list.get(i).getCateName().equals(model.getCateName())) {
					flag = true;
					break;
				}
			}
			if (flag) {
				p = list.get(i);
				p.getList().add(0, model);
			} else {
				p = new noticeList();
				p.setCateName(model.getCateName());
				p.getList().add(0, model);
				list.add(0, p);
			}
		}

	}

	private void initnavigation() {
		if (TextUtils.isEmpty(enterpriseId)) {
			initNavitation(
					getResources().getString(R.string.me_string_wqdt),
					getResources().getString(R.string.string_new_add),
					new EditClickListener() {
						@Override
						public void editClick() {
							changeViewForResult(noticeAddActivity.class,
									CommonUtil.NOTICE_RESULT_FLAG, 0);
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

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			// 动态

			if (resultCode == CommonUtil.NOTICE_RESULT_FLAG) {
				int flag = data.getIntExtra("flag", -1);

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

					@SuppressWarnings("unchecked")
					ArrayList<notice> tmplist = (ArrayList<notice>) data
							.getSerializableExtra("noticeList");
					for (int i = 0; i < list.size(); i++) {
						for (int j = 0; j < list.get(i).getList().size(); j++) {
							notice a = list.get(i).getList().get(j);
							for (notice tmp : tmplist) {
								if ((!TextUtils.isEmpty(a.getId()) && a.getId()
										.equals(tmp.getId()))) {
									list.get(i).getList().set(j, tmp);
									break;
								}
							}
						}
					}

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

	public static final String PART_BRO_ACTION_NAME = "com.wq.notice.send";

	public DataReceiver dataReceiver = null;

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(PART_BRO_ACTION_NAME);
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

			if (action.equals(PART_BRO_ACTION_NAME)) {
				boolean isCurPage = intent.getBooleanExtra("curPage", false);
				// 批量上传的标识
				boolean repCommitHttp = intent.getBooleanExtra("listFlag",
						false);
				boolean sendFlag = intent.getBooleanExtra("sendFlag", false);// 添加和修改产品的标识
				// 添加，更新后，访问网络后的结果
				if (!repCommitHttp && !sendFlag) {
					notice ec = (notice) intent.getSerializableExtra("notice");
					if (ec == null)
						return;
					int count = list.size();
					boolean isExist = false;
					for (int i = 0; i < count; i++) {
						for (int j = 0; j < list.get(i).getList().size(); j++) {
							notice item = list.get(i).getList().get(j);
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
					if (flag == 0) {
						notice p = (notice) intent
								.getSerializableExtra("notice");
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
						notice n = (notice) intent
								.getSerializableExtra("notice");
						if (n != null) {
							for (int i = 0; i < list.size(); i++) {
								for (int j = 0; j < list.get(i).getList()
										.size(); j++) {
									if ((!TextUtils.isEmpty(n.getId()) && list
											.get(i).getList().get(j).getId()
											.equals(n.getId()))) {
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

							adapter.notifyDataSetChanged();
							for (int i = 0; i < adapter.getGroupCount(); i++) {
								listview.expandGroup(i);
							}
						}
					}
				} else {
					@SuppressWarnings("unchecked")
					ArrayList<notice> tmplist = (ArrayList<notice>) intent
							.getSerializableExtra("noticeList");
					for (int i = 0; i < list.size(); i++) {
						for (int j = 0; j < list.get(i).getList().size(); j++) {
							notice a = list.get(i).getList().get(j);
							for (notice tmp : tmplist) {
								if ((!TextUtils.isEmpty(tmp.getId()) && a
										.getId().equals(tmp.getId()))) {
									list.get(i).getList().set(j, tmp);

									break;
								}
							}
						}
					}

					adapter.notifyDataSetChanged();
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
		final notice itemnotice = list.get(groupPosition).getList()
				.get(childPosition);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.NOTICE_DEL_ID, itemnotice.getId());
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.NOTICE_DEL_URL, params,
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
									db.deleteByWhere(notice.class, "id='"
											+ itemnotice.getId() + "'");
									deleLocalImage(itemnotice.getPic());
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
			noticeList itemlist = list.get(i);
			for (int j = 0; j < itemlist.getList().size(); j++) {
				notice itemProduct = list.get(i).getList().get(j);
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
			db.deleteByWhere(notice.class, "userid = '" + User.id + "'");
			db.deleteByWhere(photoModel.class,
					" ecid in " + sbPicBuilder1.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}
