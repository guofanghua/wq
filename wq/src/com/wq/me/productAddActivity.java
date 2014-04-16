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
import android.R.integer;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.ablumActivity;
import com.wq.editActivity;
import com.wq.mainActivity;
import com.wq.Adapter.addImagHorListviewAdapter;
import com.wq.Adapter.addImageGridAdapter;
import com.wq.Adapter.proAttrAdapter;
import com.wq.PicCheck.Bimp;
import com.wq.PicCheck.FileUtils;
import com.wq.UI.ClipActivity;
import com.wq.UI.InScrolllistView;
import com.wq.UI.MMAlert;
import com.wq.UI.XgridView;
import com.wq.model.User;
import com.wq.model.myObject;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

/** 添加，编辑产品 */
public class productAddActivity extends BaseActivity {
	@ViewInject(id = R.id.add_scroll)
	ScrollView scroll;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;

	@ViewInject(id = R.id.me_grid_product_ablum)
	XgridView list_ablum;// 显示相册
	@ViewInject(id = R.id.edit_pro_name)
	TextView edit_name;// 产品名称

	@ViewInject(id = R.id.edit_pro_intro)
	TextView edit_intro;// 产品简介
	@ViewInject(id = R.id.edit_pro_cate)
	TextView edit_cate;// 分类
	@ViewInject(id = R.id.pro_attr_list)
	InScrolllistView list_attr;// 显示属性
	@ViewInject(id = R.id.me_rela_add_attr)
	RelativeLayout add_attr;
	@ViewInject(id = R.id.pro_btn_del)
	Button btn_del;
	proAttrAdapter attrAdapter;
	@ViewInject(id = R.id.layout_attr)
	LinearLayout layout_attr;
	// 点击事件
	@ViewInject(id = R.id.me_rela_pro_name)
	RelativeLayout rela_pro_name;

	@ViewInject(id = R.id.me_rela_intro)
	RelativeLayout rela_pro_intro;
	@ViewInject(id = R.id.me_rela_cate)
	RelativeLayout rela_pro_cate;
	int errCount = 0;
	private StringBuilder delSb = new StringBuilder();
	private static int Max_SIZE = 6;
	/** 产品 */
	product myproduct = new product();
	addImageGridAdapter ablumAdapter;
	int groupPosition = -1;
	int childPosition = -1;
	FinalDb db;
	private String id = "";
	ArrayList<Map<String, WeakReference<Bitmap>>> bitmpalist = new ArrayList<Map<String, WeakReference<Bitmap>>>();
	ArrayList<photoModel> ablumList = new ArrayList<photoModel>();
	ArrayList<photoModel> delAblumList = new ArrayList<photoModel>();
	public static final String FILE_SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/.wqCrop";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_add_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initData();
		initNavigation();
		initListener();
	}

	private void initData() {
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			myproduct = (product) b.getSerializable("product");

			// checklocalData();
			groupPosition = b.getInt("group");
			childPosition = b.getInt("child");
			id = myproduct.getId();
			ablumList.addAll(myproduct.getPicList());
		}
		attrAdapter = new proAttrAdapter(this, myproduct.getAttrList(), true,
				new com.wq.Adapter.proAttrAdapter.itemclick() {

					@Override
					public void myclick(int position) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(productAddActivity.this,
								proAttrEditActivity.class);
						proAttr p = myproduct.getAttrList().get(position);
						Bundle b = new Bundle();
						b.putSerializable("attr", p);
						b.putInt("position", position);
						intent.putExtras(b);
						startActivityForResult(intent,
								CommonUtil.PRO_ATTR_EDIT_RESULT_FLAG);
					}
				});
		if (myproduct.getAttrList().size() > 0) {
			layout_attr.setVisibility(View.VISIBLE);
		} else {
			layout_attr.setVisibility(View.GONE);
		}
		list_attr.setAdapter(attrAdapter);
		ablumAdapter = new addImageGridAdapter(this, ablumList, bitmpalist,
				myhandler);
		list_ablum.setAdapter(ablumAdapter);
		edit_name.setText(myproduct.getTitle());
		edit_intro.setText(myproduct.getIntro());

		if (!TextUtils.isEmpty(myproduct.getCateId())) {
			edit_cate.setTag(myproduct.getCateId());
		}
		if (!TextUtils.isEmpty(myproduct.getCateName())) {
			edit_cate.setText(myproduct.getCateName());
		}
		list_attr.setFocusable(false);
		if (!TextUtils.isEmpty(myproduct.getId())) {
			btn_del.setVisibility(View.VISIBLE);
		} else
			btn_del.setVisibility(View.GONE);

	}

	private void initNavigation() {
		initNavitation(
				!TextUtils.isEmpty(id) ? getString(R.string.me_string_edit_product_title)
						: getString(R.string.me_string_add_product_title),
				getString(R.string.string_save),
				R.drawable.title_btn_right_click, new EditClickListener() {
					@Override
					public void editClick() {
						if (checkForm()) {
							addAndEditCommit();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						if (TextUtils.isEmpty(myproduct.getId())) {
							finish();
							animDown();
						} else {
							finish();
							animOut();
						}
						return;
						// TODO Auto-generated method stub
					}
				});
	}

	private void initListener() {
		rela_pro_cate.setOnClickListener(new onclick());
		rela_pro_intro.setOnClickListener(new onclick());

		rela_pro_name.setOnClickListener(new onclick());

		add_attr.setOnClickListener(new onclick());

		btn_del.setOnClickListener(new onclick());
		// setViewTouchListener(scroll);

		// 点击属性item
		list_attr.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(productAddActivity.this,
						proAttrEditActivity.class);
				Bundle b = new Bundle();
				b.putSerializable("list", myproduct.getAttrList());
				b.putInt("position", position);
				intent.putExtras(b);
				startActivityForResult(intent, 101);
				animUp();
				// TODO Auto-generated method stub

			}
		});

	}

	private class onclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			// TODO Auto-generated method stub
			switch (v.getId()) {
			// 添加图片

			case R.id.me_rela_pro_name:
				editActivity.edit_backcontent = edit_name;
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_product_name));
				// bundle.putString("limitWord", "12");
				changeView(editActivity.class, bundle, true);
				break;

			case R.id.me_rela_intro:
				editActivity.edit_backcontent = edit_intro;
				bundle.putString(
						"title",
						getResources().getString(
								R.string.me_string_product_intro));
				changeView(editActivity.class, bundle, true);
				break;
			case R.id.me_rela_cate:
				if (!TextUtils.isEmpty(edit_cate.getText())) {
					bundle.putString("id", edit_cate.getTag().toString());
					bundle.putString("name", edit_cate.getText().toString());
				}
				changeViewForResult(proCateListActivity.class, bundle, 102);
				break;
			case R.id.me_rela_add_attr:
				Bundle bundle2 = new Bundle();
				bundle2.putSerializable("proArrtList", myproduct.getAttrList());
				changeViewForResult(proArrListAcvitiy.class, bundle2, 100);
				break;
			case R.id.pro_btn_del:
				DialogUtils.showDialog(
						productAddActivity.this,
						getString(R.string.string_wxts),
						getString(R.string.string_del_pro_content),
						getString(R.string.string_dialog_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								delCommit();

							}
						}, getString(R.string.string_dialog_no),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						}).show();

				break;
			}
		}
	}

	/**
	 * 添加和编辑商品
	 * */
	private void addAndEditCommit() {
		initpro();
		showProgress(R.string.dialog_comitting);
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.PRODUCT_ADD_NAME, edit_name.getText().toString());
		params.put(httpUtil.PRODUCT_ADD_CONTENT, edit_intro.getText()
				.toString());
		// if (TextUtils.isEmpty(myproduct.getCateId()))
		// params.put(httpUtil.PRODUCT_ADD_TYPE_NAME_ID, "");
		// else
		params.put(httpUtil.PRODUCT_ADD_TYPE_NAME, myproduct.getCateName());
		params.put(httpUtil.PRODUCT_ADD_ATTR, myproduct.getProAttrStr());

		// 需要删除的图片
		if (delSb.toString().length() > 0) {
			params.put(httpUtil.Product_ADD_DEL_IMG_ID, delSb.toString()
					.substring(1));
		}
		// 图片
		int count1 = ablumList.size();

		int j = 0;
		// 原来从服务器来的图片不需要上传
		for (int i = 0; i < count1; i++) {
			if (ablumList.get(i).getId().length() <= 0) {
				Boolean tempfalg = false;
				for (int k = 0; k < myproduct.getPicList().size(); k++) {
					if (ablumList.get(i).getId()
							.equals(myproduct.getPicList().get(k).getId())) {
						tempfalg = true;
						break;
					}
				}
				if (tempfalg)
					continue;

			}
			try {
				params.put("pic" + (j++), new File(ablumList.get(i)
						.getImageUrl()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		params.put(httpUtil.PRODUCT_ADD_IMG_NUM, j + "");

		// 需求改变前的字段
		params.put(httpUtil.PRODUCT_ADD_TYPE, "0");
		String url = "";
		if (TextUtils.isEmpty(myproduct.getId())) {
			url = httpUtil.PRODUCT_ADD_URL;
		} else {
			params.put(httpUtil.PRODUCT_UPDATE_COMMODITY_ID, myproduct.getId());
			url = httpUtil.PRODUCT_UPDATE_URL;
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
							myproduct.setId(jsonResult.get("id").toString());
						}
						// 添加图片
						JSONArray pic = jsonResult.getJSONArray("imgArray");
						for (int i = 0; i < pic.length(); i++) {
							JSONObject o = pic.getJSONObject(i);
							photoModel m = new photoModel();
							m.setEcid(myproduct.getId());
							m.setFlag(0);
							m.setShareType(photoModel.PRODUCT_SHARE_FLAG);
							m.setImageUrl(o.getString("imgUrl"));
							m.setId(o.getString("imgId"));
							m.setShareModel(myproduct);
							myproduct.getPicList().add(m);
							try {
								db.save(m);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						deleLocalImage(delAblumList);

						// 添加
						if (TextUtils.isEmpty(id)) {
							try {
								db.save(myproduct);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						} else {
							// 更新
							try {
								db.update(myproduct, "id='" + myproduct.getId()
										+ "'");
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}

						Intent intent = new Intent();
						intent.setAction(productMainListActivity.PART_BRO_ACTION_NAME);
						intent.putExtra("product", myproduct);
						intent.putExtra("sendFlag", true);
						if (!TextUtils.isEmpty(id)) {
							intent.putExtra("flag", 1);
						}
						// 添加产品
						else
							intent.putExtra("flag", 0);
						sendBroadcast(intent);
						hideProgress();
						finish();
						if (TextUtils.isEmpty(id))
							animDown();
						else
							animOut();
					}

				} catch (JSONException e) {
					hideProgress();
					e.printStackTrace();
				} finally {

					// Intent intent = new Intent();
					// intent.setAction(productMainListActivity.PART_BRO_ACTION_NAME);
					// intent.putExtra("product", myproduct);
					// sendBroadcast(intent);
				}
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {

				// TODO Auto-generated method stub
				hideProgress();
				//
				// Intent intent = new Intent();
				// intent.setAction(productMainListActivity.PART_BRO_ACTION_NAME);
				// intent.putExtra("product", myproduct);
				// sendBroadcast(intent);
				CommonUtil.showToastHttp(productAddActivity.this, errorNo);
			}
		});
	}

	/**
	 * 删除商品
	 * */
	private void delCommit() {

		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.PRODUCT_DEL_COMMODITY_ID, myproduct.getId());
		showProgress(R.string.dialog_comitting);
		httpUtil.post(httpUtil.PRODUCT_DEL_URL, params,
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
									// 删除产品
									delAblumList.addAll(myproduct.getPicList());
									db.delete(myproduct);
									// 删除图片
									deleLocalImage(delAblumList);
								} catch (Exception ex) {
									ex.printStackTrace();
								}

								Intent intent = new Intent();
								intent.putExtra("group", groupPosition);
								intent.putExtra("child", childPosition);
								intent.putExtra("flag", 2);
								setResult(CommonUtil.PRODUCT_RESULT_FLAG,
										intent);
								finish();
							} else {
								hideProgress();
								showToast(errMsg);
							}

						} catch (JSONException e) {
							hideProgress();
							CommonUtil.showToast(productAddActivity.this,
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

	/**
	 * 表单检测
	 * */
	private boolean checkForm() {
		// 产品图片
		if (ablumList.size() == 0) {
			CommonUtil.showToast(this, R.string.me_string_pro_pic_form);
			return false;
		}
		// 产品分类
		if (TextUtils.isEmpty(edit_cate.getText())) {
			edit_cate.setHintTextColor(Color.RED);
			return false;
		}
		// 产品名称
		if (TextUtils.isEmpty(edit_name.getText())) {
			edit_name.setHintTextColor(Color.RED);
			return false;
		}
		// 产品简介
		if (TextUtils.isEmpty(edit_intro.getText())) {
			edit_intro.setHintTextColor(Color.RED);
			return false;
		}

		return true;
	}

	/**
	 * 选择图片后返回的结果
	 * */
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
						Toast.makeText(productAddActivity.this, "图片没找到", 0)
								.show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Bitmap bitmap = null;

					String picpath = FILE_SDCARD_PATH + "/"
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
						item.setShareModel(myproduct);
						item.setEcid(myproduct.getId());
						item.setShareType(photoModel.PRODUCT_SHARE_FLAG);
						ablumList.add(item);
						ablumAdapter = new addImageGridAdapter(this, ablumList,
								bitmpalist, myhandler);
						list_ablum.setAdapter(ablumAdapter);
					}
				}
				// } else {
				// Intent intent = new Intent(this, ClipActivity.class);
				// intent.putExtra("path", uri.getPath());
				// startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
				// }
			}
		} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
				&& resultCode == RESULT_OK) {
			File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
					MMAlert.localTempImageFileName);
		
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
				item.setShareModel(myproduct);
				item.setEcid(myproduct.getId());
				item.setShareType(photoModel.PRODUCT_SHARE_FLAG);
				ablumList.add(item);
				ablumAdapter = new addImageGridAdapter(this, ablumList,
						bitmpalist, myhandler);
				list_ablum.setAdapter(ablumAdapter);
			}
			// intent.putExtra("delFlag", true);
			// startActivityForResult(intent, MMAlert.FLAG_MODIFY_FINISH);
		} else if (requestCode == MMAlert.FLAG_MODIFY_FINISH
				&& resultCode == RESULT_OK) {
			if (data != null) {
				String picpath = data.getStringExtra("path");
				if (!TextUtils.isEmpty(picpath)) {
					photoModel item = new photoModel();
					item.setFlag(1);
					item.setImageUrl(picpath);
					item.setShareModel(myproduct);
					item.setEcid(myproduct.getId());
					item.setShareType(photoModel.PRODUCT_SHARE_FLAG);
					ablumList.add(item);
					ablumAdapter = new addImageGridAdapter(this, ablumList,
							bitmpalist, myhandler);
					list_ablum.setAdapter(ablumAdapter);
				}

			}
		}
		// 添加属性
		else if (resultCode == 100) {
			if (data == null)
				return;
			ArrayList<proAttr> k = (ArrayList<proAttr>) data
					.getSerializableExtra("list");
			if (k != null && k.size() > 0) {
				myproduct.setAttrList(k);
				layout_attr.setVisibility(View.VISIBLE);
			} else {
				myproduct.getAttrList().clear();
				layout_attr.setVisibility(View.GONE);
			}
			attrAdapter = new proAttrAdapter(this, myproduct.getAttrList(),
					true, new com.wq.Adapter.proAttrAdapter.itemclick() {

						@Override
						public void myclick(int position) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(productAddActivity.this,
									proAttrEditActivity.class);
							proAttr p = myproduct.getAttrList().get(position);
							Bundle b = new Bundle();
							b.putSerializable("attr", p);
							b.putInt("position", position);
							intent.putExtras(b);
							startActivityForResult(intent,
									CommonUtil.PRO_ATTR_EDIT_RESULT_FLAG);
						}
					});
			list_attr.setAdapter(attrAdapter);
		}
		// 编辑属性
		else if (resultCode == 101) {
			if (data == null)
				return;
			int position = data.getIntExtra("position", -1);
			proAttr item = (proAttr) data.getSerializableExtra("attrModel");
			// 标明此属性将被删除
			if (position >= 0)
				myproduct.getAttrList().set(position, item);
			attrAdapter = new proAttrAdapter(this, myproduct.getAttrList(),
					true, new com.wq.Adapter.proAttrAdapter.itemclick() {

						@Override
						public void myclick(int position) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(productAddActivity.this,
									proAttrEditActivity.class);
							proAttr p = myproduct.getAttrList().get(position);
							Bundle b = new Bundle();
							b.putSerializable("attr", p);
							b.putInt("position", position);
							intent.putExtras(b);
							startActivityForResult(intent,
									CommonUtil.PRO_ATTR_EDIT_RESULT_FLAG);
						}
					});
			list_attr.setAdapter(attrAdapter);
		}
		// 选择分类
		else if (resultCode == 102) {
			if (data == null)
				return;
			Bundle bundle = data.getExtras();

			if (bundle == null)
				return;

			myproduct.setCateId(bundle.getString("id"));
			myproduct.setCateName(bundle.getString("name"));
			edit_cate.setTag(myproduct.getCateId());
			edit_cate.setText(myproduct.getCateName());
		}
	}

	// 判断某个textview是否被修改过
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (TextUtils.isEmpty(myproduct.getId())) {
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

	// 查询数据库图片表 并将此表的所有数据图片数据删掉，再重新插入数据
	public void localImagData() {

		// 将最新照片添加到本地数据库
		for (photoModel item : myproduct.getPicList()) {
			item.setEcid(myproduct.getId());
			try {
				db.save(item);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	private void initpro() {

		// 属性
		ArrayList<proAttr> tmpList = new ArrayList<proAttr>();
		tmpList.addAll(myproduct.getAttrList());
		int count = tmpList.size();
		StringBuilder sb = new StringBuilder();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				if (!TextUtils.isEmpty(tmpList.get(i).getAttrValue())) {
					sb.append(tmpList.get(i).getAttrKey());
					sb.append("&:");
					sb.append(tmpList.get(i).getAttrValue());
					sb.append("&|");
				} else
					myproduct.getAttrList().remove(tmpList.get(i));
			}
		}
		if (sb.toString().length() > 2)
			myproduct.setProAttrStr(sb.toString().substring(0,
					sb.toString().length() - 2));
		else
			myproduct.setProAttrStr("");
		myproduct.setIntro(edit_intro.getText().toString());
		myproduct.setTitle(edit_name.getText().toString());
		myproduct.setTime(dateUtil.formatDate(new Date()));
	}

	private Handler myhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				scanPhoto(ablumList, msg.arg1, true, false, new Handler() {
					public void handleMessage(Message msg) {
						// 服务器图片
						if (ablumList.get(msg.arg1).getId().length() > 0) {
							for (int i = 0; i < myproduct.getPicList().size(); i++) {
								if (myproduct
										.getPicList()
										.get(i)
										.getId()
										.equals(ablumList.get(msg.arg1).getId())) {
									delAblumList.add(ablumList.get(msg.arg1));
									myproduct.getPicList().remove(i);
									break;
								}
							}
							delSb.append("|");
							delSb.append(ablumList.get(msg.arg1).getId());
						}
						ablumList.remove(msg.arg1);
						list_ablum.setAdapter(ablumAdapter);
					}
				});
			} else {
				if (myproduct.getPicList().size() == Max_SIZE) {
					CommonUtil
							.showToast(
									getApplicationContext(),
									String.format(
											getString(R.string.me_string_img_max_format),
											Max_SIZE));

				} else
					MMAlert.showAlert(productAddActivity.this, false, Max_SIZE
							- myproduct.getPicList().size());
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

}
