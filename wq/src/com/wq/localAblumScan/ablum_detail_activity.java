package com.wq.localAblumScan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.endure.framework.annotation.view.ViewInject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.UI.ClipActivity;
import com.wq.utils.BitmapUtil;


public class ablum_detail_activity extends BaseActivity {
	private PicAlbumMdl dataMdl;
	private List<PicItemMdl> mPicList;
	@ViewInject(id = R.id.gw_detail)
	GridView mGw;
	private PicAdapter mAdapter;
	public static int picNum = -1;// 最多只能选择几张照片

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_ablum_detail_activity);
		Bundle bd = getIntent().getExtras();
		if (bd != null) {
			this.dataMdl = (PicAlbumMdl) bd.getSerializable("data");

			if (dataMdl != null) {
				mPicList = this.dataMdl.getPicList();
			}
		}
		initnavigaiton();
		mAdapter = new PicAdapter(this, mPicList);
		mGw.setAdapter(mAdapter);
		mGw.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initnavigaiton() {
		initNavitation(dataMdl.getPathId(), getString(R.string.string_confirm),
				new BaseActivity.EditClickListener() {

					@Override
					public void editClick() {
						new prePicAsyc().execute("");
						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						finish();
						animOut();
						// TODO Auto-generated method stub

					}
				});
	}

	// 开启线程，图片压缩
	private class prePicAsyc extends AsyncTask<String, Integer, String> {
		List<PicItemMdl> tmpList = mAdapter.getList();
		ArrayList<PicItemMdl> list = new ArrayList<PicItemMdl>();

		@Override
		protected void onPreExecute() {
			showProgress("");
			tmpList = mAdapter.getList();

		}

		@Override
		protected String doInBackground(String... params) {
			for (int i = 0; i < tmpList.size(); i++) {
				if (tmpList.get(i).isSelected()) {
					PicItemMdl item = tmpList.get(i);
					item.setPath(saveCompressToLocal(item.getPath()));
					list.add(tmpList.get(i));
				}
			}
			// TODO Auto-generated method stub
			return "";
		}

		// onPostExecute方法用于在执行完后台任务后更新UI,显示结果
		@Override
		protected void onPostExecute(String result) {
			hideProgress();
			Intent intent = new Intent();
			Bundle bd = new Bundle();
			
			if (list.size() > 0)
				bd.putSerializable("data", list);
			intent.putExtras(bd);
			setResult(101, intent);
			finish();
		}

	};

	public static final String FILE_SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/wqCrop";

	// 创建压缩
	public String saveCompressToLocal(String pathStr) {
		if (!createSDCardDir(FILE_SDCARD_PATH))
			return null;
		Bitmap bm = creatCompressBitmap(pathStr);
		try {
			bm = creatCompressBitmap(pathStr);
		} catch (Exception e) {
			return ""; // TODO: handle exception
		}
		String path = FILE_SDCARD_PATH + "/" + System.currentTimeMillis()
				+ "compress.png";
		try {
			FileOutputStream fos = new FileOutputStream(path);
			boolean isinsample = false;
			try {
				File f = new File(path);
			
				if (f.length() > BitmapUtil.comMinSize) {
					isinsample = true;
				} else
					isinsample = false;

			} catch (Exception e) {
				isinsample = false;
			}
			if (isinsample)
				bm.compress(CompressFormat.JPEG, 30, fos);
			else
				bm.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return path;
	}

	/*
	 *  * 创建缩略图
	 */
	public Bitmap creatCompressBitmap(String path) {
		return BitmapUtil.rotate(
				BitmapUtil.creatBitmap(ablum_detail_activity.this, path),
				BitmapUtil.readPictureDegree(path));
	}

	// 创建文件夹
	public boolean createSDCardDir(String dirPath) {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			// 创建一个文件夹对象，赋值为外部存储器的目录

			// File sdcardDir = Environment.getExternalStorageDirectory();
			//
			// // 得到一个路径，内容是sdcard的文件夹路径和名字
			//
			// String path = sdcardDir.getPath() + "/" + dirName;

			File path1 = new File(dirPath);

			if (!path1.exists()) {

				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
			}
			return true;
		}

		else {
			return false;

		}

	}
}
