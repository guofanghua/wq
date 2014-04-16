package com.wq.partner;

import java.util.ArrayList;
import java.util.HashMap;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.webviewShowActivity;
import com.wq.Adapter.partSearchListAdapter;
import com.wq.Adapter.partyqaListAdapter;
import com.wq.BaseActivity.EditClickListener;
import com.wq.find.find_pyp_activity;
import com.wq.find.pypListener;
import com.wq.find.pypListener.OnShakeListener;
import com.wq.me.companyDetailActivity;
import com.wq.model.User;
import com.wq.model.localModel;
import com.wq.model.partComModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LocationUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class part_yqaActivity extends BaseActivity {

	@ViewInject(id = R.id.slidingDrawer1)
	SlidingDrawer mDrawer;
	@ViewInject(id = R.id.handle)
	Button mDrawerBtn;
	@ViewInject(id = R.id.rela_top)
	RelativeLayout mTitle;
	@ViewInject(id = R.id.btn_aya)
	Button btn_yqa;
	@ViewInject(id = R.id.list_result)
	ListView listview;
	@ViewInject(id = R.id.list_result_history)
	ListView listviewHistory;
	// 显示碰的结果
	@ViewInject(id = R.id.rela_pyp_result)
	RelativeLayout rela_pyp_result;
	@ViewInject(id = R.id.result_img)
	ImageView result_img;
	@ViewInject(id = R.id.result_txt_wqh)
	TextView txt_wqh;
	@ViewInject(id = R.id.result_txt_intro)
	TextView txt_intro;
	@ViewInject(id = R.id.img_anim)
	ImageView img_anim;
	ArrayList<partComModel> list = new ArrayList<partComModel>();
	ArrayList<partComModel> listHis = new ArrayList<partComModel>();
	partyqaListAdapter adapter;
	partSearchListAdapter adapterHis;
	// 定位
	localModel local = new localModel();
	private LocationClient mLocClient = null;
	public boolean isPyp = false;// 是否已在访问状态
	public boolean isPress = false;// 是否是按住状态
	FinalBitmap finalBitmap;
	FinalDb db;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	private SoundPool sp;
	private int StreamId = 0;// 声音播放的id
	private HashMap<Integer, Integer> spMap;

	// @ViewInject(id = R.id.img_small)
	// ImageView img_small;
	// @ViewInject(id = R.id.img_middle)
	// ImageView img_middle;
	// @ViewInject(id = R.id.img_big)
	// ImageView img_big;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.part_yqa_activity);
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
		checkUserInfo();// 名片资料是否完善
		btn_yqa.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					isPress = true;
					playSounds(1, -1);
					startAnim();
					if (isPyp)
						return false;
					if (!TextUtils.isEmpty(local.getLatitude())
							&& !TextUtils.isEmpty(local.getLongitude()))
						httpData();
					else {
						isPyp = true;
						initLocal();
					}

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					isPress = false;
					// isPyp = false;
					try {
						sp.stop(StreamId);
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}

					stopAnim();
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
		// 播放声音
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		spMap = new HashMap<Integer, Integer>();
		spMap.put(1, sp.load(this, R.raw.radar_hold, 1));

	}

	private void init() {
		// 此处查询本地数据库
		// 构造数据
		try {
			if (db.findAllByWhere(partComModel.class, "flag='4'").size() > 0) {
				listHis = (ArrayList<partComModel>) db.findAllByWhere(
						partComModel.class, "flag='4'");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		adapter = new partyqaListAdapter(part_yqaActivity.this, list);
		adapterHis = new partSearchListAdapter(this, listHis);
		listview.setAdapter(adapter);
		listviewHistory.setAdapter(adapterHis);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Bundle b = new Bundle();
				b.putString("title",
						getString(R.string.part_string_search_jylx));
				b.putString("id", list.get(position).getEnterpriseId());
				b.putString("channel", "5");
				changeView(part_DetailActivity.class, b);
				// TODO Auto-generated method stub

			}
		});
		listviewHistory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putString("title",
						getString(R.string.part_string_search_jylx));
				b.putString("id", listHis.get(position).getEnterpriseId());
				b.putString("channel", "5");
				changeView(part_DetailActivity.class, b);
			}
		});

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

	}

	// 声音扩散的动画
	public void startAnim() {
		img_anim.setVisibility(View.VISIBLE);
		img_anim.clearAnimation();
		Animation anim_mid = AnimationUtils.loadAnimation(
				part_yqaActivity.this, R.anim.yqa_circle_anim);
		img_anim.startAnimation(anim_mid);

	}

	public void stopAnim() {
		img_anim.setVisibility(View.GONE);
		img_anim.clearAnimation();
		// img_small.setVisibility(View.GONE);
		// img_middle.setVisibility(View.GONE);
		// img_big.setVisibility(View.GONE);
		// img_small.clearAnimation();
		// img_middle.clearAnimation();
		// img_big.clearAnimation();
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
		// params.put(httpUtil.PYPWIFI,
		// LocationUtil.getWifiSSID(part_yqaActivity.this));
		// params.put(httpUtil.PYPAddr, local.getAddr());
		params.put(httpUtil.PYPLAT, local.getLatitude());
		params.put(httpUtil.PYPLON, local.getLongitude());
		// params.put(httpUtil.PYPEId, User.id);

		httpUtil.post(httpUtil.PART_YQA_URL, params,
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
								JSONArray arr = jsonResult
										.getJSONArray("result");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject obj = arr.getJSONObject(i);
									partComModel model = new partComModel();

									model.setPid(obj.getString("id"));
									model.setEnterpriseId(obj

									.getString("enterpriseId"));
									model.setIcon(obj.getString("iconFile"));
									model.setName(obj.getString("name"));
									model.setCommodity(obj
											.getString("commodity"));
									model.setVqh(obj.getString("wqh"));
									model.setOccupation(obj
											.getString("occupation"));
									model.setContactName(obj
											.getString("contactName"));
									model.setFlag("4");
									if (!isExist(model)) {
										list.add(0, model);
										addToHis(model);
									}
								}
								if (isPress)
								// BindUI();
								{
									adapter.notifyDataSetChanged();
									adapterHis.notifyDataSetChanged();
								}
								// /else

							}

							// else
							// CommonUtil.showToast(getApplicationContext(),
							// msg);

						} catch (JSONException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
							// CommonUtil.showToast(find_pyp_activity.this,
							// R.string.string_http_err_data);
						} finally {
							isPyp = false;
							if (isPress) {
								httpData();
							}
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						isPyp = false;
						// TODO Auto-generated method stub
						if (isPress) {
							httpData();
						}

						// CommonUtil.showToastHttp(find_pyp_activity.this,
						// errorNo);
					}
				});
	}

	protected void onStop() {
		super.onStop();
		try {
			sp.stop(StreamId);
			stopAnim();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocClient != null)
			mLocClient.stop();

	}

	/**
	 * 便利list 查看最新按出来的人是否已在list中
	 * */
	private boolean isExist(partComModel model) {
		for (partComModel item : list) {
			if (item.getEnterpriseId().equals(model.getEnterpriseId())) {
				return true;
			}

		}

		return false;
	}

	// 便利历史消息，如果有。则更新，否则添加
	private void addToHis(partComModel model) {
		try {
			boolean isExist = false;
			for (int i = 0; i < listHis.size(); i++) {
				partComModel item = listHis.get(i);
				if (item.getEnterpriseId().equals(model.getEnterpriseId())) {
					isExist = true;
					db.update(model);
					listHis.set(i, model);
					break;
				}

			}
			if (!isExist) {
				db.save(model);
				listHis.add(0, model);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void initNavigation() {
		initNavitation(getString(R.string.find_string_yqa_title), "", -1,
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

	// 播放声音

	public void playSounds(int sound, int number) {
		AudioManager am = (AudioManager) this
				.getSystemService(this.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float audioCurrentVolumn = am
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = (float) ((audioCurrentVolumn / audioMaxVolumn) < 0.5 ? 0.5
				: (audioCurrentVolumn / audioMaxVolumn));
		StreamId = sp
				.play(spMap.get(sound), volumnRatio, volumnRatio, 1, -1, 1);
	}

	// 检测用户名片资料是否完善
	public void checkUserInfo() {
		int contentid = 0;
		Class<?> tmpJumpClass = null;
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

		if (contentid > 0) {
			CommonUtil.webshowDialog(part_yqaActivity.this, 0,
					R.string.string_regi_dialog_title,
					R.string.string_webview_dialog_mp_content, contentid,
					tmpJumpClass, true);

		}
	}
}
