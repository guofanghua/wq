package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.model.User;
import com.wq.model.templateModel;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class templateListAdapter extends BaseAdapter {
	ArrayList<templateModel> list;

	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private FinalBitmap finalBitmap;
	private BitmapDisplayConfig config;

	public templateListAdapter(Context context, ArrayList<templateModel> list) {

		this.list = list;
		inflater = LayoutInflater.from(context);
		finalBitmap = FinalBitmap.create(context);
		config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt_vertical));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));

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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.template_grid_item, null);
			viewHolder.img_pre = (ImageView) convertView
					.findViewById(R.id.temp_grid_item_img_pre);
			viewHolder.img_select = (ImageView) convertView
					.findViewById(R.id.temp_grid_item_img_select);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		finalBitmap.display(viewHolder.img_pre, list.get(position)
				.getPreviewImg(), config);
		if (list.get(position).getTemplateId().equals(User.templateId)) {
			viewHolder.img_select.setVisibility(View.VISIBLE);
		} else
			viewHolder.img_select.setVisibility(View.GONE);
		if (position == 0 && TextUtils.isEmpty(User.templateId)) {
			viewHolder.img_select.setVisibility(View.VISIBLE);
		}
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		ImageView img_pre;// 分面
		ImageView img_select;// 是否已经选择
	}

}
