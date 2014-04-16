package com.wq.me;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.UI.MMAlert;
import com.wq.UI.MMAlert.alertItemclickListener;
import com.wq.UI.MMAlert.onitemClick;
import com.wq.model.User;
import com.wq.model.mmAlertModel;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

public class nameCardEWMActivity extends BaseActivity {
	@ViewInject(id = R.id.large_img_logo)
	ImageView large_logo;
	@ViewInject(id = R.id.small_img_logo)
	ImageView small_logo;
	@ViewInject(id = R.id.txt_company_name)
	TextView txt_company_name;
	@ViewInject(id = R.id.txt_company_wqh)
	TextView txt_wqh;
	@ViewInject(id = R.id.img_erweima)
	ImageView img_erweima;
	@ViewInject(id = R.id.layout_main)
	ScrollView layout_main;
	FinalBitmap finalBitmap = null;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	private int statusBarHeight = 0;
	private int titleBarHeight = 0;
	String url = "";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.name_card_ewm_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		config.setIsRoundCore(true);
		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		url = this.getIntent().getStringExtra("url");
		initNavigation();
		BindUI();
	}

	private void BindUI() {
		if (!TextUtils.isEmpty(url)) {
			new twodimensionImageAsyc().execute(url);
			setViewTouchListener(layout_main);
		}
	}

	private void initNavigation() {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(this, 55),
				DensityUtil.dip2px(this, 35));
		initNavigation(0, getString(R.string.string_back),
				getString(R.string.me_name_card_ewm), "", "",
				R.drawable.btn_share_click, params, new EditClickListener() {
					@Override
					public void editClick() {
						String[] arr = getResources().getStringArray(
								R.array.mmalert_ewm_fxbc);
						ArrayList<mmAlertModel> list = CommonUtil
								.getSplitmmAlertModel(arr);
						MMAlert.showAlertListView(nameCardEWMActivity.this,
								list, true, new onitemClick() {
									@Override
									public void onItemClick(int position,
											mmAlertModel item) {
										if (item.getIndex().equals("1")) {
											Uri uri = Uri.parse("smsto:");
											Intent ii = new Intent(
													Intent.ACTION_SENDTO, uri);// 绿色文字就是启动发送短信窗口

											ii.putExtra(
													"sms_body",
													String.format(
															getString(R.string.ewm_share_mp),
															url));

											startActivity(ii);

										} else if (item.getIndex().equals("2")) {
											BitmapUtil.savePicCamer(
													nameCardEWMActivity.this,
													getShootBitmap());
										}

									}
								});

					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});
	}

	/** 开线程生成二维码 */
	private class twodimensionImageAsyc extends
			AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			return CommonUtil.createTwodimensionImage(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// 返回HTML页面的内容
			if (result != null) {
				img_erweima.setImageBitmap(result);
			}
			txt_wqh.setText(String.format(
					getString(R.string.me_string_wqh_format), User.wqh));
			txt_company_name.setText(User.contactName);
			finalBitmap.display(large_logo, User.iconFile, config, true);
			finalBitmap.display(small_logo, User.iconFile, config, true);

		}

	}

	/* 获取矩形区域内的截图 */
	private Bitmap getShootBitmap() {
		getBarHeight();

		// Bitmap screenShoot = takeScreenShot();
		int screenWidth = DensityUtil.intScreenWidth(this);

		Bitmap finalBitmap = Bitmap.createBitmap(takeScreenShot(),
				DensityUtil.dip2px(this, 20), DensityUtil.dip2px(this, 100)
						+ statusBarHeight,
				screenWidth - DensityUtil.dip2px(this, 40),
				DensityUtil.dip2px(this, 338));
		return finalBitmap;

	}

	private void getBarHeight() {
		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

		statusBarHeight = frame.top;

		int contenttop = this.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// statusBarHeight是上面所求的状态栏的高度
		titleBarHeight = contenttop - statusBarHeight;

	}

	// 获取Activity的截屏
	private Bitmap takeScreenShot() {
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}
}
