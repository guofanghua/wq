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

public class manage_main_activity extends BaseActivity {

	BitmapDisplayConfig imgConfig = new BitmapDisplayConfig();

	@ViewInject(id = R.id.listview)
	InScrolllistView listview;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_main_activity);
		BaseApplication.getInstance().addActivity(this);

		initdata();

		initnavigation();
	}

	private void initdata() {
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources().getStringArray(
				R.array.manage_main_list_item);
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
				// 公告系统
				case 1:

					break;
				// 员工
				case 2:

					break;
				// 黑名单员工
				case 3:

					break;
				// 手机网站
				case 4:
					changeView(me_space_activity.class);

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
		initNavitation(getResources().getString(R.string.string_gl),
				getString(R.string.string_yl), -1, new EditClickListener() {
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
