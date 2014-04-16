package com.wq.localAblumScan;

import java.util.ArrayList;
import java.util.List;

import com.endure.wq.R;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class PicAdapter extends BaseAdapter {

	private Context ctx;
	private LayoutInflater mInflater;
	private List<PicItemMdl> dataList;
	public static int picNum = 0;

	public PicAdapter(Context ctx, List<PicItemMdl> list) {
		this.ctx = ctx;
		this.dataList = list;
		picNum = 0;
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (this.dataList == null) {
			this.dataList = new ArrayList<PicItemMdl>();
		}
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<PicItemMdl> getList() {
		return this.dataList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		PicItemMdl item = dataList.get(position);
		if (convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.ablum_item_select, null);
			holder.mImg = (ImageView) convertView.findViewById(R.id.itemImg);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.chck);
			convertView.setTag(holder);
		} else

			holder = (Holder) convertView.getTag();
		holder.checkBox.setTag(position);

		holder.checkBox.setChecked(dataList.get(position).isSelected());
		holder.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = Integer.parseInt(v.getTag().toString());
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) v;
				if (ablum_detail_activity.picNum >= 0
						&& !dataList.get(position).isSelected()
						&& picNum >= ablum_detail_activity.picNum) {
					cb.setChecked(false);
					CommonUtil.showToast(ctx, String.format(
							ctx.getString(R.string.me_string_img_max_format),
							ablum_detail_activity.picNum));

				} else {
					boolean isCheck = dataList.get(position).isSelected();
				
					dataList.get(pos).setSelected(!isCheck);
					if (!isCheck)
						picNum++;
					else
						picNum--;
					cb.setChecked(!isCheck);
				}
			}

		});

		// holder.checkBox
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// int pos = Integer.parseInt(buttonView.getTag()
		// .toString());
		//
		// if (ablum_detail_activity.picNum >= 0 && isChecked
		// && picNum >= ablum_detail_activity.picNum) {
		// CommonUtil.showToast(
		// ctx,
		// String.format(
		// ctx.getString(R.string.me_string_img_max_format),
		// ablum_detail_activity.picNum));
		// dataList.get(pos).setSelected(false);
		// } else {
		// LoggerUtil.i("position=" + position);
		//
		// dataList.get(pos).setSelected(isChecked);
		// if (isChecked)
		// picNum++;
		// else
		// picNum--;
		// }
		//
		// }
		// });

		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
				ctx.getContentResolver(), dataList.get(position).getThumbId(),
				Thumbnails.MICRO_KIND, null);
		holder.mImg.setImageBitmap(bitmap);

		return convertView;
	}

	class Holder {
		ImageView mImg;
		CheckBox checkBox;
	}

}
