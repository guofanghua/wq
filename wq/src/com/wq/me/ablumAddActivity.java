package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.Adapter.addImagHorListviewAdapter;
import com.wq.Adapter.addImageGridAdapter;
import com.wq.PicCheck.Bimp;
import com.wq.PicCheck.FileUtils;
import com.wq.PicCheck.PicCheckMainActivity;
import com.wq.PicCheck.addImageShowAdapter;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.UI.XgridView;
import com.wq.UI.horizontalListview.HorizontalVariableListView;
import com.wq.model.User;
import com.wq.model.myObject;
import com.wq.model.ablum;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.ImageTools;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class ablumAddActivity extends BaseActivity {

	@ViewInject(id = R.id.me_grid_ablum)
	// HorizontalVariableListView horListview;
	XgridView grid_ablum;

	@ViewInject(id = R.id.me_add_ablum_edit_ms)
	TextView edit_ms;

	@ViewInject(id = R.id.ablum_btn_del)
	Button btn_del;
	// ArrayList<photoModel> listpic = new ArrayList<photoModel>();
	// addImageGridAdapter adapter;
	addImageGridAdapter adapter;
	String strTitle = "";// 顶部菜单
	public static int Max_SIZE = 6;
	ablum myablum = null;
	StringBuilder delSb = new StringBuilder();
	int groupPosition = -1;
	int childPosition = -1;
	FinalDb db;
	String id = "";
	ArrayList<Map<String, WeakReference<Bitmap>>> bitmpalist = new ArrayList<Map<String, WeakReference<Bitmap>>>();
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private static final int CROP = 2;
	private static final int CROP_PICTURE = 3;

	private static final int SCALE = 5;// 照片缩小比例

	// ArrayList<Map<String, WeakReference<Bitmap>>> bitmpalist = new
	// ArrayList<Map<String, WeakReference<Bitmap>>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ablum_add_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initData();
		initNavigation();
		initListener();
	}

	private void initData() {
		myablum = new ablum();
		Bundle b = this.getIntent().getExtras();
		if (b != null && b.getSerializable("ablum") != null) {
			myablum = (ablum) b.getSerializable("ablum");
			// checklocalData();

			groupPosition = b.getInt("group");
			childPosition = b.getInt("child");
			id = myablum.getId();
			// listpic.addAll(myablum.getList());

		}
		if (!TextUtils.isEmpty(id)) {
			strTitle = getResources().getString(
					R.string.me_string_ablum_edit_title);
		} else
			strTitle = getResources().getString(
					R.string.me_string_ablum_add_title);
		// 修改时，图片处理问题
		// for(int i=0;i<myablum.getList().size();i++)
		// {
		// Bimp.drr.add(myablum.getList().get(i).getImageUrl());
		// Bimp.bmp.add(object)
		// }
		adapter = new addImageGridAdapter(this, myablum.getList(), bitmpalist,
				myhandler);
		grid_ablum.setAdapter(adapter);
		edit_ms.setText(myablum.getContent());
		if ((myablum != null && !TextUtils.isEmpty(myablum.getId()))) {
			btn_del.setVisibility(View.VISIBLE);
		}
	}

	private void initListener() {

		btn_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delCommit();

			}
		});

	}

	private void initNavigation() {
		initNavitation(strTitle,
				getResources().getString(R.string.string_save),
				new EditClickListener() {

					@Override
					public void editClick() {

						if (!checkFrom())
							return;
						initpro();

						// TODO Auto-generated method stub
						AddAndEditcommit();
					}

					@Override
					public void backClick() {
						if (TextUtils.isEmpty(myablum.getId())) {
							finish();
							animDown();
						} else
						// TODO Auto-generated method stub
						{
							finish();
							animOut();
						}
					}
				});
	}

	public void click(View v) {

		Bundle bundle = new Bundle();

		switch (v.getId()) {
		case R.id.layout_lx:

		case R.id.me_add_ablum_edit_ms:
			editActivity.edit_backcontent = edit_ms;
			bundle.putString("title",
					getResources().getString(R.string.me_string_ablum_ms));
			// bundle.putString("limitWord", "12");
			changeView(editActivity.class, bundle, true);
			break;

		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (TextUtils.isEmpty(myablum.getId())) {
				finish();
				animDown();
			} else
			// TODO Auto-generated method stub
			{
				finish();
				animOut();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 添加或者编辑动态
	private void AddAndEditcommit() {
		showProgress(R.string.dialog_comitting);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ABLUM_ADD_SCRIPTION, edit_ms.getText().toString());

		int j = 0;
		for (int i = 0; i < myablum.getList().size(); i++) {
			if (myablum.getList().get(i).getFlag() != 1) {
				continue;
			}
			try {
				params.put("pic" + (j++), new File(myablum.getList().get(i)
						.getImageUrl()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		params.put(httpUtil.IMAGE_NUM, j + "");
		if (delSb.toString().length() > 0) {
			params.put(httpUtil.ABLUM_DEL_PIC, delSb.substring(1));
		}
		String url = "";
		if (TextUtils.isEmpty(myablum.getId())) {
			url = httpUtil.ABLUM_ADD_URL;
		} else {
			url = httpUtil.ABLUM_UPDATE_URL;
			params.put(httpUtil.ABLUM_ID, myablum.getId());
		}

		httpUtil.post(url, params, new AjaxCallBack<String>() {
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
						if (!jsonResult.isNull("id")) {
							myablum.setId(jsonResult.getString("id"));

						}

						// 添加图片
						JSONArray pic = jsonResult.getJSONArray("imgArray");
						myablum.getList().clear();
						for (int i = 0; i < pic.length(); i++) {
							JSONObject o = pic.getJSONObject(i);
							photoModel m = new photoModel();
							m.setEcid(myablum.getId());
							m.setFlag(0);
							m.setShareType(photoModel.ABLUM_SHARE_FLAG);
							m.setImageUrl(o.getString("imgUrl"));
							m.setShareModel(myablum);
							m.setId(o.getString("imgId"));
							try {
								db.save(m);
								myablum.getList().add(m);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						initpro();
						// 添加
						if (TextUtils.isEmpty(id)) {
							try {
								db.save(myablum);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						} else {
							// 更新
							try {
								db.update(myablum, "id='" + myablum.getId()
										+ "'");
							} catch (Exception ex) {
								ex.printStackTrace();
							}

						}
						hideProgress();
						Intent intent = new Intent();
						intent.setAction(ablumMainActivity.ABLUM_BRO_ACTION_NAME);
						intent.putExtra("ablum", myablum);
						intent.putExtra("sendFlag", true);
						intent.putExtra("flag", 0);
						if (!TextUtils.isEmpty(id)) {
							intent.putExtra("flag", 1);
						}
						// 添加产品
						else
							intent.putExtra("flag", 0);
						sendBroadcast(intent);
						finish();
						animOut();
					}
				} catch (JSONException e) {
					hideProgress();
					e.printStackTrace();
				} finally {

				}
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				hideProgress();
			}
		});

	}

	int errCount = 0;

	// 删除动态公告
	private void delCommit() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.ABLUM_DEL_ID, myablum.getId());
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.ABLUM_DEL_URL, params,
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
								hideProgress();
								try {
									db.delete(myablum);
									deleLocalImage(myablum.getList());
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								Intent intent = new Intent();
								intent.putExtra("group", groupPosition);
								intent.putExtra("child", childPosition);
								intent.putExtra("flag", 2);
								setResult(CommonUtil.NOTICE_RESULT_FLAG, intent);
								finish();
							} else {
								hideProgress();
								showToast(errMsg);
							}

						} catch (JSONException e) {
							hideProgress();
							CommonUtil.showToast(ablumAddActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(getApplicationContext(),
								errorNo);

					}
				});

	}

	public static final int CATE_FLAG = 110;

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
						Toast.makeText(ablumAddActivity.this, "图片没找到", 0)
								.show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Bitmap bitmap = null;

					String picpath = productAddActivity.FILE_SDCARD_PATH + "/"
							+ System.currentTimeMillis() + "compress.jepg";
					try {
						bitmap = Bimp.revitionImageSize(path);
						picpath = FileUtils.saveBitmapPath(bitmap, picpath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (!TextUtils.isEmpty(picpath)) {
						photoModel item = new photoModel();
						item.setFlag(1);
						item.setImageUrl(picpath);
						item.setShareModel(myablum);
						item.setEcid(myablum.getId());
						item.setShareType(photoModel.ABLUM_SHARE_FLAG);
						myablum.getList().add(item);
						adapter = new addImageGridAdapter(this,
								myablum.getList(), bitmpalist, myhandler);
						grid_ablum.setAdapter(adapter);
					}
				}

			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			Bitmap bitmap = null;
			String picpath = "";
			try {
				bitmap = Bimp.revitionImageSize(f.getAbsolutePath());
				picpath = FileUtils.saveBitmapPath(bitmap, f.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!TextUtils.isEmpty(picpath)) {
				photoModel item = new photoModel();
				item.setFlag(1);
				item.setImageUrl(picpath);
				item.setShareModel(myablum);
				item.setEcid(myablum.getId());
				item.setShareType(photoModel.ABLUM_SHARE_FLAG);
				myablum.getList().add(item);
				adapter = new addImageGridAdapter(this, myablum.getList(),
						bitmpalist, myhandler);
				grid_ablum.setAdapter(adapter);
			}
			// intent.putExtra("delFlag", true);
			// startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		}

	}

	private void initpro() {

		myablum.setContent(edit_ms.getText().toString());
		// if (myablum.getSendFlag().equals("1")
		// || TextUtils.isEmpty(myablum.getTime()))
		myablum.setTime(dateUtil.formatDate(new Date()));

	}

	private boolean checkFrom() {
		if (myablum.getList().size() == 0) {
			showToast(R.string.me_string_ablum_pic_warn);
			return false;
		}
		if (TextUtils.isEmpty(edit_ms.getText())) {
			edit_ms.setHintTextColor(Color.RED);
			return false;
		}
		return true;
	}

	private Handler myhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				scanPhoto(myablum.getList(), msg.arg1, true, false,
						new Handler() {
							public void handleMessage(Message msg) {
								// 服务器图片
								if (myablum.getList().get(msg.arg1).getId()
										.length() > 0) {

									delSb.append("|");
									delSb.append(myablum.getList()
											.get(msg.arg1).getId());
								}
								myablum.getList().remove(msg.arg1);
								grid_ablum.setAdapter(adapter);
							}
						});
			} else {
				if (myablum.getList().size() == Max_SIZE) {
					CommonUtil
							.showToast(
									getApplicationContext(),
									String.format(
											getString(R.string.me_string_img_max_format),
											Max_SIZE));
				} else
					MMAlert.showAlert(ablumAddActivity.this, false, Max_SIZE
							- myablum.getList().size());
			}
		}
		// 添加照片
	};

	// 删除数据库本地图片数据
	private void deleLocalImage(ArrayList<photoModel> delList) {
		if (delList.size() <= 0 || !db.checkmyTableExist(photoModel.class))
			return;
		StringBuilder sbBuilder = new StringBuilder();
		sbBuilder.append(" ( ");
		for (int i = 0; i < delList.size(); i++) {
			sbBuilder.append("'");
			sbBuilder.append(delList.get(i).getId());
			sbBuilder.append("'");
			if (i < delList.size() - 1)
				sbBuilder.append(" , ");
		}
		sbBuilder.append(" ) ");
		try {
			db.deleteByWhere(photoModel.class, " id in " + sbBuilder.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onDestory() {
		super.onDestory();
		// 高清的压缩图片全部就在 list 路径里面了
		// 高清的压缩过的 bmp 对象 都在 Bimp.bmp里面
		// 完成上传服务器后 .........
		// 清除缓存
		// FileUtils.deleteDir();
		// 清除bitmap
	}
}
