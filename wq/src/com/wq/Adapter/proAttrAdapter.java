package com.wq.Adapter;

import java.util.ArrayList;

import com.endure.wq.R;

import com.wq.model.proAttr;

import android.content.Context;

import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

public class proAttrAdapter extends BaseAdapter {
	private Context context;

	private ArrayList<proAttr> list;
	private LayoutInflater inflater;
	itemclick tClick;

	public proAttrAdapter(Context context, ArrayList<proAttr> list,
			boolean isEdit, itemclick click) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		this.tClick = click;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		proAttr item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pro_attr_show_item, null);
			holder = new ViewHolder();
			holder.txt_attrKey = (TextView) convertView
					.findViewById(R.id.txt_key);
			holder.edt_attrValue = (TextView) convertView
					.findViewById(R.id.edit_value);
			holder.splieView = (View) convertView.findViewById(R.id.split_line);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.txt_attrKey.setText(item.getAttrKey());
		
			holder.edt_attrValue.setText(item.getAttrValue());
	
		if (position == list.size() - 1) {
			holder.splieView.setVisibility(View.GONE);

		} else
			holder.splieView.setVisibility(View.VISIBLE);
		return convertView;
	}

	class ViewHolder {
		TextView txt_attrKey;
		TextView edt_attrValue;
		View splieView;

	}

	public interface itemclick {
		public void myclick(int position);

	}
}
