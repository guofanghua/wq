package com.wq.me;

/**名片强*/
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.AdapterView;
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

public class NameCardTopBgActivity extends BaseActivity {
	@ViewInject(id = R.id.attr_listview)
	InScrolllistView listView;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();
	// 模版
	ArrayList<photoModel> templeList = new ArrayList<photoModel>();
	waterPullGridAdapter tempAdapter;
	@ViewInject(id = R.id.gridview)
	XgridView gridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.name_card_top_bg_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		initdata();
		initListener();
	}

	private void initdata() {
		for (int i = 0; i < 5; i++) {
			photoModel item = new photoModel();
			item.setPid(R.drawable.welcome_to_vip);
			item.setExtStr("默认" + i);
			item.setFlag(1);

			templeList.add(item);
		}
		tempAdapter = new waterPullGridAdapter(templeList, this, null);
		gridView.setAdapter(tempAdapter);

		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources().getStringArray(
				R.array.name_card_top_bg_item);
		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getSplitHeight() > 0)
				list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
						item.getSplitHeight()));
			list.add(initListModelBase(item.isArrowShow(),
					R.drawable.item_click_bg, item.getImageid(),
					item.getTitle(), item.getTitle1(), item.getIndex(), 50,
					item.getSplitMarginLeft(), item.getTextMarginleft()));
		}
		list.add(SplitlistitemModelBase(R.drawable.item_split_bg, 40, "默认"));
		adapter = new listItemAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setFocusable(false);
		gridView.setFocusable(false);
	}

	private void initListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				switch (list.get(position).getIndex()) {
				// 选择一张照片
				case 1:
					MMAlert.showAlert(NameCardTopBgActivity.this, true, true);
					break;
				// 图片墙
				case 2:
					changeViewForResult(NameCardQiangActivity.class, 200);
					break;
				// 视频墙
				case 3:
					break;

				default:
					break;
				}
				// TODO Auto-generated method stub

			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				String picPath = FileUtils.saveBitmap(BitmapFactory
						.decodeResource(getResources(), templeList
								.get(position).getPid()), "topPic");
				if (!TextUtils.isEmpty(picPath))
					bigBgHttpData(picPath);
				// TODO Auto-generated method stub

			}
		});
	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.me_string_szbj), "",
				-1, new EditClickListener() {
					@Override
					public void editClick() {

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
					Intent intent = new Intent(this, ClipActivity2.class);
					intent.putExtra("path", path);
					intent.putExtra("isBackFlag", true);
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity2.class);
			intent.putExtra("path", f.getAbsolutePath());
			intent.putExtra("isBackFlag", true);
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		} else if (requestCode == MMAlert.FLAG_MODIFY_FINISH
				&& resultCode == RESULT_OK) {
			if (data != null) {
				String picpath = data.getStringExtra("path");
				if (!TextUtils.isEmpty(picpath)) {
					bigBgHttpData(picpath);
				}
			}
		} else if (requestCode == 200 && resultCode == 200) {
			if (data != null) {
				String picPathString = data.getStringExtra("picPath");
				bigBgHttpData(picPathString);
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
										.saveCompany(NameCardTopBgActivity.this);
								showRightDialog("设置成功",
										new showRightCallback() {

											@Override
											public void callback() {
												setResult(200);
												finish();
												animOut();
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
						CommonUtil.showToastHttp(NameCardTopBgActivity.this,
								errorNo);
					}
				});
	}

}
