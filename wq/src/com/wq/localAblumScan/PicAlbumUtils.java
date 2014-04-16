package com.wq.localAblumScan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

public class PicAlbumUtils {

	// 设置获取图片的字段信息
	private static final String[] STORE_IMAGES = {
			MediaStore.Images.Media.DISPLAY_NAME, // 显示的名称
			MediaStore.Images.Media._ID, // id
			MediaStore.Images.Media.BUCKET_ID, // dir id 目录
			MediaStore.Images.Media.DATA };

	/**
	 * 从手机中读取相册目录列表，耗时操作
	 * 
	 * @param callBack
	 * @return
	 */
	public static void getAlbumListFromDevices(final Context ctx,
			final AlbCallBack callBack) {
		new Thread() {
			public void run() {
				Cursor cursor = MediaStore.Images.Media.query(
						ctx.getContentResolver(),
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						STORE_IMAGES);
				PicAlbumMdl mdl = null;
				Map<String, PicAlbumMdl> dirMapList = new HashMap<String, PicAlbumMdl>();
				while (cursor.moveToNext()) {
					String displayName = cursor.getString(0);
					String id = cursor.getString(1);// 缩略图ID
					String dir_id = cursor.getString(2);// 目录ID,主要判断是否同一目录
					String path = cursor.getString(3);// 图片路径
					System.out.println("PATH:" + path + "  display:"
							+ displayName);
					if (dirMapList.containsKey(dir_id)) {
						mdl = dirMapList.get(dir_id);
						PicItemMdl itemMdl = new PicItemMdl();
						itemMdl.setThumbId(Integer.parseInt(id));
						itemMdl.setDisplayName(displayName);
						itemMdl.setSelected(false);
						itemMdl.setPath(path);
						mdl.add(itemMdl);
					} else {
						PicAlbumMdl ambItem = new PicAlbumMdl();
						ambItem.setPathId(dir_id);
						ambItem.setAlubPic(Integer.parseInt(id));
						PicItemMdl itemMdl = new PicItemMdl();
						itemMdl.setThumbId(Integer.parseInt(id));
						itemMdl.setDisplayName(displayName);
						itemMdl.setPath(path);
						itemMdl.setSelected(false);
						ambItem.add(itemMdl);

						dirMapList.put(dir_id, ambItem);
					}
				}

				final List<PicAlbumMdl> resultList = new ArrayList<PicAlbumMdl>();
				for (String key : dirMapList.keySet()) {
					resultList.add(dirMapList.get(key));
				}
				if (callBack != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							callBack.callBackData(resultList);
						}
					});
				}

			};

		}.start();
	}

}
