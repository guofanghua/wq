package com.wq.Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.model.DecInfo;
import com.wq.model.User;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.BitmapUtil;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class decListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<DecInfo> list;
	private ViewHolder viewHolder;
	Context context;
	SimpleDateFormat formattmp;

	private FinalBitmap bitmapUtiles;
	BitmapDisplayConfig config = new BitmapDisplayConfig();
	Handler ImgHandle;
	public ImageView image = null;
	private View layout_top;
	public TextView txt_gg_count;

	public decListAdapter(Context context, ArrayList<DecInfo> list,
			Handler ImgHandle) {
		this.context = context;
		bitmapUtiles = FinalBitmap.create(context);
		inflater = LayoutInflater.from(context);
		config.setIsRoundCore(true);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		this.list = list;
		formattmp = new SimpleDateFormat("MM-dd HH:mm");
		this.ImgHandle = ImgHandle;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return this.list.size() + 1;

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

		if (position == 0) {

			View layout_top = inflater.inflate(R.layout.inq_list_item_gg, null);
			viewHolder = new ViewHolder();
			txt_gg_count = (TextView) layout_top
					.findViewById(R.id.inq_txt_count);
			viewHolder.img_logo = (ImageView) layout_top
					.findViewById(R.id.inq_item_img);
			layout_top.setTag("1");
			viewHolder.img_logo.setImageBitmap(BitmapUtil.toRoundCorner(
					BitmapFactory.decodeResource(context.getResources(),
							R.drawable.icon), 5));

			return layout_top;
		} else {
			if (convertView == null || convertView.getTag().equals("1")) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.inq_list_item, null);
				viewHolder.txt_name = (TextView) convertView
						.findViewById(R.id.inq_item_txt_wqh);
				viewHolder.txt_commodity = (TextView) convertView
						.findViewById(R.id.inq_item_txt_wqw);
				viewHolder.img_logo = (ImageView) convertView
						.findViewById(R.id.inq_item_img);
				viewHolder.img_tip = (ImageView) convertView
						.findViewById(R.id.inq_img_tip);
				viewHolder.txt_tip = (TextView) convertView
						.findViewById(R.id.inq_txt_count);
				viewHolder.txt_time = (TextView) convertView
						.findViewById(R.id.inq_item_txt_time);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final DecInfo item = this.list.get(position - 1);
			viewHolder.img_logo.setTag("1");
			if (TextUtils.isEmpty(item.getName()))
				viewHolder.txt_name.setText(CommonUtil.ToDBC(item.getVqh()));
			else
				viewHolder.txt_name.setText(CommonUtil.ToDBC(item.getName()));
			viewHolder.txt_commodity.setText(CommonUtil.ToDBC(item.getInfo()));
			viewHolder.txt_time.setText(dateUtil.formatDate(item.getTime(),
					formattmp));
			if (TextUtils.isEmpty(item.getIcon())) {
				viewHolder.img_logo.setBackgroundResource(R.drawable.icon);
			} else
				bitmapUtiles.display(viewHolder.img_logo, item.getIcon(),
						config, true);
			int dyCount = Integer.parseInt(item.getNewDynamicCount());
			if (dyCount == -1) {
				viewHolder.img_tip.setVisibility(View.VISIBLE);
				viewHolder.txt_tip.setVisibility(View.GONE);
			} else if (dyCount == 0) {
				viewHolder.img_tip.setVisibility(View.GONE);
				viewHolder.txt_tip.setVisibility(View.GONE);
			} else if (dyCount > 0) {
				viewHolder.img_tip.setVisibility(View.GONE);
				viewHolder.txt_tip.setVisibility(View.VISIBLE);
				viewHolder.txt_tip.setText(dyCount + "");
			}
			LoggerUtil.i("wqh=" + item.getVqh());
			viewHolder.img_logo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Message msg = ImgHandle.obtainMessage();
					msg.what = position;
					msg.sendToTarget();

				}
			});
			return convertView;
		}

	}

	private class ViewHolder {
		TextView txt_name;
		TextView txt_commodity;
		ImageView img_logo;
		ImageView img_tip;
		TextView txt_tip;
		TextView txt_time;

	}
}
