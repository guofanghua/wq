package com.wq.Adapter;

import java.util.ArrayList;

import com.endure.wq.R;
import com.wq.model.dialogSelectModel;
import com.wq.model.mmAlertModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class alertGridAdapter extends BaseAdapter {
	Context context = null;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	ArrayList<mmAlertModel> mlist;

	public alertGridAdapter(Context context, ArrayList<mmAlertModel> mlist) {
		this.context = context;
		this.mlist = mlist;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mlist.size();
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
		// TODO Auto-generated method stub
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = (View) inflater.inflate(R.layout.alert_grid_item,
					null);
			viewHolder.img = (ImageView) convertView
					.findViewById(R.id.alert_grid_item_img);
			viewHolder.txt_title = (TextView) convertView
					.findViewById(R.id.alert_grid_item_txt_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}
		viewHolder.img.setBackgroundResource(mlist.get(position).getBgId());
		viewHolder.txt_title.setText(mlist.get(position).getText());
		return convertView;
	}

	private class ViewHolder {

		ImageView img;
		TextView txt_title;

	}
}
