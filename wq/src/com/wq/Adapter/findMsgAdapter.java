package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.Interface.IecCircleInterface;

import com.wq.model.ecCircleModel;
import com.wq.model.leaveMessage;

import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

import android.content.Context;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 留言 的adapter
 * 
 * */
public class findMsgAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<leaveMessage> mlist;
	private ViewHolder viewHolder;

	private IecCircleInterface click;

	private int margeSize = 0;
	private int index = 0;
	private Context mContext;

	FinalBitmap finalBitmap = null;
	BitmapDisplayConfig config = new BitmapDisplayConfig();

	public findMsgAdapter(Context context, ArrayList<leaveMessage> list,
			int index, IecCircleInterface click) {
		this.mContext = context;
		mlist = list;
		inflater = LayoutInflater.from(context);
		this.click = click;
		margeSize = DensityUtil.dip2px(context, 20);

		this.index = index;
		finalBitmap = FinalBitmap.create(context);
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
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

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		final leaveMessage msg = mlist.get(position);
		
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.find_ec_circle_leave_msg_item, null);
			// 留言人
			viewHolder.txt_lea_name = (TextView) convertView
					.findViewById(R.id.txt_leave_name);
			viewHolder.layout_main = (RelativeLayout) convertView
					.findViewById(R.id.layout_main);
			viewHolder.layout_main1 = (LinearLayout) convertView
					.findViewById(R.id.layout_main1);
			// viewHolder.img_logo = (ImageView) convertView
			// .findViewById(R.id.img_logo);
			// viewHolder.txt_time = (TextView) convertView
			// .findViewById(R.id.txt_time);
			// 留言内容
			viewHolder.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			viewHolder.txt_again = (TextView) convertView
					.findViewById(R.id.txt_again);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		params.topMargin = DensityUtil.dip2px(mContext, 3);

		int level = Integer.parseInt(msg.getLevel());
		if (level > 1) {
			params.leftMargin = margeSize * 1;
		} else
			params.leftMargin = DensityUtil.dip2px(mContext, 6);

		viewHolder.layout_main.setLayoutParams(params);
		viewHolder.txt_lea_name.setText(msg.getVqh() + ":");
		viewHolder.txt_content.setText(msg.getContent());
		// String url="http://news.baidu.com/z/resource/%E6%9C%B1%E7%BF%8A.jpg";
	
		// finalBitmap
		// .display(viewHolder.img_logo, msg.getLogoUrl(), config, true);
		// viewHolder.txt_time.setText(msg.getTime());
		// 针对此人留言
		viewHolder.layout_main1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				leaveMessage msg1 = new leaveMessage();
				msg1.setType("0");
				msg1.setSuperComId(msg.getComId());
				msg1.setLevel((Integer.parseInt(msg.getLevel()) + 1) + "");
				msg1.setSuperName(msg.getVqh());

				click.btnLeaveClick(index, msg1);

				// TODO Auto-generated method stub

			}
		});
		// 点击留言内容，进行恢复留言
		viewHolder.txt_lea_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ecCircleModel model = new ecCircleModel();
				model.setEnterpriseId(mlist.get(position).getEnterPriseId());
				model.setVqh(mlist.get(position).getVqh());
				click.checkUserClick(model);
			}
		});
		// 留言失败，再次提交
		if (msg.isSend()) {
			viewHolder.txt_again.setVisibility(View.GONE);
		} else {
			viewHolder.txt_again.setVisibility(View.VISIBLE);
			viewHolder.txt_again.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					click.commitClick(index, msg);

				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		TextView txt_lea_name;
		RelativeLayout layout_main;
		TextView txt_content;
		TextView txt_again;
		LinearLayout layout_main1;
		// ImageView img_logo;
		// TextView txt_time;

	}
}
