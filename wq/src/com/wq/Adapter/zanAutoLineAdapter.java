package com.wq.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.custom.vg.list.CustomAdapter;
import com.endure.wq.R;
import com.wq.model.leaveMessage;
import com.wq.utils.DensityUtil;

public class zanAutoLineAdapter extends CustomAdapter {

	private ArrayList<leaveMessage> list;
	private Context con;
	private LayoutInflater inflater;

	public zanAutoLineAdapter(Context context, ArrayList<leaveMessage> list) {
		this.con = context;
		this.list = list;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {
		if (list.size() > 0)
			return list.size() + 1;
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (position == 0) {
			LayoutParams params = new LayoutParams(DensityUtil.dip2px(con, 25),
					DensityUtil.dip2px(con, 25));
			params.leftMargin = 5;
			params.rightMargin = 5;
			ImageView img = new ImageView(con);
			img.setBackgroundResource(R.drawable.zan_nomal);
			img.setLayoutParams(params);
			img.setScaleType(ScaleType.FIT_XY);
			img.setTag("1");
			return img;
		} else {
			ViewHolder vh = null;
			leaveMessage item = list.get(position - 1);
			if (convertView == null
					|| String.valueOf(convertView.getTag()).equals("1")) {

				vh = new ViewHolder();
				convertView = inflater.inflate(R.layout.zan_auto_line_item,
						null);

				vh.tv = (TextView) convertView.findViewById(R.id.txt_vqh);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.tv.setText(item.getVqh());

			return convertView;
		}
	}

	public class ViewHolder {
		public TextView tv;
	}

}