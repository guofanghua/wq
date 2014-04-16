package com.wq.me;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.exception.AfinalException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.endure.wq.R.string;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.loginActivity;
import com.wq.mainActivity;
import com.wq.webviewShowActivity;
import com.wq.Adapter.listItemAdapter;
import com.wq.UI.InScrolllistView;
import com.wq.UI.MMAlert;
import com.wq.UI.MMAlert.alertItemclickListener;
import com.wq.fragment.findFragment;
import com.wq.fragment.inqFragment;
import com.wq.fragment.meFragment;
import com.wq.fragment.partFragment;
import com.wq.model.User;
import com.wq.model.VersionItem;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.service.deyService;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import com.wq.utils.stringUtils;

/**
 * 隐私
 * 
 * **/
public class settingYsActivity extends BaseActivity {

	@ViewInject(id = R.id.listview)
	InScrolllistView listview;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();

	@ViewInject(id = R.id.scroll)
	ScrollView scroll;
	FinalDb db;
	private VersionItem version = null;// 版本更新

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_ys_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initdata();

		initListener();
		initnavigation();
	}

	private void initdata() {
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources().getStringArray(R.array.sz_ys_list_item);
		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getSplitHeight() > 0)
				if (i == 0)
					list.add(SplitlistitemModelBase(
							R.drawable.item_split_bg,
							item.getSplitHeight(),
							getResources().getString(
									R.string.me_string_set_acount),
							Color.rgb(153, 153, 153)));
				else
					list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
							item.getSplitHeight()));
			if (i == 1) {
				if (!TextUtils.isEmpty(User.proEmail)) {
					item.setTitle1(User.proEmail);
				} else {
					item.setTitle1(getResources().getString(
							R.string.me_string_set_pwd_pro_hint));
				}
			}

			list.add(initListModelBase(true, R.drawable.item_click_bg, -1,
					item.getTitle(), item.getTitle1(), item.getIndex(), 50,
					item.getSplitMarginLeft(), item.getTextMarginleft()));
		}
		adapter = new listItemAdapter(this, list);
		listview.setAdapter(adapter);

	}

	private void initListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				switch (list.get(position).getIndex()) {
				case 1:
					changeView(setChangePwdActivity.class);
					break;
				case 2:
					if (TextUtils.isEmpty(User.proEmail))
						changeView(setPwdProtectActivity.class);
					else
						changeView(setPwdProtectPwdActivity.class);
					break;

				default:
					break;
				}
				// TODO Auto-generated method stub

			}
		});

		setViewTouchListener(scroll);

	}

	private void initnavigation() {
		initNavitation(getString(R.string.me_string_set_ys), "", -1,
				new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub

					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});
	}

	public void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(User.proEmail)) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getIndex() == 2) {
					list.get(i).setTittle1(User.proEmail);
					adapter.notifyDataSetChanged();
					break;
				}

			}

		}
	}
}
