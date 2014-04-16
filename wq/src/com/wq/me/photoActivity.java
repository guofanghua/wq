package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;

import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.ablumActivity;
import com.wq.Adapter.photoAdapter;
import com.wq.Adapter.waterPullGridAdapter;
import com.wq.Adapter.waterPullGridAdapter.callback;

import com.wq.UI.ClipActivity;
import com.wq.UI.CustomAlertDialog;
import com.wq.UI.CustomProgressDialog;
import com.wq.UI.MMAlert;
import com.wq.UI.StaggeredGridView;
import com.wq.UI.StaggeredGridView.OnItemClickListener;

import com.wq.model.User;

import com.wq.model.photoModel;

import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

/**
 * 公司相册
 * 
 * @author Administrator
 * 
 */
public class photoActivity extends BaseActivity {

	@ViewInject(id = R.id.photo_grid)
	StaggeredGridView photo_grid;
	// 没数据显示界面
	@ViewInject(id = R.id.no_data_layout)
	LinearLayout noData_layout;
	@ViewInject(id = R.id.no_data_txt_title)
	TextView txt_no_data;
	@ViewInject(id = R.id.no_data_btn)
	Button btn_no_data;
	waterPullGridAdapter adapter;

	/*
	 * @ViewInject(id = R.id.photo_scrollview) LinearLayout mianContainer;
	 */

	Map<String, Object> map;

	FinalBitmap bitmap = null;
	BitmapDisplayConfig bitmapConfig = null;
	ArrayList<photoModel> list = new ArrayList<photoModel>();

	private boolean delFlag = false;
	private String enterpriseId = "";
	private String enterpriseWqh = "";

	// 测试dialog

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_activity);
		BaseApplication.getInstance().addActivity(this);
		enterpriseId = this.getIntent().getStringExtra("enterpriseId");
		enterpriseWqh = this.getIntent().getStringExtra("enterpriseWqh");
		bitmap = FinalBitmap.create(this);
		bitmapConfig = new BitmapDisplayConfig();
		initNavigation();
		initUI();

	}

	private void initUI() {

		// for (int i = 0; i < 15; i++) {
		// photoModel item = new photoModel();
		// item.setId(i + "");
		// item.setImageUrl(ImageConst.urls[i]);
		// item.setFlag(0);
		// list.add(item);
		// }

		adapter = new waterPullGridAdapter(list, this, new callback() {

			@Override
			public void loadMore() {
			}

		});
		photo_grid.setAdapter(adapter);
		photo_grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(StaggeredGridView parent, View view,
					int position, long id) {
				// 自身
				if (TextUtils.isEmpty(enterpriseId)
						|| enterpriseId.equals(User.id)) {
					scanPhoto(list, position, true, false, new Handler() {
						public void handleMessage(Message msg) {
							// delPhoto(msg.obj.toString(), msg.arg1);
							Log.i("del222222", msg.arg1 + "");
							delPhoto(list.get(msg.arg1).getId(), msg.arg1);
							// list.remove(msg.arg1);
							// adapter.notifyDataSetChanged();
						}
					});
				}
				else
					scanPhoto(list, position, false, false, new Handler() {
						public void handleMessage(Message msg) {
							// delPhoto(msg.obj.toString(), msg.arg1);
							Log.i("del222222", msg.arg1 + "");
							delPhoto(list.get(msg.arg1).getId(), msg.arg1);

							// list.remove(msg.arg1);
							// adapter.notifyDataSetChanged();
						}
					});
				// startActivity(intent);
				// overridePendingTransition(R.anim.ablum_transition_in,
				// R.anim.transition_in);
				// TODO Auto-generated method stub
			}
		});
		setViewTouchListener(photo_grid);
		getPhoto();

	}

	// 没数据
	// flag=0 表示没有相关数据，需要添加，falg==1表示获取数据失败，点击重新加载
	private void noData(int flag) {
		if (list.size() <= 0) {
			noData_layout.setVisibility(View.VISIBLE);
			photo_grid.setVisibility(View.GONE);
		}
		if (flag == 0) {
			txt_no_data.setText(String.format(
					getString(R.string.string_no_data_txt_add),
					getString(R.string.me_string_wqxc)));
			if (TextUtils.isEmpty(enterpriseId)) {
				btn_no_data.setText(R.string.string_no_data_btn_add);
				btn_no_data.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						MMAlert.showAlert(photoActivity.this, true,5);
						// TODO Auto-generated method stub
					}
				});
			} else {
				btn_no_data.setVisibility(View.GONE);
			}
		} else {
			txt_no_data.setText(R.string.string_no_data_txt_rep);
			btn_no_data.setText(R.string.string_no_data_btn_rep);
			btn_no_data.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					getPhoto();
					// TODO Auto-generated method stub

				}
			});
		}

	}

	// 获取公司相册
	private void getPhoto() {
		Intent intent = this.getIntent();
		String enterpriseId = intent.getStringExtra("enterpriseId");
		AjaxParams params = new AjaxParams();

		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		if (TextUtils.isEmpty(enterpriseId)) {
			params.put(httpUtil.ENTER_PRISE_ID, User.id);
		} else {
			params.put(httpUtil.ENTER_PRISE_ID, enterpriseId);
		}
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.COMPANY_PHOTO_GET_URL, params,
				new AjaxCallBack<String>() {

					public void onStart() {

					}

					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String errmesg = jsonResult
									.getString(httpUtil.ERR_MGS);
							Log.i("msg", errmesg);
							if (errcode.equals(httpUtil.errCode_success)) {
								JSONArray album = jsonResult
										.getJSONArray("aulmuArray");
								if (album.length() > 0) {
									for (int i = 0; i < album.length(); i++) {
										JSONObject object = album
												.getJSONObject(i);
										photoModel item = new photoModel();
										item.setId(object.get("id").toString());
										item.setImageUrl(object.get("url")
												.toString());
										list.add(item);
									}
								}
								photo_grid.setVisibility(View.VISIBLE);
								noData_layout.setVisibility(View.GONE);
								// 获取公司信息
							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								noData(0);
							} else {
								noData(1);
								CommonUtil.showToast(getApplicationContext(),
										errmesg);
							}
							adapter.notifyDataSetChanged();

						} catch (JSONException e) {
							noData(1);
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							hideProgress();
						}
					}

					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						noData(1);
						CommonUtil.showToastHttp(photoActivity.this, errorNo);
					}
				});

	}

	// 增加企业图片

	private void addPhoto(final String ImagePath) {

		AjaxParams params = new AjaxParams();

		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		try {
			params.put("pic0", new File(ImagePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		httpUtil.post(httpUtil.COMPANY_PHOTO_ADD_URL, params,
				new AjaxCallBack<String>() {
					public void onLoading(long count, long current) {
					};

					public void onStart() {
						showProgress(R.string.dialog_comitting);
					}

					public void onSuccess(String result) {

						try {
							if (result != null && result.startsWith("\ufeff")) {
								result = result.substring(1);
							}
							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String errmesg = jsonResult
									.getString(httpUtil.ERR_MGS);
							Log.i("msg", errmesg);
							if (errcode.equals(httpUtil.errCode_success)) {
								photoModel item = new photoModel();
								item.setId(jsonResult.get("id").toString());
								item.setImageUrl(ImagePath);
								item.setFlag(1);
								list.add(0, item);
								Log.i("dddddd", "dddddddddd" + ImagePath);
								adapter.notifyDataSetChanged();

								// 获取公司信息
							}
							photo_grid.setVisibility(View.VISIBLE);
							noData_layout.setVisibility(View.GONE);
							CommonUtil.showToast(getApplicationContext(),
									errmesg);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							hideProgress();
						}
					}

					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToast(photoActivity.this, "访问失败"
								+ strMsg);
					}
				});

	}

	// 删除图片
	private void delPhoto(String aulmuId, final int position) {

		AjaxParams params = new AjaxParams();

		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.AULMU_ID, aulmuId);
		Log.i("userid", User.id + ",,," + aulmuId);
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.COMPANY_PHOTO_DEL_URL, params,
				new AjaxCallBack<String>() {

					public void onStart() {

					}

					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							String errmesg = jsonResult
									.getString(httpUtil.ERR_MGS);
							Log.i("msg", errmesg);
							if (errcode.equals(httpUtil.errCode_success)) {
								list.remove(position);
								delFlag = true;
							}
							CommonUtil.showToast(getApplicationContext(),
									errmesg);
							if (list.size() == 0) {
								noData(0);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							CommonUtil.showToast(photoActivity.this,
									R.string.string_http_err_data);
						} finally {
							hideProgress();
						}
					}

					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						hideProgress();
						CommonUtil.showToast(photoActivity.this,
								R.string.string_http_err_failure);
					}
				});
	}

	private void initNavigation() {
		if (TextUtils.isEmpty(enterpriseId) || enterpriseId.equals(User.id)) {
			initNavitation(
					getResources().getString(R.string.me_string_wqxc),
					getResources().getString(R.string.string_new_add),
					new EditClickListener() {

						@Override
						public void editClick() {
							MMAlert.showAlert(photoActivity.this, false,-1);

						}

						@Override
						public void backClick() {
							finish();
							animOut();
							// TODO Auto-generated method stub

						}
					});
		} else {
			initNavitation(
					getResources().getString(R.string.me_string_wqxc),
					enterpriseWqh, "", -1, new EditClickListener() {

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
						Toast.makeText(photoActivity.this, "图片没找到", 0).show();
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
					// 上传网络
					addPhoto(picpath);
				}

			}
		}
		// 删除图片时
		else if (resultCode == CommonUtil.PHOTO_RESULT_FLAG) {

		}
	}

	public void onResume() {
		super.onResume();
		if (delFlag) {
			adapter.notifyDataSetChanged();
			delFlag = false;
			if (list.size() == 0) {
				noData(0);
			}
		}
	}
}
