package com.wq.find;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.mainActivity;
import com.wq.Adapter.addImagHorListviewAdapter;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.UI.horizontalListview.HorizontalVariableListView;
import com.wq.me.ablumMainActivity;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.ecCircleModel;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

/**
 * 发布企业圈信息
 * */
public class findAddCircleActivity extends BaseActivity {

	@ViewInject(id = R.id.img_add_pic)
	ImageView img_add;
	@ViewInject(id = R.id.me_grid_ablum)
	HorizontalVariableListView horListview;

	@ViewInject(id = R.id.find_add_circle_edit_detail)
	EditText edit_detail;

	addImagHorListviewAdapter adapter;
	String strTitle = "";// 顶部菜单
	private static int Max_SIZE = 5;
	private ecCircleModel circleModel = new ecCircleModel();
	FinalDb db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_add_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initData();
		initNavigation();
		initListener();
	}

	private void initData() {
		strTitle = getResources().getString(R.string.find_string_ec_add_titile);
		adapter = new addImagHorListviewAdapter(this, circleModel.getImgList(),
				new Handler() {
					@Override
					public void handleMessage(Message msg) {

						// TODO Auto-generated method stub
						super.handleMessage(msg);
						scanPhoto(circleModel.getImgList(), msg.what, true,
								false, new Handler() {
									public void handleMessage(Message msg) {

										circleModel.getImgList().remove(
												msg.arg1);
										adapter.notifyDataSetChanged();
									}
								});
					}
				});
		horListview.setAdapter(adapter);

	}

	private void initListener() {

		img_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (circleModel.getImgList().size() == Max_SIZE) {
					CommonUtil.showToast(
							getApplicationContext(),
							String.format(
									getString(R.string.me_string_img_max_format),
									Max_SIZE));

				} else
					MMAlert.showAlert(findAddCircleActivity.this, false,
							Max_SIZE - circleModel.getImgList().size());
			}
		});
		//

		edit_detail.setOnClickListener(new onclick());
		// rela_detail.setOnClickListener(new onclick());

	}

	private void initNavigation() {
		initNavitation("", getResources().getString(R.string.string_confirm),
				new EditClickListener() {

					@Override
					public void editClick() {
						if (circleModel.getImgList().size() == 0) {
							CommonUtil.showToast(getApplicationContext(),
									R.string.cir_add_warn_pic);
							return;
						}
						if (!checkFrom())
							return;
						initpro();
						// 添加
						try {
							db.save(circleModel);
							circleModel.setPid((int) db
									.findAllByWhere(ecCircleModel.class,
											" userid='" + User.id + "'",
											"  pid desc").get(0).getPid());

						} catch (Exception ex) {
							ex.printStackTrace();
						}
						saveImage();
						Intent intent = new Intent();
						intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
						intent.putExtra("circleModel", circleModel);
						intent.putExtra("sendFlag", true);
						intent.putExtra("flag", 0);
						sendBroadcast(intent);
						finish();
						animOut();
						// TODO Auto-generated method stub
						httpAddData();
					}

					@Override
					public void backClick() {
						finish();
						animOut();
						// TODO Auto-generated method stub
					}
				});
	}

	private class onclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();

			switch (v.getId()) {

			case R.id.find_add_circle_edit_detail:
				editActivity.edit_backcontent = edit_detail;
				bundle.putString(
						"title",
						getResources().getString(
								R.string.find_string_ec_add_content_tip));
				// bundle.putString("limitWord", "12");
				break;
			}
			changeView(editActivity.class, bundle, true);
			/*
			 * overridePendingTransition(R.anim.base_slide_up,
			 * R.anim.popup_anim_out1);
			 */
			// TODO Auto-generated method stub

		}

	}

	// 添加或者编辑
	private void httpAddData() {
		// 添加

		AjaxParams params = new AjaxParams();
		for (int i = 0; i < circleModel.getImgList().size(); i++) {
			try {
				params.put("pic" + i, new File(circleModel.getImgList().get(i)
						.getImageUrl()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.CIRCLE_ADD_CONTENT, edit_detail.getText()
				.toString());

		params.put(httpUtil.CIRCLE_ADD_IMG_NUM, circleModel.getImgList().size()
				+ "");
		params.put(httpUtil.CIRCLE_ADD_TYPE, "0");
		httpUtil.post(httpUtil.CIRCLE_PRO_ADD_URL, params,
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
								circleModel.setIsSend("0");
								circleModel.setId(jsonResult.getString("id"));

							} else {
								circleModel.setIsSend("1");
							}
						} catch (JSONException e) {
							circleModel.setIsSend("1");

							e.printStackTrace();
						} finally {
							if (circleModel.getIsSend().equals("1")) {
								try {
									db.update(circleModel);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								Intent intent = new Intent();
								intent.putExtra("circleModel", circleModel);
								intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
								sendOrderedBroadcast(intent, null);
							}

						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						circleModel.setIsSend("1");
						try {
							db.update(circleModel);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						Intent intent = new Intent();
						intent.putExtra("circleModel", circleModel);
						intent.setAction(findEpCircleListActivity.PART_BRO_ACTION_NAME);
						sendOrderedBroadcast(intent, null);
					}
				});
		Intent intent = new Intent();
		intent.putExtra("circleModel", circleModel);
		setResult(CommonUtil.CIRCLE_ADD_RESULT_FLAG, intent);
		finish();

	}

	private void saveImage() {
		// 保存图片
		for (photoModel p : circleModel.getImgList()) {
			try {
			//if(p.getImageUrl().equals(cir))
				p.setEcid("circleModel" + circleModel.getPid());
				db.save(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void initpro() {
		circleModel.setTime(dateUtil.formatDate(new Date()));
		circleModel.setId("");
		circleModel.setLogoUrl(User.iconFile);
		circleModel.setName(User.name);
		circleModel.setVqh(User.wqh);
		circleModel.setEnterpriseId(User.id);
		circleModel.setIsCertification(User.isCertification);
		circleModel.setCotent(edit_detail.getText().toString());
		// circleModel.getImgList().addAll(listpic);
		circleModel.setOtherId("");
		circleModel.setIsListFlag("0");
		circleModel.setType("0");
		circleModel.setTime(dateUtil.formatDate(new Date()));

	}

	private boolean checkFrom() {
		if (circleModel.getImgList().size() == 0) {
			showToast(R.string.me_string_ablum_pic_warn);
			return false;
		}
		if (TextUtils.isEmpty(edit_detail.getText())) {
			showToast(R.string.me_string_ablum_ms_warn);
			return false;
		}
		return true;
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
				if (!TextUtils.isEmpty(picpath)) {
					photoModel item = new photoModel();
					item.setFlag(1);
					item.setImageUrl(picpath);
					circleModel.getImgList().add(item);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
