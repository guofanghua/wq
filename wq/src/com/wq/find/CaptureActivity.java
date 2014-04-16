package com.wq.find;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import net.endure.framework.annotation.view.ViewInject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.endure.wq.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.HybridBinarizer;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.UI.MMAlert;
import com.wq.UI.MMAlert.onitemClick;
import com.wq.Zxing.camera.CameraManager;
import com.wq.Zxing.decoding.CaptureActivityHandler;
import com.wq.Zxing.decoding.InactivityTimer;
import com.wq.Zxing.exeture.QRCodeReader;
import com.wq.Zxing.exeture.RGBLuminanceSource;
import com.wq.Zxing.exeture.ResultHandler;
import com.wq.Zxing.view.ViewfinderView;
import com.wq.model.mmAlertModel;
import com.wq.partner.part_DetailActivity;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.SDCardUtils;

/**
 * 扫描二维码
 * 
 * */
public final class CaptureActivity extends BaseActivity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();
	// private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private RelativeLayout layout_top;

	private Result lastResult;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;

	// private Button from_gallery;
	private final int from_photo = 010;
	static final int PARSE_BARCODE_SUC = 3035;
	static final int PARSE_BARCODE_FAIL = 3036;
	String photoPath;
	ProgressDialog mProgress;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	// Dialog dialog;
	// 开启闪光灯和从相册里面选择
	@ViewInject(id = R.id.btn_sys_light)
	Button btn_light;
	@ViewInject(id = R.id.btn_sys_photo)
	Button btn_photo;
	boolean isLight = false;

	enum IntentSource {

		ZXING_LINK, NONE

	}

	Handler barHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				// viewfinderView.setRun(false);

				String id = "";
				final String result = String.valueOf(msg.obj);
				if (TextUtils.isEmpty(result)) {
					return;
				}
				showResult(result);
				break;
			case PARSE_BARCODE_FAIL:
				// showDialog((String) msg.obj);
				if (mProgress != null && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				new AlertDialog.Builder(CaptureActivity.this)
						.setTitle("提示")
						.setMessage("扫描失败！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										startSM();
									}
								}).show();
				break;
			}
			super.handleMessage(msg);
		}

	};

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return CameraManager.get();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_capture_activity);
		BaseApplication.getInstance().addActivity(this);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		CameraManager.init(this);
		// cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(CameraManager.get());

		// title = (TitleView) findViewById(R.id.decode_title);
		// from_gallery = (Button) findViewById(R.id.from_gallery);
		// 为标题和底部按钮添加监听事件
		initNavagaiton();
		initListener();
	}

	private void initListener() {

		btn_light.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				isLight = !isLight;
				// TODO Auto-generated method stub
				// setFlashlightEnabled(!isLight);
				// try{
				// Camera m_Camera = Camera.open();
				// Camera.Parameters mParameters;
				// mParameters = m_Camera.getParameters();
				// mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				// m_Camera.setParameters(mParameters);
				// } catch(Exception ex){
				// ex.printStackTrace();
				// }
				try {
					if (isLight)
						CameraManager.get().openF();
					else
						CameraManager.get().stopF();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		btn_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<mmAlertModel> list = new ArrayList<mmAlertModel>();
				String[] arr = getResources().getStringArray(
						R.array.mmalert_ewm_sm);
				for (int i = 0; i < arr.length; i++) {
					String[] arr1 = arr[i].split("\\|");
					if (arr1.length >= 2) {
						mmAlertModel item = new mmAlertModel();
						item.setIndex(arr1[0]);
						item.setText(arr1[1]);
						list.add(item);
					}
				}
				 MMAlert.showAlertListView(CaptureActivity.this, list,
						true, new onitemClick() {

							@Override
							public void onItemClick(int position,
									mmAlertModel item) {

								// 从相册选择
								if (item.getIndex().equals("1")) {
									if (!SDCardUtils.ExistSDCard()) {
										showToast("请插入sd卡");
										return;
									}

									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_PICK);
									intent.setType("image/*");
									startActivityForResult(intent,
											MMAlert.FLAG_CHOOSE_IMG);

								} else if (item.getIndex().equals("2")) {
									// 取消

								}
								// TODO Auto-generated method stub

							}
						});
				// TODO Auto-generated method stub

			}
		});
	}

	public void showResult(final String msg) {

		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
		stopSM();
		String id = "";
		if (TextUtils.isEmpty(msg)) {
			return;
		}
		if (msg.indexOf("http://") >= 0) {
			String[] arr = msg.split("\\?gsmId=");
			if (arr.length >= 2) {
				id = arr[1].split("&")[0];
				// 跳转
				if (!TextUtils.isEmpty(id)) {
					Intent intent = new Intent(CaptureActivity.this,

					part_DetailActivity.class);

					intent.putExtra("id", id);

					intent.putExtra("channel", "3");

					startActivity(intent);

					animIn();

					finish();
				}
			}
			// 跳转http
			else {
				DialogUtils.showDialog(CaptureActivity.this, msg, "确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 更新
								Uri uri = Uri.parse(msg);
								Intent it = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(it);

							}
						}, "取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								startSM();
							}
						}).show();

			}
		}

	}

	public void initNavagaiton() {
		layout_top = (RelativeLayout) findViewById(R.id.layout_top);
		BaseActivity.initNavigation(layout_top, 0,
				getString(R.string.string_back),
				getString(R.string.string_ewm_title), "", -1, null,
				new BaseActivity.EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub

					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						overridePendingTransition(0,
								R.anim.show_transition_out_imm);
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			if (requestCode == MMAlert.FLAG_CHOOSE_IMG
					&& resultCode == RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					if (!TextUtils.isEmpty(uri.getAuthority())) {
						Cursor cursor = getContentResolver().query(uri,
								new String[] { MediaStore.Images.Media.DATA },
								null, null, null);
						if (null == cursor) {
							Toast.makeText(CaptureActivity.this, "图片没找到", 0)
									.show();
							return;
						}
						cursor.moveToFirst();
						final String path = cursor.getString(cursor
								.getColumnIndex(MediaStore.Images.Media.DATA));
						cursor.close();
						// mProgress = new ProgressDialog(CaptureActivity.this);
						// mProgress.setMessage("正在扫描...");
						// mProgress.setCancelable(false);
						// mProgress.show();
						new Thread(new Runnable() {
							@Override
							public void run() {
								Looper.prepare();
								String result = scanningImage(path);
								if (result != null) {

									Message m = Message.obtain();
									m.what = PARSE_BARCODE_SUC;
									m.obj = result;
									barHandler.sendMessage(m);
								} else {
									Message m = Message.obtain();
									m.what = PARSE_BARCODE_FAIL;
									m.obj = "扫描失败！";
									barHandler.sendMessage(m);
								}
								Looper.loop();
							}
						}).start();
					}
				}
			} else if (requestCode == MMAlert.FLAG_CHOOSE_PHONE
					&& resultCode == RESULT_OK) {
				File f = new File(MMAlert.FILE_PIC_SCREENSHOT,
						MMAlert.localTempImageFileName);
				final String path = f.getAbsolutePath();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Looper.prepare();
						String result = scanningImage(path);

						if (result != null) {

							Message m = Message.obtain();
							m.what = PARSE_BARCODE_SUC;
							m.obj = result;
							barHandler.sendMessage(m);
						} else {
							Message m = Message.obtain();
							m.what = PARSE_BARCODE_FAIL;
							m.obj = "扫描失败！";
							barHandler.sendMessage(m);
						}
						Looper.loop();
					}
				}).start();
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LoggerUtil.i("onResume");
		startSM();

	}

	// 开始扫描
	private void startSM() {
		handler = null;
		lastResult = null;
		resetStatusView();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		inactivityTimer.onResume();
		source = IntentSource.NONE;
		decodeFormats = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		/**
		 * 初始化声音
		 */
		initBeepSound();
		/**
		 * 是否震动
		 */
		vibrate = true;

	}

	// 停止扫描
	private void stopSM() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		CameraManager.get().closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
	}

	@Override
	protected void onPause() {
		LoggerUtil.i("onPause");
		if (handler != null)
			stopSM();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		if (mProgress != null) {
			mProgress.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK)
					&& lastResult != null) {

				restartPreviewAfterDelay(0L);
				finish();
				overridePendingTransition(0, R.anim.show_transition_out_imm);
				return true;
			}
			finish();
			overridePendingTransition(0, R.anim.show_transition_out_imm);
			return true;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			CameraManager.get().setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			CameraManager.get().setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 这里初始化界面，调用初始化相机
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {

		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	private static ParsedResult parseResult(Result rawResult) {
		return ResultParser.parseResult(rawResult);
	}

	// 解析二维码
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		lastResult = rawResult;

		ResultHandler resultHandler = new ResultHandler(parseResult(rawResult));

		if (barcode == null) {

		} else {
			/**
			 * 播放jeep声音
			 */
			playBeepSoundAndVibrate();
			showResult(resultHandler.getDisplayContents().toString());
		}
	}

	// 初始化照相机，CaptureActivityHandler解码
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			return;
			// throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (CameraManager.get().isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			CameraManager.get().openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						characterSet, CameraManager.get());
			}
		} catch (Exception ioe) {

			// displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		// builder.setMessage(getString(R.string.msg_camera_framework_bug));
		// builder.setPositiveButton(R.string.confirm, new
		// FinishListener(this));
		// builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {

		// viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	/**
	 * 震动时间
	 */
	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		/**
		 * 播放声音
		 */
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		/**
		 * 震动
		 */
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/*
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	protected void onStop() {
		super.onStop();
		LoggerUtil.i("onStop");
		try {
			CameraManager.get().stopF();
			isLight = false;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 扫描二维码图片
	public String scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Bitmap scanBitmap;
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints).getText();

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return "";
	}

}