package com.wq.me;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.endure.wq.R;
import com.endure.wq.R.string;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.loginActivity;
import com.wq.webviewShowActivity;
import com.wq.Adapter.listItemAdapter;
import com.wq.UI.InScrolllistView;
import com.wq.UI.MMAlert;
import com.wq.UI.MMAlert.alertItemclickListener;
import com.wq.model.User;
import com.wq.model.VersionItem;
import com.wq.model.listItemModelBase;
import com.wq.model.listSplitArrModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import com.wq.utils.stringUtils;

public class about_us_Activity extends BaseActivity {

	@ViewInject(id = R.id.listview)
	InScrolllistView listview;
	listItemAdapter adapter;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();

	@ViewInject(id = R.id.txt_version)
	TextView txt_top_vertsion;
	@ViewInject(id = R.id.me_rela1)
	RelativeLayout rela_main;

	private VersionItem version = null;// 版本更新

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_activity);
		BaseApplication.getInstance().addActivity(this);
		initdata();
		BindUI();
		initListener();
		initnavigation();
	}

	private void initdata() {
		ArrayList<listSplitArrModel> splitArrModels = new ArrayList<listSplitArrModel>();
		String[] arr = getResources()
				.getStringArray(R.array.about_wq_list_item);
		stringUtils.splitarrString(arr, splitArrModels);
		for (int i = 0; i < splitArrModels.size(); i++) {
			listSplitArrModel item = splitArrModels.get(i);
			if (item.getSplitHeight() > 0)

				list.add(SplitlistitemModelBase(R.drawable.item_split_bg,
						item.getSplitHeight()));

			list.add(initListModelBase(true, R.drawable.item_click_bg, -1,
					item.getTitle(), item.getTitle1(), item.getIndex(), 50,
					item.getSplitMarginLeft(), item.getTextMarginleft()));
		}
		adapter = new listItemAdapter(this, list);
		listview.setAdapter(adapter);

	}

	private void BindUI() {
		// 获取当前版本
		txt_top_vertsion.setText(String.format(
				getString(R.string.me_string_about_version_title),
				getAppVersion()));// 获取当前版本)
	}

	private void initListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				switch (list.get(position).getIndex()) {
				// 欢迎界面
				case 1:
					Bundle b1 = new Bundle();
					b1.putString("url", httpUtil.WELCOME_US_URL);
					b1.putBoolean("isShare", false);
					b1.putString("title",
							getString(R.string.me_string_about_gnjs));
					changeView(mewebviewShowActivity.class, b1);
					break;
				// 功能介绍
				case 2:
					Bundle b = new Bundle();
					b.putString("url", httpUtil.ABOUT_US_URL);
					b.putBoolean("isShare", false);
					b.putString("title",
							getString(R.string.me_string_about_gnjs));
					changeView(mewebviewShowActivity.class, b);
					break;
				// 反馈意见
				case 3:
					changeView(ideaBackActivity.class);
					break;
				// 版本检测
				case 4:
					checkUpdate(new Handler() {

						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							if (msg.what == 1)
								version = (VersionItem) msg.obj;

							if (version != null) {
								ShowUpdate(version);
							}
						}
					});
					break;
				default:
					break;
				}
				// TODO Auto-generated method stub

			}
		});

	}

	private void initnavigation() {
		initNavitation(getString(R.string.me_string_set_about_room), "", -1,
				new EditClickListener() {

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

	// 版本检测

	public void checkUpdate(final Handler handler) {
		showProgress(R.string.dialog_version);
		httpUtil.post(httpUtil.APP_UPDATE_URL, new AjaxCallBack<String>() {
			public void onStart() {

			}

			@SuppressWarnings("unused")
			public void onSuccess(String result) {
				try {

					JSONObject jsonResult = new JSONObject(result);
					String errcode = jsonResult.getString(httpUtil.ERR_CODE);
					if (errcode.equals(httpUtil.errCode_success)) {
						String ver = getAppVersion();// 获取当前版本

						VersionItem version = new VersionItem();

						version.verName = jsonResult.getString("ver_name");// 更新版本
						version.versionCode = jsonResult.optInt("ver", 0);
						version.mandupVer = jsonResult.getString("mandupVer");// 禁止版本
						JSONObject object = jsonResult
								.getJSONObject("updataDic");
						version.updateUrl = object.getString("downURL");// 更新地址
						version.updateContent = object.getString("updataMgs");// 更新内容

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
									if (version.versionCode == f) {
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
				} finally {
					hideProgress();
				}
			}

			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgress();

				CommonUtil.showToastHttp(getApplicationContext(), errorNo);
			}

		});

	}

	// 显示版本更新弹出框
	public void ShowUpdate(final VersionItem version) {

		// 强制更新
		if (version.UpdateFlag == -1) {
			DialogUtils.showDialog(this, version.updateContent, "更新",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 更新
							update(version.updateUrl);
						}
					}, "退出", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							BaseApplication.getInstance().exit();
						}
					}).show();

		}
		// 版本升级
		else if (version.UpdateFlag == 1) {
			DialogUtils.showDialog(this, version.updateContent, "更新",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 更新
							update(version.updateUrl);
						}
					}, "取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
		}
		// 无版本更新
		else if (version.UpdateFlag == 0) {
			showRightDialog(getString(R.string.me_new_version), false);
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
	public String getAppVersion() {
		String version = "";
		PackageManager pkgManager = getPackageManager();
		try {
			PackageInfo info = pkgManager.getPackageInfo(getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {

		}
		return version;
	}

}
