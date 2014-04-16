/**  
 * GalleryAdapter.java
 * @version 1.0
 * @author Haven
 * @createTime 2011-12-9 下午05:04:34
 */
package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.bitmap.display.mycallback;

import com.endure.wq.R;
import com.wq.ablumActivity;
import com.wq.UI.ablumImageView;
import com.wq.model.photoModel;
import com.wq.utils.BitmapAivenUtils;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView.ScaleType;

public class GalleryAdapter extends BaseAdapter {

	private Context context;
	private int images[] = { R.drawable.add_prompt, R.drawable.add_prompt,
			R.drawable.add_prompt, R.drawable.add_prompt };
	private FinalBitmap finalBitmap;
	private BitmapDisplayConfig config;
	Gallery.LayoutParams param = null;
	Bitmap bmp = null;
	ArrayList<photoModel> listpic = null;

	public GalleryAdapter(Context context, ArrayList<photoModel> listpic) {
		this.context = context;
		finalBitmap = FinalBitmap.create(context);
		config = new BitmapDisplayConfig();
		param = new Gallery.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		this.listpic = listpic;
		bmp = BitmapFactory.decodeResource(context.getResources(), images[0]);

	}

	@Override
	public int getCount() {
		return listpic.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		photoModel item = listpic.get(position);
		final ablumImageView img = new ablumImageView(context,
				DensityUtil.intScreenWidth(context),
				DensityUtil.intScreenHeight(context));
		img.setAdjustViewBounds(true);
		img.setLayoutParams(param);
		img.setScaleType(ScaleType.MATRIX);
		img.setMaxWidth(android.widget.RadioGroup.LayoutParams.FILL_PARENT);
		img.setImageBitmap(bmp);
		config.setLoadingBitmap(bmp);
		config.setLoadfailBitmap(bmp);
		config.setCallback(new mycallback() {
			@Override
			public void success(Bitmap bitmap) {
				LoggerUtil.i(bitmap.getWidth() + ",,,," + bitmap.getHeight());
//				img = new ablumImageView(context, bitmap.getWidth(), bitmap
//						.getHeight());
//				img.setAdjustViewBounds(true);
//				img.setLayoutParams(param);
//				img.setScaleType(ScaleType.MATRIX);
//				img.setMaxWidth(android.widget.RadioGroup.LayoutParams.FILL_PARENT);
//				img.setImageBitmap(bmp);
				// TODO Auto-generated method stub

			}

			@Override
			public void failure() {
				// TODO Auto-generated method stub
			}
		});
		// 为0则从web取
		if (item.getFlag() == 0)
			finalBitmap.display(img, item.getImageUrl(), config);
		else
			try {
				img.setImageBitmap(BitmapAivenUtils.readBitmap(context,
						item.getImageUrl()));
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}

		return img;
	}

}
