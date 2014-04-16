package com.wq;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.endure.wq.R;
import com.wq.model.Company;
import com.wq.model.DecInfo;
import com.wq.model.User;
import com.wq.model.VersionItem;
import com.wq.service.deyService;
import com.wq.utils.CommonUtil;
import com.wq.utils.sharedPreferenceUtil;

public class WelecomeActivity extends BaseActivity {
	protected final static int LOADING_SECONDS = 3000;// 动画时间
	private long TimeSconds = 0;
	ArrayList<DecInfo> list = new ArrayList<DecInfo>();
	Intent intent_deyService = null;
	@ViewInject(id = R.id.layout_bottom)
	LinearLayout layout_bottom;
	@ViewInject(id = R.id.btn_login)
	Button btn_login;
	@ViewInject(id = R.id.btn_register)
	Button btn_register;
	private VersionItem version = null;// 版本更新

	// private VersionItem version = new VersionItem();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(
		// WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
		// checkVersion();
		if (CheckUserIsLogin()) {
			layout_bottom.setVisibility(View.GONE);
			toMain();
		} else {
			// 检查更新
			checkUpdate(new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.what == 1)
						version = (VersionItem) msg.obj;

					if (version != null) {
						ShowUpdate(version);
					}
				}
			});
			layout_bottom.setVisibility(View.VISIBLE);
			// 登录
			btn_login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					changeView(loginActivity.class, true);

				}
			});
			// 注册
			btn_register.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					changeView(registerActivity.class, true);

				}
			});

		}

	}

	public Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				changeView(mainActivity.class);
			} catch (Exception e) {
				changeView(loginActivity.class);
				finish();
			}

		}
	};

	// 检测用户是否已登录过
	private boolean CheckUserIsLogin() {
		try {
			Company company = (Company) CommonUtil
					.decodeObject(sharedPreferenceUtil
							.readCompany(WelecomeActivity.this));
			if (company == null || TextUtils.isEmpty(company.id)) {
				return false;
			} else {
				User.init(company);
				return true;

			}
		} catch (Exception e) {
			return false;
		}
	}

	private void toMain() {
		// if (CheckUserIsLogin()) {
		// initdeyService();
		// }

		// 加载动画
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					long cur = System.currentTimeMillis();
					TimeSconds = cur;
					while (cur - TimeSconds < LOADING_SECONDS) {
						Thread.sleep(100);
						cur = System.currentTimeMillis();
					}
					// 获取Message，obtainMessage()省去创建对象申请内存的开销
					Message msg = WelecomeActivity.this.m_handler
							.obtainMessage();
					// msg.what = 1;
					// msg.obj = version;
					// 发送消息
					WelecomeActivity.this.m_handler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (!CheckUserIsLogin())
				exitBy2Click();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;// 准备退出
			CommonUtil.showToast(getApplicationContext(),
					R.string.exit_app_hint);
			tExit = new Timer();
			tExit.schedule(new TimerTask() {

				@Override
				public void run() {
					isExit = false;// 取消退出
				}
			}, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {

			BaseApplication.getInstance().exit();

		}
	}

	// 启动服务
	private void initdeyService() {

		intent_deyService = new Intent(WelecomeActivity.this, deyService.class);
		startService(intent_deyService);
	}
}
