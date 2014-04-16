package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import com.endure.wq.R;
import com.wq.model.partComModel;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class partSearchListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<partComModel> list;
	private ViewHolder viewHolder;

	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();

	// Handler handler;

	public partSearchListAdapter(Context context, ArrayList<partComModel> list) {

		bitmapUtiles = FinalBitmap.create(context);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setIsRoundCore(true);
		inflater = LayoutInflater.from(context);
		this.list = list;
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
		final partComModel item = this.list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
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

		viewHolder.txt_lxr.setText(item.getContactName());
		viewHolder.txt_zw.setText(item.getOccupation());
		bitmapUtiles.display(viewHolder.img_logo, list.get(position).getIcon(),
				config, true);

		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		TextView txt_lxr;
		TextView txt_zw;
		ImageView img_logo;


	}
}
