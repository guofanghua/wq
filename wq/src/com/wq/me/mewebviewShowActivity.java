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

public class mewebviewShowActivity extends BaseActivity {
	@ViewInject(id = R.id.webview_content)
	WebView webview;
	String mytitle;
	String url = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_webview_activity);
		initWebview();
		initNavigation(true);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebview() {
		url = this.getIntent().getStringExtra("url");

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

		webview.loadUrl(url);

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
				mewebviewShowActivity.this, 60), DensityUtil.dip2px(
				mewebviewShowActivity.this, 35));
		int bgId = -1;

		bgId = -1;

		initNavigation(R.drawable.title_btn_back_click,
				getString(R.string.string_back), mytitle, "", "", bgId,
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

}
