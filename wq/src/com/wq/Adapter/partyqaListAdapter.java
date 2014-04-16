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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class partyqaListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<partComModel> list;
	private ViewHolder viewHolder;

	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();

	// Handler handler;

	public partyqaListAdapter(Context context, ArrayList<partComModel> list) {

		bitmapUtiles = FinalBitmap.create(context);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		inflater = LayoutInflater.from(context);
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
		final partComModel item = this.list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.part_yqa_list_item, null);
			viewHolder.txt_name = (TextView) convertView
					.findViewById(R.id.part_item_txt_wqh);
			viewHolder.txt_commodity = (TextView) convertView
					.findViewById(R.id.part_item_txt_wqw);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.part_item_img);
			viewHolder.btn_gz = (Button) convertView
					.findViewById(R.id.part_btn_gz);
			viewHolder.btn_gz.setVisibility(View.GONE);
			viewHolder.rela_main = (RelativeLayout) convertView
					.findViewById(R.id.rela_main);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (TextUtils.isEmpty(item.getName())) {
			viewHolder.txt_name.setText(CommonUtil.ToDBC(item.getVqh()));
		} else
			viewHolder.txt_name
					.setText(CommonUtil.ToDBC(item.getContactName()));
		viewHolder.txt_commodity
				.setText(CommonUtil.ToDBC(item.getOccupation()));
		bitmapUtiles.display(viewHolder.img_logo, list.get(position).getIcon(),
				config, true);

		if (list.size() == 1) {
			viewHolder.rela_main
					.setBackgroundResource(R.drawable.pyp_item_bg_click);
		} else if (position == 0) {
			viewHolder.rela_main
					.setBackgroundResource(R.drawable.yqa_first_bg_click);
		} else if (position == list.size() - 1) {
			viewHolder.rela_main
					.setBackgroundResource(R.drawable.yqa_last_bg_click);
		} else {
			viewHolder.rela_main
					.setBackgroundResource(R.drawable.yqa_mid_bg_click);
		}
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		TextView txt_name;
		TextView txt_commodity;
		ImageView img_logo;
		Button btn_gz;// 关注
		RelativeLayout rela_main;

	}
}
