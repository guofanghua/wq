package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;

import com.wq.model.leaveMessage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class findEcTipListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<leaveMessage> mlist;
	private ViewHolder viewHolder;

	FinalBitmap finalBitmap;
	// Handler delHandler;
	BitmapDisplayConfig displayConfig = null;
	LayoutParams params;
	Context mContext;

	public findEcTipListAdapter(Context context, ArrayList<leaveMessage> list) {
		mContext = context;
		mlist = list;
		inflater = LayoutInflater.from(context);
		finalBitmap = FinalBitmap.create(context);
		displayConfig = new BitmapDisplayConfig();
		displayConfig.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setIsRoundCore(true);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
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
		// TODO Auto-generated method stub
		final leaveMessage msg = mlist.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.find_ec_tip_list_item, null);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.img_logo);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			// 留言人

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		finalBitmap.display(viewHolder.img_logo, msg.getLogoUrl(),
				displayConfig, true);
		viewHolder.txt_name.setText(msg.getVqh());
		if (TextUtils.isEmpty(msg.getContent())) {
			viewHolder.txt_content.setText(mContext
					.getString(R.string.find_tip_zan_title));
		} else
			viewHolder.txt_content.setText(msg.getContent());
		viewHolder.txt_time.setText(msg.getTime());
		return convertView;
	}

	private class ViewHolder {
		ImageView img_logo;
		TextView txt_name;
		TextView txt_content;
		TextView txt_time;

	}
}
