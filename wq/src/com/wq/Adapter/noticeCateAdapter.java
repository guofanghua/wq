package com.wq.Adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.model.visitDetail;
import com.wq.utils.dateUtil;

public class noticeCateAdapter extends BaseAdapter {

	private ArrayList<Map<String, String>> mlist = new ArrayList<Map<String, String>>();
	private Context mcontext;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private int timeFlag = 0;// 查询的时间类型，天，周，月，季度，年
	private long max = 0;
	private String cateName = "";

	public noticeCateAdapter(Context context,
			ArrayList<Map<String, String>> list, String cateName) {
		mcontext = context;
		mlist = list;
		inflater = LayoutInflater.from(context);
		this.cateName = cateName;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
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
		final Map<String, String> item = this.mlist.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.notice_cate_list_item, null);
			viewHolder.img_select = (ImageView) convertView
					.findViewById(R.id.img_select);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			viewHolder.split_line = (View) convertView
					.findViewById(R.id.split_line);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (cateName.equals(item.get("name"))) {
			viewHolder.img_select.setVisibility(View.VISIBLE);
		} else
			viewHolder.img_select.setVisibility(View.GONE);
		viewHolder.txt_name.setText(item.get("name"));
		if (mlist.size() - 1 == position)
			viewHolder.split_line.setVisibility(View.GONE);
		else
			viewHolder.split_line.setVisibility(View.VISIBLE);

		return convertView;
	}

	private class ViewHolder {

		ImageView img_select;
		TextView txt_name;
		View split_line;

	}

}
