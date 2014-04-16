package com.wq.find;

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
import net.endure.framework.bitmap.display.mycallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.findMsgAdapter;
import com.wq.Adapter.zanAutoLineAdapter;
import com.wq.Interface.IecCircleInterface;
import com.wq.UI.InScrolllistView;
import com.wq.me.showNoticeDetailActivity;
import com.wq.model.User;
import com.wq.model.ecCircleModel;
import com.wq.model.leaveMessage;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;

import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class findTipDetailAcitivy extends BaseActivity implements
		IecCircleInterface {
	@ViewInject(id = R.id.tip_detail_img_logo)
	ImageView img_logo;
	@ViewInject(id = R.id.tip_detail_txt_name)
	TextView txt_name;
	@ViewInject(id = R.id.tip_detail_txt_content)
	TextView txt_content;
	@ViewInject(id = R.id.txt_time)
	TextView txt_time;
	@ViewInject(id = R.id.txt_del)
	TextView txt_del;
	@ViewInject(id = R.id.layout_zan)
	LinearLayout layout_zan;

	@ViewInject(id = R.id.txt_zan)
	TextView txt_zan;
	@ViewInject(id = R.id.layout_pl)
	LinearLayout layout_pl;
	@ViewInject(id = R.id.txt_pl)
	TextView txt_pl;
	@ViewInject(id = R.id.layout_img_container)
	FrameLayout layout_img_container;
	@ViewInject(id = R.id.zan_auto_listview)
	CustomListView list_zan_container;
	@ViewInject(id = R.id.msg_list)
	InScrolllistView list_msg;
	@ViewInject(id = R.id.ec_circle_edt_msg)
	EditText edit_msg;
	@ViewInject(id = R.id.ec_circle_layout_bottom)
	LinearLayout layout_bottom;
	@ViewInject(id = R.id.zan_split)
	View zan_split;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;
	@ViewInject(id = R.id.layout_pl_main)
	LinearLayout layout_pl_main;
	@ViewInject(id = R.id.layout_main)
	FrameLayout layout_main;
	@ViewInject(id = R.id.ec_circle_btn_msg)
	Button btn_msg;
	findMsgAdapter msgAdapter;// 评论的adapter
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;
	zanAutoLineAdapter zanAdapter;

	ecCircleModel model = new ecCircleModel();
	String tradeId = "";// 数据库中自
	int ImageWidth = 0;
	int ImageHeight = 0;

	// 没数据显示界面
	@ViewInject(id = R.id.no_data_layout)
	LinearLayout noData_layout;
	@ViewInject(id = R.id.no_data_txt_title)
	TextView txt_no_data;
	@ViewInject(id = R.id.no_data_btn)
	Button btn_no_data;
	private boolean isZan = false;
	FinalDb db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_ep_tip_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initNavigation();
		ImageWidth = DensityUtil.dip2px(this, 80);
		ImageHeight = DensityUtil.dip2px(this, 80);
		finalBitmap = FinalBitmap.create(this);
		config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setIsRoundCore(true);

		initData();
		initListener();

	}

	private void initListener() {
		// 相册浏览
		layout_img_container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scanPicClick(model);
			}
		});
		// 只有
		// 评论
		layout_pl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				leaveMessage msg = new leaveMessage();
				msg.setLevel("1");
				msg.setSuperComId(model.getId());
				msg.setType("0");
				msg.setEnterPriseId(User.id);
				msg.setVqh(User.wqh);
				msg.setSuperName(model.getVqh());
				// TODO Auto-generated method stub
				btnLeaveClick(0, msg);
			}
		});
		// 赞
		layout_zan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				leaveMessage msg = new leaveMessage();
				msg.setLevel("1");
				msg.setSuperComId(model.getId());
				msg.setType("1");
				msg.setEnterPriseId(User.id);
				msg.setVqh(User.wqh);
				String wqh = "";
				String comId = "";
				int tmpPosition = 0;
				if (model.getZanList().size() > 0) {
					for (int i = 0; i < model.getZanList().size(); i++) {
						if (model.getZanList().get(i).getEnterPriseId()
								.equals(User.id)) {
							wqh = model.getZanList().get(i).getVqh();
							comId = model.getZanList().get(i).getComId();
							tmpPosition = i;
							break;
						}
					}
				}
				msg.setComId(comId);
				if (TextUtils.isEmpty(wqh)) {
					// model.getZanList().add(msg);
					txt_zan.setText(R.string.find_string_ec_yzan);
					commitClick(0, msg);
				} else {
					// model.getZanList().remove(tmpPosition);
					txt_zan.setText(R.string.find_string_ec_zan);
					delMsgClick(tmpPosition, msg);
				}
			}
		});
		// 删除
		txt_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnDelClick(0, model.getId());
			}
		});
		// 点击隐藏软键盘和输入框
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (String.valueOf(layout_bottom.getTag()).equals("1")) {

					layout_bottom.setTag("0");
					// TODO Auto-generated method stub
					layout_bottom.setVisibility(View.GONE);
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(findTipDetailAcitivy.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		btn_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(edit_msg.getText())) {
					CommonUtil.showToast(getApplicationContext(),
							R.string.find_string_ec_send_warn);
					return;
				}
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(findTipDetailAcitivy.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				layout_bottom.setVisibility(View.GONE);
				layout_bottom.setTag("0");

				httpMsgData();
			}
		});

		list_zan_container.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position <= 0)
					return;
				leaveMessage msg = model.getZanList().get(position - 1);
				ecCircleModel model = new ecCircleModel();
				model.setEnterpriseId(msg.getEnterPriseId());
				model.setVqh(msg.getVqh());
				checkUserClick(model);
			}
		});
	}

	@Override
	public void btnDelClick(int position, String tradeId) {
		// TODO Auto-generated method stub
		// 从本地删除，并发送广播
		if (model.getId().equals(User.id)) {
			try {
				db.delete(model);
				Intent intent = new Intent();
				intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
				intent.putExtra("delFlag", true);
				intent.putExtra("circleModel", model);
				sendBroadcast(intent);
				finish();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// 发送广播
		} else
			httpDelData(tradeId);
	}

	public void initData() {
		zanAdapter = new zanAutoLineAdapter(findTipDetailAcitivy.this,
				model.getZanList());

		list_zan_container.setAdapter(zanAdapter);
		tradeId = this.getIntent().getStringExtra("tradeId");
		model = (ecCircleModel) this.getIntent().getSerializableExtra(
				"circleModel");
		// 重新获取
		if (!TextUtils.isEmpty(tradeId)) {
			// 读取缓存
			localData();
			if (!TextUtils.isEmpty(model.getId()))
				bindUI();
			else
				httpData();
		} else if (model != null) {
			localData();
			bindUI();
		}
		// httpData();
	}

	private void localData() {
		// 查询生意圈信息
		String sql = "";
		if (!TextUtils.isEmpty(tradeId)) {
			sql = "select * from ecCircleModel where userid='" + User.id
					+ "'  and id='" + tradeId + "'";
		} else if (!model.getId().equals(User.id)) {
			sql = "select * from ecCircleModel where userid='" + User.id
					+ "'  and id='" + model.getId() + "'";
		} else {
			sql = "select * from ecCircleModel where  userid='" + User.id
					+ "' and pid='" + model.getPid() + "'";
		}
		try {
			if (db.findDbModelListBySQL(sql).size() > 0) {
				model = ((ArrayList<ecCircleModel>) db.findDbListBySQL(
						ecCircleModel.class, sql)).get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sqlWhere = "userId='" + User.id + "' and ecid = '"
				+ model.getId() + "' ";

		try {
			// 查询留言信息
			if (db.findAllByWhere(leaveMessage.class, sqlWhere).size() > 0) {
				ArrayList<leaveMessage> msgList = (ArrayList<leaveMessage>) db
						.findAllByWhere(leaveMessage.class, sqlWhere);
				ArrayList<leaveMessage> msgTempList = new ArrayList<leaveMessage>();
				for (leaveMessage msg : msgList) {
					if (msg.getType().equals("0"))
						msgTempList.add(msg);
					else
						model.getZanList().add(msg);
				}
				if (msgTempList.size() > 0) {
					model.getMsgList().addAll(
							CommonUtil.sortMsgList(msgTempList));
				}

			}
			// 图片部分
			if (db.findAllByWhere(photoModel.class, sqlWhere).size() > 0) {
				ArrayList<photoModel> picList = (ArrayList<photoModel>) db
						.findAllByWhere(photoModel.class, sqlWhere);
				for (photoModel pic : picList) {
					if (model.getId().equals(pic.getEcid()))
						model.getImgList().add(pic);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void bindUI() {
		setViewTouchListener(scroll_main);
		for (leaveMessage msg : model.getZanList()) {
			if (msg.getEnterPriseId().equals(User.id)) {
				isZan = true;
				break;
			}
		}
		if (isZan) {
			txt_zan.setText(R.string.find_string_ec_yzan);
		} else
			txt_zan.setText(R.string.find_string_ec_zan);
		finalBitmap.display(img_logo, model.getLogoUrl(), config, true);
		txt_name.setText(model.getVqh());
		txt_content.setText(model.getCotent());
		if (!model.getType().equals("0"))// 分享企业链接
		{
			txt_content.setBackgroundResource(R.drawable.msg_bg_click);
		} else {
			txt_content.setBackgroundDrawable(null);
		}
		txt_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				contentClick(model);
				// TODO Auto-generated method stub
			}
		});
		if (model.getEnterpriseId().equals(User.id)) {
			txt_del.setVisibility(View.VISIBLE);
		} else
			txt_del.setVisibility(View.INVISIBLE);
		txt_time.setText(dateUtil.spanNow(this, model.getTime(), new Date()));
		//
		if (model.getId().equals(User.id) || model.getIsSend().equals("1")) {
			layout_pl.setEnabled(false);
			layout_zan.setEnabled(false);
		}
		// 图片部分
		int num = model.getImgList().size();
		if (num == 1) {
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			final ImageView imge = new ImageView(this);
			imge.setScaleType(ScaleType.CENTER_CROP);
			imge.setLayoutParams(params);
			imge.setAdjustViewBounds(true);
			imge.setMaxWidth(DensityUtil.intScreenWidth(this) * 3 / 7);
			config.setCallback(new mycallback() {
				@Override
				public void success(Bitmap bitmap) {
					// TODO Auto-generated method stub
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					if (width < height)
						imge.setMaxWidth(DensityUtil
								.intScreenWidth(findTipDetailAcitivy.this) * 3 / 7);
				}

				@Override
				public void failure() {
					// TODO Auto-generated method stub
				}
			});
			if (model.getImgList().get(0).getFlag() == 1) {
				finalBitmap.displayLocal(imge, model.getImgList().get(0)
						.getImageUrl(), false, config);
			} else {
				finalBitmap.display(imge, model.getImgList().get(0)
						.getImageUrl(), config);
			}
			layout_img_container.addView(imge, params);
		}

		else if (num != 4 && num > 1) {
			for (int i = 0; i < num; i++) {
				LayoutParams params = new LayoutParams(ImageWidth, ImageHeight);

				params.leftMargin = (i % 3) * (ImageWidth + 2);
				params.topMargin = (i / 3) * (ImageHeight + 2);
				params.gravity = Gravity.TOP | Gravity.LEFT; // 兼容4.0一下版本
				ImageView imge = new ImageView(this);
				imge.setScaleType(ScaleType.CENTER_CROP);
				imge.setLayoutParams(params);
				layout_img_container.addView(imge);
				if (model.getImgList().get(0).getFlag() == 1) {
					finalBitmap.displayLocal(imge, model.getImgList().get(i)
							.getImageUrl(), false);
				} else {
					finalBitmap.display(imge, model.getImgList().get(i)
							.getImageUrl(), config);
				}
			}

		} else if (num == 4) {
			for (int i = 0; i < num; i++) {
				LayoutParams params = new LayoutParams(ImageWidth, ImageHeight);
				params.leftMargin = (i % 2) * (ImageWidth + 2);
				params.topMargin = (i / 2) * (ImageHeight + 2);
				params.gravity = Gravity.TOP | Gravity.LEFT; // 兼容4.0以下版本
				ImageView imge = new ImageView(this);
				imge.setScaleType(ScaleType.CENTER_CROP);
				layout_img_container.addView(imge, params);
				if (model.getImgList().get(0).getFlag() == 1) {
					finalBitmap.displayLocal(imge, model.getImgList().get(i)
							.getImageUrl(), false);
				} else {
					finalBitmap.display(imge, model.getImgList().get(i)
							.getImageUrl(), config);
				}
			}
		} else {
			layout_img_container.setVisibility(View.GONE);
		}
		// 赞部分

		if (model.getZanList().size() > 0) {
			layout_pl_main.setVisibility(View.VISIBLE);
			list_zan_container.setVisibility(View.VISIBLE);
			zanAdapter = new zanAutoLineAdapter(findTipDetailAcitivy.this,
					model.getZanList());
			list_zan_container.setAdapter(zanAdapter);
		} else {
			list_zan_container.setVisibility(View.GONE);
		}
		// 评论部分
		// 留言内容
		if (model.getMsgList().size() > 0) {
			if (model.getZanList().size() > 0)
				zan_split.setVisibility(View.VISIBLE);
			else
				zan_split.setVisibility(View.GONE);
			layout_pl_main.setVisibility(View.VISIBLE);
			msgAdapter = new findMsgAdapter(this, model.getMsgList(), 0, this);
			list_msg.setVisibility(View.VISIBLE);
			list_msg.setAdapter(msgAdapter);
			msgAdapter.notifyDataSetChanged();
		} else {
			list_msg.setVisibility(View.GONE);
		}

	}

	// 获取 企业信息
	private void httpData() {

		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.TRADE_ID, tradeId);

		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.CIRCLE_PRO_DETAILE_URL, params,
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
								JSONObject tradeInfo = jsonResult
										.getJSONObject("tradeInfo");
								// 企业信息
								JSONObject enterprise = tradeInfo
										.getJSONObject("enterprise");
								model.setEnterpriseId(enterprise
										.getString("id"));
								model.setName(enterprise.getString("name"));
								model.setVqh(enterprise.getString("wqh"));
								model.setIsCertification(enterprise
										.getString("isCertification"));
								model.setBigBgUrl(enterprise
										.getString("propagandaFile"));
								model.setLogoUrl(enterprise.getString("icon"));
								// 生意圈基本信息
								model.setId(tradeInfo.getString("id"));
								model.setType(tradeInfo.getString("type"));
								model.setCotent(tradeInfo.getString("content"));
								model.setTime(tradeInfo.getString("time"));
								// 图片
								JSONArray picArr = tradeInfo
										.getJSONArray("imgArray");
								for (int j = 0; j < picArr.length(); j++) {
									JSONObject picObject = picArr
											.getJSONObject(j);
									photoModel pic = new photoModel();
									// pic.setId(picObject.getString("imgId"));
									pic.setImageUrl(picObject
											.getString("imgUrl"));
									pic.setShareType(Integer.parseInt(model
											.getType()));
									pic.setShareModel(model);
									model.getImgList().add(pic);
								}
								// 留言
								JSONArray msgArr = tradeInfo
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
									msgModel.setTime(dateUtil.SubDate(msgObject
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
									if (msgModel.getType().equals("0"))
										model.getMsgList().add(msgModel);

									else
										model.getZanList().add(msgModel);
								}

								noData_layout.setVisibility(View.GONE);
								scroll_main.setVisibility(View.VISIBLE);
								bindUI();
							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								noData(0);
							}

						} catch (JSONException e) {
							noData(1);
							CommonUtil.showToast(findTipDetailAcitivy.this,
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
						noData(1);
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(findTipDetailAcitivy.this,
								errorNo);
					}
				});

	}

	// 提交留言
	private int tmp = 0;

	private void httpMsgData() {
		tmp = 0;
		final leaveMessage msg = (leaveMessage) edit_msg.getTag();
		msg.setEnterPriseId(User.id);
		msg.setVqh(User.wqh);
		msg.setSend(true);
		msg.setEcid(model.getId());
		if (TextUtils.isEmpty(msg.getContent()))
			msg.setContent(edit_msg.getText().toString());

		if (msg.getType().equals("0")) {
			boolean flag = true;
			boolean flag1 = true;
			ArrayList<leaveMessage> tmpList = new ArrayList<leaveMessage>();
			for (int i = 0; i < model.getMsgList().size(); i++) {

				if (msg.getSuperComId().equals(
						model.getMsgList().get(i).getComId())) {
					flag = false;
					flag1 = false;
				} else if ((!flag1 && !msg.getSuperComId().equals(
						model.getMsgList().get(i).getSuperComId()))) {
					tmp = i + 1;
					tmpList.add(msg);
					flag1 = true;

				}
				tmpList.add(model.getMsgList().get(i));
				if (!flag1 && (i == model.getMsgList().size() - 1)) {
					tmp = i + 1;
					tmpList.add(msg);
					flag1 = true;
				}

			}
			if (flag) {
			
				if (!msg.isExist())
					model.getMsgList().add(msg);
			} else {

				model.setMsgList(tmpList);
			}
			msgAdapter = new findMsgAdapter(this, model.getMsgList(), 0, this);
			list_msg.setVisibility(View.VISIBLE);
			list_msg.setAdapter(msgAdapter);

			if (model.getZanList().size() > 0)
				zan_split.setVisibility(View.VISIBLE);
			else
				zan_split.setVisibility(View.GONE);
			layout_pl_main.setVisibility(View.VISIBLE);
		} else {

			model.getZanList().add(msg);
			layout_pl_main.setVisibility(View.VISIBLE);
			list_zan_container.setVisibility(View.VISIBLE);
			zanAdapter = new zanAutoLineAdapter(findTipDetailAcitivy.this,
					model.getZanList());
			list_zan_container.setAdapter(zanAdapter);
		}

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
								edit_msg.setText("");
								msg.setComId(jsonResult.getString("id"));

								if (msg.getType().equals("0")) {

									model.getMsgList().set(tmp, msg);
									edit_msg.setText("");
								} else {
									model.getZanList().set(
											model.getZanList().size() - 1, msg);
									if (model.getZanList().size() == 0) {
										zan_split.setVisibility(View.GONE);
									}
								}

							}
							// 企业认证跳转
							else if (errcode
									.equals(httpUtil.errCode_isCertification)) {
								msg.setSend(false);
								model.getMsgList().set(tmp, msg);
								msgAdapter.notifyDataSetChanged();
							} else {
								msg.setSend(false);
								if (msg.getType() == "0") {
									model.getMsgList().set(tmp, msg);
									msgAdapter.notifyDataSetChanged();
								} else {
									model.getZanList().remove(
											model.getZanList().size() - 1);
									txt_zan.setText(R.string.find_string_ec_zan);
									zanAdapter = new zanAutoLineAdapter(
											findTipDetailAcitivy.this, model
													.getZanList());
									list_zan_container.setAdapter(zanAdapter);
								}
								CommonUtil.showToast(getApplicationContext(),
										errMsg);
							}

						} catch (JSONException e) {
							msg.setSend(false);
							model.getMsgList().set(tmp, msg);
							msgAdapter.notifyDataSetChanged();
							CommonUtil.showToast(findTipDetailAcitivy.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {

							try {
								if (msg.getType() == "0" || msg.isSend())
									db.save(msg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						msg.setSend(false);
						try {
							if (msg.getType() == "0" || msg.isSend())
								db.save(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (msg.getType() == "0")
							msgAdapter.notifyDataSetChanged();
						else {
							model.getZanList().remove(
									model.getZanList().size() - 1);
							txt_zan.setText(R.string.find_string_ec_zan);
							if (model.getZanList().size() == 0) {
								zan_split.setVisibility(View.GONE);
							}
							zanAdapter = new zanAutoLineAdapter(
									findTipDetailAcitivy.this, model
											.getZanList());
							list_zan_container.setAdapter(zanAdapter);
						}
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(findTipDetailAcitivy.this,
								errorNo);
					}
				});
	}

	// 删除评论,取消赞
	private void httpDelMsgData(final leaveMessage msg) {
		// 0标示普通评论 1标示赞
		if (msg.getType().equals("0")) {
			for (int i = 0; i < model.getMsgList().size(); i++) {
				if (model.getMsgList().get(i).getComId().equals(msg.getComId())) {
					model.getMsgList().remove(i);
					break;
				}
			}
			msgAdapter.notifyDataSetChanged();
		} else {
			for (int i = 0; i < model.getZanList().size(); i++) {
				if (model.getZanList().get(i).getComId().equals(msg.getComId())) {
					model.getZanList().remove(i);
					break;
				}
			}
			if (model.getZanList().size() == 0)
				list_zan_container.setVisibility(View.GONE);
			else
				list_zan_container.setVisibility(View.VISIBLE);
			zanAdapter = new zanAutoLineAdapter(findTipDetailAcitivy.this,
					model.getZanList());
			list_zan_container.setAdapter(zanAdapter);
		}

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
									db.delete(msg);
								} catch (Exception ex) {
								}
							}
							// CommonUtil.showToast(findEpCircleListActivity.this,
							// errMsg);
						} catch (JSONException e) {
							CommonUtil.showToast(findTipDetailAcitivy.this,
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

						CommonUtil.showToastHttp(findTipDetailAcitivy.this,
								errorNo);
					}
				});
	}

	// 没数据
	/** flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载 */
	private void noData(int flag) {
		if (TextUtils.isEmpty(model.getId())) {
			noData_layout.setVisibility(View.VISIBLE);
			scroll_main.setVisibility(View.GONE);
		}
		if (flag == 0) {
			txt_no_data.setText(String.format(
					getString(R.string.string_no_data_txt_add),
					getString(R.string.find_string_syq)));
			btn_no_data.setVisibility(View.GONE);

		} else {
			txt_no_data.setText(R.string.string_no_data_txt_rep);
			btn_no_data.setText(R.string.string_no_data_btn_rep);
			btn_no_data.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					httpData();
					// TODO Auto-generated method stub

				}
			});
		}

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
								try {
									db.delete(model);
									Intent intent = new Intent();
									intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
									intent.putExtra("delFlag", true);
									intent.putExtra("circleModel", model);
									sendBroadcast(intent);
									hideProgress();
									finish();
									animOut();
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							CommonUtil.showToast(findTipDetailAcitivy.this,
									errMsg);

						} catch (JSONException e) {

							CommonUtil.showToast(findTipDetailAcitivy.this,
									errMsg);
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
						CommonUtil.showToastHttp(findTipDetailAcitivy.this,
								errorNo);
					}
				});

	}

	private void initNavigation() {
		initNavitation(getString(R.string.find_tip_trade_detail_title), "", -1,
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

	/**
	 * 
	 * 事件点击接口事件
	 * */
	// 产看详情
	@Override
	public void contentClick(ecCircleModel model) {
		// TODO Auto-generated method stub
		// 分享的企业链接
		if (model.getType().equals("3")) {

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

	@Override
	public void scanPicClick(ecCircleModel model) {
		// TODO Auto-generated method stub
		scanPhoto(model.getImgList(), 0, false, true, null);
	}

	@Override
	public void delMsgClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub

		httpDelMsgData(msg);

	}

	@Override
	public void topImageClick(ImageView img) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkUserClick(ecCircleModel model) {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		b.putString("enterpriseId", model.getEnterpriseId());
		b.putString("title", model.getVqh());
		changeView(findEpCircleDetailActivity.class, b);
	}

	@Override
	public void commitClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub

		edit_msg.setTag(msg);
		httpMsgData();// 提交留言
	}

	@Override
	public void btnLeaveClick(int position, leaveMessage msg) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(msg.getLevel())
				&& Integer.parseInt(msg.getLevel()) >= 3) {

			return;
		}
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

	@Override
	public void checkUeserDetailClick(ecCircleModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commitRep(int position, ecCircleModel model) {
		// TODO Auto-generated method stub

	}
}
