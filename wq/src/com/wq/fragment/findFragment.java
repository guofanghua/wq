package com.wq.fragment;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.authApplyActivity;
import com.wq.mainActivity;
import com.wq.Adapter.listItemAdapter;
import com.wq.UI.InScrolllistView;
import com.wq.UI.MMAlert;
import com.wq.find.CaptureActivity;
import com.wq.find.findEpCircleListActivity;
import com.wq.find.findUseCenterActivity;
import com.wq.find.findZHActivity;
import com.wq.find.find_conAngencyActivity;
import com.wq.find.find_pyp_activity;
import com.wq.me.productAddActivity;
import com.wq.model.User;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.partner.part_yqaActivity;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.stringUtils;

public class findFragment extends Fragment {
	InScrolllistView listview;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();

	FinalBitmap finalBitmap;
	BitmapDisplayConfig config = new BitmapDisplayConfig();

	public static boolean broLoadFlag = true;// 用于标示，发送广播来时，是都需要加载数据
	public static final String FIND_BRO_ACTION_NAME = "com.wq.fragment.findFragment";

	public DataReceiver dataReceiver = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.find_main_fragment, container, false);
		initnavigation(v);
		finalBitmap = FinalBitmap.create(getActivity());
		config.setIsRoundCore(true);

		config.setLoadingBitmap(BitmapFactory.decodeResource(getActivity()
				.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getActivity()
				.getResources(), R.drawable.add_prompt));
		initUI(v);

		// TODO Auto-generated method stub
		return v;
	}

	private void initUI(View v) {
		listview = (InScrolllistView) v.findViewById(R.id.listview);
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources().getStringArray(
				R.array.find_fragment_list_item);
		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getSplitHeight() > 0)
				list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
						item.getSplitHeight(), "", Color.BLACK));
			// 设置logo
			// 扫一扫
			if (item.getIndex() == 1) {
				item.setImageid(R.drawable.find_sys);

			}
			// 一起按
			if (item.getIndex() == 2) {
				item.setImageid(R.drawable.yqa_logo);
			}
			// 附近名片
			if (item.getIndex() == 3) {
				item.setImageid(R.drawable.zs_logo);
			}
			// 圈子
			if (item.getIndex() == 4) {
				item.setImageid(R.drawable.fing_syq);
			}
			// 应用
			if (item.getIndex() == 5) {
				item.setImageid(R.drawable.sc_logo);
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
				switch (list.get(position).getIndex()) {
				case 1:
					Intent init_com = new Intent(getActivity(),
							CaptureActivity.class);
					getActivity().startActivity(init_com);
					animIn();
					break;
				case 2:
					Intent init_yqa = new Intent(getActivity(),
							part_yqaActivity.class);
					getActivity().startActivity(init_yqa);
					animIn();
					break;
				case 3:
					// MMAlert.showAlert(getActivity(), false, 1);
					break;

				default:
					break;
				}

				// TODO Auto-generated method stub

			}
		});

	}

	public void animIn() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			getActivity().overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.transition_in);
		}
	}

	public void onResume() {
		super.onResume();
		registerBoradcastReceiver();// 注册广播

	}

	public void initnavigation(View v) {
		BaseActivity.initNavigation(v, -1, "",
				getResources().getString(R.string.menu_fx), "", -1, null, null);
	}

	public void onPause() {

		super.onPause();
		unregisterBroadcaseReceiver();
	}

	public void onDestroy() {
		super.onDestroy();
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
			IntentFilter filter = new IntentFilter();
			filter.addAction(FIND_BRO_ACTION_NAME);
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

	private class DataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(FIND_BRO_ACTION_NAME)) {
				// 刷新，获取数据
				if (broLoadFlag) {

				}
			} else if (action.equals(mainActivity.deyServiceBor)) {
				String filePath = "";
				String fileOldPath = "";
				if (intent != null
						&& !TextUtils
								.isEmpty(intent.getStringExtra("lastIcon"))) {
					filePath = intent.getStringExtra("lastIcon");
				}
				if (User.bArr[4]) {
					// txt_tip_count.setVisibility(View.VISIBLE);
					// txt_tip_count.setText(User.tipCountArr[2]);
				} else
				// txt_tip_count.setVisibility(View.GONE);
				if (User.bArr[5]) {
					if (!fileOldPath.equals(filePath)) {
						// finalBitmap.display(img_tip, filePath);
					}
					fileOldPath = filePath;
					// frame_tip.setVisibility(View.VISIBLE);
				} else {
					// frame_tip.setVisibility(View.GONE);
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

}
