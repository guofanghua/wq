package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;

import com.wq.model.partComModel;

import com.wq.utils.LoggerUtil;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class partTipSortAdapter extends BaseAdapter {
	private ArrayList<partComModel> list = new ArrayList<partComModel>();

	private ViewHolder viewHolder;

	private LayoutInflater inflater;
	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	public TextView txt_tip = null;

	public partTipSortAdapter(Context context, ArrayList<partComModel> list) {
		this.list = list;

		bitmapUtiles = FinalBitmap.create(context);
		inflater = LayoutInflater.from(context);
		config.setIsRoundCore(true);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int object) {
		// TODO Auto-generated method stub
		return object;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final partComModel item = this.list.get(position);
		viewHolder = new ViewHolder();

		if (convertView == null
				|| String.valueOf(convertView.getTag()).equals("1")) {

			convertView = inflater.inflate(R.layout.part_sort_list_item, null);

			viewHolder.txt_lxr = (TextView) convertView
					.findViewById(R.id.txt_lxr);
			viewHolder.txt_zw = (TextView) convertView
					.findViewById(R.id.txt_zw);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.part_item_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.img_logo.setTag("1");
		if (TextUtils.isEmpty(item.getContactName())) {
			viewHolder.txt_lxr.setText(item.getName());
		} else
			viewHolder.txt_lxr.setText(item.getContactName());
		viewHolder.txt_zw.setText(item.getOccupation());
		bitmapUtiles.display(viewHolder.img_logo, item.getIcon(), config, true);
		return convertView;

		// TODO Auto-generated method stub

	}

	private class ViewHolder {

		TextView txt_lxr;
		TextView txt_zw;
		ImageView img_logo;


	}

}
