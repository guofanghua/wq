package com.wq.me;

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
import com.wq.BaseActivity;
import com.wq.loginActivity;
import com.wq.mainActivity;
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
import com.wq.utils.sharedPreferenceUtil;
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

public class rzfuShowActivity extends BaseActivity {
	@ViewInject(id = R.id.webview_content)
	WebView webview;
	// int circleFlag = 0;// 0标示普通，1企业圈
	boolean isShare = true;
	// String url = "";//

	String mytitle = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_webview_activity);

		initWebview();
		initNavigation(true);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebview() {

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
				User.cerStatus = CommonUtil.getcerStatus(mytitle);
				sharedPreferenceUtil.saveCompany(rzfuShowActivity.this);
				if (User.cerStatus.equals("1"))
					initData();
				initNavigation(true);
			}
		});
		LoggerUtil.i(httpUtil.RZXY_URL + "&id=" + User.id);
		webview.loadUrl(httpUtil.RZXY_URL + "&id=" + User.id);
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
		if (!TextUtils.isEmpty(mytitle)) {
			// mytitle.equals(object)
		}
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				rzfuShowActivity.this, 60), DensityUtil.dip2px(
				rzfuShowActivity.this, 35));
		int bgId = -1;
		// if (User.isCertification)
		// bgId = -1;
		// else
		// bgId = R.drawable.share_bg_click;
		initNavigation(R.drawable.title_btn_back_click,
				getString(R.string.string_back), mytitle, "", "认证", bgId,
				progressFlag, params, new EditClickListener() {

					@Override
					public void editClick() {

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

	// 获取并初始化数据
	private void initData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		/*
		 * params.addQueryStringParameter(httpUtil.USER_ID, Company.id);
		 * params.addQueryStringParameter(httpUtil.USER_KEY, Company.userKey);
		 */
		httpUtil.post(httpUtil.ENTER_PRISE_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {
							if (result != null && result.startsWith("\ufeff")) {
								result = result.substring(1);
							}
							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);

							if (errcode.equals(httpUtil.errCode_success)) {

								// 获取公司信息
								JSONObject enterprise = jsonResult
										.getJSONObject("enterprise");
								User.id = enterprise.getString("id");
								User.name = enterprise.getString("name");
								User.introduciont = enterprise
										.getString("introduciont");
								User.culture = enterprise.getString("culture");
								User.desire = enterprise.getString("desire");
								User.commodity = enterprise
										.getString("commodity");
								User.iconFile = enterprise
										.getString("iconFile");
								User.propagandaFile = enterprise
										.getString("propagandaFile");
								User.address = enterprise.getString("address");
								User.email = enterprise.getString("email");
								User.telePhone = enterprise
										.getString("telePhone");
								User.moblie = enterprise.getString("moblie");
								User.net = enterprise.getString("net");
								User.enterpriseNet = enterprise
										.getString("enterpriseNet");
								User.enterpriseType = enterprise
										.getString("enterpriseType");
								User.wqh = enterprise.getString("wqh");
								User.isCertification = enterprise
										.getString("isCertification");
								User.agentId = enterprise.getString("agentId");
								User.templateId = enterprise
										.getString("templateId");
								User.contactName = enterprise
										.getString("contactName");
								User.occupation = enterprise
										.getString("occupation");
								User.weChat = enterprise.getString("weChat");
								User.proEmail = enterprise.getString("mail");
								User.signature = enterprise
										.getString("signature");
								User.nameCardTempId = enterprise
										.getString("cardId");
								sharedPreferenceUtil
										.saveCompany(rzfuShowActivity.this);

							} else {
								errMsg = jsonResult.getString(httpUtil.ERR_MGS);

							}

						} catch (JSONException e) {

							e.printStackTrace();
						} finally {

						}

						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub

					}
				});
	}

}
