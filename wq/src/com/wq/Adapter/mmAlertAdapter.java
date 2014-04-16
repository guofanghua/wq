package com.wq.Adapter;

import java.util.ArrayList;

import com.endure.wq.R;
import com.wq.model.mmAlertModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class mmAlertAdapter extends BaseAdapter {
	Context mContext;
	ArrayList<mmAlertModel> arr;
	boolean isCancel;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	

	public mmAlertAdapter(Context context, ArrayList<mmAlertModel> arr,
			boolean isCancel) {
		this.mContext = context;
		this.arr = arr;
		this.isCancel = isCancel;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.size();
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
		mmAlertModel item = arr.get(position);
		// TODO Auto-generated method stub
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.alert_pic_select_item, null);
			viewHolder.btnButton = (TextView) convertView
					.findViewById(R.id.mmalert_item);
			viewHolder.layout=(LinearLayout)convertView.findViewById(R.id.mmalert_item_layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.btnButton.setText(item.getText());
	
		if (isCancel && position == arr.size() - 1) {
			
			viewHolder.btnButton
					.setBackgroundResource(R.drawable.btn_style_alert_dialog_cancel);
		} else {
			viewHolder.btnButton
					.setBackgroundResource(R.drawable.btn_style_alert_dialog_special);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView btnButton;
		LinearLayout layout;

	}

}
