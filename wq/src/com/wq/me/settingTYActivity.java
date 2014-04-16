package com.wq.me;

import java.io.File;
import java.util.ArrayList;

import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.listItemAdapter;
import com.wq.UI.InScrolllistView;
import com.wq.model.User;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.stringUtils;

/**
 * 通用
 * 
 * **/
public class settingTYActivity extends BaseActivity {

	@ViewInject(id = R.id.listview)
	InScrolllistView listview;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();

	@ViewInject(id = R.id.scroll)
	ScrollView scroll;
	FinalDb db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_ty_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initnavigation();
		initdata();
		initListener();

	}

	private void initdata() {
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources().getStringArray(R.array.sz_ty_list_item);
		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getSplitHeight() > 0)

				list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
						item.getSplitHeight()));
			if (i == 0) {

				item.setTitle1(getResources().getString(
						R.string.me_string_ty_foll_sys));

			} else if (i == 1) {
				item.setTitle1(String.format("%.2f MB", getCacheSize() / 2));
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

					break;
				case 2:
					clearCacheSize();
					break;

				default:
					break;
				}

			}
		});

	}

	private void initnavigation() {
		initNavitation(getString(R.string.me_string_set_ty), "", -1,
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

	private double getCacheSize() {
		double cacheSize = 0;
		String cacheDir = "/Android/data/"
				+ settingTYActivity.this.getPackageName() + "/cache/";
		File f = new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
		if (f.exists() && f.isDirectory()) {
			try {
				cacheSize = getFolderSize(f);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return cacheSize;
	}

	private void clearCacheSize() {

		String cacheDir = "/Android/data/"
				+ settingTYActivity.this.getPackageName() + "/cache/";
		File f = new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
		if (f.exists() && f.isDirectory()) {
			delete(f);

			if (!f.exists() || f.listFiles().length == 0) {
				showRightDialog(getString(R.string.me_string_set_cache_success));
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getIndex() == 2) {
						list.get(i).setTittle1(
								String.format("%.2f MB", getCacheSize()));
						adapter.notifyDataSetChanged();
						break;
					}
				}

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

	public void onResume() {
		super.onResume();
	}
}
