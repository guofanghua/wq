package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.utils.LoggerUtil;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class partSortAdapter extends BaseAdapter {
	private ArrayList<partComModel> list = new ArrayList<partComModel>();

	private ViewHolder viewHolder;

	private LayoutInflater inflater;
	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	public TextView txt_tip = null;

	public partSortAdapter(Context context, ArrayList<partComModel> list) {
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
		return list.size() + 2;
	}

	@Override
	public Object getItem(int object) {
		// TODO Auto-generated method stub
		return object;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position > 1) {
			position -= 2;
			// TODO Auto-generated method stub
			if (list.get(position).getPycontactName().length() == 1)// 如果是字母索引
				return false;// 表示不能点击
			return super.isEnabled(position);
		} else
			return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (position == 0) {

			convertView = inflater.inflate(R.layout.part_sort_list_tip, null);
			txt_tip = (TextView) convertView.findViewById(R.id.txt_tip);
			txt_tip.setText(R.string.part_string_new_part);
			if (User.bArr[2]) {
				txt_tip.setVisibility(View.VISIBLE);
				txt_tip.setText(User.tipCountArr[1]);
			} else
				txt_tip.setVisibility(View.GONE);
			convertView.setTag("1");
			return convertView;
		} else if (position == 1) {
			convertView = inflater
					.inflate(R.layout.part_sort_list_tip_mp, null);
			TextView txt_title = (TextView) convertView
					.findViewById(R.id.txt_tip);
			convertView.setTag("1");
			return convertView;
		} else {
			final partComModel item = this.list.get(position - 2);
			if (item.getContactName().length() == 1) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.part_sort_item_top,
						null);
				viewHolder.txt_title = (TextView) convertView
						.findViewById(R.id.txt_title);
				viewHolder.txt_title.setText(item.getContactName());

				convertView.setTag("1");
				return convertView;
			} else {
				if (convertView == null
						|| String.valueOf(convertView.getTag()).equals("1")) {
					viewHolder = new ViewHolder();
					convertView = inflater.inflate(
							R.layout.part_sort_list_item, null);

					viewHolder.txt_lxr = (TextView) convertView
							.findViewById(R.id.txt_lxr);
					viewHolder.txt_zw = (TextView) convertView
							.findViewById(R.id.txt_zw);
					viewHolder.img_logo = (ImageView) convertView
							.findViewById(R.id.part_item_img);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.img_logo.setTag("1");
				if (TextUtils.isEmpty(item.getContactName())) {
					viewHolder.txt_lxr.setText(item.getName());
				} else
					viewHolder.txt_lxr.setText(item.getContactName());
				viewHolder.txt_zw.setText(item.getOccupation());
				bitmapUtiles.display(viewHolder.img_logo, item.getIcon(),
						config, true);
				return convertView;
			}
		}
		// TODO Auto-generated method stub

	}

	private class ViewHolder {

		TextView txt_lxr;
		TextView txt_zw;
		ImageView img_logo;
		TextView txt_title;

	}

}
