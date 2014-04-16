package com.wq.find;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.endure.wq.R.string;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.loginActivity;
import com.wq.mainActivity;
import com.wq.webviewShowActivity;
import com.wq.Adapter.findEpCircleListAdapter;
import com.wq.Interface.IecCircleInterface;
import com.wq.UI.CircleAnimation;
import com.wq.UI.ClipActivity1;
import com.wq.UI.MMAlert;
import com.wq.UI.circleListView;
import com.wq.UI.circleListView.IXListViewListener;
import com.wq.me.productShowActivity;
import com.wq.me.showNoticeDetailActivity;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.ecCircleModel;
import com.wq.model.ecCircleTopModel;
import com.wq.model.leaveMessage;
import com.wq.model.partComModel;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;

import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/**
 * 企业圈列表
 * */
public class findEpCircleListActivity extends BaseActivity implements
		IXListViewListener, IecCircleInterface {
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;

	@ViewInject(id = R.id.list_main)
	circleListView listview;
	// 评论输入界面
	@ViewInject(id = R.id.ec_circle_layout_bottom)
	LinearLayout layout_bottom;
	@ViewInject(id = R.id.ec_circle_edt_msg)
	EditText edit_msg;
	@ViewInject(id = R.id.ec_circle_btn_msg)
	Button btn_msg;
	ArrayList<ecCircleModel> list = new ArrayList<ecCircleModel>();
	findEpCircleListAdapter adapter;
	ImageView img_top = null;
	private int currentPage = 0;
	private int allCount = 0;
	private int localAllCount = 0;
	private static final int pageSize = 10;
	int position = 0;
	FinalDb db;
	ecCircleTopModel topModel = new ecCircleTopModel();
	public FinalBitmap finalBitmap;
	BitmapDisplayConfig displayConfig = null;
	public static final boolean isHttp = false;
	private static int errCount = 0;
	private boolean isFirstLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_ec_circle_list_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		finalBitmap = FinalBitmap.create(this);
		initUI();
		initListener();
		initnavigation();
		registerBoradcastReceiver();

	}

	private void initUI() {
		// 查询本地数据库
		localData(currentPage);
		listview.setXListViewListener(this);
		listview.setEcListener(this);

		listview.startRefresh();
		finalBitmap.display(listview.img_logo, topModel.getIcon());
		if (!TextUtils.isEmpty(topModel.getBigImageUrl())) {
			finalBitmap.display(listview.imagetop, topModel.getBigImageUrl());
		}
		initListViewHead();

		// listview.startRefresh(this);
		adapter = new findEpCircleListAdapter(findEpCircleListActivity.this,
				list, findEpCircleListActivity.this);
		listview.setAdapter(adapter);
		// httpData(true);
	}

	private void initListViewHead() {
		listview.topModel = topModel;
		if (topModel.getSendFaulireCount() > 0) {
			listview.txt_sendWarn.setText(String.format(
					this.getString(R.string.find_ec_send_warn),
					topModel.getSendFaulireCount() + ""));
			listview.layout_send.setVisibility(View.VISIBLE);
		} else
			listview.layout_send.setVisibility(View.GONE);
		// 又最新评论
		if (User.bArr[4]) {
			listview.layout_count.setVisibility(View.VISIBLE);
			listview.txt_count.setText(String.format(
					this.getString(R.string.find_string_ec_tip_msg),
					User.tipCountArr[2]));
		} else {
			listview.layout_count.setVisibility(View.GONE);
		}

	}

	private void localData(int page) {
		// 查询顶部图片
		StringBuilder sb = new StringBuilder();
		sb.append("userId='");
		sb.append(User.id);
		sb.append("'");
		sb.append(" and ");
		sb.append("enterpriseId='");
		sb.append(User.id);
		sb.append("'");
		try {
			if (db.checkmyTableExist(ecCircleTopModel.class)) {
				topModel = (ecCircleTopModel) db.findAllByWhere(
						ecCircleTopModel.class, sb.toString()).get(0);
			}
		} catch (Exception e) {
		}
		if (!db.checkmyTableExist(ecCircleModel.class))
			return;
		// 判断是否有未传成功的企业信息
		String sqlCheck = "select id from ecCircleModel where isSend='1' and userId='"
				+ User.id + "' and enterpriseId='" + User.id + "'";
		try {
			errCount = db.findDbModelListBySQL(sqlCheck).size();
			if (errCount > 0) {
				topModel.setSendFaulireCount(errCount);
				topModel.setSendFaulire(true);
				initListViewHead();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 查询生意圈信息
		String sql = "select * from ecCircleModel where time<=(select min(time) from ( select time from ecCircleModel where userid='"
				+ User.id
				+ "' and isListFlag='0'  order by time desc limit 0,"
				+ (list.size() + 1)
				+ ") as t )  and userid='"
				+ User.id
				+ "'  and isListFlag='0'  order by time desc limit 0,"
				+ pageSize + "";
		ArrayList<ecCircleModel> tmplist = new ArrayList<ecCircleModel>();
		try {
			tmplist = (ArrayList<ecCircleModel>) db.findDbListBySQL(
					ecCircleModel.class, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb = new StringBuilder();
		sb.append(" (");
		for (int i = 0; i < tmplist.size(); i++) {

			ecCircleModel item = tmplist.get(i);
			sb.append("'");
			sb.append(item.getId());
			sb.append("'");
			if (i < tmplist.size() - 1)
				sb.append(",");
		}
		sb.append(" )  ");
		String sqlWhere = "userId='" + User.id + "' and ecid in "
				+ sb.toString();
		// 查询留言信息

		try {
			if (!db.checkmyTableExist(leaveMessage.class))
				return;
			ArrayList<leaveMessage> msgList = (ArrayList<leaveMessage>) db
					.findAll(leaveMessage.class, sqlWhere);
			for (ecCircleModel item : tmplist) {
				ArrayList<leaveMessage> msgTempList = new ArrayList<leaveMessage>();
				for (leaveMessage msg : msgList) {
					if (item.getId().equals(msg.getEcid())) {
						if (msg.getType().equals("0"))
							msgTempList.add(msg);
						else
							item.getZanList().add(msg);
					}
				}
				if (msgTempList.size() > 0)
					item.getMsgList().addAll(
							CommonUtil.sortMsgList(msgTempList));
			}

			// 图片
			sb = new StringBuilder();
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
			sqlWhere = "userId='" + User.id + "'  and ecid in " + sb.toString();
			if (!db.checkmyTableExist(photoModel.class))
				return;
			ArrayList<photoModel> picList = (ArrayList<photoModel>) db
					.findAllByWhere(photoModel.class, sqlWhere);
			for (ecCircleModel item : tmplist) {

				boolean isFlag = false;// 判断本地是否有图片
				for (photoModel pic : picList) {

					// 先查询本地
					if ((item.getId().equals(pic.getEcid()) || pic.getEcid()
							.equals("circleModel" + item.getPid()))
							&& pic.getFlag() == 1) {
						item.getImgList().add(pic);
						isFlag = true;
					}
				}
				// 再查询服务端
				if (!isFlag) {
					for (photoModel pic : picList) {
						if ((item.getId().equals(pic.getEcid()) || pic
								.getEcid()
								.equals("circleModel" + item.getPid()))
								&& pic.getFlag() == 0) {
							item.getImgList().add(pic);
						}
					}
				}
			}
			list.addAll(tmplist);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
		if (tmplist.size() < pageSize)
			listview.setPullLoadEnable(false);
		else
			listview.setPullLoadEnable(true);
		currentPage = list.size() / pageSize;// 计算需要查询的页数
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}

	}

	private void initListener() {
		listview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (String.valueOf(layout_bottom.getTag()).equals("1")) {
						layout_bottom.setVisibility(View.GONE);
						layout_bottom.setTag("0");
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(
										findEpCircleListActivity.this
												.getCurrentFocus()
												.getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
					}
					break;
				}

				return false;
			}
		});
		btn_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(edit_msg.getText())) {
					CommonUtil.showToast(getApplicationContext(),
							R.string.find_string_ec_send_warn);
					return;
				}
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(findEpCircleListActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				layout_bottom.setVisibility(View.GONE);
				layout_bottom.setTag("0");
				httpMsgData();

			}
		});
	}

	// 获取企业圈
	// 0标示刷新，重新获取 1标示加载更多
	private void httpData(final boolean isRfresh) {

		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_PRISE_ID, "");

		params.put(httpUtil.CIRCLE_CUR_PAGE, currentPage + "");

		// if (list.size() > 0) {
		// params.put(httpUtil.CIRCLE_CUR_TIME, list.get(0).getTime());
		// }

		// if (flag == 0 && list.size() == 0)
		// showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.OPERATE_TRADE_LIST_URL, params,
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
								isFirstLoad = false;
								allCount = Integer.parseInt(jsonResult
										.getString("allcount"));
							
								JSONObject enterprise = jsonResult
										.getJSONObject("enterprise");
								topModel.setBigImageUrl(enterprise
										.getString("propagandaFile"));
								topModel.setEnterpriseId(enterprise
										.getString("id"));
								topModel.setIcon(enterprise.getString("icon"));
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
									if (isRfresh)
										initListViewHead();
								} catch (Exception e) {
									e.printStackTrace();
								}
								// User.wqh = enterprise.getString("wqh");
								// User.name = enterprise.getString("name");
								User.isCertification = enterprise
										.getString("isCertification");
								User.iconFile = enterprise.getString("icon");
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
									// 企业
									JSONObject en = o
											.getJSONObject("enterprise");
									ec.setVqh(en.getString("wqh"));
									ec.setName(en.getString("name"));
									ec.setEnterpriseId(en.getString("id"));
									ec.setLogoUrl(en.getString("icon"));
									ec.setIsCertification(en
											.getString("isCertification"));
									ec.setIsListFlag("0");
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
										pic.setShareModel(ec);
										pic.setEcid(ec.getId());
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
										msgModel.setEcid(ec.getId());
										msgModel.setTime(dateUtil
												.SubDate(msgObject
														.getString("time")));
										msgModel.setContent(msgObject
												.getString("content"));
										msgModel.setType(msgObject
												.getString("type"));
										msgModel.setLevel(msgObject
												.getString("level"));
										// 企业信息
										JSONObject ec1 = msgObject
												.getJSONObject("enterprise");
										msgModel.setVqh(ec1.getString("wqh"));
										msgModel.setEnterPriseId(ec1
												.getString("id"));
										msgModel.setLogoUrl(ec1
												.getString("icon"));
										// 保存留言

										if (msgModel.getType().equals("0"))
											tmpList.add(msgModel);
										else
											ec.getZanList().add(msgModel);
										// CommonUtil.sortMsgList(tmpList);

									}
									ec.getMsgList().addAll(
											CommonUtil.sortMsgList(tmpList));
									checkEcIsExist(ec);
								}
								adapter.notifyDataSetChanged();
							}

						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							listview.onLoad();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						listview.onLoad();
					}
				});

	}

	private int tmp = 0;

	// 提交留言
	private void httpMsgData() {
		tmp = 0;
		final leaveMessage msg = (leaveMessage) edit_msg.getTag();
		msg.setEnterPriseId(User.id);
		msg.setVqh(User.wqh);
		msg.setLogoUrl(User.iconFile);
		msg.setSend(true);
		msg.setName(User.name);
		if (TextUtils.isEmpty(msg.getContent()))
			msg.setContent(edit_msg.getText().toString());
		if (msg.getType().equals("0")) {
			boolean flag = true;
			boolean flag1 = true;
			ArrayList<leaveMessage> tmpList = new ArrayList<leaveMessage>();
			for (int i = 0; i < list.get(position).getMsgList().size(); i++) {

				if (msg.getSuperComId().equals(
						list.get(position).getMsgList().get(i).getComId())) {

					flag = false;
					flag1 = false;
				} else if ((!flag1 && !msg.getSuperComId().equals(
						list.get(position).getMsgList().get(i).getSuperComId()))) {
					tmp = i + 1;
					tmpList.add(msg);
					flag1 = true;

				}
				tmpList.add(list.get(position).getMsgList().get(i));
				if (!flag1 && (i == list.get(position).getMsgList().size() - 1)) {
					tmp = i + 1;
					tmpList.add(msg);
					flag1 = true;
				}

			}

			if (flag) {
				if (!msg.isExist())
					list.get(position).getMsgList().add(msg);
			} else {
				list.get(position).setMsgList(tmpList);

			}
		}

		else
			list.get(position).getZanList().add(msg);
		adapter.notifyDataSetChanged();

		//
		// 如果需要企业认证且认证了

		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		if (msg.getType().equals("1")) {
			params.put(httpUtil.MSG_CONTENT, "");
		} else {
			if (!TextUtils.isEmpty(msg.getContent()))
				params.put(httpUtil.MSG_CONTENT, msg.getContent());
			else
				params.put(httpUtil.MSG_CONTENT, edit_msg.getText().toString());
		}
		params.put(httpUtil.MSG_LEVEL, msg.getLevel());
		params.put(httpUtil.MSG_SUPERID, msg.getSuperComId());
		params.put(httpUtil.MSG_TYPE, msg.getType());
		httpUtil.post(httpUtil.CRICLE_MSG_ADD_URL, params,
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
								msg.setComId(jsonResult.getString("id"));

								if (msg.getType().equals("0")) {
									list.get(position).getMsgList()
											.set(tmp, msg);
									edit_msg.setText("");
								} else {
									list.get(position)
											.getZanList()
											.set(list.get(position)
													.getZanList().size() - 1,
													msg);
								}

								// adapter.notifyDataSetChanged();
							}
							// 企业认证跳转
							else if (errcode
									.equals(httpUtil.errCode_isCertification)) {
								msg.setSend(false);

								// list.get(position).getZanList().set(tmp,
								// msg);
								adapter.notifyDataSetChanged();
							} else {
								msg.setSend(false);
								list.get(position).getMsgList().set(tmp, msg);
								adapter.notifyDataSetChanged();
								CommonUtil.showToast(getApplicationContext(),
										errMsg);
							}

						} catch (JSONException e) {
							msg.setSend(false);
							msg.setExist(true);
							list.get(position)
									.getMsgList()
									.set(list.get(position).getMsgList().size() - 1,
											msg);
							adapter.notifyDataSetChanged();
							// CommonUtil.showToast(findEpCircleListActivity.this,
							// R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							try {
								msg.setEcid(list.get(position).getId());
								db.save(msg);
							} catch (Exception ex) {

							}

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						msg.setSend(false);
						try {
							msg.setEcid(list.get(position).getId());
							db.save(msg);
						} catch (Exception ex) {

						}
						adapter.notifyDataSetChanged();
						// TODO Auto-generated method stub

						CommonUtil.showToastHttp(findEpCircleListActivity.this,
								errorNo);
					}
				});
	}

	// 删除企业圈信息
	private void httpDelData(String tradeId) {
		// 如果是本地的
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
								try {
									// 删除企业圈
									db.deleteByWhere(
											ecCircleModel.class,
											"userId='"
													+ User.id
													+ "'  and id='"
													+ list.get(position)
															.getId() + "' ");
									// 删除对于图片
									db.deleteByWhere(photoModel.class,
											"userid='"
													+ User.id
													+ "' and ecid='"
													+ list.get(position)
															.getId() + "'");
									// 删除对应评论
									db.deleteByWhere(leaveMessage.class,
											"userid='"
													+ User.id
													+ "' and ecid='"
													+ list.get(position)
															.getId() + "'");
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								list.remove(position);
								adapter.notifyDataSetChanged();
							}

						} catch (JSONException e) {

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
						CommonUtil.showToastHttp(findEpCircleListActivity.this,
								errorNo);
					}
				});

	}

	// 删除评论
	private void httpDelMsgData(final leaveMessage msg) {
		// 0标示普通评论 1标示赞
		if (msg.getType().equals("0")) {
			for (int i = 0; i < list.get(position).getMsgList().size(); i++) {
				if (list.get(position).getMsgList().get(i).getComId()
						.equals(msg.getComId())) {
					list.get(position).getMsgList().remove(i);
					break;
				}
			}
		} else {
			for (int i = 0; i < list.get(position).getZanList().size(); i++) {
				if (list.get(position).getZanList().get(i).getComId()
						.equals(msg.getComId())) {
					list.get(position).getZanList().remove(i);
					break;
				}
			}
		}
		adapter.notifyDataSetChanged();
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.MSG_DEL_COMMENT_ID, msg.getComId());
		httpUtil.post(httpUtil.CIRCLE_MSG_DEL_URL, params,
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
								try {
									db.deleteByWhere(
											leaveMessage.class,
											"userid='" + User.id
													+ "' and comId='"
													+ msg.getComId() + "'");
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
							// CommonUtil.showToast(findEpCircleListActivity.this,
							// errMsg);
						} catch (JSONException e) {
							CommonUtil.showToast(findEpCircleListActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub

						CommonUtil.showToastHttp(findEpCircleListActivity.this,
								errorNo);
					}
				});
	}

	private void initnavigation() {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(this, 60),
				DensityUtil.dip2px(this, 35));
		initNavigation(0, getString(R.string.string_back),
				getString(R.string.find_string_syq), "", "",
				R.drawable.btn_photo_click, params, new EditClickListener() {

					@Override
					public void editClick() {
						changeViewForResult(findAddCircleActivity.class,
								CommonUtil.CIRCLE_ADD_RESULT_FLAG);
						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						unregisterBroadcaseReceiver();
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});
	}

	@Override
	public void onRefresh() {
		// 重新去加载数据
		if (list.size() > 0) {
			ArrayList<ecCircleModel> tmp = new ArrayList<ecCircleModel>();
			for (int i = 0; i < list.size(); i++) {
				if (i < 10) {
					tmp.add(list.get(i));
				} else
					break;
			}
			list.retainAll(tmp);// 去得两个的交集
		}
		currentPage = 0;
		httpData(true);
	}

	@Override
	public void onLoadMore() {
		currentPage = list.size() / pageSize;
		localData(currentPage);
		httpData(false);
	}

	// 企业圈item中控件点击事件
	@Override
	public void topImageClick(ImageView img) {
		// TODO Auto-generated method stub
		img_top = img;
		MMAlert.showAlertClip(findEpCircleListActivity.this, true, true);
	}

	@Override
	public void checkUserClick(ecCircleModel model) {
		Bundle b = new Bundle();
		b.putString("enterpriseId", model.getEnterpriseId());
		b.putString("title", model.getName());
		changeView(findEpCircleDetailActivity1.class, b);
		// TODO Auto-generated method stub

	}

	// 留言
	@Override
	public void btnLeaveClick(int position, leaveMessage msg) {
		if (!TextUtils.isEmpty(msg.getLevel())
				&& Integer.parseInt(msg.getLevel()) >= 3) {

			return;
		}
	
		this.position = position;
		layout_bottom.setVisibility(View.VISIBLE);
		layout_bottom.setTag("1");
		edit_msg.setSelection(edit_msg.getText().length());
		edit_msg.setTag(msg);
		edit_msg.setHint("@" + msg.getSuperName());
		edit_msg.setFocusableInTouchMode(true);

		edit_msg.requestFocus();

		InputMethodManager inputManager =

		(InputMethodManager) edit_msg.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);

		inputManager.showSoftInput(edit_msg, 0);
		// TODO Auto-generated method stub
	}

	// 提交

	public void commitClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub
		this.position = position;
		edit_msg.setTag(msg);
		httpMsgData();

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
							CommonUtil.showToast(findEpCircleListActivity.this,
									R.string.string_http_err_data);
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
						Toast.makeText(findEpCircleListActivity.this, "图片没找到",
								0).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();

					Intent intent = new Intent(this, ClipActivity1.class);
					intent.putExtra("path", path);

					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				} else {

					Intent intent = new Intent(this, ClipActivity1.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity1.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		} else if (requestCode == MMAlert.FLAG_MODIFY_FINISH
				&& resultCode == RESULT_OK) {
			if (data != null) {

				String picpath = data.getStringExtra("path");
				if (!TextUtils.isEmpty(picpath)) {
					showProgress(R.string.dialog_comitting);
					bigBgHttpData(picpath);
				}

			}
		}
	}

	/**
	 * 更新顶图
	 * */
	public void bigBgHttpData(final String filePath) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);

		try {
			params.put("pic0", new File(filePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		httpUtil.post(httpUtil.CIRCLE_PROGA_PIC_URL, params,
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
								Bitmap b = BitmapFactory.decodeFile(filePath);
								if (img_top != null)
									img_top.setImageBitmap(b);
							}
							CommonUtil.showToast(findEpCircleListActivity.this,
									errMsg);

						} catch (JSONException e) {

							CommonUtil.showToast(findEpCircleListActivity.this,
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

						adapter.notifyDataSetChanged();
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(findEpCircleListActivity.this,
								errorNo);
					}
				});
	}

	@Override
	public void btnDelClick(int position, String tradeId) {
		// TODO Auto-generated method stub
		this.position = position;

		if (TextUtils.isEmpty(tradeId) || tradeId.equals(User.id)) {
			ecCircleModel e = list.get(position);
			try {
				list.remove(position);
				db.delete(e);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			adapter.notifyDataSetChanged();
		} else
			httpDelData(tradeId);
	}

	// 产看详情
	@Override
	public void contentClick(ecCircleModel model) {
		// TODO Auto-generated method stubmo
		// 分享的企业链接
		if (model.getType().equals("3")) {
			Bundle b = new Bundle();
			b.putString("url", model.getOtherId());
			b.putBoolean("isShare", false);
			b.putString("title", model.getName());
			changeView(webviewShowActivity.class, b);

		}
		// 产品
		else if (model.getType().equals("1")) {
			Bundle b = new Bundle();
			b.putString("productId", model.getOtherId());
			changeView(productShowActivity.class, b);
		}
		// 动态
		else if (model.getType().equals("2")) {
			Bundle b = new Bundle();
			b.putString("noticeId", model.getOtherId());
			changeView(showNoticeDetailActivity.class, b);

		}

	}

	@Override
	public void scanPicClick(ecCircleModel model) {
		// TODO Auto-generated method stub
		scanPhoto(model.getImgList(), 0, false, false, true, null);
	}

	@Override
	public void delMsgClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub
		this.position = position;
		httpDelMsgData(msg);

	}

	@Override
	public void checkUeserDetailClick(ecCircleModel model) {
		// TODO Auto-generated method stubid
		Bundle b = new Bundle();
		b.putString("enterpriseId", model.getEnterpriseId());
	
		b.putString("title", model.getName());

		changeView(findEpCircleDetailActivity1.class, b);
		// TODO Auto-generated method stub

	}

	public static final String PART_BRO_ACTION_NAME = "com.wq.fragment.send";

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
				// 批量上传的标志
				boolean repCommitHttp = intent.getBooleanExtra("listFlag",
						false);
				boolean del = intent.getBooleanExtra("delFlag", false);
				boolean sendFlag = intent.getBooleanExtra("sendFlag", false);// 添加和修改产品的标识
				
				// 添加，更新后，访问网络后的结果
				if (!repCommitHttp && !sendFlag && !del) {
					ecCircleModel ec = (ecCircleModel) intent
							.getSerializableExtra("circleModel");
					if (ec == null)
						return;
					for (int i = 0; i < list.size(); i++) {
						ecCircleModel item = list.get(i);
						if ((!TextUtils.isEmpty(ec.getId()) && item.getId()
								.equals(ec.getId()))
								|| (item.getPid() == ec.getPid() && TextUtils
										.isEmpty(ec.getId()))) {
							list.remove(i);
							list.add(0, ec);
							break;
						}
					}
					String sqlCheck = "select id from ecCircleModel where isSend='1' and userId='"
							+ User.id + "' and enterpriseId='" + User.id + "'";
					try {
						errCount = db.findDbListBySQL(ecCircleModel.class,
								sqlCheck).size();
						if (errCount > 0) {
							topModel.setSendFaulireCount(errCount);
							topModel.setSendFaulire(true);
						} else
							topModel.setSendFaulire(false);
						initListViewHead();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					adapter.notifyDataSetChanged();
				}
				// 首次添加和更新
				else if (sendFlag) {
					int flag = intent.getIntExtra("flag", -1);
					// 把添加和更新方广播里面，是为了处理在批量上传列表里，数据修改的时候
					// 添加产品
					if (flag == 0) {
						ecCircleModel ec = (ecCircleModel) intent
								.getSerializableExtra("circleModel");
						if (ec != null) {

							list.add(0, ec);
							adapter.notifyDataSetChanged();
						}
					}
					// 更新公告
					else if (flag == 1) {

						ecCircleModel ec = (ecCircleModel) intent
								.getSerializableExtra("circleModel");
						if (ec == null)
							return;
						for (int i = 0; i < list.size(); i++) {
							ecCircleModel item = list.get(i);
							if ((!TextUtils.isEmpty(ec.getId()) && item.getId()
									.equals(ec.getId()))
									|| (item.getPid() == ec.getPid() && TextUtils
											.isEmpty(ec.getId()))) {
								list.remove(i);
								list.add(0, ec);
								break;

							}
						}
						adapter.notifyDataSetChanged();
					}
				}
				// 一件提交
				else if (repCommitHttp) {
					@SuppressWarnings("unchecked")
					ArrayList<ecCircleModel> tmplist = (ArrayList<ecCircleModel>) intent
							.getSerializableExtra("ecCircleModelList");
			
					errCount = 0;
					for (int i = 0; i < list.size(); i++) {
						ecCircleModel a = list.get(i);
						for (ecCircleModel tmp : tmplist) {
							if ((!TextUtils.isEmpty(tmp.getId()) && a.getId()
									.equals(tmp.getId()))
									|| (a.getPid() == tmp.getPid() && TextUtils
											.isEmpty(tmp.getId()))) {
								list.set(i, tmp);
								if (tmp.getIsSend().equals("1"))
									errCount++;
								break;
							}
						}
					}
				
					listview.topModel.setSendFaulireCount(errCount);
					if (errCount > 0) {
						listview.layout_send.setVisibility(View.VISIBLE);
						listview.topModel.setSendFaulire(true);
					} else {
						listview.topModel.setSendFaulire(false);
						listview.layout_send.setVisibility(View.GONE);
					}
					adapter.notifyDataSetChanged();
				}
				// 删除某条记录
				else if (del) {
				
					boolean isExist = false;
					ecCircleModel ec = (ecCircleModel) intent
							.getSerializableExtra("circleModel");
					for (int i = 0; i < list.size(); i++) {
						if (ec.getPid() == list.get(i).getPid()) {
							list.remove(i);
							errCount--;
							isExist = true;
							break;
						}
					}
					if (isExist) {
						if (errCount > 0) {
							listview.layout_send.setVisibility(View.VISIBLE);
							listview.topModel.setSendFaulire(true);
						} else {
							listview.topModel.setSendFaulire(false);
							listview.layout_send.setVisibility(View.GONE);
						}
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			unregisterBroadcaseReceiver();
			// TODO Auto-generated method stub
			finish();
			animOut();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	// 检测model并保存一直的数据
	private void checkEcIsExist(ecCircleModel ec) {
		int tmpIndex = -1;
		if (list.size() == 0) {
			list.add(ec);
			try {
				db.save(ec);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			checkMessage(-1, ec);
			addPic(ec);
		} else {
			for (int i = 0; i < list.size(); i++) {

				if (ec.getId().equals(list.get(i).getId())) {
					// 如过微企号和logo没有改变，就不更新数据库了
					if (ec.getVqh().equals(list.get(i).getVqh())
							&& ec.getLogoUrl().equals(list.get(i).getLogoUrl())) {
						break;
					}
					list.set(i, ec);
					try {

						db.update(ec);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					checkMessage(i, ec);
					break;
				} else if (ec.getTime().compareTo(list.get(i).getTime()) <= 0
						&& tmpIndex == -1) {
					tmpIndex = i;
				}
				if (i == list.size() - 1) {
					if (tmpIndex == -1)
						list.add(0, ec);
					else
						list.add(tmpIndex, ec);
					try {
						String sql = "userId='" + User.id
								+ "' and  isListFlag='0' and id='" + ec.getId()
								+ "'";
						if (db.findAllByWhere(ecCircleModel.class, sql).size() > 0) {
							db.update(ec);
						} else
							db.save(ec);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					checkMessage(-1, ec);
					addPic(ec);
					break;
				}
			}
		}
	}

	// 检测msg
	private void checkMessage(int position, ecCircleModel ec) {

		// 新添加的
		if (position == -1) {
			for (leaveMessage msg : ec.getMsgList()) {
				try {
					db.save(msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else {
			ecCircleModel item = list.get(position);
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

	// 重新提交数据
	@Override
	public void commitRep(int position, ecCircleModel model) {
		// TODO Auto-generated method stub
		list.remove(position);
		model.setTime(dateUtil.formatDate(new Date()));
		model.setIsSend("0");
		list.add(0, model);
		topModel.setSendFaulireCount(topModel.getSendFaulireCount() - 1);
		initListViewHead();
		adapter.notifyDataSetChanged();
		httpAddData(model);

	}
}
