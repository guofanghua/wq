package com.wq.Adapter;

import java.util.ArrayList;


import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;

import com.wq.model.product;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class detailProductAdapter extends BaseAdapter {
	ArrayList<product> list = null;
	private Context context;
	private ViewHolder viewHolder;
	private FinalBitmap bitmapUtil;
	BitmapDisplayConfig config;

	public detailProductAdapter(Context context, ArrayList<product> list) {
		super();
		this.list = list;
		this.context = context;
		bitmapUtil = FinalBitmap.create(context);
		config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size() > 3 ? 3 : list.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.image_item, null);
			viewHolder.img = (ImageView) convertView
					.findViewById(R.id.img_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).getPicList().size() > 0) {
			bitmapUtil.display(viewHolder.img, list.get(position).getPicList()
					.get(0).getImageUrl(), config);
			viewHolder.img.setVisibility(View.VISIBLE);
		} else
			viewHolder.img.setVisibility(View.GONE);
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		ImageView img;
	}

}