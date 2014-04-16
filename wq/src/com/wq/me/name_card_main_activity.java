package com.wq.me;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.R.integer;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.webviewShowActivity;
import com.wq.Adapter.listItemAdapter;
import com.wq.Interface.ZxingCodeType;
import com.wq.UI.InScrolllistView;
import com.wq.UI.TypeListPopDialog;
import com.wq.find.CaptureActivity;
import com.wq.model.User;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.model.meCollectModel;
import com.wq.partner.part_yqaActivity;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.RegexpUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import com.wq.utils.stringUtils;

public class name_card_main_activity extends BaseActivity {

	@ViewInject(id = R.id.attr_listview)
	InScrolllistView listView;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();
	String[] text1Arr = { User.name, User.address, User.moblie, User.telePhone,
			User.email, User.signature };
	@ViewInject(id = R.id.top_btn_edit)
	Button btn_top;
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;
	BitmapDisplayConfig config1;
	@ViewInject(id = R.id.img_topbg)
	ImageView img_top;
	@ViewInject(id = R.id.img_logo)
	ImageView img_logo;
	@ViewInject(id = R.id.txt_occ_Name)
	TextView txt_name;
	@ViewInject(id = R.id.txt_occ_zw)
	TextView txt_zw;
	int FlagIndex = 0;
	TextView tmpTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.name_card_main_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		config = new BitmapDisplayConfig();
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config1 = new BitmapDisplayConfig();
		config1.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.welcome_to_vip));
		config1.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.welcome_to_vip));
		initdata();
		initUI();
		initListener();
		initnavigation();
	}

	private void initdata() {
		// popup
		mPlayTypes = getResources().getStringArray(R.array.popup_wdmp_arr);
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();

		String[] arr = getResources().getStringArray(R.array.mp_main_list_item);
		int[] imgArr = { R.drawable.icon, R.drawable.icon, R.drawable.icon,
				R.drawable.icon, R.drawable.icon, R.drawable.icon };

		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getIndex() <= 6) {
				item.setImageid(imgArr[item.getIndex() - 1]);
				if (TextUtils.isEmpty(text1Arr[item.getIndex() - 1])) {
					item.setTitle1(getString(R.string.string_hint_no_str));
				} else
					item.setTitle1(text1Arr[item.getIndex() - 1]);
				item.setArrowShow(false);
			}
			// else if (item.getIndex() == 7 || item.getIndex() == 8) {
			// item.setTitle1("添加");
			//
			// }
			if (item.getSplitHeight() > 0)
				list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
						item.getSplitHeight()));
			list.add(initListModelBase(item.isArrowShow(),
					R.drawable.item_click_bg, item.getImageid(),
					item.getTitle(), item.getTitle1(), item.getIndex(), 50,
					item.getSplitMarginLeft(), item.getTextMarginleft()));
		}
		listView.setFocusable(false);
	}

	private void initUI() {
		tmpTextView = new TextView(this);
		txt_name.setText(User.contactName);
		txt_zw.setText("职务:" + User.occupation);
		finalBitmap.display(img_logo, User.iconFile, config, true);
		if (!TextUtils.isEmpty(User.propagandaFile))
			finalBitmap.display(img_top, User.propagandaFile);
		adapter = new listItemAdapter(this, list);
		listView.setAdapter(adapter);
	}

	private void initListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				FlagIndex = list.get(position).getIndex();
				switch (list.get(position).getIndex()) {
				// 新浪
				case 7:
					editActivity.edit_backcontent = tmpTextView;
					Bundle bundle = new Bundle();
					bundle.putString("title",
							getResources().getString(R.string.me_string_xlwbdz));
					bundle.putString("explan", "请输入您的新浪微博id，例如：2850014655");
					changeView(editActivity.class, bundle, true);
					break;
				// qq
				case 8:
					editActivity.edit_backcontent = tmpTextView;
					Bundle bundle1 = new Bundle();
					bundle1.putString("title",
							getResources().getString(R.string.me_string_qqkjdz));
					bundle1.putString("explan", "请输入您的QQ，例如：2850014655");
					changeView(editActivity.class, bundle1, true);
					break;
				default:
					break;
				}

				// TODO Auto-generated method stub

			}
		});
		img_logo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlagIndex = 1;
				changeView(companyDetailActivity.class);
			}
		});
		img_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				changeViewForResult(NameCardTopBgActivity.class, 200);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		// 更新背景图
		if (requestCode == 200 && resultCode == 200) {

			finalBitmap.display(img_top, User.propagandaFile, config1);

		}

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.me_string_frame_name),
				getString(R.string.string_gd), new EditClickListener() {
					@Override
					public void editClick() {
						loadTypeListDialog(mPlayTypes);
						if (typeListDialog != null)
							typeListDialog.showAsDropDown(btn_top, 0,
									DensityUtil.dip2px(
											name_card_main_activity.this, 2));
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

	}

	protected void onRestart() {
		super.onRestart();
		refreshData();
	}

	private void refreshData() {
		if (FlagIndex >= 1) {
			String[] text1Arr = { User.name, User.address, User.moblie,
					User.telePhone, User.email, User.signature };
			for (int i = 0; i < list.size(); i++) {
				listItemModelBase item = list.get(i);
				if (FlagIndex == 1) {
					if (item.getIndex() > 0 && item.getIndex() <= 6) {
						item.setTittle1(text1Arr[item.getIndex() - 1]);
					}
				}
				// 新浪微博
				else if (FlagIndex == 7 && item.getIndex() == FlagIndex) {
					item.setTittle1("编辑");
					break;
				}
				// qq
				else if (FlagIndex == 8 && item.getIndex() == FlagIndex) {
					item.setTittle1("编辑");
					break;
				}

			}
			adapter.notifyDataSetChanged();

		}
		// 顶部头像
		if (FlagIndex == 1) {
			txt_name.setText(User.contactName);
			txt_zw.setText("职务:" + User.occupation);
			finalBitmap.display(img_logo, User.iconFile, config, true);

		}

		FlagIndex = 0;
	}

	// 加载popup
	private TypeListPopDialog typeListDialog;
	protected String[] mPlayTypes;// 需要查询的类型

	public void loadTypeListDialog(String[] comm) {
		if (comm == null)
			return;
		int[] imgArr = { R.drawable.inq_create_zone,
				R.drawable.name_card_detail, R.drawable.inq_laiwang,
				R.drawable.name_card_sys };
		typeListDialog = new TypeListPopDialog(this,
				R.layout.card_name_popup_dialog);
		CommonUtil.loadTypeListDialog(comm, imgArr, typeListDialog, handler);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			// 资料
			case 1:
				FlagIndex = 1;
				changeView(companyDetailActivity.class);
				break;
			// 二维码
			case 2:
				Bundle b_ewm = new Bundle();
				b_ewm.putString("url", httpUtil.mpURL(User.id));
				changeView(nameCardEWMActivity.class, b_ewm);
				break;
			// 访问统计
			case 3:
				Bundle bundle = new Bundle();
				bundle.putInt("flag", 1);
				changeView(visitMainActivity.class, bundle);
				break;
			// 分享
			case 4:

				break;

			}
			typeListDialog.dismiss();
		}

	};

}