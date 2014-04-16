package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.model.ZHModel;
import com.wq.model.leaveMessage;
import com.wq.utils.dateUtil;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class findZHListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<ZHModel> mlist;
	private ViewHolder viewHolder;
	FinalBitmap finalBitmap;
	// Handler delHandler;
	BitmapDisplayConfig displayConfig = null;
	LayoutParams params;
	Context mContext;
	Handler handler;

	public findZHListAdapter(Context context, ArrayList<ZHModel> list,
			Handler handler) {
		mContext = context;
		mlist = list;
		inflater = LayoutInflater.from(context);
		finalBitmap = FinalBitmap.create(context);
		displayConfig = new BitmapDisplayConfig();
		displayConfig.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setIsRoundCore(true);
		this.handler = handler;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
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
		// TODO Auto-generated method stub
		final ZHModel item = mlist.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.find_zh_list_item, null);
			viewHolder.img_logo = (ImageView) convertView
					.findViewById(R.id.image);
			viewHolder.txt_title = (TextView) convertView
					.findViewById(R.id.txt_title);
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			viewHolder.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		finalBitmap.display(viewHolder.img_logo, item.getLogoUrl(),
				displayConfig, true);
		viewHolder.txt_title.setText(item.getTitle());
		viewHolder.txt_time.setText(dateUtil.formatDate(item.getTime(),
				"yyyy-MM-dd"));
		viewHolder.txt_content.setText(item.getContent());
		viewHolder.img_logo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.arg1 = position;
				msg.sendToTarget();

			}
		});

		return convertView;
	}

	private class ViewHolder {
		ImageView img_logo;
		TextView txt_title;
		TextView txt_content;
		TextView txt_time;

	}
}
