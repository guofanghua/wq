package com.wq.find;

import java.io.File;
import java.util.ArrayList;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
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

import com.wq.Interface.IecCircleInterface;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;

import com.wq.me.showNoticeDetailActivity;
import com.wq.model.User;
import com.wq.model.ecCircleModel;
import com.wq.model.leaveMessage;
import com.wq.model.photoModel;
import com.wq.partner.part_DetailActivity;
import com.wq.utils.CommonUtil;

import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class findEpCircleDetailActivity extends BaseActivity implements
		IXListViewListener, IecCircleInterface {
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	@ViewInject(id = R.id.list_main)
	XListView listview;
	// 评论输入界面
	@ViewInject(id = R.id.ec_circle_layout_bottom)
	LinearLayout layout_bottom;
	@ViewInject(id = R.id.ec_circle_edt_msg)
	EditText edit_msg;
	@ViewInject(id = R.id.ec_circle_btn_msg)
	Button btn_msg;
	// 没数据显示界面
	@ViewInject(id = R.id.no_data_layout)
	LinearLayout noData_layout;
	@ViewInject(id = R.id.no_data_txt_title)
	TextView txt_no_data;
	@ViewInject(id = R.id.no_data_btn)
	Button btn_no_data;
	@ViewInject(id = R.id.frame_main)
	FrameLayout frame_main;
	ArrayList<ecCircleModel> list = new ArrayList<ecCircleModel>();

	findEpCircleDetailAdapter adapter;
	private int currentPage = 0;
	private int allCount = 0;
	private static final int pageSize = 10;
	int position = 0;
	String enterpriseId = "";
	String title = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_ec_circle_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		initUI();
		initListener();
		initnavigation();
		httpData(0);
	}

	private void initUI() {
		enterpriseId = this.getIntent().getStringExtra("enterpriseId");
		title = this.getIntent().getStringExtra("title");
		listview.setPullRefreshEnable(false);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		adapter = new findEpCircleDetailAdapter(this, list, enterpriseId, this);
		// listview.setAdapter(adapter);
		setViewTouchListener(listview);

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
										findEpCircleDetailActivity.this
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
						.hideSoftInputFromWindow(
								findEpCircleDetailActivity.this
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
	// 获取企业圈
	// 0标示刷新，重新获取 1标示加载更多
	private void httpData(final int flag) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		params.put(httpUtil.CIRCLE_CUR_PAGE, currentPage + "");
		if (flag == 0 && list.size() == 0)
			showProgress(R.string.dialog_loading);
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
								allCount = Integer.parseInt(jsonResult
										.getString("allcount"));
								JSONObject enterprise = jsonResult
										.getJSONObject("enterprise");
								adapter.bigImageUrl = enterprise
										.getString("propagandaFile");
								adapter.smallImageUrl = enterprise
										.getString("icon");
								adapter.enterpriseId = enterprise
										.getString("id");
								adapter.wqh = enterprise.getString("wqh");
								if (flag == 0) {
									list.clear();
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

										// 企业信息
										JSONObject ec1 = msgObject
												.getJSONObject("enterprise");
										msgModel.setVqh(ec1.getString("wqh"));
										msgModel.setEnterPriseId(ec1
												.getString("id"));
										msgModel.setLogoUrl(ec1
												.getString("icon"));
										if (msgModel.getType().equals("0"))
											tmpList.add(msgModel);

										else
											ec.getZanList().add(msgModel);
									}
									ec.getMsgList().addAll(
											CommonUtil.sortMsgList(tmpList));
									list.add(ec);
								}
								currentPage = list.size() / pageSize;
								if (flag == 0)
									listview.setAdapter(adapter);
								else
									adapter.notifyDataSetChanged();
								noData_layout.setVisibility(View.GONE);
								frame_main.setVisibility(View.VISIBLE);

							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								noData(0);
							}

						} catch (JSONException e) {
							noData(1);
							CommonUtil.showToast(
									findEpCircleDetailActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							LoggerUtil.i(list.size() + "," + allCount);
							if (list.size() >= allCount
									|| list.size() < pageSize) {
								listview.setPullLoadEnable(false);
							} else {
								listview.setPullLoadEnable(true);
							}
							listview.setPullRefreshEnable(true);

							// listview.setPullLoadEnable(true);
							hideProgress();
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
						hideProgress();
						CommonUtil.showToastHttp(
								findEpCircleDetailActivity.this, errorNo);
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
		if (!msg.isExist())
			msg.setExist(false);
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
							hideProgress();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						msg.setSend(false);
						adapter.notifyDataSetChanged();
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(
								findEpCircleDetailActivity.this, errorNo);
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
								list.remove(position);
								adapter.notifyDataSetChanged();
							}
							CommonUtil.showToast(
									findEpCircleDetailActivity.this, errMsg);

						} catch (JSONException e) {
							CommonUtil.showToast(
									findEpCircleDetailActivity.this,
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
								findEpCircleDetailActivity.this, errorNo);
					}
				});

	}

	// 删除评论
	private void httpDelMsgData(leaveMessage msg) {
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

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {
						// try {
						// //JSONObject jsonResult = new JSONObject(result);
						// //errcode = jsonResult.getString(httpUtil.ERR_CODE);
						// //errMsg = jsonResult.getString(httpUtil.ERR_MGS);
						// //
						// CommonUtil.showToast(findEpCircleDetailActivity.this,
						// // errMsg);
						// } catch (JSONException e) {
						// CommonUtil.showToast(
						// findEpCircleDetailActivity.this,
						// R.string.string_http_err_data);
						// e.printStackTrace();
						// } finally {
						//
						// }
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub

						CommonUtil.showToastHttp(
								findEpCircleDetailActivity.this, errorNo);
					}
				});
	}

	private void initnavigation() {
		if (TextUtils.isEmpty(title)) {
			title = getString(R.string.find_string_my_cir);
		}
		initNavigation(0, getString(R.string.string_back), title, "", "", -1,
				new EditClickListener() {

					@Override
					public void editClick() {
						changeViewForResult(findAddCircleActivity.class,
								CommonUtil.CIRCLE_ADD_RESULT_FLAG);
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

	// 没数据
	/** flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载 */
	private void noData(int flag) {
		if (list.size() <= 0) {
			noData_layout.setVisibility(View.VISIBLE);
			frame_main.setVisibility(View.GONE);
		}
		if (flag == 0) {
			txt_no_data.setText(String.format(
					getString(R.string.string_no_data_txt_add), title));
			btn_no_data.setVisibility(View.GONE);
		} else {
			txt_no_data.setText(R.string.string_no_data_txt_rep);
			btn_no_data.setText(R.string.string_no_data_btn_rep);
			btn_no_data.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					httpData(0);
					// TODO Auto-generated method stub

				}
			});
		}

	}

	@Override
	public void onRefresh() {
		currentPage = 0;
		httpData(0);
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMore() {
		httpData(1);
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
	}

	// 提交留言

	public void commitClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub
		this.position = position;
		edit_msg.setTag(msg);
		httpMsgData();

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
				this.list.add(0, item);
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
		// TODO Auto-generated method stub
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
		}
		// 动态
		else if (model.getType().equals("2")) {
			Bundle b = new Bundle();
			b.putString("noticeId", model.getOtherId());
			changeView(showNoticeDetailActivity.class, b);

		}
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
		httpDelMsgData(msg);
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
	public void commitRep(int position,ecCircleModel model) {
		// TODO Auto-generated method stub
		
	}
}
