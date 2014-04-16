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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.endure.wq.R.string;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.WelecomeActivity;
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

public class settingActivity extends BaseActivity {

	@ViewInject(id = R.id.listview)
	InScrolllistView listView;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();
	@ViewInject(id = R.id.btn_quit)
	Button btn_quit;
	@ViewInject(id = R.id.scroll)
	ScrollView scroll;
	FinalDb db;
	private VersionItem version = null;// 版本更新

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initdata();
		initListener();
		initnavigation();
	}

	private void initdata() {

		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources().getStringArray(R.array.sz_main_list_item);
		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getIndex() == 2) {
				if (item.getSplitHeight() > 0)
					list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
							item.getSplitHeight()));
				if (User.cerStatus.equals("0")) {
					list.add(initListModelBase(true, R.drawable.item_click_bg,
							-1, item.getTitle(), item.getTitle1(),
							item.getIndex(), 50, 0, item.getTextMarginleft()));
				} else {
					list.add(initListModelBase(true, R.drawable.item_click_bg,
							-1, item.getTitle(), item.getTitle1(),
							item.getIndex(), 50, item.getSplitMarginLeft(),
							item.getTextMarginleft()));
				}
			} else if (item.getIndex() == 3) {
				// 在野用户无认证权限
				if (!User.cerStatus.equals("0")) {
					if (item.getSplitHeight() > 0)
						list.add(SplitlistitemModelBase(
								R.drawable.item_split_bg, item.getSplitHeight()));
					list.add(initListModelBase(true, R.drawable.item_click_bg,
							-1, item.getTitle(), item.getTitle1(),
							item.getIndex(), 50, 0, item.getTextMarginleft()));
				}

			} else {

				if (item.getSplitHeight() > 0)
					list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
							item.getSplitHeight()));
				list.add(initListModelBase(true, R.drawable.item_click_bg, -1,
						item.getTitle(), item.getTitle1(), item.getIndex(), 50,
						item.getSplitMarginLeft(), item.getTextMarginleft()));
			}
		}

	}

	private void initListener() {
		adapter = new listItemAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				switch (list.get(position).getIndex()) {
				// 隐私
				case 1:
					changeView(settingYsActivity.class);
					break;
				// 通用
				case 2:
					changeView(settingTYActivity.class);
					break;
				// 认证服务
				case 3:
					changeView(rzfuShowActivity.class);
					break;
				// 关于微企
				case 4:
					changeView(about_us_Activity.class);
					break;

				}
				// TODO Auto-generated method stub

			}
		});
		btn_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MMAlert.dialogQuit(settingActivity.this,
						new alertItemclickListener() {
							@Override
							public void takePhotoClick() {
								if (CommonUtil.Quit(settingActivity.this)) {
									changeView(WelecomeActivity.class);
									CommonUtil.showToast(settingActivity.this,
											R.string.me_string_set_quit_msg);
								} else {
									CommonUtil
											.showToast(
													settingActivity.this,
													R.string.me_string_set_quit_err_msg);
								}

								// TODO Auto-generated method stub

							}

							@Override
							public void pickPhotoClick() {
								// TODO Auto-generated method stub

							}

							@Override
							public void cancleClick() {
								// TODO Auto-generated method stub

							}
						});

			}
		});
		setViewTouchListener(scroll);

	}

	private void initnavigation() {
		initNavitation(getString(R.string.me_string_sz), "", -1,
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

	private void clearCacheSize() {

		String cacheDir = "/Android/data/"
				+ settingActivity.this.getPackageName() + "/cache/";
		File f = new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
		if (f.exists() && f.isDirectory()) {
			Log.i("dfsdfsdf", "ddddddddd");
			delete(f);

			if (!f.exists() || f.listFiles().length == 0) {
				showRightDialog();

			}

		}

	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param file
	 *            File实例
	 * 
	 * @return long 单位为M
	 * 
	 * @throws Exception
	 */

	public static double getFolderSize(File file) throws Exception {

		double size = 0;

		File[] fileList = file.listFiles();

		for (int i = 0; i < fileList.length; i++)

		{

			if (fileList[i].isDirectory())

			{

				size = size + getFolderSize(fileList[i]);

			} else

			{
				Log.i("fileName=", fileList[i].getName());

				size = size + fileList[i].length();

			}

		}

		return size / 1024 / 1024;

	}

	public void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}

	}

}
