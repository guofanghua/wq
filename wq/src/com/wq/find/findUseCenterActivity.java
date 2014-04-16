package com.wq.find;

import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;

import com.wq.utils.DensityUtil;

/**
 * 用户中心
 * */
public class findUseCenterActivity extends BaseActivity {
	@ViewInject(id = R.id.webview_content)
	WebView webview;
	String url = "";// 制定的url

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_use_center_activity);
		BaseApplication.getInstance().addActivity(this);
		url = this.getIntent().getStringExtra("url");

		initNavigation(true);
		initWebview();
	}

	private void initWebview() {
		// setViewTouchListener(webview);
		// 支持js
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
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
		webview.loadUrl(url);
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
				findUseCenterActivity.this, 35), DensityUtil.dip2px(
				findUseCenterActivity.this, 35));

		initNavigation(R.drawable.title_btn_back_click,
				getString(R.string.string_back),
				getString(R.string.find_string_userCenter), "", "", -1,
				progressFlag, params, new EditClickListener() {

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
}
