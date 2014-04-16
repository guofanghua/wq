package com.wq.me;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.Adapter.MingPAdapter;
import com.wq.UI.ClipActivity2;
import com.wq.UI.MMAlert;
import com.wq.model.MingPMdl;
import com.wq.model.User;
import com.wq.utils.SDCardUtils;

public class MingPListActivity extends BaseActivity {
	private static final String mpDbPath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/.wqdb/mingp.db";
	@ViewInject(id = R.id.List)
	private ListView mListView;

	private FinalDb mFinalDb;
	private List<MingPMdl> dataList;
	private MingPAdapter adapter;
	private ItemListener longItemListener;
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri= Uri.parse(IMAGE_FILE_LOCATION);//to store the big bitmap

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mingp_list);
		initnavigation();
		readData();
		adapter = new MingPAdapter(this, dataList);
		mListView.setAdapter(adapter);
		longItemListener = new ItemListener();
		mListView.setOnItemLongClickListener(longItemListener);

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.mingp_box), "",
				R.drawable.btn_photo_click, new EditClickListener() {
					@Override
					public void editClick() {
						boolean b = SDCardUtils.ExistSDCard();
						if (!b) {
							showToast("请先插入sd卡");
							return;
						}
						MMAlert.showAlert(MingPListActivity.this, true,true);
					}

					@Override
					public void backClick() {
						finish();
						animDown();
					}
				});
	}

	private void readData() {
		dataList = new ArrayList<MingPMdl>();
		boolean b = SDCardUtils.ExistSDCard();
		if (!b) {
			showToast("请先插入sd卡");
			return;
		}
		mFinalDb = FinalDb.create(this, mpDbPath);
		if (mFinalDb == null)
			return;
		try {
			dataList = mFinalDb.findAll(MingPMdl.class);
			dataList = mFinalDb.findAllByWhere(MingPMdl.class, "userId='"
					+ User.id + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MMAlert.FLAG_CHOOSE_IMG && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri,
							new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					if (null == cursor) {
						Toast.makeText(this, "图片没找到", 0).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Intent intent = new Intent(this, ClipActivity2.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
					finish();
				} else {
					Intent intent = new Intent(this, ClipActivity2.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
					finish();
				}
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity2.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
			finish();
		}
	}

	class ItemListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			if (adapter != null) {
				adapter.showDelteItem(arg2);
			}
			return true;
		}

	}

}
