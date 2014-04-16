package com.wq.partner;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.showLogoActivity;
import com.wq.webviewShowActivity;
import com.wq.Adapter.detailProductAdapter;
import com.wq.BaseActivity.EditClickListener;
import com.wq.UI.CustomProgressDialog;
import com.wq.UI.MMAlert;
import com.wq.UI.MMAlert.alertItemclickListener;
import com.wq.UI.XgridView;
import com.wq.fragment.partFragment;
import com.wq.me.companyDetailActivity;
import com.wq.me.photoActivity;
import com.wq.me.productMainListActivity;
import com.wq.me.recodeRelationLog;
import com.wq.me.showNoticeListActivity;
import com.wq.model.Company;

import com.wq.model.User;
import com.wq.model.meCollectModel;
import com.wq.model.partComModel;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.utils.ClipUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.stringUtils;

public class part_DetailActivity extends BaseActivity {
	@ViewInject(id = R.id.scroll)
	ScrollView scroll;
	@ViewInject(id = R.id.part_detail_txt_name)
	TextView txt_name;
	@ViewInject(id = R.id.part_img_logo)
	ImageView img_logo;
	@ViewInject(id = R.id.part_detail_txt_wxh)
	TextView txt_wqh;
	@ViewInject(id = R.id.part_detail_txt_addr)
	TextView txt_addr;
	@ViewInject(id = R.id.part_detail_txt_lx)
	TextView txt_lx;// 产品类型
	@ViewInject(id = R.id.part_detail_txt_mp)
	TextView txt_mp;
	@ViewInject(id = R.id.part_rela_mp)
	RelativeLayout rela_mp;
	@ViewInject(id = R.id.part_rela_wqw)
	RelativeLayout rela_wqw;
	@ViewInject(id = R.id.part_detail_txt_url)
	TextView txt_url;
	@ViewInject(id = R.id.part_detail_rela_pro)
	RelativeLayout rela_pro;

	// @ViewInject(id = R.id.btn_attention)
	// Button btn_confirm;
	// @ViewInject(id = R.id.btn_cancel)
	// Button btn_cancel;
	// 获取 企业产品列表
	// 企业相册
	@ViewInject(id = R.id.part_rela_qyxc)
	RelativeLayout rela_qyxc;
	@ViewInject(id = R.id.part_detail_txt_xccount)
	TextView txt_xc_count;
	@ViewInject(id = R.id.img_channel_logo)
	ImageView channel_img_logo;
	@ViewInject(id = R.id.part_detail_txt_channel)
	TextView txt_channel;
	@ViewInject(id = R.id.part_detail_txt_attention)
	TextView txt_attention;
	// 动态
	@ViewInject(id = R.id.part_detail_txt_notice)
	TextView txt_notice;
	@ViewInject(id = R.id.part_rela_qyhd)
	RelativeLayout rela_qyhd;

	@ViewInject(id = R.id.part_layout_container_channer)
	LinearLayout layout_container_channel;
	// String channel = "0";// 渠道号
	partComModel partModel = new partComModel();
	partComModel txpartModel = new partComModel();// 通讯录的

	String enterpriseId = "";
	FinalBitmap bitmapFinal;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	// 联系人
	@ViewInject(id = R.id.txt_lx_name)
	TextView txt_lx_name;
	@ViewInject(id = R.id.txt_lx_mobile)
	TextView txt_lx_mobile;
	@ViewInject(id = R.id.txt_lx_zw)
	TextView txt_lx_zw;
	@ViewInject(id = R.id.txt_lx_we_chat)
	TextView txt_lx_weChat;
	@ViewInject(id = R.id.layout_lx_mobile)
	LinearLayout layout_lx_mobile;
	@ViewInject(id = R.id.layout_lx_we_chat)
	LinearLayout layout_lx_we_chat;
	@ViewInject(id = R.id.part_layout_channel)
	LinearLayout layout_channel;// 来源
	@ViewInject(id = R.id.btn_gz)
	Button btn_gz;
	@ViewInject(id = R.id.part_layout_bottom)
	LinearLayout layout_bottom;
	private boolean isSelf = false;
	private int topBg = -1;
	FinalDb db;
	private String id = "";
	private String flag = "";
	boolean isUpdate = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.part_data_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initData();

		initNavigation();
		initListener();
	}

	private void initData() {
		setViewTouchListener(scroll);
		enterpriseId = this.getIntent().getStringExtra("id");
		partModel.setChannel(this.getIntent().getStringExtra("channel"));
		isUpdate = this.getIntent().getBooleanExtra("isUpdate", false);
		bitmapFinal = FinalBitmap.create(this);
		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		if (User.id.equals(enterpriseId)) {
			isSelf = true;
		}
		if (isSelf) {
			layout_bottom.setVisibility(View.GONE);
			topBg = -1;
		} else {
			topBg = R.drawable.share_bg_click;
		}
		// 查询数据库
		try {
			// 查询本地是否有
			if (db.findAllByWhere(
					partComModel.class,
					"userid='" + User.id + "' and enterpriseId ='"
							+ enterpriseId + "' and flag='5' ").size() > 0) {
				ArrayList<partComModel> list = ((ArrayList<partComModel>) db
						.findAllByWhere(partComModel.class, "userid='"
								+ User.id + "' and enterpriseId ='"
								+ enterpriseId + "' and flag='5' "));

				partModel = list.get(0);
				id = partModel.getEnterpriseId();
				flag = partModel.getFlag();
				BindUI();
			} else {
				httpData();
			}
			// 访问本地数据库，看是否需要修改
			if (isUpdate) {
				ArrayList<partComModel> list = ((ArrayList<partComModel>) db
						.findAllByWhere(partComModel.class, "userid='"
								+ User.id + "' and enterpriseId ='"
								+ enterpriseId + "' and flag='0' "));
				if (list.size() > 0) {
					txpartModel = list.get(0);
					isUpdate = txpartModel.getType() == 1 ? true : false;
				}
				if (isUpdate) {
					httpData();
				}
			}
			if (partModel != null && partModel.getType() == 1) {
				httpData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			httpData();
		}

	}

	// 访问接口
	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		/*
		 * params.addQueryStringParameter(httpUtil.USER_ID, Company.id);
		 * params.addQueryStringParameter(httpUtil.USER_KEY, Company.userKey);
		 */
		if (partModel == null || TextUtils.isEmpty(id))
			showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.PART_DETAIL_URL, params,
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
								jsonResult = jsonResult
										.getJSONObject("partnerDic");
								partModel.setEnterpriseId(jsonResult
										.getString("id"));
								partModel.setAddress(jsonResult
										.getString("address"));
								partModel.setIsAttention(jsonResult
										.getString("isAttention"));
								partModel.setCommodity(jsonResult
										.getString("commodity"));
								partModel.setEnterpriseNet(jsonResult
										.getString("enterpriseNet"));
								partModel.setMobile(jsonResult
										.getString("telePhone"));
								partModel.setIcon(jsonResult.getString("icon"));
								partModel.setName(jsonResult.getString("name"));
								partModel.setXcCount(jsonResult
										.getString("aulmuNumble"));
								partModel.setNoticeTitle(jsonResult
										.getString("newPromotion"));
								partModel.setVqh(jsonResult.getString("vqh"));
								partModel.setLx_mobile(jsonResult
										.getString("moblie"));
								partModel.setContactName(jsonResult
										.getString("contactName"));
								partModel.setOccupation(jsonResult
										.getString("occupation"));
								partModel.setWeChat(jsonResult
										.getString("weChat"));
								partModel.setType(0);
								partModel.setFlag("5");
								if (!TextUtils.isEmpty(jsonResult
										.getString("channel")))
									partModel.setChannel(jsonResult
											.getString("channel"));
								try {
									if (TextUtils.isEmpty(id))
										db.save(partModel);
									else
										db.update(partModel, "userid='"
												+ User.id
												+ "' and enterpriseId ='"
												+ enterpriseId
												+ "' and flag='5' ");
									// 更新通讯录
									if (isUpdate) {
										txpartModel.setType(0);
										db.update(txpartModel, "userid='"
												+ User.id
												+ "' and enterpriseId ='"
												+ enterpriseId
												+ "' and flag='0' ");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								BindUI();
							} else {

								if (TextUtils.isEmpty(partModel
										.getEnterpriseId()))
									showToast(errMsg);
							}
						} catch (JSONException e) {
							hideProgress();
							if (TextUtils.isEmpty(partModel.getEnterpriseId()))
								showToast(errMsg);
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

					}
				});
	}

	// 取消关注
	private void cancelHttpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		/*
		 * params.addQueryStringParameter(httpUtil.USER_ID, Company.id);
		 * params.addQueryStringParameter(httpUtil.USER_KEY, Company.userKey);
		 */
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.PART_CANCEL_ATTENTION_URL, params,
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
								partFragment.isUpdate = true;
								try {
									partModel.setIsAttention("0");
									// 通讯录的换成
									db.deleteByWhere(
											partComModel.class,
											"flag='0' and enterpriseId='"
													+ partModel
															.getEnterpriseId()
													+ "' and userId='"
													+ User.id + "'");
									// 详情的缓存

								} catch (Exception e) {
									e.printStackTrace();
									// TODO: handle exception
								}
								// 更新缓存
								try {
									partModel.setIsAttention("0");
									partModel.setFlag("5");
									if (db.findAllByWhere(
											partComModel.class,
											"flag='5' and enterpriseId='"
													+ partModel
															.getEnterpriseId()
													+ "' and userId='"
													+ User.id + "'").size() > 0) {
										db.update(
												partModel,
												"flag='5' and enterpriseId='"
														+ partModel
																.getEnterpriseId()
														+ "' and userId='"
														+ User.id + "'");
									} else {
										db.save(partModel);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
								partModel.setIsAttention("0");

								layout_container_channel
										.setVisibility(View.GONE);

								txt_attention
										.setText(R.string.part_string_detail_wgz);
								initNavigation();
								BindUI();
							}
							CommonUtil.showToast(part_DetailActivity.this,
									errMsg);

						} catch (JSONException e) {

							CommonUtil.showToast(part_DetailActivity.this,
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

						CommonUtil.showToastHttp(part_DetailActivity.this,
								errorNo);
					}
				});
	}

	// 添加关注
	private void addHttpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		params.put(httpUtil.ENTER_CHANNEL, TextUtils.isEmpty(partModel
				.getChannel()) ? "8" : partModel.getChannel());
		/*
		 * params.addQueryStringParameter(httpUtil.USER_ID, Company.id);
		 * params.addQueryStringParameter(httpUtil.USER_KEY, Company.userKey);
		 */
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.PART_ATTENTION_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {
					}

					@Override
					public void onSuccess(String result) {

						try {

							result = result.trim();
							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								partFragment.isUpdate = true;
								try {
									partModel.setFlag("5");
									partModel.setIsAttention("1");
									if (db.findAllByWhere(
											partComModel.class,
											"flag='5' and enterpriseId='"
													+ partModel
															.getEnterpriseId()
													+ "' and userId='"
													+ User.id + "'").size() > 0) {
										db.update(
												partModel,
												"flag='5' and enterpriseId='"
														+ partModel
																.getEnterpriseId()
														+ "' and userId='"
														+ User.id + "'");
									} else {
										db.save(partModel);
									}
									// 通讯录
									partModel.setIsAttention("1");
									partModel.setFlag("0");
									db.save(partModel);
								} catch (Exception e) {
									e.printStackTrace();
									// TODO: handle exception
								}

								partModel.setIsAttention("1");
								layout_container_channel
										.setVisibility(View.VISIBLE);
								txt_attention
										.setText(R.string.part_string_detail_ygz);
								initNavigation();
								BindUI();
							}
							CommonUtil.showToast(part_DetailActivity.this,
									errMsg);

						} catch (JSONException e) {
							CommonUtil.showToast(part_DetailActivity.this,
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
						CommonUtil.showToastHttp(part_DetailActivity.this,
								errorNo);
					}
				});

	}

	private void BindUI() {

		try {
			// 未 关注
			if (partModel.getIsAttention().equals("0")) {
				btn_gz.setText(R.string.part_string_gz);
				txt_attention.setText(R.string.part_string_detail_wgz);
			} else {
				btn_gz.setText(R.string.part_string_fbmprz);
				txt_attention.setText(R.string.part_string_detail_ygz);
			}
			if (!enterpriseId.equals(User.id))
				btn_gz.setVisibility(View.VISIBLE);
			bitmapFinal.display(img_logo, partModel.getIcon(), config, true);
			txt_name.setText(partModel.getName());
			txt_wqh.setText(String.format(
					getString(R.string.part_string_detail_wqh),
					partModel.getVqh()));
			txt_lx.setText(partModel.getCommodity());
			txt_url.setText(getString(R.string.part_string_in_wq_room));
			txt_addr.setText(partModel.getAddress().trim());

			txt_xc_count.setText(String.format(
					getString(R.string.part_string_detail_pic_count),
					partModel.getXcCount()));
			txt_notice.setText(partModel.getNoticeTitle());
			txt_lx_name.setText(partModel.getContactName());
			txt_lx_mobile.setText(partModel.getLx_mobile());
			txt_lx_weChat.setText(partModel.getWeChat());
			txt_lx_zw.setText(partModel.getOccupation());

			String[] strArr = getResources().getStringArray(
					R.array.part_channel_arr);
			int[] dra = { R.drawable.search_qyh_logo, R.drawable.search_name,
					R.drawable.search_jyclass_logo, R.drawable.find_sys,
					R.drawable.find_pyp, R.drawable.yqa_logo,
					R.drawable.fing_syq, R.drawable.tab_frd_pressed };
			txt_channel
					.setText(strArr[Integer.parseInt(partModel.getChannel())]);
			channel_img_logo.setBackgroundResource(dra[Integer
					.parseInt(partModel.getChannel())]);
			if (partModel.getIsAttention().equals("0"))
				layout_container_channel.setVisibility(View.GONE);
			else
				layout_container_channel.setVisibility(View.VISIBLE);
			initNavigation();

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			hideProgress();
		}

	}

	private void initListener() {
		btn_gz.setOnClickListener(new click());
		rela_mp.setOnClickListener(new click());
		rela_pro.setOnClickListener(new click());
		rela_wqw.setOnClickListener(new click());
		rela_qyxc.setOnClickListener(new click());
		rela_qyhd.setOnClickListener(new click());
		layout_lx_mobile.setOnClickListener(new click());
		layout_lx_we_chat.setOnClickListener(new click());
		img_logo.setOnClickListener(new click());
	}

	private void initNavigation() {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				part_DetailActivity.this, 60), DensityUtil.dip2px(
				part_DetailActivity.this, 35));

		initNavigation(R.drawable.title_btn_back_click,
				getString(R.string.string_back),
				getResources().getString(R.string.part_string_detail_title),
				"", "", topBg, params, new EditClickListener() {

					@Override
					public void editClick() {
						MMAlert.dialogAttention(part_DetailActivity.this,
								partModel.getIsAttention(),
								new alertItemclickListener() {

									@Override
									public void takePhotoClick() {
										// TODO Auto-generated method stub
										if (partModel.getIsAttention().equals(
												"1")) {
											cancelHttpData();
										} else {
											addHttpData();

										}
									}

									@Override
									public void pickPhotoClick() {
										// TODO Auto-generated method stub

									}

									@Override
									public void cancleClick() {
										// TODO Auto-generated method stub

									}
								});
						// TODO Auto-generated method stub

					}

					@Override
					public void backClick() {

						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

	}

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			Bundle b = new Bundle();
			b.putString("enterpriseId", partModel.getEnterpriseId());
			b.putString("enterpriseWqh", partModel.getVqh());
			// TODO Auto-generated method stub
			switch (v.getId()) {
			// 查看logo图
			case R.id.part_img_logo:
				LoggerUtil.i("icon" + partModel.getIcon());
				if (!TextUtils.isEmpty(partModel.getIcon())) {
					Bundle b1 = new Bundle();
					b1.putSerializable("filePath", partModel.getIcon());
					Intent intent = new Intent(part_DetailActivity.this,
							showLogoActivity.class);
					intent.putExtras(b1);
					startActivity(intent);
					overridePendingTransition(R.anim.ablum_transition_in,
							R.anim.transition_in);
				}
				break;
			// 查看微企名片i
			case R.id.part_rela_mp:
				Bundle b_mp = new Bundle();
				b_mp.putString("mymsg", String.format(
						getString(R.string.me_name_card_msg1),
						httpUtil.mpURL(partModel.getEnterpriseId())));
				meCollectModel model_mp = new meCollectModel();
				model_mp.seteId(partModel.getEnterpriseId());
				model_mp.setIcon(partModel.getIcon());
				model_mp.setUrl(httpUtil.mpURL(partModel.getEnterpriseId()));
				model_mp.setType(1);
				model_mp.setTitle(partModel.getVqh());
				b_mp.putSerializable("meCollectModel", model_mp);
				changeView(webviewShowActivity.class, b_mp);
				break;
			// 查看微企空间
			case R.id.part_rela_wqw:
				Bundle b_webview = new Bundle();
				meCollectModel model = new meCollectModel();
				model.seteId(partModel.getEnterpriseId());
				model.setIcon(partModel.getIcon());
				model.setUrl(partModel.getEnterpriseNet());
				model.setTitle(partModel.getVqh());
				model.setType(0);
				b_webview.putSerializable("meCollectModel", model);
				changeView(webviewShowActivity.class, b_webview);
				break;
			// 产看企业相册
			case R.id.part_rela_qyxc:
				changeView(photoActivity.class, b);

				break;
			// 产看企业动态
			case R.id.part_rela_qyhd:
				changeView(showNoticeListActivity.class, b);
				break;

			// 查看企业产品
			case R.id.part_detail_rela_pro:
				changeView(productMainListActivity.class, b);
				break;
			// 联系联系人
			case R.id.layout_lx_mobile:
				if (TextUtils.isEmpty(partModel.getLx_mobile()))
					return;
				MMAlert.dialogPart(part_DetailActivity.this,
						getString(R.string.part_string_detail_pho_title),
						new alertItemclickListener() {

							@Override
							public void takePhotoClick() {
								// TODO Auto-generated method stub
								Uri uri_mobile = Uri.parse("tel:"
										+ partModel.getLx_mobile());
								Intent intent_mobile = new Intent(
										Intent.ACTION_CALL, uri_mobile);
								startActivity(intent_mobile);
							}

							@Override
							public void pickPhotoClick() {
								// TODO Auto-generated method stub

							}

							@Override
							public void cancleClick() {
								// TODO Auto-generated method stub

							}
						});

				break;
			// 跳转到微信
			case R.id.layout_lx_we_chat:
				if (TextUtils.isEmpty(partModel.getWeChat()))
					return;
				showToast(R.string.part_string_detail_lx_we_chat_clip_warn);
				MMAlert.dialogPart(part_DetailActivity.this,
						getString(R.string.part_string_detail_we_chat_title),
						new alertItemclickListener() {

							@Override
							public void takePhotoClick() {
								// TODO Auto-generated method stub
								Intent intent_we_chat = new Intent();

								ComponentName cmp = new ComponentName(
										"com.tencent.mm",
										"com.tencent.mm.ui.LauncherUI");
								intent_we_chat.setAction(Intent.ACTION_MAIN);
								intent_we_chat
										.addCategory(Intent.CATEGORY_LAUNCHER);
								intent_we_chat
										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent_we_chat.setComponent(cmp);
								startActivityForResult(intent_we_chat, 0);
								ClipUtil.copy(part_DetailActivity.this,
										partModel.getWeChat());

							}

							@Override
							public void pickPhotoClick() {
								// TODO Auto-generated method stub

							}

							@Override
							public void cancleClick() {
								// TODO Auto-generated method stub

							}
						});

				break;
			// 关注
			case R.id.btn_gz:
				if (partModel.getIsAttention().equals("0")) {
					addHttpData();
				} else {

					Bundle b_recode = new Bundle();
					b_recode.putBoolean("jumpFlag", true);
					b_recode.putSerializable("contact", partModel);
					changeView(recodeRelationLog.class, b_recode, true);
				}
				break;

			}

		}
	}
}
