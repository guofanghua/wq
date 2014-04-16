package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;

import com.wq.model.notice;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.ImageView;

import android.widget.TextView;

public class showNoticeListAdapter extends BaseAdapter {

	private ArrayList<notice> list = new ArrayList<notice>();
	private Context context;
	private ViewHolder viewHolder;

	FinalBitmap bitmapUtils;
	public FinalBitmap finalBitmap;
	BitmapDisplayConfig displayConfig = null;

	public itemclick myclick;

	public showNoticeListAdapter(ArrayList<notice> list, Context context,
			itemclick click) {
		super();
		this.list = list;
		this.context = context;
		bitmapUtils = FinalBitmap.create(context);

		this.myclick = click;

		displayConfig = new BitmapDisplayConfig();
		finalBitmap = FinalBitmap.create(context);

		// 优化性能，制作缩略图

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
		notice item = this.list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.show_notice_item, null);
			viewHolder.img = (ImageView) convertView.findViewById(R.id.img_top);
			viewHolder.txt_title = (TextView) convertView
					.findViewById(R.id.show_notice_list_item_txt_title);
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.show_notice_list_item_txt_content);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txt_title.setText(item.getTitle());
		viewHolder.txt_content.setText(item.getContent());
		if (item.getPic().size() > 0) {
			viewHolder.img.setVisibility(View.VISIBLE);
			finalBitmap.display(viewHolder.img, item.getPic().get(0)
					.getImageUrl(), displayConfig);
		} else
			viewHolder.img.setVisibility(View.GONE);
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		ImageView img;
		TextView txt_title;
		TextView txt_content;

	}

	public interface itemclick {
		public void titleClick(int position);

		public void gridClick(int position);
	}

}
