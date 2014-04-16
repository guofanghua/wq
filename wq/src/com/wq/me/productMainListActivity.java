package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.text.Format;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.R.attr;
import android.R.integer;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.mainActivity;
import com.wq.Adapter.proListAdapter;
import com.wq.BaseActivity.EditClickListener;
import com.wq.UI.CustomProgressDialog;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.expandedListview;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.product;
import com.wq.model.ecCircleModel;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.productList;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.stringUtils;

/**
 * 产品列表
 * */
public class productMainListActivity extends BaseActivity implements
		IXExplandListViewListener {
	@ViewInject(id = R.id.pro_exlist_main)
	XExpandableListView listView;
	// 没数据显示界面
	// @ViewInject(id = R.id.no_data_layout)
	// LinearLayout noData_layout;
	// @ViewInject(id = R.id.no_data_txt_title)
	// TextView txt_no_data;
	// @ViewInject(id = R.id.no_data_btn)
	// Button btn_no_data;
	@ViewInject(id = R.id.http_err_layout)
	LinearLayout layout_http_err;// 有上传失败的
	@ViewInject(id = R.id.http_err_txt_count)
	TextView txt_http_err_count;
	proListAdapter adapter;
	ArrayList<productList> list = new ArrayList<productList>();
	ArrayList<product> uplist = new ArrayList<product>();
	productList p = new productList();
	String enterpriseId = "";
	String enterpriseWqh = "";
	FinalDb db;
	private static final int pageSize = 100;// 还未进行分组。此处就设置为100
	private int currentPage = 0;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_main_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		registerBoradcastReceiver();

		initUI();

		initNavigation();
		initListener();

	}

	private void initData() {
		Intent intent = this.getIntent();
		enterpriseId = intent.getStringExtra("enterpriseId");
		enterpriseWqh = intent.getStringExtra("enterpriseWqh");
		AjaxParams params = new AjaxParams();
		if (TextUtils.isEmpty(enterpriseId)) {
			params.put(httpUtil.ENTER_PRISE_ID, User.id);
		} else {
			params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		}
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);

		httpUtil.post(httpUtil.PRODUCT_LIST_URL, params,
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
								JSONArray parentArr = jsonResult
										.getJSONArray("commoditys");
								for (int j = 0; j < parentArr.length(); j++) {
									JSONObject proObject = parentArr
											.getJSONObject(j);
									product pro = new product();
									pro.setId(proObject.getString("id"));
									// pro.setCateId(proObject.getString("typeId"));
									pro.setCateName(proObject
											.getString("typeName"));
									pro.setTime(proObject.getString("time"));
									pro.setTitle(proObject.getString("title"));
									pro.setIntro(proObject.getString("content"));
									// 属性
									String attrStr = proObject
											.getString("attribute");
									pro.setProAttrStr(attrStr);
									pro.getAttrList().addAll(
											stringUtils.splitAttr(attrStr));
									// 图片
									JSONArray picArray = proObject
											.getJSONArray("imgFileArray");
									for (int k = 0; k < picArray.length(); k++) {
										JSONObject picObject = picArray
												.getJSONObject(k);
										photoModel pic = new photoModel();
										pic.setId(picObject.getString("imgId"));
										pic.setImageUrl(picObject
												.getString("imgUrl"));
										pic.setShareModel(pro);
										pic.setShareType(photoModel.PRODUCT_SHARE_FLAG);
										pic.setEcid(pro.getId());
										pro.getPicList().add(pic);
										// 保存图片
										try {
											db.save(pic);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									try {
										db.save(pro);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// 数据分组
									explandData(pro);
								}

								listView.setPullRefreshEnable(false);
							}

						} catch (JSONException e) {

							e.printStackTrace();
						} finally {
							onLoad(listView);

							adapter.notifyDataSetChanged();
							for (int i = 0; i < adapter.getGroupCount(); i++) {
								listView.expandGroup(i);
							}
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						onLoad(listView);
						// noData(1);

					}
				});

	}

	private void initUI() {
		adapter = new proListAdapter(this, list, ClickHandler);
		localData();
		listView.setXListViewListener(this);
		if (list.size() == 0) {
			listView.setPullRefreshEnable(true);
			listView.startRefresh(this);
		} else {
			initData();
			listView.setPullRefreshEnable(false);
		}
		listView.setAdapter(adapter);
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			listView.expandGroup(i);
		}
		layout_http_err.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeViewForResult(productRepAddActivity.class, 0);
				// TODO Auto-generated method stub

			}
		});

	}

	private void localData() {

		// 图片
		try {
			// 检查表是否存在
			if (!db.checkmyTableExist(product.class)) {
				return;
			}
			ArrayList<product> tmplist = new ArrayList<product>();
			// 查询产品
			String sql = "select * from product where time<=(select min(time) from ( select time from product where userid='"
					+ User.id
					+ "'  order by time desc limit 0,"
					+ (currentPage * pageSize + 1)
					+ ") as t )  and userid='"
					+ User.id + "'  order by time asc limit 0," + pageSize + "";

			// if (db.findDbModelListBySQL(sql).size() > 0) {

			tmplist = (ArrayList<product>) db.findDbListBySQL(product.class,
					sql);

			// }
			if (tmplist != null && tmplist.size() > 0) {
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
				String sqlWhere = "select * from photoModel where "
						+ "userId='" + User.id + "'  and ecid in "
						+ sb.toString();
				if (db.findDbModelListBySQL(sqlWhere).size() > 0) {
					ArrayList<photoModel> picList = (ArrayList<photoModel>) db
							.findDbListBySQL(photoModel.class, sqlWhere);
					for (product item : tmplist) {
						for (photoModel pic : picList) {

							if (item.getId().equals(pic.getEcid())) {
								pic.setShareModel(item);
								item.getPicList().add(pic);
							}
						}

					}

				}
			}
			for (int i = 0; i < tmplist.size(); i++) {
				explandData(tmplist.get(i));
			}
			if (tmplist.size() < pageSize) {
				listView.setPullLoadEnable(false);
			} else
				listView.setPullLoadEnable(true);

			// listView.setAdapter(adapter);

			// adapter.notifyDataSetChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// 数据分组
	private void explandData(product model) {

		model.setAttrList(stringUtils.splitAttr(model.getProAttrStr()));
		if (list.size() == 0) {
			p = new productList();
			p.setId(model.getCateId());
			p.setName(model.getCateName());
			p.getProList().add(model);
			list.add(p);
		} else {
			int count = list.size();
			boolean flag = false;
			int i = 0;
			for (i = 0; i < count; i++) {

				if (list.get(i).getName().equals(model.getCateName())) {
					flag = true;
					break;
				}
			}
			if (flag) {
				p = list.get(i);
				p.getProList().add(0, model);
			} else {
				p = new productList();
				p.setId(model.getCateId());
				p.setName(model.getCateName());
				p.getProList().add(0, model);
				list.add(0, p);
			}
		}

	}

	private void initListener() {
		setViewTouchListener(listView);

		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				if (TextUtils.isEmpty(enterpriseId)) {
					product product = list.get(groupPosition).getProList()
							.get(childPosition);
					Bundle b = new Bundle();
					b.putSerializable("product", product);
					b.putInt("group", groupPosition);
					b.putInt("child", childPosition);
					changeViewForResult(productAddActivity.class, b,
							CommonUtil.PRO_ADD_RESULT_FLAG);
				}
				// 查看企业详情界面
				else {
					LoggerUtil.i("sfsdfsdf");
					product product = list.get(groupPosition).getProList()
							.get(childPosition);
					Bundle b = new Bundle();
					b.putSerializable("product", product);
					changeView(productShowActivity.class, b);
				}
				// TODO Auto-generated method stub
				return false;

			}
		});
		// TODO Auto-generated method stub
		listView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// TODO Auto-generated method stub
				listView.expandGroup(groupPosition);
			}
		});

	}

	private void initNavigation() {
		if (!TextUtils.isEmpty(enterpriseId)) {
			initNavitation(getString(R.string.me_string_pro_list),
					enterpriseWqh, "", -1, new EditClickListener() {
						@Override
						public void editClick() {

							// TODO Auto-generated method stub
						}

						@Override
						public void backClick() {
							unregisterBroadcaseReceiver();
							finish();
							animOut();
							// TODO Auto-generated method stub
						}
					});
		} else {
			initNavitation(getString(R.string.me_string_pro_list),
					getString(R.string.string_new_add),
					R.drawable.title_btn_right_click, new EditClickListener() {
						@Override
						public void editClick() {
							changeViewForResult(productAddActivity.class,
									CommonUtil.PRO_ADD_RESULT_FLAG, 1);

							// TODO Auto-generated method stub
						}

						@Override
						public void backClick() {
							unregisterBroadcaseReceiver();
							finish();
							animDown(); // TODO Auto-generated method stub
						}
					});
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		{
			super.onActivityResult(requestCode, resultCode, data);
			if (data != null) {

				if (resultCode == CommonUtil.PRODUCT_RESULT_FLAG) {

					int flag = data.getIntExtra("flag", -1);
					// 删除
					if (flag == 2) {
						showRightDialog();
						int groupPosition = data.getIntExtra("group", -1);
						int childPosition = data.getIntExtra("child", -1);
						if (groupPosition >= 0 && childPosition >= 0) {
							list.get(groupPosition).getProList()
									.remove(childPosition);
							if (list.get(groupPosition).getProList().size() == 0) {
								list.remove(groupPosition);
							}
							adapter.notifyDataSetChanged();

						}
						if (list.size() >= 0) {
							listView.setPullRefreshEnable(false);
							onLoad(listView);

						}
					}
				}
			}
		}
	}

	/**
	 * p 需修改或者添加的产品 flag 1表示编辑产品 2表示添加产品
	 * */
	protected void CheckCate(product p, int flag, int groupPosition,
			int childPosition) {
		boolean tmp = true;

		for (int i = 0; i < list.size(); i++) {
			if (p.getCateId().equals(list.get(i).getId())) {
				// 编辑产品
				if (flag == 1) {

					list.get(i).getProList().set(childPosition, p);
					if (groupPosition != i) {
						list.get(groupPosition).getProList()
								.remove(childPosition);
						if (list.get(groupPosition).getProList().size() == 0) {
							list.remove(groupPosition);
						}
					}

				} else if (flag == 2) {
					list.get(i).getProList().add(p);
				}
				tmp = false;
				break;

			}
		}
		if (tmp) {
			productList tmplist = new productList();
			tmplist.setId(p.getCateId());
			tmplist.setName(p.getCateName());
			tmplist.getProList().add(p);
			list.add(0, tmplist);
		}

	}

	protected void onResume() {
		super.onResume();

	}

	private Handler ClickHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				final product product = list.get(msg.arg1).getProList()
						.get(msg.arg2);
				for (photoModel pic : product.getPicList()) {
					pic.setShareModel(product);
					pic.setShareType(photoModel.PRODUCT_SHARE_FLAG);
				}
				scanPhoto(product.getPicList(), 0, false, false, null);
			}
			// 提交图片
			else if (msg.what == 2) {

				adapter.notifyDataSetChanged();

			}
		}
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (list.size() == 0) {
			initData();
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

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

	public static final String PART_BRO_ACTION_NAME = "com.wq.product.send";

	public DataReceiver dataReceiver = null;

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(PART_BRO_ACTION_NAME);
			filter.addAction(mainActivity.deyServiceBor);
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
				// 批量上传的标识
				boolean isCurPage = intent.getBooleanExtra("curPage", false);
				boolean repCommitHttp = intent.getBooleanExtra("listFlag",
						false);

				boolean sendFlag = intent.getBooleanExtra("sendFlag", false);// 添加和修改产品的标识
				// 添加，更新后，访问网络后的结果
				if (!repCommitHttp && !sendFlag) {
					if (intent.getSerializableExtra("product") == null)
						return;
					product ec = (product) intent
							.getSerializableExtra("product");
					boolean isExist = false;
					for (int i = 0; i < list.size(); i++) {
						for (int j = 0; j < list.get(i).getProList().size(); j++) {
							product item = list.get(i).getProList().get(j);
							if ((!TextUtils.isEmpty(item.getId()) && item
									.getId().equals(ec.getId()))) {
								list.get(i).getProList().remove(j);
								if (list.get(i).getProList().size() == 0) {
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
					adapter.notifyDataSetChanged();
					for (int i = 0; i < adapter.getGroupCount(); i++) {
						listView.expandGroup(i);
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
						product p = (product) intent
								.getSerializableExtra("product");
						if (p != null) {
							explandData(p);
							adapter.notifyDataSetChanged();
							for (int i = 0; i < adapter.getGroupCount(); i++) {
								listView.expandGroup(i);
							}
						}
					}
					// 更新公告
					else if (flag == 1) {
						boolean isExit = false;
						product n = (product) intent
								.getSerializableExtra("product");
						if (n != null) {
							for (int i = 0; i < list.size(); i++) {
								for (int j = 0; j < list.get(i).getProList()
										.size(); j++) {
									product item = list.get(i).getProList()
											.get(j);
									if ((!TextUtils.isEmpty(item.getId()) && n
											.getId().equals(item.getId()))) {

										list.get(i).getProList().remove(j);
										if (list.get(i).getProList().size() == 0) {
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
								listView.expandGroup(i);
							}
						}
					}
				} else {
					@SuppressWarnings("unchecked")
					ArrayList<product> tmplist = (ArrayList<product>) intent
							.getSerializableExtra("productList");
					for (int i = 0; i < list.size(); i++) {
						for (int j = 0; j < list.get(i).getProList().size(); j++) {
							product a = list.get(i).getProList().get(j);
							for (product tmp : tmplist) {
								if ((!TextUtils.isEmpty(tmp.getId()) && a
										.getId().equals(tmp.getId()))) {
									list.get(i).getProList().set(j, tmp);

									break;
								}
							}
						}
					}

					adapter.notifyDataSetChanged();
				}
				if (list.size() >= 0) {
					listView.setPullRefreshEnable(false);
					onLoad(listView);
				}
			}
		}
	}

	//
	private void DeleLocalData() {
		if (list.size() <= 0)
			return;

		StringBuilder sbPicBuilder = new StringBuilder();
		StringBuilder sbPicBuilder1 = new StringBuilder();
		sbPicBuilder.append(" ( ");
		for (int i = 0; i < list.size(); i++) {
			productList itemlist = list.get(i);
			for (int j = 0; j < itemlist.getProList().size(); j++) {
				product itemProduct = list.get(i).getProList().get(j);
				// 删除产品列表
				sbPicBuilder.append("'");
				sbPicBuilder.append(itemProduct.getId());
				sbPicBuilder.append("'");
				sbPicBuilder.append(" ,");
			}
			// 删除产品相关列表
		}
		LoggerUtil.i("lenth1=" + sbPicBuilder.length() + ",,,,,lenght2="
				+ sbPicBuilder.toString().length());
		if (sbPicBuilder.toString().length() > 1) {

			sbPicBuilder1.append(sbPicBuilder.toString().substring(0,
					sbPicBuilder.toString().length() - 1));
		}
		sbPicBuilder1.append(" ) ");
		try {
			db.deleteByWhere(product.class, "userid = '" + User.id + "'");
			db.deleteByWhere(photoModel.class,
					" ecid in " + sbPicBuilder1.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}
