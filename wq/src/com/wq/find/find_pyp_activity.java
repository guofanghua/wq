package com.wq.find;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;

import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;

import com.wq.Adapter.partSearchListAdapter;
import com.wq.find.pypListener.OnShakeListener;
import com.wq.model.User;
import com.wq.model.localModel;
import com.wq.model.partComModel;
import com.wq.partner.part_DetailActivity;

import com.wq.utils.CommonUtil;
import com.wq.utils.LocationUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class find_pyp_activity extends BaseActivity {
	pypListener mShakeListener = null;
	Vibrator mVibrator;
	@ViewInject(id = R.id.shakeImgLeft)
	LinearLayout mImgLeft;
	@ViewInject(id = R.id.shakeImgRight)
	LinearLayout mImgRight;
	@ViewInject(id = R.id.slidingDrawer1)
	SlidingDrawer mDrawer;
	@ViewInject(id = R.id.handle)
	Button mDrawerBtn;
	@ViewInject(id = R.id.rela_top)
	RelativeLayout mTitle;
	@ViewInject(id = R.id.img_left)
	ImageView img_left;
	@ViewInject(id = R.id.img_right)
	ImageView img_right;
	@ViewInject(id = R.id.list_result)
	ListView listview;
	@ViewInject(id = R.id.btn_pyp)
	Button btn_pyp;
	// 显示碰的结果
	@ViewInject(id = R.id.rela_pyp_result)
	RelativeLayout rela_pyp_result;
	@ViewInject(id = R.id.result_img)
	ImageView result_img;
	@ViewInject(id = R.id.result_txt_wqh)
	TextView txt_wqh;
	@ViewInject(id = R.id.result_txt_intro)
	TextView txt_intro;
	ArrayList<partComModel> list = new ArrayList<partComModel>();
	partSearchListAdapter adapter;
	partComModel model = new partComModel();
	// 定位
	localModel local = new localModel();
	private LocationClient mLocClient = null;
	public boolean isPyp = false;// 是否发生碰撞
	FinalBitmap finalBitmap;
	FinalDb db;
	BitmapDisplayConfig config = new BitmapDisplayConfig();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_pyp_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		db = FinalDb.create(this);
		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setIsRoundCore(true);
		initNavigation();
		initLocal();// 定位
		init();

		btn_pyp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPyp)
					return;

				startAnim(); // 开始 摇一摇手掌动画

				startVibrato(); // 开始 震动

				if (!TextUtils.isEmpty(local.getLatitude())
						&& !TextUtils.isEmpty(local.getLongitude()))
					httpData();
				else {
					isPyp = true;
					initLocal();
				}
			}
		});

	}

	private void init() {
		// 此处查询本地数据库
		list = (ArrayList<partComModel>) db.findAll(partComModel.class);
		adapter = new partSearchListAdapter(find_pyp_activity.this, list);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Bundle b = new Bundle();
				b.putString("title",
						getString(R.string.part_string_search_jylx));
				b.putString("id", list.get(position).getEnterpriseId());
				b.putString("channel", "4");
				changeView(part_DetailActivity.class, b);
				// TODO Auto-generated method stub

			}
		});

		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);
		// mDrawer.setAnimation(animation)
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			public void onDrawerOpened() {
				mDrawerBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.shake_report_dragger_down));
				// 顶部title
				TranslateAnimation titleup = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, -1.0f);
				titleup.setDuration(200);
				titleup.setFillAfter(true);
				mTitle.startAnimation(titleup);
			}
		});
		/* 设定SlidingDrawer被关闭的事件处理 */
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			public void onDrawerClosed() {
				mDrawerBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.shake_report_dragger_up));
				TranslateAnimation titledn = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, -1.0f,
						Animation.RELATIVE_TO_SELF, 0f);
				titledn.setDuration(200);
				titledn.setFillAfter(false);
				mTitle.startAnimation(titledn);
			}
		});

		mShakeListener = new pypListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				// Toast.makeText(getApplicationContext(),
				// "抱歉，暂时没有找到在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_SHORT).show();
				if (isPyp)
					return;

				startAnim(); // 开始 摇一摇手掌动画

				startVibrato(); // 开始 震动
				// 此处开线程访问
				if (!TextUtils.isEmpty(local.getLatitude())
						&& !TextUtils.isEmpty(local.getLongitude()))
					httpData();
				else {
					isPyp = true;
					initLocal();
				}

			}
		});
	}

	public void startAnim() {
		Animation animrora = AnimationUtils.loadAnimation(
				find_pyp_activity.this, R.anim.pyp_rorate);
		img_left.startAnimation(animrora);
		img_right.startAnimation(animrora);
	}

	public void stopAnim() {
		img_left.clearAnimation();
		img_right.clearAnimation();
	}

	// 查询完后绑定控件
	public void BindUI() {

		if (!TextUtils.isEmpty(model.getPid())) {
			try {
				if (db.findById(model.getId(), partComModel.class) != null) {
					db.update(model);
				} else
					db.save(model);// 保存数据库
				// adapter.notifyDataSetChanged();
			} catch (Exception e) {

			}
			rela_pyp_result.setVisibility(View.VISIBLE);
			finalBitmap.display(result_img, model.getIcon(), config);
			if (TextUtils.isEmpty(model.getName())) {
				txt_wqh.setText(model.getVqh());
			} else
				txt_wqh.setText(model.getName());
			txt_intro.setText(model.getCommodity());
			rela_pyp_result.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Bundle b = new Bundle();
					b.putString("title",
							getString(R.string.part_string_search_jylx));
					b.putString("id", model.getEnterpriseId());
					b.putString("channel", "4");
					changeView(part_DetailActivity.class, b);
					rela_pyp_result.setVisibility(View.GONE);

				}
			});
		}

		stopAnim();
		// 查询所有本地保存
		list = (ArrayList<partComModel>) db.findAll(partComModel.class);
		adapter.notifyDataSetChanged();
		// mDrawer.animateOpen();
	}

	// 提交访问数据
	/*
	 * * isRefresh 是否刷新
	 */
	private void httpData() {
		isPyp = true;
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.PYPWIFI,
				LocationUtil.getWifiSSID(find_pyp_activity.this));
		params.put(httpUtil.PYPAddr, local.getAddr());
		params.put(httpUtil.PYPLAT, local.getLatitude());
		params.put(httpUtil.PYPLON, local.getLongitude());
		// params.put(httpUtil.PYPEId, User.id);

		httpUtil.post(httpUtil.FIND_PYP_URL, params,
				new AjaxCallBack<String>() {
					public void onLoading(long count, long current) {
					};

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String msg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								jsonResult = jsonResult
										.getJSONObject("tradeInfo");

								model.setPid(jsonResult.getString("id"));
								model.setEnterpriseId(jsonResult

								.getString("enterpriseId"));
								model.setIcon(jsonResult.getString("iconFile"));
								model.setName(jsonResult.getString("name"));
								model.setCommodity(jsonResult
										.getString("commodity"));
								model.setIsAttention(jsonResult
										.getString("isAttention"));
								model.setVqh(jsonResult.getString("wqh"));
								model.setContactName(jsonResult
										.getString("contactName"));
								model.setOccupation(jsonResult
										.getString("occupation"));
								model.setFlag("4");

								BindUI();
							}

							else if (errcode.equals(httpUtil.errCode_nodata)) {
								
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
							// CommonUtil.showToast(find_pyp_activity.this,
							// R.string.string_http_err_data);
						} finally {
							isPyp = false;
							hideProgress();
							stopAnim();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						isPyp = false;
						stopAnim();
						// TODO Auto-generated method stub

						showToast(R.string.find_pyp_again);
						// CommonUtil.showToastHttp(find_pyp_activity.this,
						// errorNo);
					}
				});
	}

	// 控件旋转动画
	public void resultAnim() { // 定义摇一摇动画动画
		// left
		Animation animLeft = AnimationUtils.loadAnimation(
				find_pyp_activity.this, R.anim.pyp_left);
		animLeft.setFillAfter(true);
		mImgLeft.startAnimation(animLeft);
		// right
		Animation animRight = AnimationUtils.loadAnimation(
				find_pyp_activity.this, R.anim.pyp_right);
		animRight.setFillAfter(true);
		mImgRight.startAnimation(animRight);
	}

	public void startVibrato() { // 定义震动
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1); // 第一个｛｝里面是节奏数组，
																	// 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}

	public void linshi(View v) { // 标题栏
		resultAnim();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocClient != null)
			mLocClient.stop();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	private void initNavigation() {
		initNavitation(getString(R.string.find_string_pyp), "", -1,
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

	// 设置定位相关参数
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(mGpsCheck.isChecked()); // 打开gps
		option.setCoorType(localModel.localClass); // 设置坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(localModel.isShowAddr);
		if (localModel.isShowAddr)
			option.setAddrType("all");

		option.setScanSpan(localModel.interval); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.setPriority(localModel.priority); // 设置网络优先
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}

	// 获取具体位置
	private void initLocal() {

		mLocClient = ((BaseApplication) getApplication()).mLocationClient;

		((BaseApplication) getApplication()).localHander = localHander;
		// 开始
		setLocationOption();
		mLocClient.start();

		// 定位
		if (mLocClient != null && mLocClient.isStarted()) {
			setLocationOption();
			mLocClient.requestLocation();
		}

	}

	// 定位的handler
	public Handler localHander = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				local = (localModel) msg.obj;
				if (isPyp) {
					httpData();

				}
				mLocClient.stop();
			}
		}
	};

	// 提交数据

}
