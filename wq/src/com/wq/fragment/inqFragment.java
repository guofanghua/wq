package com.wq.fragment;

import java.util.ArrayList;
import java.util.Timer;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.mainActivity;
import com.wq.webviewShowActivity;
import com.wq.Adapter.decListAdapter;
import com.wq.UI.TypeListPopDialog;
import com.wq.UI.XListView;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.find.CaptureActivity;
import com.wq.find.findAddCircleActivity;
import com.wq.inquiry.jzActivity;
import com.wq.me.companyDetailActivity;
import com.wq.me.me_space_activity;
import com.wq.me.recodeRelationLog;
import com.wq.model.DecInfo;
import com.wq.model.User;
import com.wq.model.meCollectModel;
import com.wq.partner.part_DetailActivity;
import com.wq.partner.part_yqaActivity;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

public class inqFragment extends Fragment implements IXListViewListener {

	public static boolean isUpdate = false;// 用于标示，发送广播来时，是都需要改变用户的logo
	public static boolean broLoadFlag = true;// 用于标示resume是否需要加载数据
	public static final String INQ_BRO_ACTION_NAME = "com.wq.fragment.inqFragment";
	public DataReceiver dataReceiver = null;

	private FrameLayout layout_fc;
	private XListView listview;
	public ArrayList<DecInfo> list = new ArrayList<DecInfo>();
	decListAdapter adapter;
	Timer timer = null;// 定时器定时访问
	private boolean flashFlag = true;
	private boolean tipFlag = false;// 小标题点
	FinalDb db;
	Handler dsqhandler = null;
	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	protected String[] mPlayTypes;// 需要查询的类型
	Button btn_top;// 右上角图片

	// 企业

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isUpdate = false;
		View v = inflater.inflate(R.layout.inq_main_fragment, container, false);
		bitmapUtiles = FinalBitmap.create(getActivity());
		config.setIsRoundCore(true);
		config.setLoadingBitmap(BitmapFactory.decodeResource(getActivity()
				.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getActivity()
				.getResources(), R.drawable.add_prompt));
		db = FinalDb.create(getActivity());
		mPlayTypes = getResources().getStringArray(R.array.linq_select_types);
		initnavigation(v);
		initUI(v);

		// httpData(true);

		return v;
	}

	public class onclick implements OnClickListener {
		// /storage/sdcard0/wqCrop/temp.png
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.frame_fc:
				layout_fc.setVisibility(View.GONE);
				sharedPreferenceUtil.saveIsFirst(getActivity(), "1");
				break;

			}

		}
		// TODO Auto-generated method stub

	}

	public void onResume() {
		super.onResume();
		registerBoradcastReceiver();
		if (isUpdate) {
			if (adapter != null && adapter.image != null) {
				if (User.iconFile.indexOf("http") >= 0)
					bitmapUtiles.display(adapter.image, User.iconFile, config,
							true);
				else if (!TextUtils.isEmpty(User.iconFile)) {
					adapter.image.setImageBitmap(BitmapUtil.toRoundCorner(
							BitmapAivenUtils.readBitmap(getActivity(),
									User.iconFile), 5));

				}
			}
			isUpdate = false;
		}

	}

	/**
	 * 从下面进入activity动画
	 * */
	public void animUp() {

		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.show_transition_in);
		}
	}

	public void onDestory() {
		super.onDestroy();
		unregisterBroadcaseReceiver();
	}

	private void initUI(View v) {
		btn_top = (Button) v.findViewById(R.id.top_btn_edit);
		// 本地数据查询
		try {
			if (db.findAllByWhere(DecInfo.class, "userId='" + User.id + "'")
					.size() > 0)
				list = (ArrayList<DecInfo>) db.findAllByWhere(DecInfo.class,
						"userId='" + User.id + "'", "time desc");
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		layout_fc = (FrameLayout) v.findViewById(R.id.frame_fc);

		if (TextUtils.isEmpty(sharedPreferenceUtil.readIsFirst(getActivity()))) {
			layout_fc.setVisibility(View.GONE);
		} else {
			layout_fc.setVisibility(View.GONE);
		}
		listview = (XListView) v.findViewById(R.id.inq_list_main);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		adapter = new decListAdapter(getActivity(), list, new Handler() {
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				Intent intent = new Intent(getActivity(),
						part_DetailActivity.class);
				DecInfo item = list.get(msg.what - 1);
				Bundle b = new Bundle();
				b.putString("id", item.getPartnerId());
				b.putString("channel", "");
				intent.putExtras(b);
				getActivity().startActivity(intent);
				animIn();
			}
		});
		listview.setAdapter(adapter);
		layout_fc.setOnClickListener(new onclick());
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// 公告
				if (position == 1) {
					Intent intent = new Intent(getActivity(),
							webviewShowActivity.class);
					getActivity().startActivity(intent);
					animIn();
				} else {
					position -= 2;
					DecInfo item = list.get(position);
					Bundle b = new Bundle();
					b.putString("title", item.getName());
					b.putBoolean("isShare", true);
					meCollectModel model_wz = new meCollectModel();
					model_wz.seteId(item.getPartnerId());
					model_wz.setIcon(item.getIcon());
					model_wz.setUrl(item.getUrl());
					model_wz.setType(0);
					model_wz.setTitle(item.getVqh());
					b.putSerializable("meCollectModel", model_wz);
					Intent intent = new Intent(getActivity(),
							webviewShowActivity.class);
					intent.putExtras(b);
					getActivity().startActivity(intent);
					animIn();
					if (!item.isClickFlag()) {
						int count = Integer.parseInt(User.tipCountArr[0]);
						int dyCount = Integer.parseInt(item
								.getNewDynamicCount());
						if (dyCount > 0) {
							count -= dyCount;
						}
						if (count <= 0) {
							User.bArr[0] = false;

						}
						User.tipCountArr[0] = count < 0 ? "0" : (count + "");
						Message msg = mainActivity.tipHandler.obtainMessage();
						msg.what = 1;
						msg.sendToTarget();
						item.setClickFlag(true);
						item.setNewDynamicCount("0");
						try {
							db.update(item);
						} catch (Exception e) {
							e.printStackTrace();
						}
						list.set(position, item);
						adapter.notifyDataSetChanged();
					}
					// 是否有類型為-1的
					tipFlag = false;
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getNewDynamicCount().equals("-1")) {
							tipFlag = true;
							break;
						}
					}
					if (tipFlag) {
						User.bArr[1] = true;

					} else
						User.bArr[1] = false;
					Message msg = mainActivity.tipHandler.obtainMessage();
					msg.what = 1;
					msg.sendToTarget();
				}
			}
		});

	}

	public void initnavigation(View v) {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				getActivity(), 55), DensityUtil.dip2px(getActivity(), 35));

		BaseActivity.initNavigation(v, -1, "",
				getResources().getString(R.string.menu_xp), "",
				R.drawable.btn_share_click, params, new EditClickListener() {
					@Override
					public void editClick() {
						loadTypeListDialog(mPlayTypes);
						if (typeListDialog != null)
							typeListDialog.showAsDropDown(btn_top, 0,
									DensityUtil.dip2px(getActivity(), 2));
						if (TextUtils.isEmpty(User.enterpriseNet)
								|| User.enterpriseNet.indexOf("http:") < 0) {
							return;
						}

					}

					@Override
					public void backClick() {
					}
				});

	}

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(INQ_BRO_ACTION_NAME);
			filter.addAction(mainActivity.deyServiceBor);
			getActivity().registerReceiver(dataReceiver, filter);
		}

	}

	// 注销广播
	public void unregisterBroadcaseReceiver() {
		if (dataReceiver != null) {
			getActivity().unregisterReceiver(dataReceiver);
			dataReceiver = null;
		}
	}

	public void animIn() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			getActivity().overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.transition_in);
		}
	}

	private class DataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(INQ_BRO_ACTION_NAME)) {
				// 刷新，获取数据
				if (broLoadFlag) {

				}
			} else if (action.equals(mainActivity.deyServiceBor)) {
				if (intent != null
						&& intent.getSerializableExtra("list") != null) {
					@SuppressWarnings("unchecked")
					ArrayList<DecInfo> listtmp = (ArrayList<DecInfo>) intent
							.getSerializableExtra("list");
					for (int i = 0; i < listtmp.size(); i++) {
						DecInfo info = listtmp.get(i);
						boolean flag = true;
						try {
							for (int j = 0; j < list.size(); j++) {
								if (list.get(j).getPartnerId()
										.equals(info.getPartnerId())) {
									list.set(j, info);
									db.update(
											info,
											"userid='" + User.id
													+ "' and sdmicId='"
													+ info.getSdmicId() + "' ");
									flag = false;
								}
							}
							if (flag)
								db.save(info);
						} catch (Exception e) {
							e.printStackTrace();
							// TODO: handle exception
						}
						if (flag) {
							list.add(0, info);
						}

					}
					if (listtmp.size() > 0) {
						synchronized (adapter) {
							if (adapter != null)
								adapter.notifyDataSetChanged();
						}

					}

				}
				initTip();
			}

		}
	}

	private void httpData() {
		if (!flashFlag)
			return;
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);

		httpUtil.post(httpUtil.INQ_INFO_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);

							if (errcode.equals(httpUtil.errCode_success)) {

								JSONArray array = jsonResult
										.getJSONArray("spatialDynamicArray");
								ArrayList<DecInfo> tmplist = new ArrayList<DecInfo>();
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									DecInfo info = new DecInfo();
									info.setIcon(obj.getString("icon"));
									info.setInfo(obj.getString("info"));
									info.setName(obj.getString("name"));
									info.setNewDynamicCount(obj
											.getString("newDynamicCount"));
									info.setPartnerId(obj
											.getString("partnerId"));
									info.setSdmicId(obj.getString("sdmicId"));

									info.setSouceType(obj
											.getString("souceType"));
									info.setTime(obj.getString("time"));
									info.setUrl(obj.getString("url"));
									info.setVqh(obj.getString("wqh"));
									// int typecount = Integer.parseInt(info
									// .getNewDynamicCount());
									// if (typecount <= 0) {
									// if (typecount < 0)
									// tipFlag = true;
									// count--;
									// }
									tmplist.add(info);
									boolean flag = true;
									try {
										for (int j = 0; j < list.size(); j++) {
											if (list.get(j)
													.getPartnerId()
													.equals(info.getPartnerId())) {
												list.set(j, info);
												db.update(list.get(j));

												flag = false;
											}
										}
										if (flag)
											db.save(info);
									} catch (Exception e) {
										e.printStackTrace();
										// TODO: handle exception
									}
									if (flag) {
										list.add(0, info);
									}

								}
								adapter.notifyDataSetChanged();
								isUpdate = false;
								broLoadFlag = false;
							} else {
								errMsg = jsonResult.getString(httpUtil.ERR_MGS);

							}

						} catch (JSONException e) {

							e.printStackTrace();
						} finally {
							initTip();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						initTip();

						broLoadFlag = true;
					}
				});

	}

	public void onPause() {

		super.onPause();
		unregisterBroadcaseReceiver();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	// 计算并显示tip和coung
	public void initTip() {

		int count = 0;
		tipFlag = false;
		for (int i = 0; i < list.size(); i++) {
			int type = Integer.parseInt(list.get(i).getNewDynamicCount());
			if (type == -1) {
				tipFlag = true;
			} else if (type > 0) {
				count += type;
			}

		}
		if (tipFlag) {

			User.bArr[1] = true;
			Message msg = mainActivity.tipHandler.obtainMessage();
			msg.what = 1;
			msg.sendToTarget();
		} else {
			User.bArr[1] = false;
			Message msg = mainActivity.tipHandler.obtainMessage();
			msg.what = 1;
			msg.sendToTarget();
		}

		if (count > 0) {

			User.bArr[0] = true;
			User.tipCountArr[0] = count + "";
			Message msg = mainActivity.tipHandler.obtainMessage();
			msg.what = 1;
			msg.sendToTarget();
		}

	}

	// 加载popup
	private TypeListPopDialog typeListDialog;

	public void loadTypeListDialog(String[] comm) {

		if (comm == null)
			return;

		int[] imgArr = { R.drawable.inq_create_zone,
				R.drawable.name_card_detail, R.drawable.inq_laiwang,
				R.drawable.name_card_sys, R.drawable.inq_yqa };
		typeListDialog = new TypeListPopDialog(getActivity(),
				R.layout.card_name_popup_dialog);
		CommonUtil.loadTypeListDialog(comm, imgArr, typeListDialog, handler);

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			// 完善资料
			case 1:
				Intent intent = new Intent(getActivity(),
						me_space_activity.class);
				getActivity().startActivity(intent);
				animIn();
				sharedPreferenceUtil.saveIsFirst(getActivity(), "1");
				break;
			// 完善名片
			case 2:
				Intent intent_mp = new Intent(getActivity(),
						companyDetailActivity.class);
				getActivity().startActivity(intent_mp);
				animIn();

				break;
			case 3:
				Intent intent2 = new Intent(getActivity(),
						recodeRelationLog.class);
				getActivity().startActivity(intent2);
				animIn();
				break;
			// 扫一扫
			case 4:
				Intent intent_sys = new Intent(getActivity(),
						CaptureActivity.class);
				getActivity().startActivity(intent_sys);
				animIn();
				break;
			// 按一按
			case 5:
				Intent intent_aya = new Intent(getActivity(),
						part_yqaActivity.class);
				getActivity().startActivity(intent_aya);
				animIn();
				break;

			}
			typeListDialog.dismiss();
		}

	};
}
