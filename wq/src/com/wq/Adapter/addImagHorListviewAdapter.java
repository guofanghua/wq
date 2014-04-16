package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.endure.wq.R;
import com.wq.UI.horizontalListview.BaseAdapterExtended;
import com.wq.model.photoModel;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.DensityUtil;

/**
 * 添加图片水平滑动listview
 * */
public class addImagHorListviewAdapter extends BaseAdapterExtended<photoModel> {

	Context context;
	ArrayList<photoModel> listpic = new ArrayList<photoModel>();
	private ViewHolder viewHolder;
	private FinalBitmap bitmapUtils;
	BitmapDisplayConfig config = null;
	Handler handler=null;

	public addImagHorListviewAdapter(Context context,
			ArrayList<photoModel> listpic, Handler imgClickHandler) {
		super();
		this.context = context;
		this.listpic = listpic;
		this.handler = imgClickHandler;
		this.bitmapUtils = FinalBitmap.create(context);
		this.config = new BitmapDisplayConfig();
		this.config.setBitmapHeight(DensityUtil.dip2px(context, 90));
		this.config.setBitmapWidth(DensityUtil.dip2px(context, 90));
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listpic.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		photoModel item = this.listpic.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.add_hor_img_item, null);
			viewHolder.img = (ImageView) convertView
					.findViewById(R.id.img_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (item.getFlag() == 0)
			bitmapUtils.display(viewHolder.img, item.getImageUrl(), config);
		else
			viewHolder.img.setImageBitmap(BitmapAivenUtils.readBitmap(
					context, item.getImageUrl()));
		viewHolder.img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (handler == null)
					return;
				Message msg = handler.obtainMessage();
				msg.what = position;
				msg.sendToTarget();
			
				// TODO Auto-generated method stub

			}
		});

		return convertView;
	}

	@Override
	public photoModel getItem(int position) {
		// TODO Auto-generated method stub
		return listpic.get(position);
	}

	private class ViewHolder {
		ImageView img;
	}
}
