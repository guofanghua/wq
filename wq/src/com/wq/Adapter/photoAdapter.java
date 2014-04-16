package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import com.endure.wq.R;
import com.wq.model.photoModel;


import android.content.Context;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

/**
 * 企业相册
 * */
public class photoAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<photoModel> list;
	private ViewHolder viewHolder;

	FinalBitmap bitmapUtils;
	// Handler delHandler;
	BitmapDisplayConfig displayConfig = null;
	LayoutParams params;

	public photoAdapter(Context context, ArrayList<photoModel> list) {
	
		bitmapUtils = FinalBitmap.create(context);
		displayConfig = new BitmapDisplayConfig();
		displayConfig.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		inflater = LayoutInflater.from(context);
		this.list = list;
		// this.delHandler = delHander;
		// params = new LayoutParams(DensityUtil.intScreenWidth(context) / 4,
		// DensityUtil.intScreenWidth(context) / 4);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
	}

	@Override
	public Object getItem(int item) {
		// TODO Auto-generated method stub
		return item;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		photoModel m = this.list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.photo_grid_item, null);
			viewHolder.img_pic = (ImageView) convertView
					.findViewById(R.id.company_photo_img_item);
			// viewHolder.img_pic.setLayoutParams(params);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// viewHolder.img_pic.setImageBitmap(BitmapFactory.decodeResource(
		// context.getResources(), R.drawable.demo1));
		if (m.getFlag() == 0) {
			bitmapUtils.display(viewHolder.img_pic, m.getImageUrl(),
					displayConfig);
		} else
			bitmapUtils.displayLocal(viewHolder.img_pic, m.getImageUrl(),
					false);

		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		ImageView img_pic;

	}
}
