package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.R.xml;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.Adapter.addImagHorListviewAdapter;
import com.wq.Adapter.addImageGridAdapter;
import com.wq.PicCheck.Bimp;
import com.wq.PicCheck.FileUtils;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.UI.XgridView;
import com.wq.UI.horizontalListview.HorizontalVariableListView;
import com.wq.model.User;
import com.wq.model.myObject;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class noticeAddActivity extends BaseActivity {

	@ViewInject(id = R.id.me_grid_ablum)
	XgridView horListview;
	@ViewInject(id = R.id.me_add_notice_edit_bt)
	TextView edit_title;
	@ViewInject(id = R.id.me_add_notice_edit_detail)
	TextView edit_detail;
	@ViewInject(id = R.id.me_add_notice_edit_lx)
	TextView edit_lx;
	private String id = "";
	@ViewInject(id = R.id.notice_btn_del)
	Button btn_del;
	// ArrayList<photoModel> listpic = new ArrayList<photoModel>();
	addImageGridAdapter adapter;
	String strTitle = "";// 顶部菜单
	private static int Max_SIZE = 6;
	notice myNotice = null;
	StringBuilder delSb = new StringBuilder();
	int groupPosition = -1;
	int childPosition = -1;
	FinalDb db;
	ArrayList<Map<String, WeakReference<Bitmap>>> bitmpalist = new ArrayList<Map<String, WeakReference<Bitmap>>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_add_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initData();
		initNavigation();

	}

	private void initData() {
		myNotice = new notice();
		Bundle b = this.getIntent().getExtras();
		if (b != null && b.getSerializable("notice") != null) {
			myNotice = (notice) b.getSerializable("notice");

			groupPosition = b.getInt("group");
			childPosition = b.getInt("child");
			id = myNotice.getId();
			// listpic.addAll(myNotice.getPic());

		}
		if (!TextUtils.isEmpty(id)) {
			strTitle = getResources().getString(
					R.string.me_string_edit_notice_title);
		} else
			strTitle = getResources().getString(
					R.string.me_string_add_notice_title);
		adapter = new addImageGridAdapter(this, myNotice.getPic(), bitmpalist,
				myhandler);
		horListview.setAdapter(adapter);

		edit_title.setText(myNotice.getTitle());
		edit_detail.setText(myNotice.getContent());
		edit_lx.setText(myNotice.getCateName());
		btn_del.setVisibility(View.GONE);
	}

	private void initNavigation() {
		initNavitation(strTitle,
				getResources().getString(R.string.string_save),
				new EditClickListener() {

					@Override
					public void editClick() {
						if (!checkFrom()) {
							return;
						}
						// TODO Auto-generated method stub
						AddAndEditcommit();
					}

					@Override
					public void backClick() {
						if (TextUtils.isEmpty(myNotice.getId())) {
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
		case R.id.me_add_notice_edit_lx:
			bundle.putString("cateName", edit_lx.getText().toString());
			changeViewForResult(noticeCateActivity.class, bundle, 0);
			break;
		case R.id.layout_title:
		case R.id.me_add_notice_edit_bt:
			editActivity.edit_backcontent = edit_title;
			bundle.putString("title",
					getResources().getString(R.string.me_string_add_notice_bt));
			// bundle.putString("limitWord", "12");
			changeView(editActivity.class, bundle, true);
			break;
		case R.id.layout_content:
		case R.id.me_add_notice_edit_detail:
			editActivity.edit_backcontent = edit_detail;
			bundle.putString(
					"title",
					getResources().getString(
							R.string.me_string_add_notice_detail));
			// bundle.putString("limitWord", "12");
			changeView(editActivity.class, bundle, true);
			break;
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// exitBy2Click();
			if (TextUtils.isEmpty(myNotice.getId())) {
				finish();
				animDown();
			} else {
				finish();
				animOut();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	// 添加或者编辑动态
	private void AddAndEditcommit() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.NOTICE_ADD_TITLE, edit_title.getText().toString()
				.trim());
		params.put(httpUtil.NOTICE_ADD_CONTENT, edit_detail.getText()
				.toString().trim());
		params.put(httpUtil.NOTICE_ADD_CATENAME, edit_lx.getText().toString()
				.trim());
		showProgress(R.string.dialog_comitting);
		int j = 0;
		for (int i = 0; i < myNotice.getPic().size(); i++) {
			if (myNotice.getPic().get(i).getFlag() != 1) {
				continue;
			}
			try {
				params.put("pic" + (j++), new File(myNotice.getPic().get(i)
						.getImageUrl()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		params.put(httpUtil.IMAGE_NUM, j + "");
		if (delSb.toString().length() > 0) {
			params.put(httpUtil.NOTICE_DEL_PIC, delSb.substring(1));
		}
		String url = "";
		if (TextUtils.isEmpty(myNotice.getId())) {
			url = httpUtil.NOTICE_ADD_URL;

		} else {
			url = httpUtil.NOTICE_UPDATE_URL;
			params.put(httpUtil.NOTICE_UPDATE_ID, myNotice.getId());
		}

		httpUtil.post(url, params, new AjaxCallBack<String>() {
			private String errMsg = "";
			private String errcode = "";

			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {

				;
				try {

					JSONObject jsonResult = new JSONObject(result);
					errcode = jsonResult.getString(httpUtil.ERR_CODE);
					errMsg = jsonResult.getString(httpUtil.ERR_MGS);
					if (errcode.equals(httpUtil.errCode_success)) {
						hideProgress();
						if (!jsonResult.isNull("id")) {
							myNotice.setId(jsonResult.getString("id"));

							// 添加图片
							JSONArray pic = jsonResult.getJSONArray("imgArray");
							myNotice.getPic().clear();
							for (int i = 0; i < pic.length(); i++) {
								JSONObject o = pic.getJSONObject(i);
								photoModel m = new photoModel();
								m.setEcid(myNotice.getId());
								m.setFlag(0);
								m.setShareType(photoModel.NOTICE_SHARE_FLAG);
								m.setImageUrl(o.getString("imgUrl"));
								m.setShareModel(myNotice);
								m.setId(o.getString("imgId"));
								try {
									db.save(m);
									myNotice.getPic().add(m);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
							initpro();
							// 添加
							if (TextUtils.isEmpty(id)) {
								try {
									db.save(myNotice);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							} else {
								// 更新
								try {
									db.update(myNotice,
											"id='" + myNotice.getId() + "'");
								} catch (Exception ex) {
									ex.printStackTrace();
								}

							}

							Intent intent = new Intent();
							intent.setAction(noticeMainActivity.PART_BRO_ACTION_NAME);
							intent.putExtra("notice", myNotice);
							intent.putExtra("sendFlag", true);
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
						Toast.makeText(noticeAddActivity.this, "图片没找到", 0)
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
						item.setShareModel(myNotice);
						item.setEcid(myNotice.getId());
						item.setShareType(photoModel.NOTICE_SHARE_FLAG);
						myNotice.getPic().add(item);
						adapter = new addImageGridAdapter(this,
								myNotice.getPic(), bitmpalist, myhandler);
						horListview.setAdapter(adapter);
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
				item.setShareModel(myNotice);
				item.setEcid(myNotice.getId());
				item.setShareType(photoModel.NOTICE_SHARE_FLAG);
				myNotice.getPic().add(item);
				adapter = new addImageGridAdapter(this, myNotice.getPic(),
						bitmpalist, myhandler);
				horListview.setAdapter(adapter);
			}
			// intent.putExtra("delFlag", true);
			// startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		}
		// 添加分类
		else if (resultCode == CATE_FLAG) {
			if (data != null) {
				LoggerUtil.i(data.getStringExtra("cateName"));
				edit_lx.setText(data.getStringExtra("cateName"));
			}
		}
	}

	private void initpro() {
		myNotice.setCateName(edit_lx.getText().toString());
		myNotice.setTitle(edit_title.getText().toString());
		myNotice.setContent(edit_detail.getText().toString());
		myNotice.setTime(dateUtil.formatDate(new Date()));

	}

	private boolean checkFrom() {
		if (myNotice.getPic().size() == 0) {
			showToast(R.string.me_string_notice_pic_warn);
			return false;
		}
		// 动态类型
		if (TextUtils.isEmpty(edit_lx.getText())) {
			edit_lx.setHintTextColor(Color.RED);
			return false;
		}
		// 标题
		if (TextUtils.isEmpty(edit_title.getText())) {
			edit_title.setHintTextColor(Color.RED);
			return false;
		}
		// 内容
		if (TextUtils.isEmpty(edit_detail.getText())) {
			edit_detail.setHintTextColor(Color.RED);
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

				scanPhoto(myNotice.getPic(), msg.arg1, true, false,
						new Handler() {
							public void handleMessage(Message msg) {
								if (myNotice.getPic().get(msg.arg1).getId()
										.length() > 0) {
									delSb.append("|");
									delSb.append(myNotice.getPic()
											.get(msg.arg1).getId());
								}
								myNotice.getPic().remove(msg.arg1);
								horListview.setAdapter(adapter);
								// adapter.notifyDataSetChanged();
							}
						});
			} else {
				if (myNotice.getPic().size() == Max_SIZE) {
					CommonUtil
							.showToast(
									getApplicationContext(),
									String.format(
											getString(R.string.me_string_img_max_format),
											Max_SIZE));

				} else
					MMAlert.showAlert(noticeAddActivity.this, false, Max_SIZE
							- myNotice.getPic().size());
			}
		}
		// 添加照片

	};
}
