package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;

import com.wq.model.partComModel;
import com.wq.utils.CommonUtil;

import android.content.Context;
import android.graphics.BitmapFactory;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

public class partListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<partComModel> list;
	private ViewHolder viewHolder;
	private Context mContext;

	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();

	public partListAdapter(Context context, ArrayList<partComModel> list) {

		bitmapUtiles = FinalBitmap.create(context);
		inflater = LayoutInflater.from(context);
		config.setIsRoundCore(true);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		this.list = list;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (list.size() > 0)
			return this.list.size() + 3;
		else
			return this.list.size() + 2;
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
		if (position == 0 || position == 1) {
			View v = inflater.inflate(R.layout.part_list_item1, null);
			TextView txt_tip_count = (TextView) v.findViewById(R.id.txt_title);
			if (position == 0) {
				txt_tip_count.setText(mContext
						.getString(R.string.part_string_sys_part));
			} else if (position == 1) {
				txt_tip_count.setText(mContext
						.getString(R.string.part_string_pyp_part));
			}
			v.setTag("1");
			return v;
		} else if (position == 2) {
			View v = inflater
					.inflate(R.layout.part_main_list_parent_view, null);

			v.setTag("1");
			return v;
		} else {
			final partComModel item = this.list.get(position - 3);
			if (convertView == null
					|| String.valueOf(convertView.getTag()).equals("1")) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.part_list_item1, null);
				viewHolder.txt_name = (TextView) convertView
						.findViewById(R.id.part_item_txt_wqh);
				viewHolder.txt_intro = (TextView) convertView
						.findViewById(R.id.part_item_txt_intro);
				viewHolder.txt_lxr = (TextView) convertView
						.findViewById(R.id.part_item_txt_lxr);
				viewHolder.txt_zw = (TextView) convertView
						.findViewById(R.id.part_item_txt_zw);
				viewHolder.img_logo = (ImageView) convertView
						.findViewById(R.id.part_item_img);

				// viewHolder.btn_gz = (Button) convertView
				// .findViewById(R.id.part_btn_gz);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.img_logo.setTag("1");
			if (TextUtils.isEmpty(item.getName()))
				viewHolder.txt_name.setText(CommonUtil.ToDBC(item.getVqh()));
			else
				viewHolder.txt_name.setText(CommonUtil.ToDBC(item.getName()));
			viewHolder.txt_intro.setText(CommonUtil.ToDBC(item.getCommodity()));
			viewHolder.txt_lxr.setText(item.getContactName());
			viewHolder.txt_zw.setText(item.getOccupation());
			bitmapUtiles.display(viewHolder.img_logo, item.getIcon(), config,
					true);

			// TODO Auto-generated method stub
			return convertView;
		}

	}

	private class ViewHolder {
		TextView txt_name;
		TextView txt_lxr;
		TextView txt_zw;
		ImageView img_logo;
		TextView txt_intro;
		// Button btn_gz;// 关注

	}
}
