package com.wq.me;

import net.endure.framework.annotation.view.ViewInject;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.webviewShowActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.UI.MMAlert;
import com.wq.model.meCollectModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.RegexpUtil;

public class MeCollectShowActivity extends BaseActivity {
	@ViewInject(id = R.id.webview_content)
	WebView webview;
	boolean isShare = false;
	String contentString = "";
	meCollectModel model;
	String mytitle = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_collect_show_activity);
		BaseApplication.getInstance().addActivity(this);
		model = (meCollectModel) this.getIntent().getSerializableExtra(
				"meCollectModel");
		initWebview();

	}

	private void initWebview() {
		// setViewTouchListener(webview);

		String kString = "<html><head>"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ "<meta charset=\"utf-8\">"
				// +
				// "<meta id=\"viewport\" name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\" />"
				+ "<meta content=\"telephone=no\" name=\"format-detection\" />"
				+ "<style>.qr_text{font-family:Helvetica,\"微软雅黑\",\"黑体\",Arial,Tahoma;padding-top:8px;padding-bottom:8px;margin-top:10px;border-top:1px solid #B1B3B7;border-bottom:1px solid #B1B3B7;word-wrap:break-word;word-break:break-all;}</style>"
				+ "<title>详情</title>" + "</head><body>" + "<p>%s</p>"
				+ "<p>我:</p>" + "<center>" + "<div class=\"qr_text\">%s</div>"
				+ "</center>" + "</body></html>";
		contentString = String
				.format(kString, model.getTime(), model.getName());
		// 支持js
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);

		// webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// 点中链接在本webview里面跳转
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				view.loadUrl(url);
				return true;

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
		webview.getSettings().setDefaultTextEncodingName("utf-8");
		// webview.loadUrl(collectModel.getUrl());
		webview.loadData(contentString, "text/html; charset=UTF-8", null);

	}

	private void initNavigation(boolean progressFlag) {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				MeCollectShowActivity.this, 60), DensityUtil.dip2px(
				MeCollectShowActivity.this, 35));
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			animOut();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onDestory() {
		super.onDestory();

	}

}
