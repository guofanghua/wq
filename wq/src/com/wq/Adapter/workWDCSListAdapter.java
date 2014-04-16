package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import com.endure.wq.R;

import com.wq.model.partComModel;
import com.wq.model.workRepModel;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**抄送给我的*/
public class workWDCSListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<workRepModel> list;
	private ViewHolder viewHolder;

	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	private String[] typeArr;
	private String[] statusArr;

	// Handler handler;

	public workWDCSListAdapter(Context context, ArrayList<workRepModel> list) {

		bitmapUtiles = FinalBitmap.create(context);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setIsRoundCore(true);
		inflater = LayoutInflater.from(context);
		typeArr = context.getResources().getStringArray(R.array.work_rep_arr);
		statusArr = context.getResources().getStringArray(
				R.array.work_rep_status);
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
		final workRepModel item = this.list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.work_wdsh_list_item, null);
			viewHolder.txt_type = (TextView) convertView
					.findViewById(R.id.txt_type);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			viewHolder.txt_status = (TextView) convertView
					.findViewById(R.id.txt_status);
			viewHolder.img_spline = (TextView) convertView
					.findViewById(R.id.spline);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.img_logo);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		int type = Integer.parseInt(item.getType());
		if (type <= typeArr.length)
			viewHolder.txt_type.setText(typeArr[type]);

		viewHolder.txt_time.setText("[ " + item.getTime() + " ]");
		int status = Integer.parseInt(item.getStatus());
		if (status <= statusArr.length)
			viewHolder.txt_status.setText(statusArr[status]);
		viewHolder.txt_name.setText(item.getName());
		bitmapUtiles.display(viewHolder.img_logo, item.getIcon(), config, true);
		if (status == 0)
			viewHolder.txt_status.setTextColor(Color.RED);
		else
			viewHolder.txt_status.setTextColor(Color.GRAY);

		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		TextView txt_type;
		TextView txt_time;
		TextView txt_status;
		TextView img_spline;
		ImageView img_logo;
		TextView txt_name;

	}
}
