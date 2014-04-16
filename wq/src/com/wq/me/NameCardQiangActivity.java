package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.addImageGridAdapter;
import com.wq.Adapter.listItemAdapter;
import com.wq.Adapter.waterPullGridAdapter;
import com.wq.Adapter.waterPullGridAdapter.callback;
import com.wq.BaseActivity.EditClickListener;
import com.wq.PicCheck.FileUtils;
import com.wq.UI.ClipActivity1;
import com.wq.UI.ClipActivity2;
import com.wq.UI.ClipActivity3;
import com.wq.UI.InScrolllistView;
import com.wq.UI.MMAlert;
import com.wq.UI.XgridView;
import com.wq.find.findEpCircleListActivity;
import com.wq.model.User;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import com.wq.utils.stringUtils;

public class NameCardQiangActivity extends BaseActivity {

	@ViewInject(id = R.id.img1)
	ImageView img1;
	@ViewInject(id = R.id.img2)
	ImageView img2;
	@ViewInject(id = R.id.img3)
	ImageView img3;
	int FlagIndex = 0;
	FinalBitmap finalbitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.name_card_qiang_activity);
		BaseApplication.getInstance().addActivity(this);
		finalbitmap = FinalBitmap.create(this);
		initnavigation();

		initListener();
	}

	private void initListener() {
		img1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlagIndex = 1;
				MMAlert.showAlert(NameCardQiangActivity.this, true, true);
				// TODO Auto-generated method stub

			}
		});
		img2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlagIndex = 2;
				MMAlert.showAlert(NameCardQiangActivity.this, true, true);
				// TODO Auto-generated method stub

			}
		});
		img3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlagIndex = 3;
				MMAlert.showAlert(NameCardQiangActivity.this, true, true);
				// TODO Auto-generated method stub

			}
		});

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.me_string_sztpq),
				"保存", 0, new EditClickListener() {
					@Override
					public void editClick() {
						String picPath = FileUtils.saveBitmap(getShootBitmap(),
								"topPic");

						if (!TextUtils.isEmpty(picPath)) {
							Intent intent = new Intent();
							intent.putExtra("picPath", picPath);
							setResult(200, intent);
							finish();
						}

					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

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
					Intent intent = new Intent();
					if (FlagIndex == 1)
						intent.setClass(NameCardQiangActivity.this,
								ClipActivity3.class);
					else {
						intent.setClass(NameCardQiangActivity.this,
								ClipActivity1.class);
					}
					intent.putExtra("path", path);
					intent.putExtra("isBackFlag", true);
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent();
			if (FlagIndex == 1)
				intent.setClass(NameCardQiangActivity.this, ClipActivity3.class);
			else {
				intent.setClass(NameCardQiangActivity.this, ClipActivity2.class);
			}
			intent.putExtra("path", f.getAbsolutePath());
			intent.putExtra("isBackFlag", true);
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		} else if (requestCode == MMAlert.FLAG_MODIFY_FINISH
				&& resultCode == RESULT_OK) {
			if (data != null) {
				String picpath = data.getStringExtra("path");
				if (!TextUtils.isEmpty(picpath)) {
					if (FlagIndex == 1) {
						finalbitmap.displayLocal(img1, picpath, true);
					} else if (FlagIndex == 2)
						finalbitmap.displayLocal(img2, picpath, false);
					else if (FlagIndex == 3) {
						finalbitmap.displayLocal(img3, picpath, false);
					}

				}
			}
		}
	}

	/*
	 * 更新顶图
	 */
	public void bigBgHttpData(String filePath) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		showProgress(R.string.dialog_comitting);

		try {
			params.put("pic0", new File(filePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		httpUtil.post(httpUtil.CIRCLE_PROGA_PIC_URL, params,
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
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								User.propagandaFile = jsonResult
										.getString("propagandaImg");
								sharedPreferenceUtil
										.saveCompany(NameCardQiangActivity.this);
								showRightDialog("设置成功",
										new showRightCallback() {

											@Override
											public void callback() {

												// TODO Auto-generated method
												// stub
												setResult(200);
												finish();
											}
										});
							} else {
								showToast(errMsg);
							}

						} catch (JSONException e) {

							showToast(R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							hideProgress();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {

						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(NameCardQiangActivity.this,
								errorNo);
					}
				});
	}

	// private int statusBarHeight = 0;
	private int titleBarHeight = 0;

	private Bitmap getShootBitmap() {
		getBarHeight();
		int screenWidth = DensityUtil.intScreenWidth(this);
		Bitmap finalBitmap = Bitmap.createBitmap(takeScreenShot(), 0,
				titleBarHeight + DensityUtil.dip2px(this, 50), screenWidth,
				DensityUtil.dip2px(this, 250));
		return finalBitmap;

	}

	private void getBarHeight() {
		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// statusBarHeight = frame.top;
		int contenttop = this.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		titleBarHeight = contenttop;

	}

	// 获取Activity的截屏
	private Bitmap takeScreenShot() {
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}
}
