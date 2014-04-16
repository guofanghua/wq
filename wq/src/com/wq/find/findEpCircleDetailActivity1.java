package com.wq.find;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.webviewShowActivity;
import com.wq.Adapter.findEpCircleDetailAdapter;
import com.wq.Adapter.findEpCircleDetailAdapter1;
import com.wq.Interface.IecCircleInterface;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;

import com.wq.me.showNoticeDetailActivity;
import com.wq.model.User;
import com.wq.model.ablumList;
import com.wq.model.ecCircleModel;
import com.wq.model.ecCircleTopModel;
import com.wq.model.ecParentCircleModel;
import com.wq.model.leaveMessage;
import com.wq.model.photoModel;
import com.wq.partner.part_DetailActivity;
import com.wq.utils.CommonUtil;

import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class findEpCircleDetailActivity1 extends BaseActivity implements
		IXExplandListViewListener, IecCircleInterface {
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.list_main)
	XExpandableListView listview;
	findEpCircleDetailAdapter1 adapter;
	private int currentPage = 0;
	private static final int pageSize = 10;
	int position = 0;
	String enterpriseId = "";
	String title = "";
	ArrayList<ecParentCircleModel> plist = new ArrayList<ecParentCircleModel>();
	ecParentCircleModel parent = new ecParentCircleModel();
	ecCircleTopModel topModel = new ecCircleTopModel();
	FinalDb db;
	int newAddCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_ec_circle_detail_activity1);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initUI();
		initListener();
		initnavigation();
		registerBoradcastReceiver();
		// httpData();
	}

	private void initUI() {
		enterpriseId = this.getIntent().getStringExtra("enterpriseId");
		title = this.getIntent().getStringExtra("title");
		adapter = new findEpCircleDetailAdapter1(this, plist, topModel, this);
		localData(currentPage);
		listview.setPullRefreshEnable(false);
		listview.setXListViewListener(this);
		// listview.startRefresh(this);
		listview.setAdapter(adapter);
		// 展开所有项
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			listview.expandGroup(i);
		}
		httpData(0);
	}

	private void initListener() {
		listview.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// TODO Auto-generated method stub
				listview.expandGroup(groupPosition);
			}
		});

	}

	// 获取企业圈
	// 0标示刷新，重新获取 1标示加载更多
	// 获取企业圈
	// 0标示刷新，重新获取 1标示加载更多
	private void httpData(int currPage) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);

		params.put(httpUtil.CIRCLE_CUR_PAGE, currPage + "");

		httpUtil.post(httpUtil.OPERATE_TRADE_LIST_URL, params,
				new AjaxCallBack<String>() {
					// private String errMsg = "";
					private String errcode = "";

					public void onStart() {
					}

					@Override
					public void onSuccess(String result) {
						try {

							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							// errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								// allCount = Integer.parseInt(jsonResult
								// .getString("allcount"));
								JSONObject enterprise = jsonResult
										.getJSONObject("enterprise");
								topModel.setBigImageUrl(enterprise
										.getString("propagandaFile"));
								topModel.setEnterpriseId(enterprise
										.getString("id"));

								topModel.setIcon(enterprise.getString("icon"));
								
								topModel.setWqh(enterprise.getString("wqh"));
								adapter.topModel = topModel;
								try {
									if (db.findAllByWhere(
											ecCircleTopModel.class,
											"userId='"
													+ User.id
													+ "' and enterpriseId='"
													+ topModel
															.getEnterpriseId()
													+ "'").size() > 0) {
										db.update(topModel);
									} else
										db.save(topModel);
								} catch (Exception e) {
									e.printStackTrace();
								}
								JSONArray ecArr = jsonResult
										.getJSONArray("tradeArray");
								for (int i = 0; i < ecArr.length(); i++) {
									JSONObject o = ecArr.getJSONObject(i);
									ecCircleModel ec = new ecCircleModel();
									ec.setId(o.getString("id"));
									ec.setType(o.getString("type"));
									ec.setCotent(o.getString("content"));
									ec.setTime(o.getString("time"));
									ec.setOtherId(o.getString("otherId"));
									ec.setIsListFlag("1");
									// 企业
									JSONObject en = o
											.getJSONObject("enterprise");
									ec.setVqh(en.getString("wqh"));
									ec.setName(en.getString("name"));
									ec.setEnterpriseId(en.getString("id"));
									ec.setLogoUrl(en.getString("icon"));
									ec.setIsCertification(en
											.getString("isCertification"));
									ec.setBigBgUrl(en
											.getString("propagandaFile"));
									// 图片
									JSONArray picArr = o
											.getJSONArray("imgArray");
									for (int j = 0; j < picArr.length(); j++) {
										JSONObject picObject = picArr
												.getJSONObject(j);
										photoModel pic = new photoModel();
										// pic.setId(picObject.getString("imgId"));
										pic.setImageUrl(picObject
												.getString("imgUrl"));
										pic.setShareType(Integer.parseInt(ec
												.getType()));
										pic.setEcid(ec.getId());
										pic.setShareModel(ec);
										ec.getImgList().add(pic);
									}
									// 留言
									JSONArray msgArr = o
											.getJSONArray("commentArray");
									ArrayList<leaveMessage> tmpList = new ArrayList<leaveMessage>();
									for (int k = 0; k < msgArr.length(); k++) {
										JSONObject msgObject = msgArr
												.getJSONObject(k);
										leaveMessage msgModel = new leaveMessage();
										msgModel.setComId(msgObject
												.getString("comId"));
										msgModel.setSuperComId(msgObject
												.getString("superComId"));
										msgModel.setTime(dateUtil
												.SubDate(msgObject
														.getString("time")));
										msgModel.setContent(msgObject
												.getString("content"));
										msgModel.setType(msgObject
												.getString("type"));
										msgModel.setLevel(msgObject
												.getString("level"));
										msgModel.setEcid(ec.getId());
										// 企业信息
										JSONObject ec1 = msgObject
												.getJSONObject("enterprise");
										msgModel.setVqh(ec1.getString("wqh"));
										msgModel.setEnterPriseId(ec1
												.getString("id"));
										msgModel.setLogoUrl(ec1
												.getString("icon"));
										msgModel.setEcid(ec.getId());

									}
									checkEcIsExist(ec);
								}
								adapter.notifyDataSetChanged();
								for (int i = 0; i < adapter.getGroupCount(); i++) {
									listview.expandGroup(i);
								}

							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								// noData(0);
							}

						} catch (JSONException e) {
							// noData(1);
							CommonUtil.showToast(
									findEpCircleDetailActivity1.this,
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
						// noData(1);

						onLoad(listview);
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(
								findEpCircleDetailActivity1.this, errorNo);
					}
				});

	}

	// 删除企业圈信息
	private void httpDelData(String tradeId) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.CIRCLE_DEL_TRADE_ID, tradeId);
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.CIRCLE_PRO_DEL_URL, params,
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
								// list.remove(position);
								adapter.notifyDataSetChanged();
								for (int i = 0; i < adapter.getGroupCount(); i++) {
									listview.expandGroup(i);
								}
							}
							CommonUtil.showToast(
									findEpCircleDetailActivity1.this, errMsg);

						} catch (JSONException e) {
							CommonUtil.showToast(
									findEpCircleDetailActivity1.this,
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
						CommonUtil.showToastHttp(
								findEpCircleDetailActivity1.this, errorNo);
					}
				});

	}

	private void initnavigation() {
		initNavigation(0, getString(R.string.string_back), title, "", "", -1,
				new EditClickListener() {
					@Override
					public void editClick() {
						// changeViewForResult(findAddCircleActivity.class,
						// CommonUtil.CIRCLE_ADD_RESULT_FLAG);
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
	}

	private void localData(int page) {
		if (!db.checkmyTableExist(ecCircleModel.class))
			return;
		// 查询顶部图片
		StringBuilder sb = new StringBuilder();
		sb.append("userId='");
		sb.append(User.id);
		sb.append("'");
		sb.append(" and ");
		sb.append("enterpriseId='");
		sb.append(enterpriseId);
		sb.append("'");

		if (db.findAllByWhere(ecCircleTopModel.class, sb.toString()).size() > 0) {
			topModel = (ecCircleTopModel) db.findAllByWhere(
					ecCircleTopModel.class, sb.toString()).get(0);

			adapter.topModel = topModel;
		}
		// 查询生意圈信息
		String sql = "select * from ecCircleModel where time<=(select min(time) from ( select time from ecCircleModel where userid='"
				+ User.id
				+ "'  and enterpriseId='"
				+ enterpriseId
				+ "'    order by time desc limit 0,"
				+ (currentPage * pageSize + 1 + newAddCount)
				+ ") as t )  and userid='"
				+ User.id
				+ "' and enterpriseId='"
				+ enterpriseId
				+ "'  order by time desc limit 0,"
				+ pageSize
				+ "";
		ArrayList<ecCircleModel> tmplist = new ArrayList<ecCircleModel>();
		try {

			tmplist = (ArrayList<ecCircleModel>) db.findDbListBySQL(
					ecCircleModel.class, sql);
			if (tmplist == null || tmplist.size() == 0) {
				return;
			}

			// 图片
			if (!db.checkmyTableExist(photoModel.class))
				return;
			sb = new StringBuilder();
			sb.append(" (");
			for (int i = 0; i < tmplist.size(); i++) {
				ecCircleModel item = tmplist.get(i);
				
				sb.append("'");
				sb.append(item.getId());
				sb.append("'");
				sb.append(",");
				sb.append("'");
				sb.append("ec" + item.getPid());
				sb.append("'");
				if (i < tmplist.size() - 1)
					sb.append(",");
			}
			sb.append(" )  ");
			// 当自身添加图片时，ec寸的是自增长id
			String sqlWhere = "userId='" + User.id + "'  and ecid in "
					+ sb.toString();
			ArrayList<photoModel> picList = (ArrayList<photoModel>) db
					.findAllByWhere(photoModel.class, sqlWhere);
			for (ecCircleModel item : tmplist) {
				boolean isFlag = false;// 判断本地是否有图片
				for (photoModel pic : picList) {
					// 先查询本地
					if ((item.getId().equals(pic.getEcid()) || pic.getEcid()
							.equals("ec" + item.getPid()))
							&& pic.getFlag() == 1) {
						item.getImgList().add(pic);
						isFlag = true;
					}
				}
				// 再查询服务端
				if (!isFlag) {
					for (photoModel pic : picList) {
						if ((item.getId().equals(pic.getEcid()) || pic
								.getEcid().equals("ec" + item.getPid()))
								&& pic.getFlag() == 0)
							item.getImgList().add(pic);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
		if (tmplist.size() < pageSize)
			listview.setPullLoadEnable(false);
		else
			listview.setPullLoadEnable(true);
		currentPage += tmplist.size() / pageSize;
		for (ecCircleModel tmp : tmplist)
			httpDataExplan(tmp, true);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			// onLoad(listview);
		}

	}

	// 访问网络获取数据进行分组
	private void httpDataExplan(ecCircleModel model, boolean isLoadMore) {

		if (plist.size() == 0) {
			parent = new ecParentCircleModel();
			parent.setTime(model.getTime());
			parent.getEclist().add(model);
			plist.add(parent);
		} else {
			int tmpIndex = -1;
			int count = plist.size();
			boolean flag = false;
			int i = 0;
			for (i = 0; i < count; i++) {
				SimpleDateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");
				if (dateUtil
						.formatDate(plist.get(i).getTime(), formattmp)
						.equals(dateUtil.formatDate(model.getTime(), formattmp))) {
					flag = true;
					break;
				} else if (dateUtil.formatDate(model.getTime(), formattmp)
						.compareTo(
								dateUtil.formatDate(plist.get(i).getTime(),
										formattmp)) < 0) {
					tmpIndex = i;
				} else {
					tmpIndex = count - 1;
				}
			}
			if (flag) {
				parent = plist.get(i);
				parent.getEclist().add(0, model);
			} else {
				parent = new ecParentCircleModel();
				parent.setTime(model.getTime());
				parent.getEclist().add(0, model);
				if (tmpIndex >= 0 && tmpIndex < count - 1) {
					plist.add(tmpIndex, parent);
				} else
					plist.add(parent);
			}
		}
	}

	@Override
	public void onRefresh() {

		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMore() {
		// httpData();
		localData(currentPage);
		httpData(currentPage);
		// TODO Auto-generated method stub
	}

	// 企业圈item中控件点击事件
	@Override
	public void topImageClick(ImageView img) {
		// TODO Auto-generated method stub
	}

	@Override
	public void checkUserClick(ecCircleModel id) {
		// Bundle b = new Bundle();
		// b.putString("enterpriseId", id);
		// changeView(findEpCircleDetailActivity.class, b);
		// Bundle b=new Bundle();
		// TODO Auto-generated method stub

	}

	// 留言
	@Override
	public void btnLeaveClick(int position, leaveMessage msg) {

	}

	// 提交留言

	public void commitClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub

		// httpMsgData();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MMAlert.FLAG_CHOOSE_IMG && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri,
							new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					if (null == cursor) {
						showToast("图片没找到");
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Intent intent = new Intent(this, ClipActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				} else {
					Intent intent = new Intent(this, ClipActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				}
			}
		}
		// 修改大图
		else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		} else if (resultCode == CommonUtil.CIRCLE_ADD_RESULT_FLAG) {
			if (data != null) {
				ecCircleModel item = (ecCircleModel) data
						.getSerializableExtra("circleModel");
				// this.list.add(0, item);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void btnDelClick(int position, String tradeId) {
		// TODO Auto-generated method stub
		this.position = position;
		httpDelData(tradeId);
	}

	// 查看企业内容详情
	@Override
	public void contentClick(ecCircleModel model) {

		Bundle b = new Bundle();
		b.putSerializable("circleModel", model);
		changeView(findTipDetailAcitivy.class, b);
	}

	// 浏览图片
	@Override
	public void scanPicClick(ecCircleModel model) {
		scanPhoto(model.getImgList(), 0, false, false, true, null);
		// TODO Auto-generated method stub

	}

	@Override
	public void delMsgClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub
		this.position = position;
		// httpDelMsgData(msg);
	}

	@Override
	public void checkUeserDetailClick(ecCircleModel model) {
		// TODO Auto-generated method stubid
		Bundle b = new Bundle();
		b.putString("id", model.getEnterpriseId());
		b.putString("channel", "6");
		changeView(part_DetailActivity.class, b);
		// TODO Auto-generated method stub

	}

	@Override
	public void commitRep(int position, ecCircleModel model) {
		// TODO Auto-generated method stub

	}

	// 检测model并保存一直的数据
	private void checkEcIsExist(ecCircleModel ec) {

		if (plist.size() == 0) {
			httpDataExplan(ec, false);
			try {
			
				db.save(ec);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			checkMessage(-1, 0, ec);
			addPic(ec);
		} else {
			boolean isExist = false;

			for (int i = 0; i < plist.size(); i++) {
				ecParentCircleModel p = plist.get(i);
				// 当出现某个分组的时间比需要添加的时间小或者相等时
				for (int j = 0; j < p.getEclist().size(); j++) {
					ecCircleModel m = p.getEclist().get(j);
					if (ec.getId().equals(m.getId())) {
						p.getEclist().set(j, ec);
						try {
						
							db.update(ec);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						checkMessage(i, j, ec);
						isExist = true;
						break;
					}

				}
				if (isExist)
					break;
				if (i == plist.size() - 1) {
					httpDataExplan(ec, false);
					try {
						
						db.save(ec);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					checkMessage(-1, 0, ec);
					addPic(ec);
					break;
				}

			}
		}
	}

	// 检测msg
	private void checkMessage(int groupPosition, int childPosition,
			ecCircleModel ec) {

		// 新添加的
		if (groupPosition == -1) {
			for (leaveMessage msg : ec.getMsgList()) {
				try {
					db.save(msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else {
			ecCircleModel item = plist.get(groupPosition).getEclist()
					.get(childPosition);
			for (leaveMessage msg : item.getMsgList()) {
				boolean notIsExit = true;
				for (leaveMessage msg1 : ec.getMsgList()) {
					if (msg1.getComId().equals(msg.getComId())) {
						notIsExit = false;
						break;
					}
				}
				if (notIsExit) {
					try {
						db.save(msg);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	// 添加图片
	private void addPic(ecCircleModel ec) {
		for (photoModel item : ec.getImgList()) {
			try {
				db.save(item);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

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
			
				boolean delFlag = intent.getBooleanExtra("delFlag", false);
				ecCircleModel ec = (ecCircleModel) intent
						.getSerializableExtra("circleModel");
			
				if (ec == null)
					return;
				if (delFlag) {
					for (int i = 0; i < plist.size(); i++) {
						for (int j = 0; j < plist.get(i).getEclist().size(); j++) {
							ecCircleModel item = plist.get(i).getEclist()
									.get(j);
							if (item.getId().equals(ec.getId())
									|| item.getPid() == ec.getPid()) {
								plist.get(i).getEclist().remove(i);
								if (plist.get(i).getEclist().size() == 0) {
									plist.remove(i);
								}
								break;
							}
						}
					}
					adapter.notifyDataSetChanged();
					for (int i = 0; i < adapter.getGroupCount(); i++) {
						listview.expandGroup(i);
					}
				}
			}
		}
	}

}
