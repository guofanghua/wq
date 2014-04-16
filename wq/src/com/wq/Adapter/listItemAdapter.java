package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.model.listItemModelBase;
import com.wq.utils.DensityUtil;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/** 自定义列表界面 */
public class listItemAdapter extends BaseAdapter {

	private Context context;
	ArrayList<listItemModelBase> list = new ArrayList<listItemModelBase>();
	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	private LayoutInflater inflater;
	private ViewHolder viewHolder;

	public listItemAdapter(Context context, ArrayList<listItemModelBase> list) {
		super();
		this.context = context;
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
		viewHolder = new ViewHolder();
		listItemModelBase item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_model_item, null);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.img_item);
			viewHolder.txt_title = (TextView) convertView
					.findViewById(R.id.txt_title);
			viewHolder.txt_title1 = (TextView) convertView
					.findViewById(R.id.txt_title1);
			viewHolder.layout_main = (LinearLayout) convertView
					.findViewById(R.id.layout_main);
			viewHolder.img_arrow = (ImageView) convertView
					.findViewById(R.id.img_arr);
			viewHolder.view_spline = (LinearLayout) convertView
					.findViewById(R.id.view_splite);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 分割

		// 图片
		if (item.getImageId() > 0) {
			viewHolder.img_logo.setVisibility(View.VISIBLE);
			viewHolder.img_logo.setBackgroundResource(item.getImageId());

		} else {
			viewHolder.img_logo.setVisibility(View.GONE);

		}
		viewHolder.view_spline
				.setPadding(
						DensityUtil.dip2px(context, item.getSplitMarginLeft()),
						0, 0, 0);
		// 标题
		// LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// param.leftMargin = DensityUtil.dip2px(context,
		// item.getTittleMarginLeft());
		viewHolder.txt_title.setPadding(
				DensityUtil.dip2px(context, item.getTittleMarginLeft()), 0, 0,
				0);
		viewHolder.txt_title.setText(item.getTittle());
		viewHolder.txt_title.setTextColor(item.getTitleTextColor());

		// 标题1
		viewHolder.txt_title1.setText(item.getTittle1());
		viewHolder.txt_title1.setTextColor(item.getTitle1TextColor());
		viewHolder.img_arrow.setVisibility(item.isArrawIsShow() ? View.VISIBLE
				: View.GONE);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				DensityUtil.dip2px(context, item.getItemHeight()));
		viewHolder.layout_main.setLayoutParams(params);
		viewHolder.layout_main.setBackgroundResource(item.getBackground());
		return convertView;
	}

	private class ViewHolder {

		ImageView img_logo;
		TextView txt_title;
		TextView txt_title1;
		ImageView img_arrow;
		LinearLayout view_spline;
		LinearLayout layout_main;

	}
}
