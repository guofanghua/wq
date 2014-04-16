package com.wq;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;

import com.endure.wq.R;
import com.wq.UI.ClipActivity2;
import com.wq.UI.CustomViewPager;
import com.wq.UI.MMAlert;
import com.wq.fragment.findFragment;
import com.wq.fragment.inqFragment;
import com.wq.fragment.meFragment;
import com.wq.fragment.myFragmentPagerAdapter;
import com.wq.fragment.partFragment;
import com.wq.me.rzfuShowActivity;

import com.wq.model.Company;
import com.wq.model.User;
import com.wq.model.VersionItem;
import com.wq.service.deyService;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class mainActivity extends BaseActivity {

	//public static mainActivity instance = null;
	CustomViewPager mTabPager;
	ImageView mTabXp;// 询盘
	ImageView mTabHB;// 伙伴
	ImageView mTabFX;// 发现
	ImageView mTabWO;// wo
	TextView txt_inq;
	TextView txt_part;
	TextView txt_fx;
	TextView txt_me;
	// ImageView mTabImg;// wo
	FrameLayout layout_xp;
	FrameLayout layout_hb;
	FrameLayout layout_fx;
	FrameLayout layout_wo;
	private int one;// 单个水平动画位移
	private int two;
	private int three;
	public static int currIndex = 0;// 当前页卡编号
	public static int preIndex = 0;
	private int zero = 0;// 动画图片偏移量
	private ArrayList<Fragment> fragmentsList;
	private VersionItem version = null;// 版本更新
	// 菜单栏有新动态，显示的控件
	TextView tab1_txt_count;// tab1关于自身动态条数
	TextView tab2_txt_count;// tab2关于自身动态条数
	TextView tab3_txt_count;// tab3关于自身动态条数
	TextView tab4_txt_count;// tab4关于自身动态条数
	TextView tab1_txt_up;// tab1系统动态
	TextView tab2_txt_up;// tab1动态条数
	TextView tab3_txt_up;// tab1动态条数
	TextView tab4_txt_up;// tab1动态条数
	public static Handler tipHandler = new Handler();
	Intent intent_deyService = null;
	public static final String deyServiceBor = "com.wq.deyService.bro";
	public DataReceiver dataReceiver = null;
	boolean registerFlag = false;// 注册过来的流程

	boolean loginFlag = false;// 注销登录过
	String comName = "";// 公司名称
	FinalDb db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	//	instance = this;
		currIndex = 0;
		preIndex = 0;

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		loginFlag = this.getIntent().getBooleanExtra("loginFlag", false);
		registerFlag = this.getIntent().getBooleanExtra("registerFlag", false);
		// fjNodataFlag = this.getIntent().getBooleanExtra("nodataFlag", false);
		comName = this.getIntent().getStringExtra("comName");
		if (!registerFlag && !loginFlag)
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
		// 如果是新注册用户
		else if (registerFlag)
			DialogUtils.showDialog1(this,
					getString(R.string.string_regi_dialog_title),
					getString(R.string.string_regi_dialog_content),
					getString(R.string.string_wzdl),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 更新

						}
					}).show();

		initUI();
	
		initListener();
		// 获取屏幕分辨率，以计算偏移量
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		one = width / 4;
		two = one * 2;
		three = one * 3;
		addView();

	}

	private void initUI() {
		tipHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					bindTip();
				}
			}
		};
		
		txt_fx = (TextView) findViewById(R.id.txt_fx);
		txt_inq = (TextView) findViewById(R.id.txt_inq);
		txt_part = (TextView) findViewById(R.id.txt_part);
		txt_me = (TextView) findViewById(R.id.txt_me);
		mTabPager = (CustomViewPager) findViewById(R.id.tabpager);

		mTabXp = (ImageView) findViewById(R.id.img_xp);// 询盘

		mTabHB = (ImageView) findViewById(R.id.img_hb);// 伙伴

		mTabFX = (ImageView) findViewById(R.id.img_fx);// 发现

		mTabWO = (ImageView) findViewById(R.id.img_wo);// wo

		// mTabImg = (ImageView) findViewById(R.id.img_tab_now);// wo

		layout_xp = (FrameLayout) findViewById(R.id.layout_xp);

		layout_hb = (FrameLayout) findViewById(R.id.layout_hb);

		layout_fx = (FrameLayout) findViewById(R.id.layout_fx);

		layout_wo = (FrameLayout) findViewById(R.id.layout_wo);
		tab1_txt_count = (TextView) findViewById(R.id.tab1_txt_count);
		tab2_txt_count = (TextView) findViewById(R.id.tab2_txt_count);
		tab3_txt_count = (TextView) findViewById(R.id.tab3_txt_count);
		tab4_txt_count = (TextView) findViewById(R.id.tab4_txt_count);
		tab1_txt_up = (TextView) findViewById(R.id.tab1_txt_new);
		tab2_txt_up = (TextView) findViewById(R.id.tab2_txt_new);
		tab3_txt_up = (TextView) findViewById(R.id.tab3_txt_new);
		tab4_txt_up = (TextView) findViewById(R.id.tab4_txt_new);

	}

	private void initListener() {
		layout_xp.setOnClickListener(new myOnClickListener(0));
		layout_hb.setOnClickListener(new myOnClickListener(1));
		layout_fx.setOnClickListener(new myOnClickListener(2));
		layout_wo.setOnClickListener(new myOnClickListener(3));
		mTabXp.setOnClickListener(new myOnClickListener(0));
		mTabHB.setOnClickListener(new myOnClickListener(1));
		mTabFX.setOnClickListener(new myOnClickListener(2));
		mTabWO.setOnClickListener(new myOnClickListener(3));
		mTabPager.setOnPageChangeListener(new myOnPageChangeListener());
	}

	// 加载布局
	private void addView() {
		fragmentsList = new ArrayList<Fragment>();
		inqFragment inq = new inqFragment();
		partFragment part = new partFragment();
		findFragment find = new findFragment();
		meFragment me = new meFragment();
		fragmentsList.add(inq);
		fragmentsList.add(part);
		fragmentsList.add(find);
		fragmentsList.add(me);

		mTabPager.setAdapter(new myFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));
		mTabPager.setCurrentItem(0);
		mTabPager.setOffscreenPageLimit(4);

		// mTabPager.setOffscreenPageLimit(4);//不进行预加载
		mTabPager.setOnPageChangeListener(new myOnPageChangeListener());

	}

	// 菜单点击事件
	public class myOnClickListener implements OnClickListener {
		int index = 0;

		public myOnClickListener(int i) {
			this.index = i;
		}

		@Override
		public void onClick(View v) {
			// 设置为false，店家tab按钮，smoothScroll效果去除
			mTabPager.setCurrentItem(index, false);

		}
	}

	// page滑动事件
	public class myOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {

			Animation anim = null;
			switch (position) {

			case 0:
				layout_xp.setBackgroundResource(R.drawable.bottom_bar_press);
				txt_inq.setTextColor(Color.WHITE);
				mTabXp.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_xunpan_pressed));
				if (currIndex == 1) {
					layout_hb.setBackgroundDrawable(null);
					anim = new TranslateAnimation(one, 0, 0, 0);
					mTabHB.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_frd_normal));
					txt_part.setTextColor(Color.GRAY);
				} else if (currIndex == 2) {
					layout_fx.setBackgroundDrawable(null);
					anim = new TranslateAnimation(two, 0, 0, 0);
					mTabFX.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_normal));
					txt_fx.setTextColor(Color.GRAY);
				} else if (currIndex == 3) {
					layout_wo.setBackgroundDrawable(null);
					anim = new TranslateAnimation(three, 0, 0, 0);
					mTabWO.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_wo_normal));
					txt_me.setTextColor(Color.GRAY);
				}
				break;
			case 1:
				layout_hb.setBackgroundResource(R.drawable.bottom_bar_press);
				mTabHB.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_frd_pressed));
				txt_part.setTextColor(Color.WHITE);
				if (currIndex == 0) {
					layout_xp.setBackgroundDrawable(null);
					anim = new TranslateAnimation(zero, one, 0, 0);
					mTabXp.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_xunpan_normal));
					txt_inq.setTextColor(Color.GRAY);
				} else if (currIndex == 2) {
					layout_fx.setBackgroundDrawable(null);
					anim = new TranslateAnimation(two, one, 0, 0);
					mTabFX.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_normal));
					txt_fx.setTextColor(Color.GRAY);
				} else if (currIndex == 3) {
					layout_wo.setBackgroundDrawable(null);
					anim = new TranslateAnimation(three, one, 0, 0);
					mTabWO.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_wo_normal));
					txt_me.setTextColor(Color.GRAY);
				}
				break;
			case 2:
				layout_fx.setBackgroundResource(R.drawable.bottom_bar_press);
				mTabFX.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_pressed));
				txt_fx.setTextColor(Color.WHITE);
				if (currIndex == 0) {
					layout_xp.setBackgroundDrawable(null);
					anim = new TranslateAnimation(zero, two, 0, 0);
					mTabXp.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_xunpan_normal));
					txt_inq.setTextColor(Color.GRAY);
				} else if (currIndex == 1) {
					layout_hb.setBackgroundDrawable(null);
					anim = new TranslateAnimation(one, two, 0, 0);
					mTabHB.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_frd_normal));
					txt_part.setTextColor(Color.GRAY);
				} else if (currIndex == 3) {
					layout_wo.setBackgroundDrawable(null);
					anim = new TranslateAnimation(three, two, 0, 0);
					mTabWO.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_wo_normal));
					txt_me.setTextColor(Color.GRAY);
				}
				break;
			case 3:
				layout_wo.setBackgroundResource(R.drawable.bottom_bar_press);
				mTabWO.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_wo_pressed));
				txt_me.setTextColor(Color.WHITE);
				if (currIndex == 0) {
					layout_xp.setBackgroundDrawable(null);
					anim = new TranslateAnimation(zero, three, 0, 0);
					mTabXp.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_xunpan_normal));
					txt_inq.setTextColor(Color.GRAY);
				} else if (currIndex == 1) {
					layout_hb.setBackgroundDrawable(null);
					anim = new TranslateAnimation(one, three, 0, 0);
					mTabHB.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_frd_normal));
					txt_part.setTextColor(Color.GRAY);
				} else if (currIndex == 2) {
					layout_fx.setBackgroundDrawable(null);
					anim = new TranslateAnimation(two, three, 0, 0);
					mTabFX.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_normal));
					txt_fx.setTextColor(Color.GRAY);
				}
				break;
			}
			if (currIndex != position) {
				sendBroadCast(position);
			}
			currIndex = position;
			anim.setFillAfter(true);// 图片停在偏移的位置
			anim.setDuration(150);
			// mTabImg.startAnimation(anim);
			// TODO Auto-generated method stub

		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

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
			stopService();
			BaseApplication.getInstance().exit();

		}
	}

	// 向fragment发送广播
	private void sendBroadCast(int index) {
		Intent intent = new Intent();
		if (index == 0)
			intent.setAction(inqFragment.INQ_BRO_ACTION_NAME);
		else if (index == 1)
			intent.setAction(partFragment.PART_BRO_ACTION_NAME);
		else if (index == 2)
			intent.setAction(findFragment.FIND_BRO_ACTION_NAME);
		else if (index == 3)
			intent.setAction(meFragment.ME_BRO_ACTION_NAME);
		this.sendBroadcast(intent);

	}

	// 版本检测

	protected void checkUpdate(final Handler handler) {

		httpUtil.post(httpUtil.APP_UPDATE_URL, new AjaxCallBack<String>() {
			public void onStart() {

			}

			public void onSuccess(String result) {

				try {
					if (result != null && result.startsWith("\ufeff")) {
						result = result.substring(1);
					}

					JSONObject jsonResult = new JSONObject(result);
					String errcode = jsonResult.getString(httpUtil.ERR_CODE);
					if (errcode.equals(httpUtil.errCode_success)) {
						int ver = CommonUtil.getAppVersion(mainActivity.this);// 获取当前版本
						VersionItem version = new VersionItem();
						version.verName = jsonResult.getString("ver_name");// 更新版本
						version.versionCode = jsonResult.optInt("ver", 0);
						version.mandupVer = jsonResult.getString("mandupVer");// 禁止版本
						JSONObject object = jsonResult
								.getJSONObject("updataDic");
						version.updateUrl = object.getString("downURL");// 更新地址
						version.updateContent = object.getString("updataMgs");// 更新内容
						boolean b = false;
						// -1: 当前版本已被禁止，0:当前版本最新 ，1需要更新
						if (version.mandupVer != null
								&& version.mandupVer.trim().length() > 0) {
							String[] forbidden = version.mandupVer.split("\\|");
							if (forbidden != null && forbidden.length > 0) {

								for (int i = 0; i < forbidden.length; i++) {
									int f = -1;
									try {
										f = Integer.parseInt(forbidden[i]);
									} catch (Exception e) {
										f = -1;
										e.printStackTrace();
									}
									if (ver == f) {
										b = true;
										break;
									}
								}
							}
						}

						int code = getVersionCode();
						if (b) {
							version.UpdateFlag = -1;
						} else if (version.versionCode > code) {
							version.UpdateFlag = 1;
						} else {
							version.UpdateFlag = 0;
						}
						Message msg = new Message();
						msg.what = 1;
						msg.obj = version;
						handler.sendMessage(msg);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable t, int errorNo, String strMsg) {

			}

		});

	}

	// 显示版本更新弹出框
	public void ShowUpdate(final VersionItem version) {

		// 强制更新
		if (version.UpdateFlag == -1) {
			try {
				CommonUtil.clearDbData(mainActivity.this);
			} catch (Exception ex) {
			}
			DialogUtils.showDialog(this, version.updateContent,
					getString(R.string.string_dialog_update),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// dialog.dismiss();
							// 更新
							update(version.updateUrl);
						}
					}, getString(R.string.string_dialog_quit),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							BaseApplication.getInstance().exit();
						}
					}).show();

		}
		// 版本升级
		else if (version.UpdateFlag == 1) {
			DialogUtils.showDialog(this, version.updateContent,
					getString(R.string.string_dialog_update),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 更新
							update(version.updateUrl);
						}
					}, getString(R.string.string_cacel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
		}

	}

	public void update(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	/**
	 * 获取应用版本信息
	 */
	public String getAppVersion() {
		String version = "";
		PackageManager pkgManager = getPackageManager();
		try {
			PackageInfo info = pkgManager.getPackageInfo(getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {

		}
		return version;
	}

	protected void onResume() {
		super.onResume();
		registerBoradcastReceiver();
		// 开启服务
		initdeyService();
		bindTip();
	}

	protected void onPause() {
		super.onPause();
		stopService();
	}

	private void bindTip() {
		tab1_txt_count.setVisibility(User.bArr[0] ? View.VISIBLE : View.GONE);
		tab1_txt_up.setVisibility(User.bArr[1] ? View.VISIBLE : View.GONE);
		tab2_txt_count.setVisibility(User.bArr[2] ? View.VISIBLE : View.GONE);
		tab2_txt_up.setVisibility(User.bArr[3] ? View.VISIBLE : View.GONE);
		tab3_txt_count.setVisibility(User.bArr[4] ? View.VISIBLE : View.GONE);
		tab3_txt_up.setVisibility(User.bArr[5] ? View.VISIBLE : View.GONE);
		tab4_txt_count.setVisibility(User.bArr[6] ? View.VISIBLE : View.GONE);
		tab4_txt_up.setVisibility(User.bArr[7] ? View.VISIBLE : View.GONE);
		tab1_txt_count.setText(User.tipCountArr[0]);
		tab2_txt_count.setText(User.tipCountArr[1]);
		tab3_txt_count.setText(User.tipCountArr[2]);
		tab4_txt_count.setText(User.tipCountArr[3]);
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
										.saveCompany(mainActivity.this);

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

	// 退出重新登录时的初始化数据
	public void onDestory() {
		super.onDestroy();
		stopService();
		unregisterBroadcaseReceiver();
	}

	// 启动服务
	private void initdeyService() {
		intent_deyService = new Intent(mainActivity.this, deyService.class);
		startService(intent_deyService);
	}

	// 停止服务
	private void stopService() {
		Intent intent = new Intent();
		intent.setAction("com.dey.service");
		stopService(intent);

	}

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter(deyServiceBor);
			this.registerReceiver(dataReceiver, filter);
		}

	}

	// 注销广播
	public void unregisterBroadcaseReceiver() {
		if (dataReceiver != null) {
			this.unregisterReceiver(dataReceiver);
			dataReceiver = null;
		}
	}

	private class DataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(deyServiceBor)) {
				bindTip();
			}

		}
	}

	public int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			int version = packInfo.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
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
						Toast.makeText(this, "图片没找到", 0).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Intent intent = new Intent(this, ClipActivity2.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				} else {
					Intent intent = new Intent(this, ClipActivity2.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity2.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		}
	}
}
