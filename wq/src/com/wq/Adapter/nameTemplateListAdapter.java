package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.bitmap.display.mycallback;

import com.endure.wq.R;
import com.wq.me.nameCardplateListActivity;
import com.wq.model.User;
import com.wq.model.nameCardMb;
import com.wq.model.templateModel;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.sharedPreferenceUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class nameTemplateListAdapter extends BaseAdapter {
	ArrayList<nameCardMb> list;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private FinalBitmap finalBitmap;
	private BitmapDisplayConfig config;
	private Context context;

	public nameTemplateListAdapter(Context context, ArrayList<nameCardMb> list) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		finalBitmap = FinalBitmap.create(context);
		config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt_vertical));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt_vertical));

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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.name_card_mb_item, null);
			viewHolder.img_pre = (ImageView) convertView
					.findViewById(R.id.mb_image);
			viewHolder.img_select = (ImageView) convertView
					.findViewById(R.id.mn_check);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		finalBitmap.display(viewHolder.img_pre, list.get(position)
				.getFrontImgUrl(), config);
		int height = (DensityUtil.intScreenWidth(context) / 2 - DensityUtil
				.dip2px(context, 12)) * 480 / 320;
		viewHolder.img_pre.setMinimumHeight(height);

		if (TextUtils.isEmpty(User.nameCardTempId)) {
			User.nameCardTempId = list.get(position).getCardId();
			sharedPreferenceUtil.saveCompany(this.context);
			if (position == 0)
				viewHolder.img_select.setVisibility(View.VISIBLE);
			else
				viewHolder.img_select.setVisibility(View.GONE);
		} else {
			if (list.get(position).getCardId().equals(User.nameCardTempId)) {
				viewHolder.img_select.setVisibility(View.VISIBLE);
			} else
				viewHolder.img_select.setVisibility(View.GONE);
		}
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder {
		ImageView img_pre;// 分面
		ImageView img_select;// 是否已经选择
	}

}
