package com.wq.me;

import java.util.ArrayList;

import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.webviewShowActivity;
import com.wq.Adapter.listItemAdapter;
import com.wq.Interface.ZxingCodeType;
import com.wq.UI.InScrolllistView;
import com.wq.model.User;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.BitmapUtil;
import com.wq.utils.stringUtils;

public class me_space_activity extends BaseActivity {

	BitmapDisplayConfig imgConfig = new BitmapDisplayConfig();

	@ViewInject(id = R.id.listview)
	InScrolllistView listview;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_space_activity);
		BaseApplication.getInstance().addActivity(this);

		initdata();

		initnavigation();
	}

	private void initdata() {
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources().getStringArray(R.array.me_sjwz_list_item);
		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getSplitHeight() > 0)
				list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
						item.getSplitHeight()));
			list.add(initListModelBase(true, R.drawable.item_click_bg, -1,
					item.getTitle(), item.getTitle1(), item.getIndex(), 50,
					item.getSplitMarginLeft(), item.getTextMarginleft()));
		}
		adapter = new listItemAdapter(this, list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				switch (list.get(position).getIndex()) {
				// 企业资料
				case 1:
					if (TextUtils.isEmpty(User.name)) {
						changeViewForResult(meProNameActivity.class, 0);
						break;
					}
					changeView(meComInfoActivity.class);
					break;
				// 微企动态
				case 2:
					changeView(noticeMainActivity.class, true);
					break;
				// 微企产品
				case 3:
					changeView(productMainListActivity.class, true);
					break;
				// 微企相册
				case 4:
					changeView(ablumMainActivity.class, true);
					break;
				// 微企模版
				case 5:
					changeView(templateListActivity.class);
					break;
				// 微企二维码
				case 6:
					Bundle bd = new Bundle();
					bd.putInt(ZxingCodeType.ZXCODE_BUNDL,
							ZxingCodeType.TYPE_QIYE);
					changeView(erweimaActivity.class, bd);
					break;
				// 网站访问统计
				case 7:
					changeView(visitMainActivity.class);
					break;
				default:
					break;
				}
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			// 动态

		}
	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.string_sjwz),
				getString(R.string.string_yl), new EditClickListener() {
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						changeView(webviewShowActivity.class);
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

	}

}
