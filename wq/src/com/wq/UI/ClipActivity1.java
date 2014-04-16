package com.wq.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import com.endure.wq.R;
import com.wq.BaseApplication;
import com.wq.PicCheck.Bimp;
import com.wq.PicCheck.FileUtils;
import com.wq.utils.BitmapUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup.LayoutParams;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;

/** 带有截取框 */
public class ClipActivity1 extends Activity implements OnClickListener,
		OnTouchListener {
	public static int clipFlag = 0;

	private ImageView screenshot_imageView;
	private Button cancel_btn, ok_btn, rotate_btn;
	// private ClipView clipview;
	Context mcontext;
	private Bitmap bitmap;
	private int width;// 屏幕宽度
	private int height;// 屏幕高度
	private Rect rectIV;
	private int statusBarHeight = 0;
	private int titleBarHeight = 0;
	private int angleInt = 0; // 旋转次数
	private int n = 0;// angleInt % 4 的值，用于计算旋转后图片区域

	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	DisplayMetrics dm;

	float minScaleR;// 最小缩放比例
	static final float MAX_SCALE = 15f;// 最大缩放比例

	// We can be in one of these 3 states
	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	private static final String TAG = "11";
	int mode = NONE;

	// Remember some things for zooming
	PointF prev = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	int degree = 0;
	// public static final File FILE_SDCARD = Environment
	// .getExternalStorageDirectory();
	// public static final File FILE_LOCAL = new File(FILE_SDCARD, "weixin");
	String mPath = "";// 图片所在手机的路径
	public static final String FILE_SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/.wqCrop";
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// 将截图显示到右上角
			if (bitmap != null && !bitmap.isRecycled()) {
				screenshot_imageView = null;
				bitmap.recycle();
				System.gc();
			}
			Intent intent = new Intent();
			intent.putExtra("path", msg.obj.toString());
			setResult(RESULT_OK, intent);
			finish();
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏显示
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// EY = DensityUtil.dip2px(ClipActivity.this, EY);
		// EX = DensityUtil.dip2px(ClipActivity.this, EX);
		// SX = DensityUtil.dip2px(ClipActivity.this, SX);
		setContentView(R.layout.photo_crop_activity1);
		BaseApplication.getInstance().addActivity(this);
		setUpViews();
		setUpListeners();
		mPath = getIntent().getStringExtra("path");
		try {
			bitmap = Bimp.revitionImageSize(mPath);
			if (bitmap == null) {
				Toast.makeText(ClipActivity1.this, "没有找到图片", 0).show();
				finish();
			} else {
				degree = BitmapUtil.readPictureDegree(mPath);
				bitmap = BitmapUtil.rotate(bitmap, degree);
				screenshot_imageView.setImageBitmap(bitmap);
			}
		} catch (Exception e) {
			Toast.makeText(ClipActivity1.this, "没有找到图片", 0).show();
			finish();
		}
		try {
			if (screenshot_imageView != null
					&& screenshot_imageView.getDrawable() != null)
				rectIV = screenshot_imageView.getDrawable().getBounds();
			getStatusBarHeight();
			minZoom();
			center();
			screenshot_imageView.setImageMatrix(matrix);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 获取状态栏高度 下面方法在oncreate中调用时获得的状态栏高度为0，不能用 Rect frame = new Rect();
	 * getWindow().getDecorView().getWindowVisibleDisplayFrame(frame); int
	 * statusBarHeight = frame.top;
	 */
	private void getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			// 通过反射来获取状态栏的高
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpViews() {
		screenshot_imageView = (ImageView) findViewById(R.id.imageView);
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
		ok_btn = (Button) findViewById(R.id.ok_btn);
		rotate_btn = (Button) findViewById(R.id.rotate_btn);
		// clipview = (ClipView) findViewById(R.id.clipview);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = DensityUtil.intScreenWidth(ClipActivity1.this);
		height = DensityUtil.intScreenHeight(ClipActivity1.this);

		screenshot_imageView.setImageMatrix(matrix);
	}

	private void setUpListeners() {
		cancel_btn.setOnClickListener(this);
		ok_btn.setOnClickListener(this);
		rotate_btn.setOnClickListener(this);
		screenshot_imageView.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_btn:
			finish();
			break;
		case R.id.ok_btn:
			Runnable crop = new Runnable() {
				public void run() {

					String path = saveToLocal(getBitmap());

					Message msg = Message.obtain(mHandler);
					msg.obj = path;
					msg.sendToTarget();
				}
			};
			startBackgroundJob("截图", "正在保存", crop, mHandler);
			break;
		case R.id.rotate_btn:
			n = ++angleInt % 4;
			// 图片旋转-90度
			matrix.postRotate(-90, screenshot_imageView.getWidth() / 2,
					screenshot_imageView.getHeight() / 2);
			savedMatrix.postRotate(-90);
			screenshot_imageView.setImageMatrix(matrix);
			break;
		}
	}

	/**
	 * 下面的触屏方法摘自网上经典的触屏方法 只在判断是否在图片区域内做了少量修改
	 * 
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			// 设置初始点位置
			if (rectIV == null)
				break;
			prev.set(event.getX(), event.getY());
			if (isOnCP(event.getX(), event.getY())) {
				// 触点在图片区域内
				mode = DRAG;
			} else {
				mode = NONE;
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			// 判断触点是否在图片区域内
			boolean isonpic = isOnCP(event.getX(), event.getY());
			Log.d(TAG, "oldDist=" + oldDist);
			// 如果连续两点距离大于10，则判定为多点模式
			if (oldDist > 10f && isonpic) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - prev.x, event.getY()
						- prev.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		view.setImageMatrix(matrix);
		CheckView();
		return true; // indicate event was handled
	}

	/**
	 * 两点的距离 Determine the space between the first two fingers
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点 Calculate the mid point of the first two fingers
	 * */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/**
	 * 判断点所在的控制点
	 * 
	 * @param evX
	 * @param evY
	 * @return
	 */
	private boolean isOnCP(float evx, float evy) {
		float p[] = new float[9];
		matrix.getValues(p);
		float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
		// 由于本人很久不用数学，矩阵的计算已经忘得差不多了，所以图片区域的计算只能按最笨的办法，
		// 根据旋转角度分四种情况计算图片区域，如果哪位达人修改一下只用一个表达式，那可以减少很多代码
		RectF rectf = null;
		switch (n) {
		case 0:
			rectf = new RectF(p[2], p[5], (p[2] + rectIV.width() * scale),
					(p[5] + rectIV.height() * scale));
			break;
		case 1:
			rectf = new RectF(p[2], p[5] - rectIV.width() * scale, p[2]
					+ rectIV.height() * scale, p[5]);
			break;
		case 2:
			rectf = new RectF(p[2] - rectIV.width() * scale, p[5]
					- rectIV.height() * scale, p[2], p[5]);
			break;
		case 3:
			rectf = new RectF(p[2] - rectIV.height() * scale, p[5], p[2], p[5]
					+ rectIV.width() * scale);
			break;
		}
		if (rectf != null && rectf.contains(evx, evy)) {
			return true;
		}
		return false;
	}

	/**
	 * 最小缩放比例，最大为100%
	 */
	private void minZoom() {
		minScaleR = Math.min((float) dm.widthPixels / (float) bitmap.getWidth()
				/ 2, (float) dm.widthPixels / (float) bitmap.getHeight() / 2);
		if (minScaleR < 1.0 / 2) {
			float scale = Math.max(
					(float) dm.widthPixels / (float) bitmap.getWidth(),
					(float) dm.widthPixels / (float) bitmap.getHeight());
			matrix.postScale(scale, scale);
		} else {
			minScaleR = 1.0f;
		}
	}

	/**
	 * 限制最大最小缩放比例
	 */
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
		float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
		if (mode == ZOOM) {
			if (scale < minScaleR) {
				matrix.setScale(minScaleR, minScaleR);
				center();
			}
			if (scale > MAX_SCALE) {
				matrix.set(savedMatrix);
			}
		}
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 横向、纵向居中
	 */
	protected void center(boolean horizontal, boolean vertical) {

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height - statusBarHeight) / 2
						- rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = screenshot_imageView.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right > screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
		if (n % 4 != 0) {
			matrix.postRotate(-90 * (n % 4),
					screenshot_imageView.getWidth() / 2,
					screenshot_imageView.getHeight() / 2);
		}
	}

	/* 获取矩形区域内的截图 */
	private Bitmap getBitmap() {
		getBarHeight();
		Bitmap screenShoot = takeScreenShot();
		Bitmap finalBitmap = Bitmap.createBitmap(screenShoot, ClipView.width
				/ 2 - ClipView.SX + 1, ClipView.height / 2 - ClipView.SX
				+ ClipView.SY + 1, ClipView.SX * 2 - 1,
				(ClipView.SX - ClipView.SY) * 2 - 1);
		try {
			screenShoot.recycle();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

		Log.v(TAG, "statusBarHeight = " + statusBarHeight
				+ ", titleBarHeight = " + titleBarHeight);
	}

	// 保存图片
	public String saveToLocal(Bitmap bm) {
		if (!createSDCardDir(FILE_SDCARD_PATH))
			return null;
		String path = FILE_SDCARD_PATH + "/temp.jepg";
		try {
			if (TextUtils.isEmpty(FileUtils.saveBitmapPath(bm, path))) {
				if (bm != null && !bm.isRecycled())
					bm.recycle();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return path;
	}

	// 创建文件夹
	public boolean createSDCardDir(String dirPath) {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			// 创建一个文件夹对象，赋值为外部存储器的目录

			// File sdcardDir = Environment.getExternalStorageDirectory();
			//
			// // 得到一个路径，内容是sdcard的文件夹路径和名字
			//
			// String path = sdcardDir.getPath() + "/" + dirName;

			File path1 = new File(dirPath);

			if (!path1.exists()) {

				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
			}
			return true;
		}

		else {
			return false;

		}

	}

	private Bitmap takeScreenShot() {
		// View是你需要截图的View
		View view = getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		System.out.println(statusBarHeight);

		// 获取屏幕长和高
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		try {
			b1.recycle();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	// 根据路径来创建图片
	public Bitmap createBitmap(String path, int w, int h) {
		Bitmap b = null;
		try {

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			//
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, w, h) + 1;

			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			b = BitmapFactory.decodeFile(path, newOpts);
			// /return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception

		} finally {
			return b;
		}
	}

	// 另开线程做图片存储
	public void startBackgroundJob(String title, String message, Runnable job,
			Handler handler) {

		new Thread(new CropJob(job)).start();
	}

	private class CropJob implements Runnable {

		private final Runnable mJob;

		public CropJob(Runnable job) {

			mJob = job;
		}

		public void run() {
			try {
				mJob.run();
			} catch (Exception e) {
			} finally {

			}
		}
	}

	protected void onDestroy() {
		LoggerUtil.i("destoryyyyyyyyy");
		// TODO Auto-generated method stub
		super.onDestroy();
		if (bitmap != null && !bitmap.isRecycled())
			bitmap.recycle();
	}
}
