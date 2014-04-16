package com.wq;

import java.util.Timer;
import java.util.TimerTask;

import net.endure.framework.FinalBitmap;
import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.bitmap.display.mycallback;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.endure.wq.R;
import com.wq.UI.DragImageView;
import com.wq.UI.ablumImageView;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;

public class showLogoActivity extends BaseActivity {
	@ViewInject(id = R.id.img)
	DragImageView dragImageView;
	Bitmap mybitmap;
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	String filePath = "";
	private int window_width, window_height;// 控件宽度
	private int state_height;// 状态栏的高度
	FinalDb db;
	private ViewTreeObserver viewTreeObserver;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_logo_activity);
		BaseApplication.getInstance().addActivity(this);
		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		finalBitmap = FinalBitmap.create(this);
		filePath = this.getIntent().getStringExtra("filePath");
		config.setIsRoundCore(true);
		config.setCallback(new mycallback() {

			@Override
			public void success(Bitmap bitmap) {
				if (bitmap == null)
					return;
				mybitmap = BitmapUtil.getBitmap(bitmap, window_width,
						window_height);
				// TODO Auto-generated method stub
				//dragImageView.setImageBitmap(mybitmap);
				viewTreeObserver = dragImageView.getViewTreeObserver();
				viewTreeObserver
						.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

							@Override
							public void onGlobalLayout() {
								if (state_height == 0) {
									// 获取状况栏高度
									Rect frame = new Rect();
									getWindow()
											.getDecorView()
											.getWindowVisibleDisplayFrame(frame);
									state_height = frame.top;
									dragImageView.setScreen_H(window_height
											- state_height);
									dragImageView.setScreen_W(window_width);
								}

							}
						});

			}

			@Override
			public void failure() {
				// TODO Auto-generated method stub

			}
		});

		dragImageView.setmActivity(this);// 注入Activity.
		if (TextUtils.isEmpty(filePath)) {
			dragImageView.setImageBitmap(BitmapUtil.ReadBitmapById(
					showLogoActivity.this, R.drawable.add_prompt));
		} else
			finalBitmap.display(dragImageView, filePath, config);
		/** 测量状态栏高度 **/
		layout_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// exitBy2Click();
				if (mybitmap != null && !mybitmap.isRecycled())
					mybitmap.recycle();
				finish();
				overridePendingTransition(0, R.anim.ablum_transition_out);
			}
		});
		dragImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, R.anim.ablum_transition_out);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(0, R.anim.ablum_transition_out);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;// 准备退出

			tExit = new Timer();
			tExit.schedule(new TimerTask() {

				@Override
				public void run() {
					isExit = false;// 取消退出
				}
			}, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			if (mybitmap != null && !mybitmap.isRecycled())
				mybitmap.recycle();
			finish();
			overridePendingTransition(0, R.anim.ablum_transition_out);

		}
	}
}
