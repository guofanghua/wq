package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.custom.vg.list.CustomAdapter;
import com.endure.wq.R;
import com.wq.model.partComModel;
import com.wq.utils.LoggerUtil;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class retationLogAdapter extends CustomAdapter {
	private ArrayList<partComModel> list = new ArrayList<partComModel>();
	private ViewHolder viewHolder;
	private LayoutInflater inflater;
	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	public TextView txt_tip = null;

	public retationLogAdapter(Context context, ArrayList<partComModel> list) {
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
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.retation_grid_item, null);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.grid_item_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		LoggerUtil.i("adapter" + item.getIcon());
		bitmapUtiles.display(viewHolder.img_logo, item.getIcon(), config, true);
		return convertView;
		// TODO Auto-generated method stub
	}

	private class ViewHolder {
		ImageView img_logo;
	}

}
