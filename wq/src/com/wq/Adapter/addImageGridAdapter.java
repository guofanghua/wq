package com.wq.Adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.custom.vg.list.CustomAdapter;
import com.endure.wq.R;
import com.wq.me.ablumAddActivity;
import com.wq.model.photoModel;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.DensityUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class addImageGridAdapter extends BaseAdapter {
	Context context;
	ArrayList<photoModel> listpic = new ArrayList<photoModel>();
	private ViewHolder viewHolder;
	private FinalBitmap bitmapUtils;
	BitmapDisplayConfig config = null;
	Handler handler = null;
	//ArrayList<Map<String, WeakReference<Bitmap>>> bitmpalist = new ArrayList<Map<String, WeakReference<Bitmap>>>();

	public addImageGridAdapter(Context context, ArrayList<photoModel> listpic,
			ArrayList<Map<String, WeakReference<Bitmap>>> bitmapalist,
			Handler imgClickHandler) {
		super();
		this.context = context;
		this.listpic = listpic;
		this.handler = imgClickHandler;
		this.bitmapUtils = FinalBitmap.create(context);
		this.config = new BitmapDisplayConfig();
		this.config.setBitmapHeight(DensityUtil.dip2px(context, 90));
		this.config.setBitmapWidth(DensityUtil.dip2px(context, 90));
		config.setLoadingBitmap(BitmapAivenUtils.readBitMap(context,
				R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapAivenUtils.readBitMap(context,
				R.drawable.add_prompt));
		//this.bitmpalist = bitmapalist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (listpic.size() < ablumAddActivity.Max_SIZE)
			return listpic.size() + 1;
		else {
			return listpic.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (listpic.size() == 6 || position < getCount() - 1) {

			photoModel item = this.listpic.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.add_hor_img_item, null);
				viewHolder.img = (ImageView) convertView
						.findViewById(R.id.img_item);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (item.getFlag() == 0)
				bitmapUtils.display(viewHolder.img, item.getImageUrl(), config);
			else
				try {
//					boolean isFlag = false;
//					for (Map<String, WeakReference<Bitmap>> bitmpatem : bitmpalist) {
//						if (bitmpatem.get(item.getImageUrl()) != null
//								&& bitmpatem.get(item.getImageUrl()).get() != null) {
//							viewHolder.img.setImageBitmap(bitmpatem.get(
//									item.getImageUrl()).get());
//							isFlag = true;
//							break;
//						}
//					}
//					if (!isFlag) {
						Map<String, WeakReference<Bitmap>> map = new HashMap<String, WeakReference<Bitmap>>();

						WeakReference<Bitmap> tmpReference = new WeakReference<Bitmap>(
								BitmapAivenUtils.readBitmap(context,
										item.getImageUrl()));
						map.put(item.getImageUrl(), tmpReference);
						viewHolder.img.setImageBitmap(map.get(
								item.getImageUrl()).get());
				//	}
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}

			viewHolder.img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (handler == null)
						return;
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.arg1 = position;
					msg.sendToTarget();

					// TODO Auto-generated method stub

				}
			});
			return convertView;
		} else {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.add_hor_img_item, null);
				viewHolder.img = (ImageView) convertView
						.findViewById(R.id.img_item);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.img.setBackgroundResource(R.drawable.bt_add_pic);
			viewHolder.img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (handler == null)
						return;
					Message msg = handler.obtainMessage();
					msg.what = 2;
					msg.sendToTarget();
					// TODO Auto-generated method stub

				}
			});

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView img;
	}

}
