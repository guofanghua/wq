package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.model.photoModel;
import com.wq.utils.DensityUtil;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 自定义的名片模板
 * */
public class waterPullGridAdapter extends BaseAdapter {
	private ArrayList<photoModel> dataList;
	private LayoutInflater mInflater;

	FinalBitmap bit;
	BitmapDisplayConfig config;

	callback call;
	Context context;
	Holder holder = null;
	ImageView temp;

	public waterPullGridAdapter(ArrayList<photoModel> list, Context con,
			callback callback) {
		mInflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dataList = list;
		this.context = con;
		bit = FinalBitmap.create(con);
		config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				con.getResources(), R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		config.setBitmapWidth(DensityUtil.intScreenWidth(context) / 3);
		config.setBitmapHeight(DensityUtil.intScreenWidth(context) / 3);
		this.call = callback;
		temp = new ImageView(context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.water_full_grid_item, null);
			holder = new Holder();
			holder.img = (ImageView) convertView
					.findViewById(R.id.water_full_img_item);
			holder.txt_title = (TextView) convertView
					.findViewById(R.id.txt_title);
			int layoutHeight = (DensityUtil.intScreenWidth(context) / 4);// 调整高度
			LayoutParams lp = new LayoutParams(
					DensityUtil.intScreenWidth(context) / 3, layoutHeight);
			holder.img.setLayoutParams(lp);
			convertView.setTag(holder);
		} else
			holder = (Holder) convertView.getTag();

		if (dataList.get(position).getFlag() == 0)
			bit.display(holder.img, dataList.get(position).getImageUrl(),
					config);
		else {
			holder.img.setBackgroundResource(dataList.get(position).getPid());
		//	bit.displayLocal(holder.img, dataList.get(position).getImageUrl(),
					//false);

		}
		if (position == dataList.size() - 1 && this.call != null) {
			this.call.loadMore();
		}
		holder.txt_title.setText(dataList.get(position).getExtStr());

		return convertView;
	}

	class Holder {
		ImageView img;
		TextView txt_title;
	}

	public interface callback {
		public void loadMore();
	}
}
