package com.wq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalActivity;
import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;

import com.endure.wq.R;
import com.wq.UI.CustomProgressDialog;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XListView;
import com.wq.failueque.base.IMediator;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.model.VersionItem;
import com.wq.model.listItemModelBase;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.setting;
import com.wq.utils.sharedPreferenceUtil;

import android.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author Administrator
 * 
 */
/**
 * @author Administrator
 * 
 */
public class BaseActivity extends IMediator implements OnGestureListener {

	private static final int FLING_MIN_DISTANCE = 160;
	private static final int FLING_MIN_VELOCITY = 0;
	GestureDetector mGestureDetector;
	Button btn_back;// 返回
	protected FinalBitmap utilBitmap;
	TextView txt_title;// 标题
	TextView txt_title1;// 标题1
	Class<? extends BaseActivity> cls = null;// 手势向右滑动时，需要跳转的activity
	Bundle JumpBundle = null;
	Button btn_edit;// 编辑
	ProgressBar progress;
	private boolean isUpAnim = false;// activity是否从下面进入
	CustomProgressDialog progressDialog = null;
	// 保存用户的公告属性
	public static ArrayList<proAttr> publicAttr = new ArrayList<proAttr>();
	private boolean isFling = true;
	FinalDb db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFling = true;
		utilBitmap = FinalBitmap.create(this);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		// 去除标题
		if (!hasTitle()) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		// 是否全屏
		if (isFullScreen()) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		// 将所有的activity都放入activitylist中。做完全退出

		// 将当前implements OnGestureListener传递过去，
		// this->OnGestureListener
		mGestureDetector = new GestureDetector(this);

	}

	// 设置返回按钮

	/**
	 * 是否保留系统标题，如果不需要标题，可在子类中重写此方法
	 * 
	 * 
	 * @return true 带系统标题，反之不显示系统标题
	 */
	public boolean hasTitle() {
		return false;
	}

	public void setIsFling(boolean isFling) {
		this.isFling = isFling;
	}

	/**
	 * 
	 * 是否全屏
	 * 
	 * @return true 全屏，反之不全屏
	 */
	public boolean isFullScreen() {
		return false;
	}

	/**
	 * 界面间跳转
	 * 
	 * @param cls
	 */
	public void changeView(Class<?> cls) {
		changeView(cls, null);
	}

	/**
	 * 界面间跳转
	 * 
	 * @param cls
	 */
	public void changeView(Class<?> cls, boolean isup) {
		changeView(cls, null, isup);
	}

	public void changeViewByResult(Class<?> cls, int requestCode) {
		changeViewByResult(cls, null, requestCode);
	}

	public void changeViewByResult(Class<?> cls, Bundle bd, int requestCode) {
		if (cls == null)
			return;
		Intent intent = new Intent(this, cls);
		if (bd != null) {
			intent.putExtras(bd);
		}
		this.startActivityForResult(intent, requestCode);
		animIn();
	}

	/**
	 * 附帶bundle的介面间跳转
	 * 
	 * @param cls
	 * @param bd
	 */
	public void changeView(Class<?> cls, Bundle bd) {
		if (cls == null)
			return;
		Intent intent = new Intent(this, cls);
		if (bd != null) {
			intent.putExtras(bd);
		}
		this.startActivity(intent);
		animIn();
		// overridePendingTransition(enterAnim, exitAnim)

	}

	public void changeViewForResult(Class<?> cls, int requesCode) {
		changeViewForResult(cls, null, requesCode);
	}

	/**
	 * 期待有返回结果的界面跳转
	 * */
	public void changeViewForResult(Class<?> cls, Bundle bd, int requesCode) {
		if (cls == null)
			return;
		Intent intent = new Intent(this, cls);
		if (bd != null) {
			intent.putExtras(bd);
		}
		startActivityForResult(intent, requesCode);
		animIn();
	}

	public void changeViewForResult(Class<?> cls, Bundle bd, int requesCode,
			int animFlag) {
		if (cls == null)
			return;
		Intent intent = new Intent(this, cls);
		if (bd != null) {
			intent.putExtras(bd);
		}
		startActivityForResult(intent, requesCode);
		if (animFlag == 1) {
			animUp();
		} else
			animIn();
	}

	public void changeViewForResult(Class<?> cls, int requesCode, int animFlag) {
		changeViewForResult(cls, null, requesCode, animFlag);
	}

	/*
	 * * 附帶bundle的介面间跳转
	 * 
	 * @param cls
	 * 
	 * @param bd
	 */
	public void changeView(Class<?> cls, Bundle bd, boolean isUp) {
		isUpAnim = isUp;
		if (cls == null)
			return;
		Intent intent = new Intent(this, cls);
		if (bd != null) {
			intent.putExtras(bd);
		}
		startActivity(intent);

		if (isUp)
			animUp();
		else
			animIn();
		// overridePendingTransition(enterAnim, exitAnim)

	}

	// 捕捉回退键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBack();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	/**
	 * Activity返回事件，需要自定义返回可重写该方法
	 * 
	 * @param v
	 */
	public void onBack() {
		finish();
		animOut();
	}

	/**
	 * 从右侧进入activity动画
	 */
	public void animIn() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.transition_in);
		}
	}

	/**
	 * 从右侧退出activity动画
	 */
	public void animOut() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			overridePendingTransition(0, R.anim.base_slide_right_out);
		}
	}

	/**
	 * 从下面进入activity动画
	 * */
	public void animUp() {

		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.show_transition_in);
		}
	}

	/**
	 * 从下面进入activity动画
	 * */
	public void animDown() {

		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			overridePendingTransition(0, R.anim.slide_down_out);
		}
	}

	/**
	 * 使用了XListView控件更新页面方法
	 * 
	 * @param xListView
	 */
	public void onLoad(XListView xListView) {
		if (xListView == null)
			return;

		xListView.stopRefresh();
		xListView.stopLoadMore();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String result = sdf.format(new Date());
		xListView.setRefreshTime(result);
	}

	public void onLoad(XExpandableListView xListView) {
		if (xListView == null)
			return;

		xListView.stopRefresh();
		xListView.stopLoadMore();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String result = sdf.format(new Date());
		xListView.setRefreshTime(result);
	}

	protected void checkUpdate(final Handler handler) {

		httpUtil.post(httpUtil.APP_UPDATE_URL, new AjaxCallBack<String>() {
			public void onStart() {

			}

			public void onSuccess(String result) {

				try {
					if (result != null && result.startsWith("\ufeff")) {
						result = result.substring(1);
					}
					LoggerUtil.i(result);
					JSONObject jsonResult = new JSONObject(result);
					String errcode = jsonResult.getString(httpUtil.ERR_CODE);
					if (errcode.equals(httpUtil.errCode_success)) {
						int ver = CommonUtil.getAppVersion(BaseActivity.this);// 获取当前版本

						VersionItem version = new VersionItem();

						version.verName = jsonResult.getString("ver_name");// 更新版本
						version.versionCode = jsonResult.optInt("ver", 0);
						version.mandupVer = jsonResult.optString("mandupVer");// 禁止版本
						JSONObject object = jsonResult
								.getJSONObject("updataDic");
						version.updateUrl = object.getString("downURL");// 更新地址
						version.updateContent = object.getString("updataMgs");// 更新内容
						// version.isClearLocalData=object.getString("")

						boolean b = false;
						// -1: 当前版本已被禁止，0:当前版本最新 ，1需要更新
						if (version.mandupVer != null
								&& version.mandupVer.trim().length() > 0) {
							String[] forbidden = version.mandupVer.split("\\|");
							if (forbidden != null && forbidden.length > 0) {
								for (int i = 0; i < forbidden.length; i++) {
									int f = -1;
									try {
										f = Integer.parseInt(forbidden[i]);
									} catch (Exception e) {
										f = -1;
										e.printStackTrace();
									}
									if (ver == f) {
										b = true;
										break;
									}
								}
							}
						}

						int code = getVersionCode();
						if (b) {
							version.UpdateFlag = -1;

						} else if (version.versionCode > code) {
							version.UpdateFlag = 1;
						} else {
							version.UpdateFlag = 0;
						}

						Message msg = new Message();
						msg.what = 1;
						msg.obj = version;
						handler.sendMessage(msg);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable t, int errorNo, String strMsg) {

			}

		});

	}

	public int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			int version = packInfo.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 显示版本更新弹出框
	public void ShowUpdate(final VersionItem version) {

		// 强制更新
		if (version.UpdateFlag == -1) {
			try {
				CommonUtil.clearDbData(this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			DialogUtils.showDialog(this, version.updateContent,
					getString(R.string.string_dialog_update),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// dialog.dismiss();
							// 更新
							update(version.updateUrl);
						}
					}, getString(R.string.string_dialog_quit),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							BaseApplication.getInstance().exit();
						}
					}).show();

		}
		// 版本升级
		else if (version.UpdateFlag == 1) {
			DialogUtils.showDialog(this, version.updateContent,
					getString(R.string.string_dialog_update),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 更新
							update(version.updateUrl);
						}
					}, getString(R.string.string_cacel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
		}

	}

	public void update(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	/**
	 * 获取应用版本信息
	 */

	public interface EditClickListener {
		void editClick();

		void backClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 将touch时间交给mgestureDetector来处理
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (!isFling)
			return false;

		/* 在用了ScrollView等上下滑动时会抛出java.lang.NullPointerException */
		if (e1 != null && e2 != null) {
			if (Math.abs(e1.getY() - e2.getY()) < FLING_MIN_DISTANCE
					&& e1.getX() - e2.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				LoggerUtil.d("向左手势");
				onNext();
			} else if (Math.abs(e1.getY() - e2.getY()) < FLING_MIN_DISTANCE
					&& e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				LoggerUtil.d("向右手势");
				onBack(null);
			}
		}

		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 设置控件的滑动事件
	 * 
	 * @param v
	 */
	public void setViewTouchListener(View v) {
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return BaseActivity.this.onTouchEvent(event);
			}
		});
	}

	public void setJumpViewTouchListener(View v,
			Class<? extends BaseActivity> cls, Bundle bd) {
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return BaseActivity.this.onTouchEvent(event);
			}
		});
	}

	/**
	 * 进度条等待框
	 * */
	// public void showProgress(String msg) {
	// showProgress(msg, false);
	// }

	public void showProgress(int msgId) {
		showProgress(msgId, true);
	}

	public void showProgress(String msg) {
		showProgress(msg, true);
	}

	public void showProgress(int msg, boolean isCancel) {
		try {

			if (progressDialog == null) {
				progressDialog = new CustomProgressDialog(this);

			}
			progressDialog.create(msg);
			progressDialog.setCancelable(isCancel);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void showProgress(String msg, boolean isCancel) {
		try {

			if (progressDialog == null) {
				progressDialog = new CustomProgressDialog(this);

			}
			progressDialog.create(msg);
			progressDialog.setCancelable(isCancel);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void hideProgress() {
		if (progressDialog != null)
			progressDialog.cancel();
	}

	/*
	 * Activity前进事件，某些页面支持左滑手势
	 */
	public void onNext() {
		if (cls != null)
			changeView(cls, JumpBundle);
	}

	/*
	 * Activity返回事件，需要自定义返回可重写该方法
	 * 
	 * @param v
	 */
	public void onBack(View v) {
		finish();
		if (isUpAnim) {
			animDown();
		} else
			animOut();
	}

	/**
	 * 相册浏览
	 * 
	 * list 图片浏览数组 position 从第几个开始流浪 canDel 是否可以删除 handler 删除触发的handler
	 * */
	public void scanPhoto(ArrayList<photoModel> list, int position,
			boolean canDel, boolean shareFlag, Handler handler) {
		scanPhoto(list, position, canDel, shareFlag, true, handler);
	}

	public void scanPhoto(ArrayList<photoModel> list, int position,
			boolean canDel, boolean shareFlag, boolean saveFlag, Handler handler) {
		scanPhoto(list, position, canDel, shareFlag, saveFlag, false, handler);
	}

	public void scanPhoto(ArrayList<photoModel> list, int position,
			boolean canDel, boolean shareFlag, boolean saveFlag,
			boolean clickFinishFlag, Handler handler) {
		Bundle b = new Bundle();
		// 删除图片的handler
		ablumActivity.delHandler = handler;
		ablumActivity.clickFinshFlag = clickFinishFlag;
		b.putSerializable("ablumArray", list);
		b.putInt("index", position);
		b.putBoolean("Del", canDel);
		b.putBoolean("share", shareFlag);
		b.putBoolean("saveFlag", saveFlag);

		Intent intent = new Intent(this, ablumActivity.class);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
		overridePendingTransition(R.anim.ablum_transition_in,
				R.anim.transition_in);
	}

	/**
	 * 查询数据失败或者暂无数据
	 * */

	/* navigation 构造方法 */
	/** 返回默认，右边 全自定义 */
	public void initNavitation(String titleStr, String rightStr, int EditBgId,
			EditClickListener click) {
		initNavigation(0, null, titleStr, "", rightStr, EditBgId, click);
	}

	/** 返回默认，右边 全自定义 */
	public void initNavitation(String titleStr, String rightStr, int EditBgId,
			LayoutParams params, EditClickListener click) {
		initNavigation(0, null, titleStr, "", rightStr, EditBgId, params, click);
	}

	/** 返回默认，右边文字自定义，背景设置为默认 */
	public void initNavitation(String titleStr, String rightStr,
			EditClickListener click) {
		initNavigation(0, null, titleStr, "", rightStr, 0, click);
	}

	/** 返回全全自定义，右边文字自定义 */
	public void initNavigation(int backBgId, String backStr, String titleStr,
			String title1Str, String rightStr, EditClickListener click) {
		initNavigation(backBgId, backStr, titleStr, title1Str, rightStr, 0,
				click);
	}

	/**
	 * top 包含第二标题
	 * */
	public void initNavitation(String titleStr, String title1Str,
			String rightStr, int EditBgId, EditClickListener click) {
		initNavigation(0, null, titleStr, title1Str, rightStr, EditBgId, click);
	}

	/**
	 * 
	 * */

	/**
	 * @param backBgId
	 *            如果isback 为-1，表示没有返回操作,如果为0则表示默认的返回按钮
	 * @param backStr
	 *            返回字段的文字，如果字段为“”或者null，则默认为返回
	 * @param titleStr
	 * @param rightStr返回字段的文字
	 *            ，如果字段为“”或者null，则默认为""
	 * @param EditBgId
	 *            ,如果editbg为-1，则表示bg不需要，如果为0，则表示默认，点击事件也无效
	 */
	public void initNavigation(int backBgId, String backStr, String titleStr,
			String title1Str, String rightStr, int EditBgId,
			final EditClickListener click) {
		initNavigation(backBgId, backStr, titleStr, title1Str, rightStr,
				EditBgId, null, click);

	}

	public void initNavigation(int backBgId, String backStr, String titleStr,
			String title1Str, String rightStr, int EditBgId,
			LayoutParams params, final EditClickListener click) {
		initNavigation(backBgId, backStr, titleStr, title1Str, rightStr,
				EditBgId, false, params, click);

	}

	public void initNavigation(String titleStr, String titleStr1,
			String rightStr, int EditBgId, LayoutParams params,
			final EditClickListener click) {
		initNavigation(0, null, titleStr, "", rightStr, EditBgId, false,
				params, click);

	}

	public void initNavigation(int backBgId, String backStr, String titleStr,
			String title1Str, String rightStr, int EditBgId,
			boolean showProgress, LayoutParams params,
			final EditClickListener click) {

		progress = (ProgressBar) this.findViewById(R.id.top_progress);
		if (showProgress)
			progress.setVisibility(View.VISIBLE);
		else
			progress.setVisibility(View.GONE);
		btn_back = (Button) this.findViewById(R.id.top_btn_back);
		txt_title = (TextView) this.findViewById(R.id.top_txt_title);
		txt_title1 = (TextView) this.findViewById(R.id.top_txt_title1);

		if (TextUtils.isEmpty(title1Str)) {
			txt_title1.setVisibility(View.GONE);
		} else {
			txt_title1.setVisibility(View.VISIBLE);
			txt_title1.setText(title1Str);
		}

		btn_edit = (Button) this.findViewById(R.id.top_btn_edit);
		if (params != null) {

			btn_edit.setLayoutParams(params);
		}
		// btn_back对应的背景
		if (backBgId == -1) {
			btn_back.setVisibility(View.GONE);
		} else if (backBgId >= 0) {

			if (backBgId > 0)
				btn_back.setBackgroundResource(backBgId);
			btn_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (click != null)
						click.backClick();

					// TODO Auto-generated method stub

				}
			});
		}
		// btn_back对应的文字描述
		if (!TextUtils.isEmpty(backStr)) {
			btn_back.setText(backStr);
		}
		// 对应的title的现实
		if (!TextUtils.isEmpty(titleStr)) {
			txt_title.setText(titleStr);
		}

		// btn_edit对应的背景
		if (EditBgId == -1) {
			btn_edit.setVisibility(View.GONE);
		} else if (EditBgId == -2) {
			btn_edit.setBackgroundDrawable(null);
		} else if (EditBgId >= 0) {
			if (EditBgId > 0)
				btn_edit.setBackgroundResource(EditBgId);
			btn_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (click != null)
						click.editClick();

					// TODO Auto-generated method stub

				}
			});
		}
		// edit对应的文字
		if (!TextUtils.isEmpty(rightStr)) {
			btn_edit.setText(rightStr);
		}

	}

	public static void initNavigation(View v, int backBgId, String backStr,
			String titleStr, String rightStr, int EditBgId,
			LayoutParams params, final EditClickListener click) {

		Button btn_back = (Button) v.findViewById(R.id.top_btn_back);
		TextView txt_title = (TextView) v.findViewById(R.id.top_txt_title);
		Button btn_edit = (Button) v.findViewById(R.id.top_btn_edit);
		if (params != null) {

			btn_edit.setLayoutParams(params);
		}
		// btn_back对应的背景
		if (backBgId == -1) {
			btn_back.setVisibility(View.GONE);
		} else if (backBgId >= 0) {
			if (backBgId > 0)
				btn_back.setBackgroundResource(backBgId);
			btn_back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (click != null)
						click.backClick();
					// TODO Auto-generated method stub

				}
			});
		}
		// btn_back对应的文字描述
		if (!TextUtils.isEmpty(backStr)) {
			btn_back.setText(backStr);
		}
		// 对应的title的现实
		if (!TextUtils.isEmpty(titleStr)) {
			txt_title.setText(titleStr);
		}
		// btn_edit对应的背景
		if (EditBgId == -1) {
			btn_edit.setVisibility(View.GONE);
		} else if (EditBgId == -2) {
			btn_edit.setBackgroundDrawable(null);
		} else if (EditBgId >= 0) {
			if (EditBgId > 0)
				btn_edit.setBackgroundResource(EditBgId);
			btn_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (click != null)
						click.editClick();

					// TODO Auto-generated method stub

				}
			});
		}
		// edit对应的文字
		if (!TextUtils.isEmpty(rightStr)) {
			btn_edit.setText(rightStr);
		}

	}

	public static void initNavigation(Activity v, int backBgId, String backStr,
			String titleStr, String rightStr, int EditBgId,
			LayoutParams params, final EditClickListener click) {

		Button btn_back = (Button) v.findViewById(R.id.top_btn_back);
		TextView txt_title = (TextView) v.findViewById(R.id.top_txt_title);
		Button btn_edit = (Button) v.findViewById(R.id.top_btn_edit);
		if (params != null) {

			btn_edit.setLayoutParams(params);
		}
		// btn_back对应的背景
		if (backBgId == -1) {
			btn_back.setVisibility(View.GONE);
		} else if (backBgId >= 0) {
			if (backBgId > 0)
				btn_back.setBackgroundResource(backBgId);
			btn_back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (click != null)
						click.backClick();
					// TODO Auto-generated method stub

				}
			});
		}
		// btn_back对应的文字描述
		if (!TextUtils.isEmpty(backStr)) {
			btn_back.setText(backStr);
		}
		// 对应的title的现实
		if (!TextUtils.isEmpty(titleStr)) {
			txt_title.setText(titleStr);
		}
		// btn_edit对应的背景
		if (EditBgId == -1) {
			btn_edit.setVisibility(View.GONE);
		} else if (EditBgId == -2) {
			btn_edit.setBackgroundDrawable(null);
		} else if (EditBgId >= 0) {
			if (EditBgId > 0)
				btn_edit.setBackgroundResource(EditBgId);
			btn_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (click != null)
						click.editClick();

					// TODO Auto-generated method stub

				}
			});
		}
		// edit对应的文字
		if (!TextUtils.isEmpty(rightStr)) {
			btn_edit.setText(rightStr);
		}

	}

	// 改变回退activity跳转方式
	public void initNavigation1(int backBgId, String backStr, String titleStr,
			String rightStr, int EditBgId, final EditClickListener click) {
		btn_back = (Button) this.findViewById(R.id.top_btn_back);
		txt_title = (TextView) this.findViewById(R.id.top_txt_title);
		btn_edit = (Button) this.findViewById(R.id.top_btn_edit);
		// btn_back对应的背景
		if (backBgId == -1) {
			btn_back.setVisibility(View.GONE);
		} else if (backBgId == -2) {
			btn_back.setBackgroundDrawable(null);
		} else if (backBgId >= 0) {
			if (backBgId > 0)
				btn_back.setBackgroundResource(backBgId);
			btn_back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (click != null)
						click.backClick();
					//
					// TODO Auto-generated method stub

				}
			});
		}
		// btn_back对应的文字描述
		if (!TextUtils.isEmpty(backStr)) {
			btn_back.setText(backStr);
		}
		// 对应的title的现实
		if (!TextUtils.isEmpty(titleStr)) {
			txt_title.setText(titleStr);
		}
		// btn_edit对应的背景
		if (EditBgId == -1) {
			btn_edit.setVisibility(View.GONE);
		} else if (EditBgId == -2) {
			btn_edit.setBackgroundDrawable(null);
		} else if (EditBgId >= 0) {
			if (EditBgId > 0)
				btn_edit.setBackgroundResource(EditBgId);
			btn_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (click != null)
						click.editClick();
					// TODO Auto-generated method stub

				}
			});
		}
		if (!TextUtils.isEmpty(rightStr)) {
			btn_edit.setText(rightStr);
		}

	}

	public void onDestory() {
		super.onDestroy();

	}

	// 回复被系统回收的静态数据数据
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (TextUtils.isEmpty(User.id)) {
			Company company = (Company) CommonUtil
					.decodeObject(sharedPreferenceUtil.readCompany(this));
			if (company == null || TextUtils.isEmpty(company.id)) {
			} else {
				User.init(company);

			}
		}
	}

	public interface flashBtnClick {
		public void click();
	}

	/**
	 * 输出打印信息
	 * 
	 * @param context
	 * @param msg
	 */
	public void showToast(String msg) {
		if (setting.toastSwitch)
			Toast.makeText(this, msg, 1000).show();
	}

	/**
	 * 输出打印消息
	 * 
	 * @param context
	 * @param msgId
	 */
	public void showToast(int msgId) {
		if (setting.toastSwitch)
			Toast.makeText(this, getResources().getString(msgId), 1000).show();
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	// 定时隐藏
	public void showRightDialog() {
		showRightDialog("");
	}

	public void showRightDialog(int id) {
		showRightDialog(getString(id));
	}

	public void showRightDialog(String msg) {
		showRightDialog(msg, true);
	}

	public void showRightDialog(String msg, Boolean isAutoColse) {
		myhandler.removeMessages(100);
		editDialog = DialogUtils.showConfirmDialog(this, msg);
		editDialog.show();
		if (isAutoColse) {
			myhandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = myhandler.obtainMessage();
					msg.what = 100;
					msg.sendToTarget();
				}
			}, 800);
		}
	}
	public void showRightDialog(String msg, final showRightCallback callback) {
		myhandler.removeMessages(100);
		editDialog = DialogUtils.showConfirmDialog(this, msg);
		editDialog.show();
	
			myhandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = myhandler.obtainMessage();
					msg.what = 100;
					msg.sendToTarget();
					callback.callback();
				}
			}, 800);
		
		
	}
	private Dialog editDialog = null;
	Handler myhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (editDialog != null)
				editDialog.cancel();
		}

	};

	protected void onResume() {
		super.onResume();

	}

	// 初始化列表
	// listItemModelBase item1 = new listItemModelBase();
	// item.setArrawIsShow(true);
	// item.setBackground(R.drawable.item_click_bg);
	// item.setImageId(-1);
	// item.setTittle(txtArrayList.get(0));
	// item.setIndex(Integer.parseInt(numArrayList.get(0)));
	// item.setItemHeight(50);
	// item.setSplitMarginLeft(0);
	// item1.setTittleMarginLeft(10 + 10);
	// item.setType(0);
	// itemModelList.add(item);

	// 分隔item的定制无文字
	protected listItemModelBase SplitlistitemModelBase(int bgid, int itemHeight) {
		return initListModelBase(false, bgid, -1, "", -1, itemHeight, 0, 0);
	}

	// 分割item 有文字
	protected listItemModelBase SplitlistitemModelBase(int bgid,
			int itemHeight, String title, int titleColor) {
		return initListModelBase(false, bgid, -1, title, -1, itemHeight, 0, 15,
				titleColor);
	}

	protected listItemModelBase SplitlistitemModelBase(int bgid,
			int itemHeight, String title) {
		return SplitlistitemModelBase(bgid, itemHeight, title,
				Color.rgb(153, 153, 153));

	}

	// 缺少右边标题
	protected listItemModelBase initListModelBase(boolean arrawIsShow,
			int bgId, int ImgId, String title, int index, int itemHieght,
			int splitMarginLeft, int TitleMargleft) {
		return initListModelBase(arrawIsShow, bgId, ImgId, title, "", index,
				itemHieght, splitMarginLeft, TitleMargleft, 0, Color.BLACK,
				Color.rgb(153, 153, 153));
	}

	// 右边缺少标题，但是可以设置左边的文字颜色
	protected listItemModelBase initListModelBase(boolean arrawIsShow,
			int bgId, int ImgId, String title, int index, int itemHieght,
			int splitMarginLeft, int TitleMargleft, int titleColor) {
		return initListModelBase(arrawIsShow, bgId, ImgId, title, "", index,
				itemHieght, splitMarginLeft, TitleMargleft, 0, titleColor,
				Color.rgb(153, 153, 153));
	}

	// 添加右边标题
	protected listItemModelBase initListModelBase(boolean arrawIsShow,
			int bgId, int ImgId, String title, String title1, int index,
			int itemHieght, int splitMarginLeft, int TitleMargleft) {
		return initListModelBase(arrawIsShow, bgId, ImgId, title, title1,
				index, itemHieght, splitMarginLeft, TitleMargleft, 0,
				Color.BLACK, Color.rgb(153, 153, 153));
	}

	protected listItemModelBase initListModelBase(boolean arrawIsShow,
			int bgId, int ImgId, String title, int index, int itemHieght,
			int splitMarginLeft, int TitleMargleft, int titleColor,
			int title1Color) {
		return initListModelBase(arrawIsShow, bgId, ImgId, title, "", index,
				itemHieght, splitMarginLeft, TitleMargleft, 0, titleColor,
				title1Color);
	}

	/**
	 * @param arrawIsShow
	 *            箭头是否现实
	 * @param bgId
	 *            item的背景
	 * @param ImgId
	 *            icon －1表示不显示
	 * @param title
	 *            左边的title
	 * @param title1
	 *            右边title
	 * @param index
	 *            用于itemonclick时，标示点击后根据index进行相应操作
	 * @param itemHieght
	 *            item的高度
	 * @param splitMarginLeft
	 *            item分割线隔左边的距离
	 * @param TitleMargleft
	 *            左边文字离左边的距离
	 * @param type
	 *            预留字段
	 * @return
	 */
	protected listItemModelBase initListModelBase(boolean arrawIsShow,
			int bgId, int ImgId, String title, String title1, int index,
			int itemHieght, int splitMarginLeft, int TitleMargleft, int type,
			int titleColor, int title1Color) {
		listItemModelBase item = new listItemModelBase();
		item.setArrawIsShow(arrawIsShow);
		item.setBackground(bgId);
		item.setImageId(ImgId);
		item.setTittle(title);
		item.setIndex(index);
		item.setItemHeight(itemHieght);
		item.setTittle1(title1);
		item.setSplitMarginLeft(splitMarginLeft);
		item.setTittleMarginLeft(TitleMargleft);
		item.setType(type);
		item.setTitleTextColor(titleColor);
		item.setTitle1TextColor(title1Color);
		return item;
	}
	public interface showRightCallback
	{
		public void callback();
	};
}
