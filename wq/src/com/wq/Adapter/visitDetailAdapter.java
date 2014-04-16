package com.wq.Adapter;

import java.util.ArrayList;
import java.util.Date;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.endure.wq.R;

import com.wq.model.visitDetail;

import com.wq.utils.dateUtil;

public class visitDetailAdapter extends BaseAdapter {

	private ArrayList<visitDetail> mlist = new ArrayList<visitDetail>();
	private Context mcontext;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private int timeFlag = 0;// 查询的时间类型，天，周，月，季度，年
	private long max = 0;

	public visitDetailAdapter(Context context, ArrayList<visitDetail> list,
			int timeFlag) {
		mcontext = context;
		mlist = list;
		inflater = LayoutInflater.from(context);
		this.timeFlag = timeFlag;

		// 获取到最大值
		try {
			max = Long.parseLong(mlist.get(0).getCount());
			for (int i = 1; i < mlist.size(); i++) {
				max = Math.max(max, Long.parseLong(mlist.get(i).getCount()));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}

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
		final visitDetail item = this.mlist.get(position);
		int proPrecent = (int) Math.ceil(Double.parseDouble(item.getCount()
				+ "")
				/ max * 100);
		if (proPrecent < 55)
			proPrecent = 55;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.visit_detail_list_item,
					null);
			viewHolder.progress = (ProgressBar) convertView
					.findViewById(R.id.visit_item_progress);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.visit_item_txt_time);
			viewHolder.txt_count = (TextView) convertView
					.findViewById(R.id.visit_item_txt_count);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.progress.setProgress(proPrecent);

		// 月，周不是的当天的数据，显示成灰色背景
		if ((timeFlag == 1 || timeFlag == 2) && position > 0) {
			viewHolder.progress.setProgressDrawable(mcontext.getResources()
					.getDrawable(R.drawable.progress_gray_bg));
			viewHolder.txt_time.setText(item.getTime());

		} else if ((timeFlag == 1 || timeFlag == 2)) {
			if (dateUtil.spanNowDay(item.getTime(), new Date()) != 0) {
				viewHolder.txt_time.setText(item.getTime());
			} else
				viewHolder.txt_time.setText(String.format(
						mcontext.getString(R.string.visit_detail_today),
						item.getTime()));
			viewHolder.progress.setProgressDrawable(mcontext.getResources()
					.getDrawable(R.drawable.progress_green_bg));
		} else {
			viewHolder.txt_time.setText(item.getTime());
			viewHolder.progress.setProgressDrawable(mcontext.getResources()
					.getDrawable(R.drawable.progress_green_bg));
		}
		viewHolder.txt_count.setText(String.format(
				mcontext.getString(R.string.visit_detail_format_count),
				item.getCount() + ""));

		return convertView;
	}

	private class ViewHolder {
		ProgressBar progress;
		TextView txt_time;
		TextView txt_count;

	}

}
