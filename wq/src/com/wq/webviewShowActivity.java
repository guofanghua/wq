package com.wq;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.bitmap.display.mycallback;

import com.endure.wq.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.wq.UI.CustomAlertDialog1;
import com.wq.UI.MMAlert;
import com.wq.UI.MMAlert.onitemClick;
import com.wq.me.ablumAddActivity;
import com.wq.me.companyDetailActivity;
import com.wq.me.meComInfoActivity;
import com.wq.me.meConUsActivity;
import com.wq.me.meProNameActivity;
import com.wq.me.noticeAddActivity;
import com.wq.me.productAddActivity;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.ecCircleModel;
import com.wq.model.meCollectModel;
import com.wq.model.mmAlertModel;
import com.wq.model.notice;
import com.wq.model.partComModel;
import com.wq.model.photoModel;
import com.wq.model.product;
import com.wq.utils.BitmapUtil;
import com.wq.utils.ClipUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;
import com.wq.wechat.Constants;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class webviewShowActivity extends BaseActivity {
	@ViewInject(id = R.id.webview_content)
	WebView webview;
	// int circleFlag = 0;// 0标示普通，1企业圈
	boolean isShare = true;
	// String url = "";//

	String mytitle = "";
	FinalDb db;
	String mymsg = "";
	private IWXAPI api;
//收藏
	meCollectModel collectModel = new meCollectModel();
	FinalBitmap finalbitmap;
	BitmapDisplayConfig config;
	Bitmap mbitmap = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_webview_activity);

		BaseApplication.getInstance().addActivity(this);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.registerApp(Constants.APP_ID);
		finalbitmap = FinalBitmap.create(this);
		db = FinalDb.create(this);
		config = new BitmapDisplayConfig();
		if (this.getIntent().getSerializableExtra("meCollectModel") != null) {
			collectModel = (meCollectModel) this.getIntent()
					.getSerializableExtra("meCollectModel");
		}
		isShare = this.getIntent().getBooleanExtra("isShare", true);
		// 自己传递名片分享文字
		mymsg = this.getIntent().getStringExtra("mymsg");
		if (TextUtils.isEmpty(collectModel.getUrl()))
			collectModel.setUrl(User.enterpriseNet);
		checkDetail();
		initWebview();
		initNavigation(true);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebview() {
		
		config.setCallback(new mycallback() {

			@Override
			public void success(Bitmap bitmap) {
				
				mbitmap = bitmap;
				// TODO Auto-generated method stub

			}

			@Override
			public void failure() {
				mbitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.add_prompt);
				// TODO Auto-generated method stub

			}
		});
	
		ImageView imageView = new ImageView(this);
		finalbitmap.display(imageView, collectModel.getIcon(), config);
		// setViewTouchListener(webview);

		// 支持js
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webview.setDownloadListener(new MyWebViewDownLoadListener());
		// webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// 点中链接在本webview里面跳转
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				if (url.endsWith(".apk")) {
					Intent viewIntent = new Intent(
							"android.intent.action.VIEW", Uri.parse(url));
					startActivity(viewIntent);
					return false;
				} else {
					view.loadUrl(url);
					return true;
				}

			}
		});

		// 当加载失败
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				initNavigation(false);

			}
		});
		// 加载完成
		webview.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				// 页面下载完毕,却不代表页面渲染完毕显示出来
				// WebChromeClient中progress==100时也是一样

				// 这个时候网页才显示
				initNavigation(false);

			}
		});
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				mytitle = title;
				initNavigation(true);
			}
		});

		webview.loadUrl(collectModel.getUrl());

	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {

			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initNavigation(boolean progressFlag) {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				webviewShowActivity.this, 60), DensityUtil.dip2px(
				webviewShowActivity.this, 35));
		int bgId = -1;
		if (!isShare)
			bgId = -1;
		else
			bgId = R.drawable.share_bg_click;
		initNavigation(R.drawable.title_btn_back_click,
				getString(R.string.string_back), mytitle, "", "", bgId,
				progressFlag, params, new EditClickListener() {

					@Override
					public void editClick() {
						ArrayList<mmAlertModel> tmp = CommonUtil
								.getSplitmmAlertModel(getResources()
										.getStringArray(
												R.array.share_dialog_select));
						int[] imgSrc = { R.drawable.share_link,
								R.drawable.share_llq, R.drawable.share_flash,
								R.drawable.share_msg, R.drawable.share_sc,
								R.drawable.share_wx, R.drawable.share_pyq };

						// 根据权限
						// 没有认证
						ArrayList<mmAlertModel> mmlist = new ArrayList<mmAlertModel>();
						if (User.cerStatus.equals("0")) {

							String indexString = ",1,2,3,4,5,";
							for (int i = 0; i < tmp.size(); i++) {
								mmAlertModel itemAlertModel = tmp.get(i);

								if (indexString.contains(","
										+ itemAlertModel.getIndex() + ",")) {
									itemAlertModel.setBgId(imgSrc[i]);
									mmlist.add(itemAlertModel);
								}
							}

						}
						// 已经认证
						else if (!User.cerStatus.equals("0")) {
							String indexString = ",1,2,3,4,5,6,7,";
							for (int i = 0; i < tmp.size(); i++) {
								mmAlertModel itemAlertModel = tmp.get(i);
								if (indexString.contains(","
										+ itemAlertModel.getIndex() + ",")) {
									itemAlertModel.setBgId(imgSrc[i]);
									mmlist.add(itemAlertModel);
								}

							}
						}
						MMAlert.shareWebViewDialog(webviewShowActivity.this,
								mmlist, new onitemClick() {

									@Override
									public void onItemClick(int position,
											mmAlertModel item) {
										String index = item.getIndex();
										// 复制链接
										if (index.equals("1")) {
											ClipUtil.copy(
													webviewShowActivity.this,
													collectModel.getUrl());
											CommonUtil.showToast(
													webviewShowActivity.this,
													R.string.clip_succes_warn);
										}
										// 浏览器中打开
										else if (index.equals("2")) {

											Intent viewIntent = new

											Intent(
													"android.intent.action.VIEW",
													Uri.parse(collectModel
															.getUrl()));

											startActivity(viewIntent);

										}
										// 刷新
										else if (index.equals("3")) {
											initNavigation(true);

											// initWebview();
											webview.reload(); // 刷新
										}
										// 发送短信
										else if (index.equals("4")) {
											Uri uri = Uri.parse("smsto:");
											Intent ii = new Intent(
													Intent.ACTION_SENDTO, uri);// 绿色文字就是启动发送短信窗口
											if (TextUtils.isEmpty(mymsg))
												ii.putExtra(
														"sms_body",
														String.format(
																getString(R.string.send_msg_string),
																collectModel
																		.getUrl()));
											else
												ii.putExtra("sms_body", mymsg);
											startActivity(ii);
										}
										// 我的收藏
										else if (index.equals("5")) {
											collectModel.setTime(dateUtil
													.formatDate(new Date()));
											collectModel.setName(mytitle);
											try {
												// 检测收藏是否已存在

												if (collectModel.getType() <= 1
														&& db.findAllByWhere(
																meCollectModel.class,
																"url='"
																		+ collectModel
																				.getUrl()
																		+ "'")
																.size() > 0) {
													db.update(collectModel);
												} else {
													db.save(collectModel);
												}

												showRightDialog(getString(R.string.me_collect_success_warn));
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
										// 微信分享
										else if (index.equals("6")) {

											sendToWX(false);
										}
										// 微信朋友圈
										else if (index.equals("7")) {
											sendToWX(true);
										}
										// TODO Auto-generated method stub

									}
								});

						// MMAlert.shareWebViewDialog(webviewShowActivity.this,
						// myhandler);
						// TODO Auto-generated method stub
						// MMAlert.shareSelectDialog(webviewShowActivity.this,
						// true, false, false, myhandler);
					}

					@Override
					public void backClick() {
						if (webview.canGoBack())
							webview.goBack();
						else {
							{
								finish();
								animOut();
							}
						}
					}
				});
	}

	private void sendToWX(boolean isPYQ) {
		WXWebpageObject localweObject = new WXWebpageObject();
		localweObject.webpageUrl = collectModel.getUrl();
		// LoggerUtil.i("url" +
		// httpUtil.WQ_DOWN_URL);
		WXMediaMessage localWxMediaMessage = new WXMediaMessage(localweObject);
		if (collectModel.getType() == 0) {
			localWxMediaMessage.title = mytitle;
			localWxMediaMessage.description = "我的手机网站";
		} else if (collectModel.getType() == 1) {
			localWxMediaMessage.title = mytitle;
			localWxMediaMessage.description = "我的手机名片";
		}
		if (mbitmap != null)
			mbitmap = Bitmap.createScaledBitmap(mbitmap, 80, 80, true);
		 localWxMediaMessage.thumbData = BitmapUtil
		 .Bitmap2Bytes(mbitmap != null ? mbitmap : BitmapFactory
		 .decodeResource(
		 webviewShowActivity.this.getResources(),
		 R.drawable.add_prompt));
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = System.currentTimeMillis() + "";
		localReq.message = localWxMediaMessage;
		localReq.scene = isPYQ ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		api.sendReq(localReq);
	}

	// 检测，并弹出框
	private void checkDetail() {

		int contentid = 0;
		Class<?> tmpJumpClass = null;
		// // 访问自己的网站
		// if (collectModel.getType() == 0
		// && User.id.equals(collectModel.geteId())) {
		// // 微企介绍
		//
		// if (TextUtils.isEmpty(User.introduciont)) {
		// contentid = R.string.me_string_info_tile;
		// tmpJumpClass = meComInfoActivity.class;
		// }
		// // 经营范围
		// else if (TextUtils.isEmpty(User.commodity)) {
		// contentid = R.string.me_string_info_tile;
		// tmpJumpClass = meComInfoActivity.class;
		// }
		// // 企业愿望
		// else if (TextUtils.isEmpty(User.desire)) {
		// contentid = R.string.me_string_info_tile;
		// tmpJumpClass = meComInfoActivity.class;
		// }
		// // 动态
		// else if (Exist(0) <= 0) {
		// contentid = R.string.me_string_wqdt;
		// tmpJumpClass = noticeAddActivity.class;
		//
		// }
		// // 产品
		// else if (Exist(1) <= 0) {
		// contentid = R.string.me_string_frame_pro;
		// tmpJumpClass = productAddActivity.class;
		//
		// }
		// // 相册
		// else if (Exist(2) <= 0) {
		// contentid = R.string.me_string_wqxc;
		// tmpJumpClass = ablumAddActivity.class;
		//
		// }
		//
		// }
		// 名片
		// else
		if (collectModel.getType() == 1
				&& User.id.equals(collectModel.geteId())) {
			// // 公司名称
			if (TextUtils.isEmpty(User.name)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 联系人姓名
			else if (TextUtils.isEmpty(User.contactName)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 联系人职务
			else if (TextUtils.isEmpty(User.occupation)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 联系人手机
			else if (TextUtils.isEmpty(User.moblie)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 电话
			else if (TextUtils.isEmpty(User.telePhone)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 邮箱
			else if (TextUtils.isEmpty(User.email)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 地址
			else if (TextUtils.isEmpty(User.address)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 微信
			else if (TextUtils.isEmpty(User.weChat)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}
			// 联系人姓名
			else if (TextUtils.isEmpty(User.signature)) {
				contentid = R.string.string_webview_dialog_grxx;
				tmpJumpClass = companyDetailActivity.class;
			}

		}
		if (contentid > 0) {
			if (collectModel.getType() == 0)
				CommonUtil.webshowDialog(webviewShowActivity.this, 0,
						R.string.string_regi_dialog_title,
						R.string.string_webview_dialog_content, contentid,
						tmpJumpClass, true);
			else if (collectModel.getType() == 1) {
				CommonUtil.webshowDialog(webviewShowActivity.this, 0,
						R.string.string_regi_dialog_title,
						R.string.string_webview_dialog_mp_content, contentid,
						tmpJumpClass, true);
			}
		}
		// 访问自己的名片
	}

	// 检测动态，产品，相册
	private int Exist(int modelFlag) {
		// 动态
		if (modelFlag == 0) {
			if (db.checkmyTableExist(notice.class)) {
				return db.findAllByWhere(notice.class,
						"userId='" + User.id + "'").size();
			} else
				return 0;
		} else if (modelFlag == 1) {
			if (db.checkmyTableExist(product.class)) {
				return db.findAllByWhere(product.class,
						"userId='" + User.id + "'").size();
			} else
				return 0;
		} else if (modelFlag == 2) {
			if (db.checkmyTableExist(ablum.class)) {
				return db.findAllByWhere(ablum.class,
						"userId='" + User.id + "'").size();
			} else
				return 0;
		}
		return 0;

	}
}
