package com.wq.Adapter;

import java.util.ArrayList;

import com.endure.wq.R;

import com.wq.me.proCateListActivity;

import com.wq.model.productCategory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

public class proCateListAdapter extends BaseAdapter {
	ArrayList<productCategory> list;
	LayoutInflater inflater;
	public static int index = -1;
	private cateDelClick delclick;
	Context context;

	public proCateListAdapter(ArrayList<productCategory> list, Context context,
			cateDelClick cateDelClick) {
		super();
		this.list = list;
		inflater = LayoutInflater.from(context);
		this.delclick = cateDelClick;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pro_cate_item, null);
			holder = new ViewHolder();
			holder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			holder.img = (ImageView) convertView.findViewById(R.id.img_select);
			holder.btn_del = (Button) convertView.findViewById(R.id.btn_del);
			holder.txt_count = (TextView) convertView
					.findViewById(R.id.txt_count);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		holder.btn_del.setVisibility(proCateListActivity.IsEdit ? View.VISIBLE
				: View.GONE);
		holder.txt_name.setText(list.get(position).getName());
		holder.txt_count.setText(String.format(
				context.getString(R.string.me_string_pro_cate_str),
				(list.get(position).getCount() + "")));
		if (position == index) {
			holder.img.setVisibility(View.VISIBLE);
			holder.txt_name.setTextColor(Color.rgb(43, 156, 31));
			holder.txt_count.setTextColor(Color.rgb(43, 156, 31));
		} else {
			holder.img.setVisibility(View.GONE);
			holder.txt_name.setTextColor(Color.BLACK);
			holder.txt_count.setTextColor(Color.BLACK);
		}

		holder.btn_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				delclick.click(position);

				// TODO Auto-generated method stub

			}
		});
		return convertView;
	}

	private class ViewHolder {
		TextView txt_name;
		ImageView img;
		Button btn_del;
		TextView txt_count;

	}

	public interface cateDelClick {
		public void click(int position);
	}

}
