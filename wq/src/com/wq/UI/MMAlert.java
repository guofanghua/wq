package com.wq.UI;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.endure.wq.R;
import com.wq.Adapter.alertGridAdapter;
import com.wq.Adapter.mmAlertAdapter;
import com.wq.PicCheck.PicCheckMainActivity;
import com.wq.localAblumScan.ablum_detail_activity;
import com.wq.localAblumScan.ablum_main_activity;
import com.wq.model.dialogSelectModel;
import com.wq.model.mmAlertModel;
import com.wq.utils.LoggerUtil;
import com.wq.utils.SDCardUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public final class MMAlert {
	// private static alertItemclickListener itemclickListener;
	public static final String IMAGE_PATH = "My_weixin";
	public static String localTempImageFileName = "";
	public static final int FLAG_CHOOSE_IMG = 5;
	public static final int FLAG_CHOOSE_PHONE = 6;
	public static final int FLAG_MODIFY_FINISH = 7;

	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();
	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL,
			"images/screenshots");

	public interface OnAlertSelectId {
		void onClick(int whichButton);
	}

	public MMAlert() {
	}

	public static Dialog showAlertListView(final Context context,
			final ArrayList<mmAlertModel> list, boolean isCancel,
			final onitemClick click) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		mmAlertAdapter adapter = new mmAlertAdapter(context, list, isCancel);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_pic_select2, null);
		ListView listView = (ListView) layout.findViewById(R.id.listview);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				dlg.dismiss();
				click.onItemClick(position, list.get(position));
				// TODO Auto-generated method stub
			}
		});
		listView.setAdapter(adapter);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom

		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * @param context
	 * 
	 * @param relpaceFlag
	 *            true标示第二张图片将替换第一张图片
	 * @return
	 */
	public static Dialog showAlert(final Context context, boolean relpaceFlag,
			int picNumber) {
		return showAlert(context, relpaceFlag, false, false, picNumber);

	}

	public static Dialog showAlert(final Context context, boolean relpaceFlag,
			boolean clipactivity2) {

		return showAlert(context, relpaceFlag, false, clipactivity2, 1);

	}

	// 选择图片
	public static Dialog showAlert(final Context context, boolean relpaceFlag,
			boolean IsRect, boolean IsRect1, final int picNumber) {
		ClipActivity2.isRect = IsRect1;
		ClipActivity.replaceFlag = relpaceFlag;

		// itemclickListener = tmpItemclickListener;
		// String cancel = context.getString(R.string.app_cancel);
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_pic_select, null);
		Button btn_takePhonto = (Button) layout
				.findViewById(R.id.popup_btn_takePhoto);
		btn_takePhonto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!SDCardUtils.ExistSDCard()) {
					Toast.makeText(context, "请插入sd卡", Toast.LENGTH_SHORT)
							.show();
					dlg.dismiss();
					return;
				}
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					try {
						localTempImageFileName = "";
						localTempImageFileName = String.valueOf((new Date())
								.getTime()) + ".jpg";
						File filePath = FILE_PIC_SCREENSHOT;
						if (!filePath.exists()) {
							filePath.mkdirs();
						}
						dlg.dismiss();
						Intent intent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File f = new File(filePath, localTempImageFileName);
						// localTempImgDir和localTempImageFileName是自己定义的名字

						Uri u = Uri.fromFile(f);
						intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
						((Activity) context).startActivityForResult(intent,
								FLAG_CHOOSE_PHONE);

					} catch (ActivityNotFoundException e) {
						//
					}
				}
			}
		});
		Button btn_pickPhonto = (Button) layout
				.findViewById(R.id.popup_btn_pickPhoto);
		btn_pickPhonto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!SDCardUtils.ExistSDCard()) {
					Toast.makeText(context, "请插入sd卡", Toast.LENGTH_SHORT)
							.show();
					dlg.dismiss();
					return;
				}
				dlg.dismiss();
				// Intent intent = new Intent(context,
				// PicCheckMainActivity.class);
				// intent.setAction(Intent.ACTION_PICK);
				// intent.setType("image/*");
				// ((Activity) context).startActivityForResult(intent,
				// FLAG_CHOOSE_IMG);

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				((Activity) context).startActivityForResult(intent,
						FLAG_CHOOSE_IMG);

				// TODO Auto-generated method stub

			}
		});
		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();

				// TODO Auto-generated method stub

			}
		});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	// 可以单张图片截取
	public static Dialog showAlertClip(final Context context,
			boolean relpaceFlag, boolean isRect) {

		ClipActivity.replaceFlag = relpaceFlag;

		// itemclickListener = tmpItemclickListener;
		// String cancel = context.getString(R.string.app_cancel);
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_pic_select, null);
		Button btn_takePhonto = (Button) layout
				.findViewById(R.id.popup_btn_takePhoto);
		btn_takePhonto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dlg.dismiss();
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					try {
						localTempImageFileName = "";
						localTempImageFileName = String.valueOf((new Date())
								.getTime()) + ".png";
						File filePath = FILE_PIC_SCREENSHOT;
						if (!filePath.exists()) {
							filePath.mkdirs();
						}
						Intent intent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File f = new File(filePath, localTempImageFileName);
						// localTempImgDir和localTempImageFileName是自己定义的名字

						Uri u = Uri.fromFile(f);
						intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
						((Activity) context).startActivityForResult(intent,
								FLAG_CHOOSE_PHONE);
					} catch (ActivityNotFoundException e) {
						//
					}
				}
			}
		});
		Button btn_pickPhonto = (Button) layout
				.findViewById(R.id.popup_btn_pickPhoto);
		btn_pickPhonto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				((Activity) context).startActivityForResult(intent,
						FLAG_CHOOSE_IMG);

				// TODO Auto-generated method stub

			}
		});
		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();

				// TODO Auto-generated method stub

			}
		});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	// 选择图片
	public static Dialog showAlert(final Context context,
			final alertItemclickListener tmpItemclickListener) {
		// itemclickListener = tmpItemclickListener;
		// String cancel = context.getString(R.string.app_cancel);
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_pic_select, null);
		Button btn_takePhonto = (Button) layout
				.findViewById(R.id.popup_btn_takePhoto);
		btn_takePhonto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				tmpItemclickListener.takePhotoClick();
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					localTempImageFileName = "";
					localTempImageFileName = String.valueOf((new Date())
							.getTime() + ".png");
					File filePath = FILE_PIC_SCREENSHOT;
					if (!filePath.exists()) {
						filePath.mkdirs();
					}
					Intent intent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(filePath, localTempImageFileName);
					Uri u = Uri.fromFile(f);
					intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
					((Activity) context).startActivityForResult(intent,
							FLAG_CHOOSE_PHONE);
				}
				// TODO Auto-generated method stub

			}
		});
		Button btn_pickPhonto = (Button) layout
				.findViewById(R.id.popup_btn_pickPhoto);
		btn_pickPhonto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				tmpItemclickListener.pickPhotoClick();
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				((Activity) context).startActivityForResult(intent,
						FLAG_CHOOSE_IMG);
				// TODO Auto-generated method stub

			}
		});
		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				tmpItemclickListener.cancleClick();
				// TODO Auto-generated method stub

			}
		});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	// 注销账号
	public static Dialog dialogQuit(final Context context,
			final alertItemclickListener click) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_pic_select, null);
		Button btn_quit = (Button) layout
				.findViewById(R.id.popup_btn_pickPhoto);
		btn_quit.setText(context.getString(R.string.me_string_set_quit_user));
		btn_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dlg.dismiss();
				click.takePhotoClick();

			}
		});

		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();

				// TODO Auto-generated method stub

			}
		});
		Button btn_takePhonto = (Button) layout
				.findViewById(R.id.popup_btn_takePhoto);
		btn_takePhonto.setVisibility(View.GONE);

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	// 伙伴界面弹出框
	public static Dialog dialogPart(final Context context, String title,
			final alertItemclickListener click) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_pic_select, null);
		Button btn_comfirm = (Button) layout
				.findViewById(R.id.popup_btn_pickPhoto);
		btn_comfirm.setText(title);
		btn_comfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				click.takePhotoClick();

			}
		});

		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();

				// TODO Auto-generated method stub

			}
		});
		Button btn_takePhonto = (Button) layout
				.findViewById(R.id.popup_btn_takePhoto);
		btn_takePhonto.setVisibility(View.GONE);

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

//	// 保存二维码
//	public static Dialog dialogewm(final Context context, String buttonText1,
//			String buttonText2, String buttonText3,
//			final alertItemclickListener click) {
//		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
//		LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		LinearLayout layout = (LinearLayout) inflater.inflate(
//				R.layout.alert_pic_select, null);
//		Button btn_pickPhonto = (Button) layout
//				.findViewById(R.id.popup_btn_takePhoto);
//		btn_pickPhonto.setText(buttonText1);
//		btn_pickPhonto.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dlg.dismiss();
//				click.takePhotoClick();
//			}
//		});
//		Button btn_comfirm = (Button) layout
//				.findViewById(R.id.popup_btn_pickPhoto);
//		btn_comfirm.setText(buttonText2);
//		btn_comfirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dlg.dismiss();
//				click.pickPhotoClick();
//
//			}
//		});
//
//		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);
//		btn_cancel.setText(buttonText3);
//		btn_cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dlg.dismiss();
//
//				// TODO Auto-generated method stub
//
//			}
//		});
//		final int cFullFillWidth = 10000;
//		layout.setMinimumWidth(cFullFillWidth);
//		// 按钮的执行动作
//		// set a large value put it in bottom
//		Window w = dlg.getWindow();
//		// 获得对应的window的属性
//		WindowManager.LayoutParams lp = w.getAttributes();
//		lp.x = 0;
//		final int cMakeBottom = -1000;
//		lp.y = cMakeBottom;
//		lp.gravity = Gravity.BOTTOM;
//		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
//		dlg.onWindowAttributesChanged(lp);// 重新设置属性
//		dlg.setCanceledOnTouchOutside(true);
//		dlg.setContentView(layout);
//		dlg.show();
//
//		return dlg;
//	}

	// 关注和取消关注
	public static Dialog dialogAttention(final Context context,
			String isAttention, final alertItemclickListener click) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_pic_select, null);
		Button btn_Att = (Button) layout.findViewById(R.id.popup_btn_pickPhoto);

		btn_Att.setText(isAttention.equals("1") ? context
				.getString(R.string.part_string_cancel_gz) : context
				.getString(R.string.part_string_gz));
		btn_Att.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dlg.dismiss();
				click.takePhotoClick();

			}
		});

		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();

				// TODO Auto-generated method stub

			}
		});
		Button btn_takePhonto = (Button) layout
				.findViewById(R.id.popup_btn_takePhoto);
		btn_takePhonto.setVisibility(View.GONE);

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	// 图片浏览界面的分享和删除图片
	public static Dialog shareSelectDialog(final Context context,
			boolean shareFlag, boolean delFlag, final Handler handler) {
		return shareSelectDialog(context, shareFlag, delFlag, true, handler);
	}

	// 图片浏览界面的分享和删除图片和保存到本地
	public static Dialog shareSelectDialog(Context context,
			final boolean shareFlag, boolean delFlag, boolean bsaveLocal,
			final Handler handler) {
		LoggerUtil.i(shareFlag + "," + delFlag + "," + bsaveLocal);
		if (!delFlag && !shareFlag && !bsaveLocal) {
			return null;
		}

		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_ablum_select, null);
		Button btn_qyq = (Button) layout.findViewById(R.id.popup_btn_qyq);
		if (shareFlag) {
			btn_qyq.setVisibility(View.VISIBLE);
			btn_qyq.setText(context.getString(R.string.ablum_qyq));
		} else
			btn_qyq.setVisibility(View.GONE);

		btn_qyq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dlg.dismiss();

				Message msg = handler.obtainMessage();
				// 分享到朋友圈

				msg.what = 1;

				msg.sendToTarget();

			}
		});
		// 保存图片
		Button btn_save = (Button) layout.findViewById(R.id.popup_btn_save);

		if (bsaveLocal) {
			btn_save.setVisibility(View.VISIBLE);

		} else
			btn_save.setVisibility(View.GONE);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dlg.dismiss();

				Message msg = handler.obtainMessage();
				// 保存图片到本地

				msg.what = 3;

				msg.sendToTarget();

			}
		});
		// 删除
		Button btn_del = (Button) layout.findViewById(R.id.popup_btn_del);
		if (delFlag)
			btn_del.setVisibility(View.VISIBLE);
		else
			btn_del.setVisibility(View.GONE);
		btn_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				// 删除
				msg.what = 2;
				msg.sendToTarget();
				dlg.dismiss();
			}
		});
		Button btn_cancel = (Button) layout.findViewById(R.id.popup_btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();

				// TODO Auto-generated method stub

			}
		});

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	/**
	 * 浏览器图片分享设置
	 * */
	public static Dialog shareWebViewDialog(Context context,
			final ArrayList<mmAlertModel> list, final onitemClick click) {

		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_grid_select, null);
		GridView gridview = (GridView) layout.findViewById(R.id.alert_grid);
		alertGridAdapter adapter = new alertGridAdapter(context, list);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dlg.dismiss();
				// TODO Auto-generated method stub
				click.onItemClick(position, list.get(position));

			}
		});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// 按钮的执行动作
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		// 获得对应的window的属性
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		// lp.width=WindowManager.LayoutParams.FILL_PARENT;
		dlg.onWindowAttributesChanged(lp);// 重新设置属性
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	public interface alertItemclickListener {
		void takePhotoClick();

		void pickPhotoClick();

		void cancleClick();

	}

	public interface onitemClick {
		void onItemClick(int position, mmAlertModel item);
	}
}
