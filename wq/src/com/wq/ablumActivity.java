package com.wq;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.bitmap.display.mycallback;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.endure.wq.R;

import com.wq.Adapter.GalleryAdapter;
import com.wq.UI.MMAlert;
import com.wq.UI.ablumGallery;
import com.wq.model.User;
import com.wq.model.ablum;
import com.wq.model.ecCircleModel;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.model.product;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

/**
 * @author guofanghua
 * 
 */
public class ablumActivity extends BaseActivity implements OnTouchListener {

	// public static int screenWidth;

	// public static int screenHeight;

	private ablumGallery gallery;
	ArrayList<photoModel> listpic = new ArrayList<photoModel>();
	private int index = 0;

	@ViewInject(id = R.id.layout_content)
	LinearLayout layout_content;
	@ViewInject(id = R.id.txt_current_content)
	TextView txt_content;
	GalleryAdapter adapter;
	public static Handler delHandler = null;
	public boolean del_show = true;//
	// public boolean share_show = true;//
	public boolean save_flag = false;//
	public static boolean clickFinshFlag = false;
	public int shareFlag = 0;
	String id = "";
	String content = "";
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;
	FinalDb db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ablum_activity);
		finalBitmap = FinalBitmap.create(this);
		db = FinalDb.create(this);
		config = new BitmapDisplayConfig();
		BaseApplication.getInstance().addActivity(this);
		initData();
		initNavigation(1);
		initGallery();
	}

	private void initGallery() {
		adapter = new GalleryAdapter(this, listpic);
		gallery = (ablumGallery) findViewById(R.id.gallery);
		gallery.setVerticalFadingEdgeEnabled(false);
		gallery.setHorizontalFadingEdgeEnabled(false);
		gallery.setAdapter(adapter);
		gallery.setOnItemSelectedListener(new GalleryChangeListener());
		gallery.setSelection(index);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				finish();
				overridePendingTransition(0, R.anim.ablum_transition_out);
			}
		});

	}

	float beforeLenght = 0.0f;
	float afterLenght = 0.0f;
	boolean isScale = false;
	float currentScale = 2.0f;

	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = this.getIntent();
		listpic.clear();
		listpic.addAll((ArrayList<photoModel>) intent
				.getSerializableExtra("ablumArray"));
		index = intent.getIntExtra("index", 0);
		del_show = intent.getBooleanExtra("Del", false);
		// share_show = intent.getBooleanExtra("share", false);
		save_flag = intent.getBooleanExtra("saveFlag", false);
		layout_content.getBackground().setAlpha(50);
		txt_content.setTextColor(Color.argb(240, 255, 255, 255));
		// 企业相册
		if (listpic.get(0).getShareType() == photoModel.ABLUM_SHARE_FLAG) {
			ablum model = (ablum) listpic.get(0).getShareModel();
			content = model.getContent();
			id = model.getId();
		}
		// 动态
		else if (listpic.get(0).getShareType() == photoModel.NOTICE_SHARE_FLAG) {
			notice model = (notice) listpic.get(0).getShareModel();
			content = model.getContent();
			id = model.getId();

		}
		// 产品
		else if (listpic.get(0).getShareType() == photoModel.PRODUCT_SHARE_FLAG) {
			product product = (product) listpic.get(0).getShareModel();
			content = product.getIntro();
			id = product.getId();
		}
		// 分享产品
		else if (listpic.get(0).getShareType() == photoModel.NOMAL_SHARE_FLAG) {
			if (listpic.get(0).getShareModel() instanceof ecCircleModel) {
				ecCircleModel model = (ecCircleModel) listpic.get(0)
						.getShareModel();
				content = model.getCotent();
				id = model.getId();
			}
		}

	}

	private class GalleryChangeListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int position, long arg3) {

			index = position;
			initNavigation(index + 1);
			if (TextUtils.isEmpty(content)) {
				layout_content.setVisibility(View.GONE);
			} else {
				layout_content.setVisibility(View.VISIBLE);
				txt_content.setText(content);
			}
			currentScale = 2.0f;
			isScale = false;
			beforeLenght = 0.0f;
			afterLenght = 0.0f;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	private void initNavigation(int index) {
		int editId = -1;
		// if (!del_show && !share_show && !save_flag) {
		if (!del_show && !save_flag) {
			editId = -1;
		} else
			editId = R.drawable.share_bg_click;
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				ablumActivity.this, 60), DensityUtil.dip2px(ablumActivity.this,
				35));
		String title = "";
		if (listpic != null && listpic.size() > (index - 1)) {
			if (listpic.get(index - 1).getExtStr() != null) {
				title = listpic.get(index - 1).getExtStr();
			} else {
				title = (index + " / " + listpic.size());
			}
		} else {
			title = (index + " / " + listpic.size());
		}
		initNavigation(R.drawable.title_btn_back_click,
				getString(R.string.string_back), title, "", "", editId, params,
				new EditClickListener() {
					@Override
					public void editClick() {

						// MMAlert.shareSelectDialog(ablumActivity.this,
						// share_show, del_show, save_flag, myHandler);
						MMAlert.shareSelectDialog(ablumActivity.this, false,
								del_show, save_flag, myHandler);
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						overridePendingTransition(0,
								R.anim.ablum_transition_out);
					}
				});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(0, R.anim.ablum_transition_out);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void httpData() {
		AjaxParams params = new AjaxParams();

		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.CIRCLE_ADD_OTHER_ID, id);
		params.put(httpUtil.CIRCLE_ADD_CONTENT, content);
		params.put(httpUtil.CIRCLE_ADD_TYPE, listpic.get(0).getShareType() + "");
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.CIRCLE_PRO_ADD_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								ecCircleModel model = new ecCircleModel();
								// 缺少生意圈id和时间
								// model.setId(errcode);
								model.setType(listpic.get(0).getShareType()
										+ "");
								model.setCotent(content);
								model.setEnterpriseId(User.id);
								model.setLogoUrl(User.iconFile);
								model.setName(User.name);
								model.setVqh(User.wqh);
								model.setOtherId(id);
								model.setIsCertification(User.isCertification);
								db.save(model);
							}
							CommonUtil.showToast(ablumActivity.this, errMsg);
						} catch (JSONException e) {
							CommonUtil.showToast(ablumActivity.this,
									R.string.string_http_err_data);
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

						CommonUtil.showToastHttp(ablumActivity.this, errorNo);
					}
				});
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {

			case 1:
				showProgress(R.string.dialog_comitting);
				httpData();
				break;

			case 2:
				if (clickFinshFlag) {
					DialogUtils.showDialog(
							ablumActivity.this,
							getString(R.string.string_wxts),
							getString(R.string.string_del_pic_content),
							getString(R.string.string_dialog_yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (delHandler != null) {
										Message Delmsg = delHandler
												.obtainMessage();
										Delmsg.arg1 = index;
										Delmsg.sendToTarget();
									}
									listpic.clear();
									finish();

								}
							}, getString(R.string.string_dialog_no),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							}).show();
				} else {
					if (delHandler != null) {
						Message Delmsg = delHandler.obtainMessage();
						Delmsg.arg1 = index;
						Delmsg.sendToTarget();
					}
					listpic.remove(index);
					if (index == listpic.size()) {
						--index;
					}
					if (listpic.size() > 0)
						initGallery();
					else {
						finish();
						overridePendingTransition(0,
								R.anim.ablum_transition_out);
					}
				}
				break;

			case 3:

				if (TextUtils.isEmpty(listpic.get(index).getImageUrl()))
					return;
				ImageView img = new ImageView(ablumActivity.this);
				// config.setCompress(false);

				config.setCallback(new mycallback() {

					@Override
					public void success(Bitmap bitmap) {
						BitmapUtil.savePicCamer(ablumActivity.this, bitmap);

					}

					@Override
					public void failure() {

						// TODO Auto-generated method stub
					}
				});
				// 网络图片
				if (listpic.get(index).getId().length() > 0)
					finalBitmap.display(img, listpic.get(index).getImageUrl(),
							config);
				// 本地图片
				else {
					Bitmap bitmap = BitmapFactory.decodeFile(listpic.get(index)
							.getImageUrl());
					BitmapUtil.savePicCamer(ablumActivity.this, bitmap);
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();

					}

				}
				break;
			}
		}

	};
}
