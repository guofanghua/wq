package com.wq;

import java.io.File;
import java.util.ArrayList;

import com.endure.wq.R;

import com.wq.Adapter.addImagHorListviewAdapter;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.UI.horizontalListview.HorizontalVariableListView;

import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.RegexpUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

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

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.ScrollView;
import android.widget.TextView;

import net.endure.framework.annotation.view.ViewInject;

public class authApplyActivity extends BaseActivity {
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll;
	// 单位名称
	@ViewInject(id = R.id.edit_com_name)
	EditText edit_name;
	// 单位地址
	@ViewInject(id = R.id.edit_com_addr)
	EditText edit_addr;
	// 单位电话
	@ViewInject(id = R.id.edit_com_mobile)
	EditText edit_mobile;
	// 企业简介
	@ViewInject(id = R.id.edit_com_intro)
	EditText edit_intro;
	// 法人代表
	@ViewInject(id = R.id.me_edit_qydb)
	EditText edit_db;
	// 认证申请协议
	@ViewInject(id = R.id.txt_read_rzxy)
	TextView txt_rzxy;
	@ViewInject(id = R.id.check_box)
	CheckBox chck;
	addImagHorListviewAdapter ablumAdapter;
	@ViewInject(id = R.id.img_add_pic)
	ImageView img_addPic;// 添加图片按钮
	@ViewInject(id = R.id.auth_ablum_listview)
	HorizontalVariableListView list_ablum;// 显示相册
	private ArrayList<photoModel> picList = new ArrayList<photoModel>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth_apply_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavigation();
		BindUI();
	}

	private void BindUI() {
		ablumAdapter = new addImagHorListviewAdapter(this, picList,
				imgClickHandler);
		list_ablum.setAdapter(ablumAdapter);
		edit_addr.setOnClickListener(new click());
		edit_db.setOnClickListener(new click());
		edit_intro.setOnClickListener(new click());
		edit_mobile.setOnClickListener(new click());
		edit_name.setOnClickListener(new click());
		txt_rzxy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		img_addPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MMAlert.showAlert(authApplyActivity.this, false, -1);
			}
		});
		// btn_up_pic.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// MMAlert.showAlert(authApplyActivity.this, true);
		// }
		// });
		// img_pic.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ArrayList<photoModel> p = new ArrayList<photoModel>();
		// photoModel item = new photoModel();
		//
		// item.setFlag(Integer.parseInt(String.valueOf(img_pic.getTag())));
		// // TODO Auto-generated method stub
		// if (!TextUtils.isEmpty(picpath))
		//
		// item.setImageUrl(picpath);
		// p.add(item);
		// scanPhoto(p, 0, false, null);
		// }
		// });
	}

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			Intent intent = new Intent(authApplyActivity.this,
					editActivity.class);
			switch (v.getId()) {

			// 单位地址
			case R.id.edit_com_addr:

				editActivity.edit_backcontent = edit_addr;
				bundle.putString("title",
						getResources().getString(R.string.string_auth_com_addr));
				break;
			// 联系电话
			case R.id.edit_com_mobile:

				editActivity.edit_backcontent = edit_mobile;
				bundle.putString("Regexmsg",
						RegexpUtil.phone_regexp);
				bundle.putString("title",
						getResources()
								.getString(R.string.string_auth_com_photo));
				break;

			// 企业简介
			case R.id.edit_com_intro:

				editActivity.edit_backcontent = edit_intro;
				bundle.putString("title",
						getResources()
								.getString(R.string.string_auth_com_intro));
				break;

			// 企业代表
			case R.id.me_edit_qydb:

				editActivity.edit_backcontent = edit_db;
				bundle.putString("title",
						getResources().getString(R.string.string_auth_com_qydb));
				break;
			}
			intent.putExtras(bundle);
			startActivity(intent);
			animUp();
			// TODO Auto-generated method stub
		}
	}

	// 检测输入是否合法
	private boolean checkForm() {
		if (TextUtils.isEmpty(edit_name.getText())) {
			CommonUtil.showToast(this, R.string.string_auth_form_warn_name);
			return false;
		}

		if (TextUtils.isEmpty(edit_addr.getText())) {
			CommonUtil.showToast(this, R.string.string_auth_form_warn_addr);
			return false;
		}
		if (TextUtils.isEmpty(edit_mobile.getText())) {
			CommonUtil.showToast(this, R.string.string_auth_form_warn_mobile);
			return false;
		}

		if (TextUtils.isEmpty(edit_intro.getText())) {
			CommonUtil.showToast(this, R.string.string_auth_form_warn_intro);
			return false;
		}
		if (!chck.isChecked()) {
			CommonUtil.showToast(this, R.string.string_auth_form_warn_read_xy);
			return false;
		}
		return true;
	}

	// 提交申请
	private void httpCommitData() {
	}

	private void initNavigation() {
		initNavitation(getString(R.string.string_auth_title), "", -1,
				new EditClickListener() {

					@Override
					public void editClick() {
						if (checkForm())
							// TODO Auto-generated method stub
							httpCommitData();
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
						showToast("图片没找到");

						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();

					Intent intent = new Intent(this, ClipActivity.class);
					intent.putExtra("path", path);

					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				} else {

					Intent intent = new Intent(this, ClipActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		} else if (requestCode == MMAlert.FLAG_MODIFY_FINISH
				&& resultCode == RESULT_OK) {
			if (data != null) {

				String picpath = data.getStringExtra("path");
				photoModel item = new photoModel();
				item.setFlag(1);
				item.setImageUrl(picpath);
				picList.add(item);
				ablumAdapter.notifyDataSetChanged();
				// Log.i("path", picpath.toString());
				// Bitmap b = BitmapFactory.decodeFile(picpath);
				// img_pic.setVisibility(View.VISIBLE);
				// img_pic.setImageBitmap(b);
				// img_pic.setTag("1");

			}
		}
	}

	/**
	 * 水平滑动listview itemclick失效，
	 * */
	private Handler imgClickHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			super.handleMessage(msg);
			scanPhoto(picList, msg.what, true, false, new Handler() {
				public void handleMessage(Message msg) {
					picList.remove(msg.arg1);
					ablumAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// exitBy2Click();
			DialogUtils.showDialog(this, getString(R.string.string_back_warn),
					"", getString(R.string.string_dialog_qd),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							finish();
							animOut();

						}
					}, getString(R.string.string_cacel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

}
