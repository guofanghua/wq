package com.wq.Adapter;

import java.util.ArrayList;
import java.util.Date;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.model.meCollectModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class collectListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<meCollectModel> list;
	private ViewHolder viewHolder;
	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	private int mRightWidth = 0;
	private Context context;

	public collectListAdapter(Context context, ArrayList<meCollectModel> list,
			int rightWidth) {
		this.context = context;
		bitmapUtiles = FinalBitmap.create(context);
		inflater = LayoutInflater.from(context);
		this.list = list;
		config.setIsRoundCore(true);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		this.mRightWidth = rightWidth;
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
		meCollectModel item = this.list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.collect_list_item, null);
			viewHolder.item_left = (RelativeLayout) convertView
					.findViewById(R.id.item_left);
			viewHolder.item_right = (RelativeLayout) convertView
					.findViewById(R.id.item_right);
			viewHolder.txt_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.txt_Time = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.iv_icon);
			viewHolder.image_type = (ImageView) convertView
					.findViewById(R.id.img_type);
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.tv_msg);
			viewHolder.image_other_logo = (ImageView) convertView
					.findViewById(R.id.img_other_logo);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		bitmapUtiles.display(viewHolder.img_logo, list.get(position).getIcon(),
				config, true);
		viewHolder.txt_title.setText(item.getTitle());
		viewHolder.txt_content.setText(item.getName());
		viewHolder.txt_Time.setText(dateUtil.spanNow(context, item.getTime(),
				new Date()));
		LinearLayout.LayoutParams lp1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		viewHolder.item_left.setLayoutParams(lp1);
		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
				LayoutParams.MATCH_PARENT);
		viewHolder.item_right.setLayoutParams(lp2);
		viewHolder.item_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		if (item.getType() <= 1) {
			viewHolder.image_type.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.cir_link));
			// viewHolder.image_type.setVisibility(View.VISIBLE);
			// viewHolder.image_other_logo.setVisibility(View.GONE);
		} else {
			// viewHolder.image_type.setVisibility(View.GONE);
			// viewHolder.image_other_logo.setVisibility(View.VISIBLE);
			// 文字
			if (item.getType() == 2) {
				viewHolder.image_type.setImageBitmap(BitmapFactory
						.decodeResource(context.getResources(),
								R.drawable.collect_wz_logo));
				// viewHolder.image_other_logo.setImageBitmap(BitmapFactory
				// .decodeResource(context.getResources(),
				// R.drawable.coloect_wz));
			}
		}
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		RelativeLayout item_left;
		RelativeLayout item_right;
		TextView txt_title;
		TextView txt_Time;
		ImageView img_logo;
		ImageView image_type;
		ImageView image_other_logo;
		TextView txt_content;
	}

	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}
}
