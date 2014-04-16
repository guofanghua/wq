package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;

import com.wq.model.photoModel;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;


//为Gallery控件提供适配器的类
public class imageSwitchAdapter extends BaseAdapter {
	private Context mcontext;
	private ArrayList<photoModel> list = new ArrayList<photoModel>(); // 通过放射机制保存所有图片的id
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;

	public imageSwitchAdapter(Context context, ArrayList<photoModel> list) {
		mcontext = context;
		this.list = list;
		finalBitmap = FinalBitmap.create(context);
		config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));

	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView img;
		if (convertView == null) {
			img = new ImageView(mcontext);
			img.setScaleType(ImageView.ScaleType.CENTER_CROP);
			img.setLayoutParams(new Gallery.LayoutParams(
					Gallery.LayoutParams.FILL_PARENT, DensityUtil.dip2px(
							mcontext, 160))); // 图片显示宽和长
			finalBitmap.display(img, list.get(position).getImageUrl(), config);
		} else {
			img = (ImageView) convertView;
		}
		return img;
	}

}