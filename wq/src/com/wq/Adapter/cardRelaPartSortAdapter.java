package com.wq.Adapter;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.Adapter.waterPullGridAdapter.Holder;
import com.wq.model.partComModel;
import com.wq.utils.LoggerUtil;

public class cardRelaPartSortAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	public TextView txt_tip = null;
	private ContListener listener;

	public cardRelaPartSortAdapter(Context context, ContListener clistener) {
		this.listener = clistener;
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
		return listener.getCount();
	}

	@Override
	public Object getItem(int object) {
		return object;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position > 0) {
			position -= 1;
			if (listener.getItem(position).getPycontactName().length() == 1)// 如果是字母索引
				return false;// 表示不能点击
			return super.isEnabled(position);
		} else
			return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final partComModel item = listener.getItem(position);
		ViewHolder viewHolder;
		if (item.getContactName().length() == 1) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.part_sort_item_top, null);
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
						R.layout.card_rela_select_sort_list_item, null);
				viewHolder.txt_lxr = (TextView) convertView
						.findViewById(R.id.txt_lxr);
				viewHolder.txt_zw = (TextView) convertView
						.findViewById(R.id.txt_zw);
				viewHolder.img_logo = (ImageView) convertView
						.findViewById(R.id.part_item_img);
				viewHolder.check = (CheckBox) convertView
						.findViewById(R.id.check);
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
			
			bitmapUtiles.display(viewHolder.img_logo, item.getIcon(), config,
					true);
			viewHolder.check.setChecked(item.isCheck());
			viewHolder.check.setTag(position);
			viewHolder.check.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.count(Integer.parseInt(v.getTag().toString()));
					}
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		TextView txt_lxr;
		TextView txt_zw;
		ImageView img_logo;
		TextView txt_title;
		CheckBox check;
	}

	public interface ContListener {
		public void count(int position);

		public int getCount();

		public partComModel getItem(int position);
	}

}
