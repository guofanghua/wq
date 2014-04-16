package com.wq.fragment;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.wq.BaseActivity;
import com.wq.Adapter.listItemAdapter;
import com.wq.BaseActivity.EditClickListener;
import com.wq.mainActivity;
import com.wq.webviewShowActivity;
import com.wq.UI.InScrolllistView;
import com.wq.UI.TypeListPopDialog;
import com.wq.find.CaptureActivity;
import com.wq.find.findEpCircleDetailActivity1;
import com.wq.me.WorkRepListActivity;
import com.wq.me.card_relation_list_activity;
import com.wq.me.collectActivity;
import com.wq.me.companyDetailActivity;
import com.wq.me.manage_main_activity;
import com.wq.me.me_name_card_activity;
import com.wq.me.me_space_activity;
import com.wq.me.name_card_main_activity;
import com.wq.me.recodeRelationLog;
import com.wq.me.settingActivity;
import com.wq.me.staffManageActivity;
import com.wq.me.templateListActivity;
import com.wq.me.visitMainActivity;
import com.wq.model.User;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.partner.part_yqaActivity;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import com.wq.utils.stringUtils;
import com.wq.wechat.Constants;

public class meFragment extends Fragment {
	ImageView img_logo;// logo
	// ImageView me_img_two_dem;// 二维码
	TextView txt_company_name;// 公司名称
	TextView txt_wqh;// 微企号
	RelativeLayout me_relalogo;// logo所在的Rela
	public static boolean isUpdate = false;// 用于标示resume是否需要加载数据
	public static boolean broLoadFlag = true;// 用于标示，发送广播来时，是都需要加载数据f
	public static final String ME_BRO_ACTION_NAME = "com.wq.fragment.meFragment";
	public DataReceiver dataReceiver = null;
	public int resumeCount = 0;// 判断是否是第一次显示界面

	InScrolllistView listview;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();
	// 返回按钮
	Button btn_top;
	Button btn_edit;
	String[] mPlayTypes;
	int bgId = -1;
	String editText = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isUpdate = false;
		View v = inflater.inflate(R.layout.me_main_fragment, container, false);

		initUI(v);

		initnavigation(v);
		//if (User.cerStatus.equals("2") || User.cerStatus.equals("3")) {
			//btn_edit.setVisibility(View.VISIBLE);
			//btn_edit.setText("管理");
		//} else {
			btn_edit.setVisibility(View.GONE);
		//}
		return v;
	}

	public void initnavigation(View v) {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				getActivity(), 60), DensityUtil.dip2px(getActivity(), 35));

		BaseActivity.initNavigation(v, 0, "",
				getResources().getString(R.string.menu_wo), editText, -1,
				params, new EditClickListener() {
					@Override
					public void editClick() {

					}

					@Override
					public void backClick() {
						mPlayTypes = getResources().getStringArray(
								R.array.me_fragment_qx);
						loadTypeListDialog(mPlayTypes);
						if (typeListDialog != null)
							typeListDialog.showAsDropDown(btn_top, 0,
									DensityUtil.dip2px(getActivity(), 2));
					}
				});
		btn_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						manage_main_activity.class);
				getActivity().startActivity(intent);
				animIn();
			}
		});
	}

	private void bindUI() {
		FinalBitmap bitmapUtil = FinalBitmap.create(getActivity());
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setIsRoundCore(true);
		if (User.iconFile.indexOf("http") >= 0)
			bitmapUtil.display(img_logo, User.iconFile, config, true);
		else// 本地
		{
			img_logo.setImageBitmap(BitmapUtil.toRoundCorner(
					BitmapAivenUtils.readBitmap(getActivity(), User.iconFile),
					5));
		}

		// 此处字节生二维码

		txt_company_name.setText(User.contactName);
		txt_wqh.setText(String.format(getString(R.string.me_string_wqh_format),
				User.wqh));
		if (User.isCertification.equals("1")) {
			// txt_zpxx_show.setVisibility(View.GONE);
			// txt_cxzs_show.setVisibility(View.GONE);
			// txt_sjtj_show.setVisibility(View.GONE);
		}

	}

	private void initUI(View v) {
		btn_top = (Button) v.findViewById(R.id.top_btn_back);
		btn_edit = (Button) v.findViewById(R.id.top_btn_edit);
		img_logo = (ImageView) v.findViewById(R.id.me_img_logo);
		txt_company_name = (TextView) v.findViewById(R.id.me_txt_company_name);
		txt_wqh = (TextView) v.findViewById(R.id.me_txt_company_wqh);
		me_relalogo = (RelativeLayout) v.findViewById(R.id.me_rela2);
		listview = (InScrolllistView) v.findViewById(R.id.listview);
		me_relalogo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent init_com = new Intent(getActivity(),
						companyDetailActivity.class);
				getActivity().startActivity(init_com);
				animIn();
			}
		});

		initStatusView();

		bindUI();

	}

	public void animIn() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			getActivity().overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.transition_in);
		}
	}

	// 获取并初始化数据
	private void initData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);

		params.put(httpUtil.ENTER_UPDATE_NAME, User.name);

		params.put(httpUtil.ENTER_UPDATE_INTRO, User.introduciont);
		params.put(httpUtil.ENTER_UPDATE_COMMODITY, User.commodity);
		params.put(httpUtil.ENTER_UPDATE_DESIRE, User.desire);
		params.put(httpUtil.ENTER_UPDATE_ADDR, User.address);
		params.put(httpUtil.ENTER_UPDATE_EMAIL, User.email);
		params.put(httpUtil.ENTER_UPDATE_NET, User.enterpriseNet);
		params.put(httpUtil.ENTER_UPDATE_TEL, User.telePhone);
		params.put(httpUtil.ENTER_UPDATE_LX_MOBILE, User.moblie);
		params.put(httpUtil.ENTER_UPDATE_LX_NAME, User.contactName);
		params.put(httpUtil.ENTER_UPDATE_LX_WECHAT, User.weChat);
		params.put(httpUtil.ENTER_UPDATE_LX_ZW, User.occupation);
		params.put(httpUtil.ENTER_UPDATE_TYPE, User.enterpriseType);
		params.put(httpUtil.ENTER_UPDATE_CULTURE, User.culture);
		params.put(httpUtil.ENTER_UPRATE_SIGN, User.signature);

		httpUtil.post(httpUtil.ENTER_PRISE_UPDATE_URL, params,
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

								sharedPreferenceUtil.saveCompany(getActivity());
								sharedPreferenceUtil
										.clearUPCompany(getActivity());
								isUpdate = false;
							} else {
								errMsg = jsonResult.getString(httpUtil.ERR_MGS);
								// CommonUtil.showToast(getActivity(), errMsg);
							}

						} catch (JSONException e) {
							// CommonUtil.showToast(getActivity(),
							// R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							bindUI();
						}

						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub

						bindUI();
						// CommonUtil.showToastHttp(getActivity(), errorNo);
						broLoadFlag = true;
					}
				});
	}

	public void onResume() {
		super.onResume();
		registerBoradcastReceiver();
		if (mainActivity.currIndex == 3 && isUpdate) {
			bindUI();
			isUpdate = false;
		}

	}

	public void onStop() {
		super.onStop();
		unregisterBroadcaseReceiver();
	}

	public void onDestory() {
		super.onDestroy();

		unregisterBroadcaseReceiver();

	}

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter(ME_BRO_ACTION_NAME);
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

	public void onPause() {

		super.onPause();
		unregisterBroadcaseReceiver();
	}

	private class DataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			txt_company_name.setText(User.contactName);
			if (action.equals(ME_BRO_ACTION_NAME)) {
				// 刷新，获取数据
				if (broLoadFlag) {
					if (!TextUtils.isEmpty(sharedPreferenceUtil
							.readUPCompany(getActivity()))) {
						// 重新提交数据
						initData();
					}

				}

			}

		}
	}

	// 分割item 有文字
	public listItemModelBase SplitlistitemModelBase(int bgid, int itemHeight,
			String title, int titleColor) {
		return initListModelBase(false, bgid, -1, title, -1, itemHeight, 0, 15,
				titleColor);
	}

	// 右边缺少标题，但是可以设置左边的文字颜色
	public listItemModelBase initListModelBase(boolean arrawIsShow, int bgId,
			int ImgId, String title, int index, int itemHieght,
			int splitMarginLeft, int TitleMargleft, int titleColor) {
		return initListModelBase(arrawIsShow, bgId, ImgId, title, "", index,
				itemHieght, splitMarginLeft, TitleMargleft, 0, titleColor,
				Color.rgb(153, 153, 153));
	}

	// 缺少右边标题
	public listItemModelBase initListModelBase(boolean arrawIsShow, int bgId,
			int ImgId, String title, int index, int itemHieght,
			int splitMarginLeft, int TitleMargleft) {
		return initListModelBase(arrawIsShow, bgId, ImgId, title, "", index,
				itemHieght, splitMarginLeft, TitleMargleft, 0, Color.BLACK,
				Color.rgb(153, 153, 153));
	}

	protected listItemModelBase initListModelBase(boolean arrawIsShow,
			int bgId, int ImgId, String title, String title1, int index,
			int itemHieght, int splitMarginLeft, int TitleMargleft, int type,
			int titleColor, int title1Color) {
		listItemModelBase item = new listItemModelBase();
		item.setArrawIsShow(arrawIsShow);
		item.setBackground(bgId);
		item.setImageId(ImgId);
		item.setTittle(title);
		item.setIndex(index);
		item.setItemHeight(itemHieght);
		item.setTittle1(title1);
		item.setSplitMarginLeft(splitMarginLeft);
		item.setTittleMarginLeft(TitleMargleft);
		item.setType(type);
		item.setTitleTextColor(titleColor);
		item.setTitle1TextColor(title1Color);
		return item;
	}

	private void initStatusView() {
		list.clear();
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = null;
		// 在野
		if (User.cerStatus.equals("0")) {
			arr = getResources().getStringArray(
					R.array.me_fragment_zy_list_item);
		}
		// 收编
		else if (User.cerStatus.equals("1")) {
			arr = getResources().getStringArray(
					R.array.me_fragment_sb_list_item);
		}
		// 超级管理员
		else if (User.cerStatus.equals("2")) {
			arr = getResources().getStringArray(
					R.array.me_fragment_gly_list_item);
		}
		// 子管理员
		else if (User.cerStatus.equals("3")) {
			arr = getResources().getStringArray(
					R.array.me_fragment_zgly_list_item);
		}

		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getSplitHeight() > 0)
				list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
						item.getSplitHeight(), "", Color.BLACK));
			// 设置logo
			// 网站
			if (item.getIndex() == 1) {
				item.setImageid(R.drawable.kj_logo);
			}
			// 名片
			else if (item.getIndex() == 2) {
				item.setImageid(R.drawable.product_logo);
			}
			// 我的相册
			else if (item.getIndex() == 3) {
				item.setImageid(R.drawable.photo_logo);
			}
			// 我的收藏
			else if (item.getIndex() == 4) {
				item.setImageid(R.drawable.sc_logo);
			}
			// 设置
			else if (item.getIndex() == 5) {
				item.setImageid(R.drawable.set_logo);
			}
			// 组织
			else if (item.getIndex() == 6) {
				item.setImageid(R.drawable.notice_logo);
			}
			// 员工
			else if (item.getIndex() == 7) {
				item.setImageid(R.drawable.notice_logo);

			}
			list.add(initListModelBase(true, R.drawable.item_click_bg,
					item.getImageid(), item.getTitle(), item.getIndex(), 50,
					item.getSplitMarginLeft(), item.getTextMarginleft()));
		}
		adapter = new listItemAdapter(getActivity(), list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (list.get(position).getIndex()) {
				// 我的网站
				case 1:
					Intent init_space = new Intent(getActivity(),
							me_space_activity.class);
					getActivity().startActivity(init_space);
					animIn();
					break;
				// 我的名片
				case 2:
					Intent init_mp = new Intent(getActivity(),
							name_card_main_activity.class);
					getActivity().startActivity(init_mp);
					animIn();
					break;
				// 我的相册
				case 3:
					// Intent intent_user = new Intent(getActivity(),
					// card_relation_list_activity.class);
					// getActivity().startActivity(intent_user);
					// animIn();
					break;
				// 我的收藏
				case 4:
					Intent init_sc = new Intent(getActivity(),
							collectActivity.class);
					getActivity().startActivity(init_sc);
					animIn();
					break;
				// 设置
				case 5:
					Intent intent_setting = new Intent(getActivity(),
							settingActivity.class);
					getActivity().startActivity(intent_setting);
					animIn();
					break;
				// 组织
				case 6:
					break;
				// 员工
				case 7:
					Intent intent_staff = new Intent(getActivity(),
							staffManageActivity.class);
					getActivity().startActivity(intent_staff);
					animIn();
					break;

				default:
					break;
				}

			}
		});
	}

	// 加载popup
	private TypeListPopDialog typeListDialog;

	public void loadTypeListDialog(String[] comm) {
		if (comm == null)
			return;
		int[] imgArr = { R.drawable.inq_create_zone,
				R.drawable.name_card_detail, R.drawable.inq_laiwang,
				R.drawable.name_card_sys };
		typeListDialog = new TypeListPopDialog(getActivity(),
				R.layout.card_name_popup_dialog);
		typeListDialog.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		typeListDialog.loadContentView(comm, null, handler);
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

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (User.cerStatus.equals(msg.what + "")) {
				return;
			}
			User.cerStatus = msg.what + "";
			btn_top.setText(mPlayTypes[msg.what]);
			initStatusView();
			if (User.cerStatus.equals("2") || User.cerStatus.equals("3")) {
				btn_edit.setVisibility(View.VISIBLE);
				btn_edit.setText("管理");
			} else {
				btn_edit.setVisibility(View.GONE);
			}
			typeListDialog.dismiss();
		}

	};
}
