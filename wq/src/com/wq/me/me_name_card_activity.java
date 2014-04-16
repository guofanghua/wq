package com.wq.me;

import net.endure.framework.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.UI.TypeListPopDialog;
import com.wq.model.User;
import com.wq.utils.DensityUtil;
import com.wq.utils.httpUtil;

public class me_name_card_activity extends BaseActivity {
	@ViewInject(id = R.id.webview_content)
	WebView webview;
	String url = "";
	protected String[] mPlayTypes;// 需要查询的类型
	private TypeListPopDialog typeListDialog;
	@ViewInject(id = R.id.top_btn_edit)
	Button btn_top;// 右上角图片
	public static boolean isUpdate = false;// 是否需要重新刷新webview

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_name_card_activity);
		BaseApplication.getInstance().addActivity(this);
		isUpdate = false;
		initnavigation();
		initWebview();
		initUI();
	}

	private void initUI() {
		mPlayTypes = getResources().getStringArray(R.array.name_card_types);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebview() {

		url = httpUtil.mpURL(User.id);
		// 判断网络是否存在

		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 点中链接在本webview里面跳转
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		// 加载完成
		webview.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				// 页面下载完毕,却不代表页面渲染完毕显示出来
				// 这个时候网页才显示

			}
		});
		// 当加载失败
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.stopLoading();
				view.clearView();
				view.setVisibility(View.GONE);
				if (!httpUtil.isNetworkAvailable(me_name_card_activity.this)) {
					showToast(R.string.sys_network_no_available1);
				} else {
					showToast(R.string.string_http_err_failure);
				}

			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webview.loadUrl(url);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(false);
		webview.getSettings().setSupportZoom(false);
		webview.setInitialScale(0);

	}

	private void initnavigation() {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(this, 55),
				DensityUtil.dip2px(this, 35));

		initNavitation(getResources().getString(R.string.me_name_card_title),
				"", R.drawable.btn_share_click, params,
				new EditClickListener() {
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						loadTypeListDialog(mPlayTypes);
						if (typeListDialog != null)
							// typeListDialog.setBackgroundDrawable(background);
							typeListDialog.showAsDropDown(btn_top, 2,
									DensityUtil.dip2px(
											me_name_card_activity.this, 0));
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			// 资料
			case 0:
				Bundle b = new Bundle();
				b.putBoolean("cardFlag", true);
				changeView(companyDetailActivity.class, b);
				break;
			// 模板
			case 1:
				changeView(nameCardplateListActivity.class);
				break;
			// 发送短信名片
			case 2:
				Uri uri = Uri.parse("smsto:");
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				intent.putExtra("sms_body", String.format(
						getString(R.string.me_name_card_msg), User.contactName,
						url));
				startActivity(intent);
				break;
			// 发送邮件
			case 3:
				Bundle b_ewm = new Bundle();
				b_ewm.putString("url", url);
				changeView(nameCardEWMActivity.class, b_ewm);
				break;
			}
			typeListDialog.dismiss();
		}

	};

	protected void onResume() {
		super.onResume();
		if (isUpdate) {
			if (httpUtil.isNetworkAvailable(me_name_card_activity.this)) {
				initWebview();
				isUpdate = false;
			}
		}
	}

	public void loadTypeListDialog(String[] comm) {
		if (comm == null)
			return;

		int[] imgArr = { R.drawable.name_card_detail, R.drawable.name_card_mb,
				R.drawable.name_card_send, R.drawable.name_card_ewm };
		typeListDialog = new TypeListPopDialog(this,
				R.layout.card_name_popup_dialog);
		typeListDialog.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		typeListDialog.loadContentView(comm, imgArr, handler);
		// 为弹出框设置自定义的布局文件
		typeListDialog.setCardContentView();
		typeListDialog.setFocusable(true);
		typeListDialog.setBackgroundDrawable(new BitmapDrawable());// 一定要设置背景才起点击外面关闭才起作用
		typeListDialog.setOutsideTouchable(true);
		// 消失是出发的事件
		typeListDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub

			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
