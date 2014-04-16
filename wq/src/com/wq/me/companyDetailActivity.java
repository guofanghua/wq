package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.ablumActivity;
import com.wq.editActivity;
import com.wq.loginActivity;
import com.wq.mainActivity;
import com.wq.showLogoActivity;
import com.wq.Adapter.viewPageAdapter;
import com.wq.UI.ClipActivity;
import com.wq.UI.ClipActivity1;
import com.wq.UI.CustomProgressDialog;
import com.wq.UI.MMAlert;
import com.wq.find.findZHActivity;
import com.wq.fragment.inqFragment;
import com.wq.fragment.meFragment;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.RegexpUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

public class companyDetailActivity extends BaseActivity {
	@ViewInject(id = R.id.me_rela_tx)
	LinearLayout rela_tx;// 头像
	@ViewInject(id = R.id.me_img_logo)
	ImageView img_logo;// 头像
	@ViewInject(id = R.id.me_rela1)
	ScrollView rela_main;
	@ViewInject(id = R.id.me_rela_lx_name)
	RelativeLayout rela_lx_name;// 姓名
	@ViewInject(id = R.id.me_edit_lx_name)
	TextView edit_lx_name;// 姓名
	@ViewInject(id = R.id.me_rela_lx_zw)
	RelativeLayout rela_lx_zw;// 职务
	@ViewInject(id = R.id.me_edit_lx_zw)
	TextView edit_lx_zw;// 职务
	@ViewInject(id = R.id.me_rela_lx_mobile)
	RelativeLayout rela_lx_mobile;// 手机
	@ViewInject(id = R.id.me_edit_lx_mobile)
	TextView edit_lx_mobile;// 手机
	@ViewInject(id = R.id.me_rela_lx_we_chat)
	RelativeLayout rela_lx_we_chat;// 微信
	@ViewInject(id = R.id.me_edit_lx_we_chat)
	TextView edit_lx_we_chat;// 微信
	@ViewInject(id = R.id.me_rela_lx_gx)
	LinearLayout rela_gx;// 个性签名
	@ViewInject(id = R.id.me_edit_lx_gx)
	TextView txt_gx;// 个性签名
	@ViewInject(id = R.id.me_rela_lx_photo)
	RelativeLayout rela_lx_photo;// 电话
	@ViewInject(id = R.id.me_edit_lx_photo)
	TextView edit_lx_photo;// 电话
	@ViewInject(id = R.id.me_rela_lx_email)
	RelativeLayout rela_lx_email;// 邮箱
	@ViewInject(id = R.id.me_edit_lx_email)
	TextView edit_lx_email;// 邮箱
	@ViewInject(id = R.id.me_rela_lx_addr)
	RelativeLayout rela_lx_addr;// 地址
	@ViewInject(id = R.id.me_edit_lx_addr)
	TextView edit_lx_addr;// 地址
	// 微企号
	@ViewInject(id = R.id.txt_com_wq_name)
	TextView txt_wq_name;
	// 公司名称
	@ViewInject(id = R.id.me_rela_com_name)
	RelativeLayout rela_lx_com_name;// 公司名称
	@ViewInject(id = R.id.txt_com_name)
	TextView edit_lx_com_name;// 公司名称
	@ViewInject(id = R.id.img_com_name)
	ImageView img_com_arrow;
	int errCount = 0;
	public static boolean isUpdate = false;
	public String picpath = "";
	BitmapDisplayConfig imgConfig = new BitmapDisplayConfig();

	private boolean isEditFlag = false;// 是否编辑

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_company_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		imgConfig.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		imgConfig.setLoadfailBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.add_prompt));
	
		initnavigation();
		initListener();
		BindUI();
	}

	private void BindUI() {
		img_logo.setTag("1");
		if (!TextUtils.isEmpty(User.iconFile)) {
			if (User.iconFile.indexOf("http") >= 0) {
				utilBitmap.display(img_logo, User.iconFile, imgConfig, true);
			} else {
				utilBitmap.displayLocal(img_logo, User.iconFile, true);
			}
		}
		// edit_com_name.setText(User.name);
		// txt_wq_name.setText(User.wqh);
		txt_wq_name.setText(User.wqh);
		edit_lx_mobile.setText(User.moblie);
		edit_lx_name.setText(User.contactName);
		edit_lx_we_chat.setText(User.weChat);
		edit_lx_zw.setText(User.occupation);
		edit_lx_addr.setText(User.address);
		edit_lx_email.setText(User.email);
		edit_lx_photo.setText(User.telePhone);
		if (TextUtils.isEmpty(User.name)) {
			img_com_arrow.setVisibility(View.VISIBLE);
		} else {
			edit_lx_com_name.setText(User.name);
			img_com_arrow.setVisibility(View.GONE);
		}

		txt_gx.setText(User.signature);
	}

	private void initListener() {

		rela_tx.setOnClickListener(new onclick());
		// rela_qym.setOnClickListener(new onclick());
		// rela_wqm.setOnClickListener(new onclick());

		rela_lx_mobile.setOnClickListener(new onclick());

		rela_lx_name.setOnClickListener(new onclick());
		rela_lx_we_chat.setOnClickListener(new onclick());
		rela_lx_photo.setOnClickListener(new onclick());
		rela_lx_email.setOnClickListener(new onclick());
		rela_lx_addr.setOnClickListener(new onclick());
		rela_lx_zw.setOnClickListener(new onclick());
		rela_gx.setOnClickListener(new onclick());
		img_logo.setOnClickListener(new onclick());

		if (TextUtils.isEmpty(User.name)) {
			rela_lx_com_name.setOnClickListener(new onclick());
		}
		// setViewTouchListener(rela_main);

	}

	private void initnavigation() {
		// 名片

		initNavitation(
				getResources().getString(R.string.me_string_com_info_title),
				"", -1, new EditClickListener() {

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

	private class onclick implements OnClickListener {
		Bundle bundle = new Bundle();
		boolean isJump = true;

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			// 头像
			case R.id.me_rela_tx:
				isJump = false;
				MMAlert.showAlertClip(companyDetailActivity.this, true, true);
				break;
			case R.id.me_img_logo:
				isJump = false;
				Bundle b = new Bundle();
				if (!TextUtils.isEmpty(User.iconFile)) {
					b.putSerializable("filePath", User.iconFile);
					Intent intent = new Intent(companyDetailActivity.this,
							showLogoActivity.class);
					intent.putExtras(b);
					startActivity(intent);
					overridePendingTransition(R.anim.ablum_transition_in,
							R.anim.transition_in);
				} else {
					MMAlert.showAlertClip(companyDetailActivity.this, true,
							true);
				}

				break;

			// 微企名
			case R.id.me_rela_wqm:
				isJump = false;
				showToast(R.string.me_string_wqh_warn);
				break;
			// 企业名
			case R.id.me_rela_com_name:
				isJump = false;
				changeViewForResult(meProNameActivity.class, 111);
				break;
			// 职务
			case R.id.me_rela_lx_zw:
				editActivity.edit_backcontent = edit_lx_zw;
				edit_lx_zw.setTag("1");
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_lx_zw));

				break;
			// 手机

			case R.id.me_rela_lx_mobile:
				editActivity.edit_backcontent = edit_lx_mobile;
				edit_lx_mobile.setTag("1");
				bundle.putString("Regexmsg", RegexpUtil.phone_regexp);
				bundle.putString("RegexWarn", getString(R.string.regex_mobile));
				bundle.putBoolean("IsPhoto", true);
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_lx_mobile));

				break;
			// 电话
			case R.id.me_rela_lx_photo:
				editActivity.edit_backcontent = edit_lx_photo;
				edit_lx_photo.setTag("1");
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_mobile));
				break;
			// 邮箱
			case R.id.me_rela_lx_email:
				editActivity.edit_backcontent = edit_lx_email;
				edit_lx_email.setTag("1");
				bundle.putString("Regexmsg", RegexpUtil.email_regexp);
				bundle.putString("RegexWarn", getString(R.string.regex_email));
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_email));
				break;
			// 地址
			case R.id.me_rela_lx_addr:
				editActivity.edit_backcontent = edit_lx_addr;
				edit_lx_addr.setTag("1");
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_addr));
				break;
			// 姓名
			case R.id.me_rela_lx_name:
				editActivity.edit_backcontent = edit_lx_name;
				edit_lx_name.setTag("1");
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_lx_name));
				break;
			// 微信号
			case R.id.me_rela_lx_we_chat:
				editActivity.edit_backcontent = edit_lx_we_chat;
				edit_lx_we_chat.setTag("1");
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_lx_we_chat));
				break;
			// 个性签名
			case R.id.me_rela_lx_gx:
				editActivity.edit_backcontent = txt_gx;
				txt_gx.setTag("1");
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_com_info_lx_gx));
				break;
			}

			if (isJump) {
				isEditFlag = true;
				changeView(editActivity.class, bundle, true);
			}
			// TODO Auto-generated method stub

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
						Toast.makeText(companyDetailActivity.this, "图片没找到", 0)
								.show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();

					Intent intent = new Intent(this, ClipActivity1.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				} else {

					Intent intent = new Intent(this, ClipActivity1.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
			Intent intent = new Intent(this, ClipActivity1.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		} else if (requestCode == MMAlert.FLAG_MODIFY_FINISH
				&& resultCode == RESULT_OK) {
			if (data != null) {
				picpath = data.getStringExtra("path");
				img_logo.setImageBitmap(BitmapUtil.toRoundCorner(
						BitmapAivenUtils.readBitmap(companyDetailActivity.this,
								picpath), 5));
				commmit(true, true);
			}
		}
		// 更新公司名称
		else if (resultCode == 111) {
			if (TextUtils.isEmpty(data.getStringExtra("name"))) {
			} else {
				rela_lx_com_name.setEnabled(false);
				img_com_arrow.setVisibility(View.GONE);
				edit_lx_com_name.setText(data.getStringExtra("name"));
			}

		}
	}

	// 提交修改
	private void commmit(final boolean isUpImage, boolean isshowDialog) {
		if (isshowDialog) {
			showProgress(R.string.dialog_comitting);
		}
		User.moblie = edit_lx_mobile.getText().toString();
		User.contactName = edit_lx_name.getText().toString();
		User.occupation = edit_lx_zw.getText().toString();
		User.weChat = edit_lx_we_chat.getText().toString();
		User.signature = txt_gx.getText().toString();
		User.address = edit_lx_addr.getText().toString();
		User.email = edit_lx_email.getText().toString();
		User.telePhone = edit_lx_photo.getText().toString();
		sharedPreferenceUtil.saveCompany(this);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		if (!isUpImage) {
			params.put(httpUtil.ENTER_UPDATE_LX_MOBILE,
					CommonUtil.UpdateStr(edit_lx_mobile));
			params.put(httpUtil.ENTER_UPDATE_LX_NAME,
					CommonUtil.UpdateStr(edit_lx_name));
			params.put(httpUtil.ENTER_UPDATE_LX_WECHAT,
					CommonUtil.UpdateStr(edit_lx_we_chat));
			params.put(httpUtil.ENTER_UPDATE_LX_ZW,
					CommonUtil.UpdateStr(edit_lx_zw));
			params.put(httpUtil.ENTER_UPRATE_SIGN, txt_gx.getText().toString());
			params.put(httpUtil.ENTER_UPDATE_TEL, edit_lx_photo.getText()
					.toString());
			params.put(httpUtil.ENTER_UPDATE_ADDR, edit_lx_addr.getText()
					.toString());
			params.put(httpUtil.ENTER_UPDATE_EMAIL, edit_lx_email.getText()
					.toString());
			params.put(httpUtil.ENTER_UPDATE_CULTURE, " ");
		} else {
			if (!TextUtils.isEmpty(picpath)) {
				try {
					params.put("pic0", new File(picpath));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		httpUtil.post(httpUtil.ENTER_PRISE_UPDATE_URL, params,
				new AjaxCallBack<String>() {
					public void onLoading(long count, long current) {
					};

					public void onStart() {
					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String msg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								hideProgress();
								showRightDialog();
								if (!isUpImage)
									sharedPreferenceUtil
											.clearUPCompany(companyDetailActivity.this);
								if (!TextUtils.isEmpty(jsonResult
										.getString("icon"))) {
									// 上传成功了。就清除掉
									User.iconFile = jsonResult
											.getString("icon");
									utilBitmap.display(img_logo, User.iconFile,
											imgConfig, true);
									sharedPreferenceUtil
											.saveCompany(companyDetailActivity.this);
								}
								
							} else {
								showToast(msg);
								hideProgress();
								if (isUpImage)
									return;
								Company c = new Company();
								c.initData();
								sharedPreferenceUtil
										.saveUPCompany(companyDetailActivity.this);

							}
						} catch (JSONException e) {
							hideProgress();
							if (isUpImage)
								return;
							// TODO Auto-generated catch
							Company c = new Company();
							c.initData();
							sharedPreferenceUtil
									.saveUPCompany(companyDetailActivity.this);
							e.printStackTrace();
							showToast(R.string.string_http_err_data);
						} finally {

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						if (isUpImage)
							return;
						errCount++;
						// if (errCount < httpUtil.ERR_MAX) {
						// commmit(false, false);
						// }
						// if (errCount == 1)
							CommonUtil.showToastHttp(getApplicationContext(),
									errorNo);
						Company c = new Company();
						c.initData();
						sharedPreferenceUtil
								.saveUPCompany(companyDetailActivity.this);
					}

				});

	}

	// 判断某个textview是否被修改过
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			animOut();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onRestart() {
		super.onRestart();
		if (isUpdate) {
			edit_lx_com_name.setText(User.name);
			isUpdate = false;
		}
		LoggerUtil.i(editActivity.isConfirm + ",,,," + isEditFlag);
		// 显示edit编辑成功
		if (editActivity.isConfirm && isEditFlag) {
			// 弹出勾

			isEditFlag = false;
			editActivity.isConfirm = false;
			if (checkUpdate())
				commmit(false, true);
		}
	}

	// 检测是否已经修改

	public boolean checkUpdate() {
		// LoggerUtil.i("ssss"+);
		// 联系人
		if (edit_lx_name.getTag().toString().equals("1")) {
			edit_lx_name.setTag("0");
			if (!edit_lx_name.getText().toString().equals(User.contactName)) {

				return true;
			}

		}
		// 职务

		else if (edit_lx_zw.getTag().toString().equals("1")) {
			edit_lx_zw.setTag("0");
			if (!edit_lx_zw.getText().toString().equals(User.occupation)) {

				return true;
			}

		}
		// 手机
		else if (edit_lx_mobile.getTag().toString().equals("1")) {
			edit_lx_mobile.setTag("0");
			if (!edit_lx_mobile.getText().toString().equals(User.moblie)) {
				return true;
			}

		}
		// 电话
		else if (edit_lx_photo.getTag().toString().equals("1")) {
			edit_lx_photo.setTag("0");
			if (!edit_lx_photo.getText().toString().equals(User.telePhone)) {
				return true;
			}

		}
		// 邮箱
		else if (edit_lx_email.getTag().toString().equals("1")) {
			edit_lx_email.setTag("0");
			if (!edit_lx_email.getText().toString().equals(User.email)) {
				return true;
			}

		}
		// 地址
		else if (edit_lx_addr.getTag().toString().equals("1")) {
			edit_lx_addr.setTag("0");
			if (!edit_lx_addr.getText().toString().equals(User.address)) {
				return true;
			}

		}
		// 微信
		else if (edit_lx_we_chat.getTag().toString().equals("1")) {
			edit_lx_we_chat.setTag("0");
			if (!edit_lx_we_chat.getText().toString().equals(User.weChat)) {
				return true;
			}
		}
		// 个性签名
		else if (txt_gx.getTag().toString().equals("1")) {
			txt_gx.setTag("0");
			if (!txt_gx.getText().toString().equals(User.signature)) {
				return true;
			}

		}
		return false;

	}
}
